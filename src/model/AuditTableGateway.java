package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * AuditTableGateway
 * Acts as a connector between the app and the MySQL database
 * @author Manuel Gomez IV
 */
public class AuditTableGateway {
	
	public Connection conn;
	
	private static Logger logger = LogManager.getLogger(AuditTableGateway.class);

	/**
	 * Constructor
	 * sets conn equal to a new JDBC database connection
	 */
	public AuditTableGateway() {
		conn = SingletonSwitcher.getInstance().establishDBConnection();
	}

	/**
	 * getBookAuditHistory
	 * @param book - book to return audit history for
	 * @return - ObservableList of audit trails for the requested book
	 */
	public ObservableList<AuditTrailEntry> getBookAuditHistory(Book book) {
		ObservableList<AuditTrailEntry> ateList = FXCollections.observableArrayList();
		PreparedStatement stmt;
		ResultSet rs;
		try {
			stmt = conn.prepareStatement("SELECT * from book_audit_trail where book_id=?");
			stmt.setInt(1, book.getId());
			rs = stmt.executeQuery();
			while (rs.next()) {
				ateList.add(new AuditTrailEntry(rs.getInt(1), rs.getTimestamp(3).toLocalDateTime(), rs.getString(4)));
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return ateList;
	}

	/**
	 * insertAuditTrailEntryForRoyaltyChanges
	 * Inserts am audit trail entry for royalty changes into the DB
	 * @param book - The book that was altered
	 * @param fieldChanged - String containing the changed authors name ex: "someone's royalty"
	 * @param oldVal - The value before the change
	 * @param newVal - The value after the change
	 */
	public void insertAuditTrailEntryForRoyaltyChanges(Book book, String fieldChanged, int oldVal, int newVal) {
		PreparedStatement stmt;
		try {
			stmt = conn.prepareStatement("INSERT into book_audit_trail(book_id, entry_msg) VALUES(?,?)");
			stmt.setInt(1, book.getId());
			stmt.setString(2, fieldChanged + " changed from " + String.valueOf((float) oldVal / 100000) + " to "
					+ String.valueOf((float) newVal / 100000));
			stmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * insertAuditTrailEntry
	 * Inserts am audit trail entry for book changes into the DB
	 * @param book - The book that was altered
	 * @param fieldChanged - String containing the changed authors name and the field that was changed
	 * @param oldVal - The value before the change
	 * @param newVal - The value after the change
	 */
	public void insertAuditTrailEntry(Book book, String fieldChanged, String oldVal, String newVal) {
		PreparedStatement stmt;
		try {
			stmt = conn.prepareStatement("INSERT into book_audit_trail(book_id, entry_msg) VALUES(?,?)");
			stmt.setInt(1, book.getId());
			stmt.setString(2, fieldChanged + " changed from " + oldVal + " to " + newVal);
			stmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * insertAuditTrailEntry
	 * Inserts am audit trail entry for addition or removal of an Author
	 * @param book - The book that was altered
	 * @param author - The author to be added or removed
	 * @param trueIfAddedFalseIfRemoved - Boolean determining whether to remove or add the Author from the book
	 */
	public void insertAuditTrailEntry(Book book, Author author, boolean trueIfAddedFalseIfRemoved) {
		PreparedStatement stmt;
		try {
			stmt = conn.prepareStatement("INSERT into book_audit_trail(book_id, entry_msg) VALUES(?,?)");
			stmt.setInt(1, book.getId());
			if (trueIfAddedFalseIfRemoved) {
				stmt.setString(2, author.getfName() + " " + author.getlName() + " added to list of authors");
			} else {
				stmt.setString(2, author.getfName() + " " + author.getlName() + " removed from list of authors");
			}
			stmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	/**
	 * insertAuditTrailEntry
	 * Inserts am audit trail entry for book changes into the DB, uses int params instead of String
	 * @param book - The book that was altered
	 * @param fieldChanged - String containing the changed authors name and the field that was changed
	 * @param oldVal - The value before the change
	 * @param newVal - The value after the change
	 */
	public void insertAuditTrailEntry(Book book, String fieldChanged, int oldVal, int newVal) {
		PreparedStatement stmt;
		try {
			stmt = conn.prepareStatement("INSERT into book_audit_trail(book_id, entry_msg) VALUES(?,?)");
			stmt.setInt(1, book.getId());
			stmt.setString(2, fieldChanged + " changed from " + oldVal + " to " + newVal);
			stmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * insertNewBookAuditEntry
	 * Adds the initial AuditTrailEntry when a book is first added to the database
	 * @param book - The book that was added to the DB
	 */
	public void insertNewBookAuditEntry(Book book) {
		PreparedStatement stmt;
		try {
			stmt = conn.prepareStatement("INSERT into book_audit_trail(book_id, entry_msg) VALUES(?,?)");
			stmt.setInt(1, book.getId());
			stmt.setString(2, "Book Added");
			stmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}