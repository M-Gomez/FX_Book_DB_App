package model;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

/**
 * BookTableGateway
 * Gateway used to interface between the MySQL DB and the Application for Book related queries and insertions
 * @author Manuel Gomez IV
 */
public class BookTableGateway {

	public Connection conn;
	private PublisherTableGateway ptGate; // Stored references to the at and pt Gates to make code more readable and less verbose.
	private AuditTableGateway atGate;
	private static Logger logger = LogManager.getLogger(BookTableGateway.class);

	/**
	 * Constructor
	 * Creates a connection by calling establishDBConnection in SingletonSwitcher
	 * @author Manuel Gomez IV
	 */
	public BookTableGateway() {
		conn = SingletonSwitcher.getInstance().establishDBConnection();
	}

	/**
	 * getBooksPublishedByWithAuthors
	 * Returns ArrayList of Books published by the selected Publisher that have at least 1 author attached
	 * @param publisher - The selected Publisher
	 * @param pageNum - The page to display, necessary because there is a limit of 50 records per query instituted to avoid heavy load on DB and App
	 * @return - The ArrayList of Books
	 */
	public ArrayList<Book> getBooksPublishedByWithAuthors(Publisher publisher, int pageNum) {
		ArrayList<Book> bookArr = new ArrayList<Book>();
		PreparedStatement stmt;
		try {
			stmt = conn.prepareStatement(
					"SELECT DISTINCT id, title, isbn, publisher_id FROM Book INNER JOIN author_book on publisher_id=? AND Book.id=author_book.book_id  LIMIT 50 OFFSET ?");
			stmt.setInt(1, publisher.getId());
			stmt.setInt(2, pageNum * 50);
			ResultSet bookRS = stmt.executeQuery();
			while (bookRS.next()) {
				Book book = new Book();
				book.setId(bookRS.getInt(1));
				book.setTitle(bookRS.getString(2));
				book.setISBN(bookRS.getString(3));
				book.setPublisher(new Publisher(bookRS.getInt(4), ptGate.getPublisherName(bookRS.getInt(4))));
				bookArr.add(book);
			}
			stmt.close();
			bookRS.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return bookArr;
	}

	/**
	 * getBooksPublishedBy
	 * Returns ArrayList of Books published by the selected Publisher
	 * @param publisher - The selected Publisher
	 * @param pageNum - The page to display, necessary because there is a limit of 50 records per query instituted to avoid heavy load on DB and App
	 * @return - The ArrayList of Books
	 */
	public ArrayList<Book> getBooksPublishedBy(Publisher publisher, int pageNum) {
		ArrayList<Book> bookArr = new ArrayList<Book>();
		PreparedStatement stmt;
		try {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
			stmt = conn.prepareStatement("SELECT * FROM Book WHERE publisher_id=? LIMIT 50 OFFSET ?");
			stmt.setInt(1, publisher.getId());
			stmt.setInt(2, pageNum * 50);
			ResultSet bookRS = stmt.executeQuery();
			while (bookRS.next()) {
				LocalDateTime lastModified = bookRS.getTimestamp(8).toLocalDateTime();
				String date = bookRS.getString(7);
				int cutoff = date.lastIndexOf(':');
				date = date.substring(0, cutoff);
				Book book = new Book(bookRS.getInt(1), bookRS.getInt(2), bookRS.getString(3), bookRS.getString(4),
						bookRS.getString(6), LocalDateTime.parse(date, formatter), lastModified,
						new Publisher(bookRS.getInt(5), ptGate.getPublisherName(bookRS.getInt(5))));
				bookArr.add(book);
			}
			stmt.close();
			bookRS.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return bookArr;
	}

	/**
	 * getBooks
	 * @param pageNum - The section of the results to return
	 * @param searchTerm - The term that was searched if any
	 * @return - ObservableList of 50 Books based on pageNum and searchTerm
	 */
	public ObservableList<Book> getBooks(int pageNum, String searchTerm) {
		ObservableList<Book> books = FXCollections.observableArrayList();
		PreparedStatement stmt;
		ResultSet bookRS;
		try {
			if (searchTerm == null || searchTerm == "" || searchTerm == "\n" || searchTerm == " " || searchTerm == " \n"
					|| searchTerm.length() == 0) {
				stmt = conn.prepareStatement("SELECT * FROM Book LIMIT 50 OFFSET ?");
				stmt.setInt(1, pageNum * 50); // Zero based page index.
			} else {
				stmt = conn.prepareStatement(
						"SELECT * FROM Book WHERE MATCH(title) AGAINST(?) ORDER BY MATCH(title) AGAINST(?) DESC LIMIT 50 OFFSET ?");
				stmt.setString(1, searchTerm);
				stmt.setString(2, searchTerm);
				stmt.setInt(3, pageNum * 50); // Zero based page index.
			}
			stmt.executeQuery();
			bookRS = stmt.getResultSet();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
			LocalDateTime lastModified;
			while (bookRS.next()) {
				lastModified = bookRS.getTimestamp(8).toLocalDateTime();
				String date = bookRS.getString(7);
				int cutoff = date.lastIndexOf(':');
				date = date.substring(0, cutoff);
				Book newBook = new Book(bookRS.getInt(1), bookRS.getInt(2), bookRS.getString(3), bookRS.getString(4),
						bookRS.getString(6), LocalDateTime.parse(date, formatter), lastModified,
						new Publisher(bookRS.getInt(5), ptGate.getPublisherName(bookRS.getInt(5))));
				books.add(newBook);
			}
			bookRS.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return books;
	}

	/**
	 * updateBook
	 * Updates a book within the DB based on changes made to it
	 * @param book - The book to change
	 * @return - A Book OBJ that reflects the changes that were made in the DB
	 */
	public Book updateBook(Book book) {
		PreparedStatement stmt;
		ResultSet rs;
		try {

			stmt = conn.prepareStatement("SELECT * from Book where id=?");
			stmt.setInt(1, book.getId());
			rs = stmt.executeQuery();
			rs.next();
			if (book.getLastModified().equals(rs.getTimestamp(8).toLocalDateTime())) {
				if (book.getYearPublished() != rs.getInt(2)) {
					atGate.insertAuditTrailEntry(book, "year_published", rs.getInt(2), book.getYearPublished());
				}
				if (!book.getTitle().equals(rs.getString(3))) {
					atGate.insertAuditTrailEntry(book, "title", rs.getString(3), book.getTitle());
				}
				if (!book.getSummary().equals(rs.getString(4))) {
					atGate.insertAuditTrailEntry(book, "summary", rs.getString(4), book.getSummary());
				}
				if (book.getPublisher().getId() != rs.getInt(5)) {
					atGate.insertAuditTrailEntry(book, "publisher_id", rs.getInt(5), book.getPublisher().getId());
				}
				if (!book.getIsbn().equals(rs.getString(6))) {
					atGate.insertAuditTrailEntry(book, "isbn", rs.getString(6), book.getIsbn());
				}
				stmt = conn.prepareStatement(
						"UPDATE Book SET year_published = ? , title = ? , summary = ? , isbn = ?, publisher_id = ? where id=?");
				stmt.setInt(1, book.getYearPublished());
				stmt.setString(2, book.getTitle());
				stmt.setString(3, book.getSummary());
				stmt.setString(4, book.getIsbn());
				stmt.setInt(5, book.getPublisher().getId());
				stmt.setInt(6, book.getId());
				stmt.executeUpdate();
				stmt = conn.prepareStatement("SELECT last_modified from Book where id=?");
				stmt.setInt(1, book.getId());
				rs = stmt.executeQuery();
				rs.next();
				book.setLastModified(rs.getTimestamp(1).toLocalDateTime());
				rs.close();
				stmt.close();
			} else {
				Alert alert = new Alert(Alert.AlertType.ERROR,
						"This Book has been modified since it was opened, please return to the booklist to refresh this entry!",
						ButtonType.CLOSE);
				alert.showAndWait();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return book;
	}

	/**
	 * addAuthorToBook
	 * Adds an AuthorBook entry to the DB that links an Author to a Book
	 * @param author - The author to be Bound
	 * @param book - The Book they authored
	 * @param royalty - Their royalty
	 */
	public void addAuthorToBook(Author author, Book book, float royalty) {
		PreparedStatement stmt;
		try {
			stmt = conn.prepareStatement("INSERT into author_book(author_id, book_id, royalty) VALUES(?,?,?)");
			stmt.setInt(1, author.getId());
			stmt.setInt(2, book.getId());
			stmt.setBigDecimal(3, BigDecimal.valueOf(royalty));
			stmt.executeUpdate();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * insertBook
	 * Inserts a new Book record into the DB
	 * @param book - The book to insert
	 * @return - A copy of the inserted book as a Book OBJ
	 */
	public Book insertBook(Book book) {
		PreparedStatement stmt;
		ResultSet rs;
		try {
			stmt = conn.prepareStatement("SELECT id from Book where id=?");
			stmt.setInt(1, book.getId());
			rs = stmt.executeQuery();
			logger.info(book.getId());
			if (rs.next()) {
				return book;
			}
			stmt = conn.prepareStatement(
					"INSERT into Book(year_published, title, summary, publisher_id, isbn) VALUES(?,?,?,?,?)");
			stmt.setInt(1, book.getYearPublished());
			stmt.setString(2, book.getTitle());
			stmt.setString(3, book.getSummary());
			stmt.setInt(4, book.getPublisher().getId());
			stmt.setString(5, book.getIsbn());
			stmt.executeUpdate();
			stmt = conn.prepareStatement(
					"SELECT * from Book where year_published=? AND title=? AND summary=? AND publisher_id=? AND isbn=?");
			stmt.setInt(1, book.getYearPublished());
			stmt.setString(2, book.getTitle());
			stmt.setString(3, book.getSummary());
			stmt.setInt(4, book.getPublisher().getId());
			stmt.setString(5, book.getIsbn());
			rs = stmt.executeQuery();
			rs.next();
			book.setId(rs.getInt(1));
			book.setYearPublished(rs.getInt(2));
			book.setDateAdded(rs.getTimestamp(7).toLocalDateTime());
			book.setLastModified(rs.getTimestamp(8).toLocalDateTime());
			atGate.insertNewBookAuditEntry(book);
			rs.close();
			stmt.close();
			return book;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * deleteMethod
	 * Deletes a book from the DB based on ID
	 * @param book - The book to be deleted
	 * @return - Boolean relaying whether Deletion was successful or not
	 */
	public boolean deleteMethod(Book book) {
		try {
			PreparedStatement stmt = conn.prepareStatement("DELETE FROM Book where id=?");
			stmt.setInt(1, book.getId());
			stmt.executeUpdate();
			stmt.close();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * removeAuthorBook
	 * Removes an AuthorBook entry from the DB, unlinking an Author from a Book
	 * @param author - The Author to be removed
	 * @param book - The book to remove them from
	 */
	public void removeAuthorBook(Author author, Book book) {
		try {
			PreparedStatement stmt = conn.prepareStatement("DELETE from author_book where author_id=? AND book_id=?");
			stmt.setInt(1, author.getId());
			stmt.setInt(2, book.getId());
			stmt.executeUpdate();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void setPTGate(PublisherTableGateway ptGate) {
		this.ptGate = ptGate;
	}

	public void setATGate(AuditTableGateway atGate) {
		this.atGate = atGate;
	}

	/**
	 * insertBookArray
	 * inserts an ArrayList of new Books into the DB, used for insertion of large amounts of books at once
	 * @param bookArr - The ArrayList of books to be inserted
	 */
	public void insertBookArray(ArrayList<Book> bookArr) {
		PreparedStatement stmt;
		try {
			stmt = conn.prepareStatement(
					"INSERT into Book(year_published, title, summary, publisher_id, isbn) VALUES(?,?,?,?,?)");
			for (Book book : bookArr) {
				stmt.setInt(1, book.getYearPublished());
				stmt.setString(2, book.getTitle());
				stmt.setString(3, book.getSummary());
				stmt.setInt(4, book.getPublisher().getId());
				stmt.setString(5, book.getIsbn());
				stmt.addBatch();
			}
			stmt.executeBatch();
		} catch (Exception e) {

		}
	}

	/**
	 * getAuthorsForBook
	 * Returns ObservableList of Authors who authored a Book
	 * @param book - The book to return authors for
	 * @return - The ObservableList of Authors associated with book
	 */
	public ObservableList<AuthorBook> getAuthorsForBook(Book book) {
		ObservableList<AuthorBook> atList = FXCollections.observableArrayList();
		PreparedStatement stmt;
		ResultSet rs1, rs2;
		try {
			stmt = conn.prepareStatement("SELECT * from author_book where book_id=?");
			stmt.setInt(1, book.getId());
			rs1 = stmt.executeQuery();
			while (rs1.next()) {
				stmt = conn.prepareStatement("SELECT * from Author where id=?");
				stmt.setInt(1, rs1.getInt(1));
				rs2 = stmt.executeQuery();
				rs2.next();
				atList.add(new AuthorBook(
						new Author(rs2.getInt(1), rs2.getString(2), rs2.getString(3), rs2.getDate(4).toLocalDate(),
								rs2.getString(5), rs2.getString(6)),
						book, Math.round(rs1.getFloat(3) * 100000), false));
				if (rs1.isLast()) {
					rs2.close();
				}
			}
			rs1.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return atList;
	}

	/**
	 * addAuthor
	 * Creates a new Author and adds them to the list of existing authors
	 * @param author - The new Author to be added
	 * @return - A copy of the Author as it exists in the DB
	 */
	public Author addAuthor(Author author) {
		PreparedStatement stmt;
		ResultSet rs;
		try {
			stmt = conn.prepareStatement(
					"insert into Author(id, first_name, last_name, dob, gender, web_site) VALUES(?,?,?,?,?,?)");
			stmt.setInt(1, 0);
			stmt.setString(2, author.getfName());
			stmt.setString(3, author.getlName());
			stmt.setDate(4, Date.valueOf(author.getDateOfBirth()));
			stmt.setString(5, author.getGender());
			if (author.getWebSite() != null) {
				stmt.setString(6, author.getWebSite());
			} else {
				stmt.setString(6, "");
			}
			stmt.executeUpdate();
			stmt = conn.prepareStatement("select id from Author where first_name=? AND last_name=? AND dob=?");
			stmt.setString(1, author.getfName());
			stmt.setString(2, author.getlName());
			stmt.setDate(3, Date.valueOf(author.getDateOfBirth()));
			rs = stmt.executeQuery();
			rs.next();
			author.setId(rs.getInt(1));
			if (rs.next()) {
				stmt.close();
				rs.close();
				return author;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return author;
	}

	/**
	 * getAuthors
	 * Returns an ObservableList of all Authors in the DB
	 * @return - ObservableList of all Authors in the DB
	 */
	public ObservableList<Author> getAuthors() {
		ObservableList<Author> aList = FXCollections.observableArrayList();
		PreparedStatement stmt;
		ResultSet rs;
		try {
			stmt = conn.prepareStatement("select * from Author");
			rs = stmt.executeQuery();
			while (rs.next()) {
				aList.add(new Author(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getDate(4).toLocalDate(),
						rs.getString(5), rs.getString(6)));
			}
			stmt.close();
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return aList;
	}

	/**
	 * updateAuthorRoyalty
	 * Changes an authors Royalty in the given Book entry in the DB
	 * @param author - The author to update the royalty of
	 * @param newRoyalty - Their new royalty value
	 */
	public void updateAuthorRoyalty(Author author, Book book, int newRoyalty) {
		PreparedStatement stmt;
		try {
			stmt = conn.prepareStatement("update author_book SET royalty=? where author_id=? AND book_id=?");
			stmt.setBigDecimal(1, BigDecimal.valueOf((float) newRoyalty / 100000));
			stmt.setInt(2, author.getId());
			stmt.setInt(3, book.getId());
			stmt.executeUpdate();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * getTotalBookCount
	 * Returns the total number of Book entries that exist in the DB, or the total number of Books that fit a search term
	 * @param searchTerm - The search term used, blank string for no search term
	 * @return - The number of results
	 */
	public int getTotalBookCount(String searchTerm) {
		PreparedStatement stmt;
		ResultSet rs;
		int bookCount = 0;
		try {
			if (searchTerm == null || searchTerm == "" || searchTerm == "\n" || searchTerm == " " || searchTerm == " \n"
					|| searchTerm.length() == 0) {
				stmt = conn.prepareStatement("SELECT COUNT(*) as bookCount FROM Book");
			} else {
				stmt = conn.prepareStatement("SELECT COUNT(*) as bookCount FROM Book WHERE MATCH(title) AGAINST(?)");
				stmt.setString(1, searchTerm);
			}
			rs = stmt.executeQuery();
			rs.next();
			bookCount = rs.getInt("bookCount");
			stmt.close();
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return bookCount;
	}

	/**
	 * getPublisherCatalogSizeWithAuthors
	 * Returns the number of Book entries published by publisher that have at least 1 Author
	 * @param publisher - The publisher to find the book entry count for
	 * @return - The number of associated Books
	 */
	public int getPublisherCatalogSizeWithAuthors(Publisher publisher) {
		PreparedStatement stmt;
		int bookCount = 0;
		try {
			stmt = conn.prepareStatement(
					"SELECT COUNT(*) FROM (SELECT id, COUNT(*) FROM (SELECT id as id from Book INNER JOIN author_book on publisher_id=?) AS T GROUP BY id) AS A");
			stmt.setInt(1, publisher.getId());
			ResultSet rs = stmt.executeQuery();
			rs.next();
			bookCount = rs.getInt(1);
			stmt.close();
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bookCount;
	}

	/**
	 * getPublisherCatalogSize
	 * Returns the number of Book entries published by publisher, whether they have any Authors or not
	 * @param publisher - The publisher to find the book entry count for
	 * @return - The number of associated Books
	 */
	public int getPublisherCatalogSize(Publisher publisher) {
		PreparedStatement stmt;
		int bookCount = 0;
		try {
			stmt = conn.prepareStatement("SELECT COUNT(*) as bookCount FROM Book WHERE publisher_id=?");
			stmt.setInt(1, publisher.getId());
			ResultSet rs = stmt.executeQuery();
			rs.next();
			bookCount = rs.getInt("bookCount");
			stmt.close();
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bookCount;
	}
}
