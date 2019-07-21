package model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Book
 * Representation of a Book
 * @author Manuel Gomez IV
 */
public class Book {

	private int id, yearPublished;
	private Publisher publisher;
	private String title, summary, isbn;
	private LocalDateTime dateAdded, lastModified;

	/**
	 * Constructor
	 * Blank constructor used to create blank Book objects
	 */
	public Book() {
		setPublisher(new Publisher(0, "Unknown"));
		setId(0);
		setYearPublished(0001);
		setTitle("");
		setSummary("");
	}

	/**
	 * Constructor
	 * Used to create a Book object given specific arguments
	 */
	public Book(int id, int yearPublished, String title, String summary, String isbn, LocalDateTime dateAdded,
			LocalDateTime lastModified, Publisher publisher) {
		setPublisher(publisher);
		setId(id);
		setYearPublished(yearPublished);
		setTitle(title);
		setSummary(summary);
		setIsbn(isbn);
		setDateAdded(dateAdded);
		setLastModified(lastModified);
	}

	/**
	 * Constructor
	 * Used to clone values from an existing Book
	 * @param book - The book to take the values from
	 */
	public Book(Book book) {
		setId(book.id);
		setYearPublished(book.yearPublished);
		setPublisher(book.publisher);
		setTitle(book.title);
		setSummary(book.summary);
		setIsbn(book.isbn);
		setDateAdded(book.dateAdded);
		setLastModified(book.lastModified);
	}

	public void setLastModified(LocalDateTime lastModified) {
		this.lastModified = lastModified;
	}

	public LocalDateTime getLastModified() {
		return lastModified;
	}

	public String toString() {
		return title;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
			this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public Boolean setTitle(String title) {
		if (title.length() > 1 && title.length() < 256) {
			this.title = title;
			return true;
		} else {
			return false;
		}
	}

	public String getSummary() {
		return summary;
	}

	public Publisher getPublisher() {
		return publisher;
	}

	public void setPublisher(Publisher publisher) {
		this.publisher = publisher;
	}

	public Boolean setSummary(String summary) {
		if (summary.length() < 65536) {
			this.summary = summary;
			return true;
		} else {
			return false;
		}
	}

	public String getIsbn() {
		return isbn;
	}

	public Boolean setIsbn(String isbn) {
		if (isbn.length() < 14) {
			this.isbn = isbn;
			return true;
		} else {
			return false;
		}
	}

	public LocalDateTime getDateAdded() {
		return dateAdded;
	}

	/**
	 * setDateAdded
	 * date_added should only be assigned by MySQL at the time the Book record is inserted into the db
	 */
	public void setDateAdded(LocalDateTime dateAdded) {
			this.dateAdded = dateAdded;
	}

	public int getYearPublished() {
		return yearPublished;
	}

	public Boolean setYearPublished(int yearPublished) {
		if (yearPublished <= LocalDate.now().getYear()) {
			this.yearPublished = yearPublished;
			return true;
		} else {
			return false;
		}
	}

	/**
	 * getAuditTrail
	 * @return - The ObservableList of AuditTrailEntries associated with this Book
	 */
	public ObservableList<AuditTrailEntry> getAuditTrail() {
		ObservableList<AuditTrailEntry> ateList = FXCollections.observableArrayList();
		ateList = SingletonSwitcher.getInstance().getATGate().getBookAuditHistory(this);
		;
		return ateList;
	}

	/**
	 * getAuthors
	 * @return - The ObservableList of Authors associated with this Book
	 */
	public ObservableList<AuthorBook> getAuthors() {
		ObservableList<AuthorBook> aList = FXCollections.observableArrayList();
		aList = SingletonSwitcher.getInstance().getBTGate().getAuthorsForBook(this);
		return aList;
	}

	public void setISBN(String isbn) {
		this.isbn = isbn;
	}
}
