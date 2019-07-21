package model;

/**
 * AuthorBook
 * Class used to represent the bond between an Author and a Book they've written
 * @author Manuel Gomez IV
 */
public class AuthorBook {

	protected Author author;
	protected Book book;
	protected int royalty; // Royalty multiplied by 100000
	protected boolean newRecord;

	/**
	 * Constructor
	 * Creates a new AuthorBook OBJ based on passed args
	 * @param author - The Author to be bound
	 * @param book - The Book they authored
	 * @param royalty - Their royalty
	 * @param newRecord - Whether this is an existing record that was altered, or a wholly new entry
	 */
	public AuthorBook(Author author, Book book, int royalty, boolean newRecord) {
		this.author = author;
		this.book = book;
		this.royalty = royalty;
		this.newRecord = newRecord;
	}

	/**
	 * Constructor
	 * Clones the values of an existing AuthorBook OBJ
	 * @param ab - The AuthorBook to be cloned
	 */
	public AuthorBook(AuthorBook ab) {
		this.author = ab.author;
		this.book = ab.book;
		this.royalty = ab.royalty;
		this.newRecord = ab.newRecord;
	}

	public Author getAuthor() {
		return author;
	}

	public void setAuthor(Author author) {
		this.author = author;
	}

	public Book getBook() {
		return book;
	}

	public void setBook(Book book) {
		this.book = book;
	}

	public int getRoyalty() {
		return royalty;
	}

	public void setRoyalty(int royalty) {
		this.royalty = royalty;
	}

	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}
}
