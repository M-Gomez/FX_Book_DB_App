package model;

import java.util.Collection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TextField;

/**
 * AuthorBookTableEntry
 * Extension of AuthorBook that adds a royalty TextField and allows for easy collection into an ObservableList for display purposes
 * @author Manuel Gomez IV
 */
public class AuthorBookTableEntry extends AuthorBook {

	private TextField royaltyTextField;

	/**
	 * Constructor
	 * @param author - Author to be bound
	 * @param book - Book they authored
	 * @param royalty - Their Royalty
	 * @param newRecord - Whether this is an existing record that was altered, or a wholly new entry
	 */
	public AuthorBookTableEntry(Author author, Book book, int royalty, boolean newRecord) {
		super(author, book, royalty, newRecord);
		royaltyTextField = new TextField(String.valueOf(royalty / 1000) + "%");
	}

	/**
	 * Constructor
	 * @param ab - Existing AuthorBook entry to use the values from
	 */
	public AuthorBookTableEntry(AuthorBook ab) {
		super(ab);
		royaltyTextField = new TextField(String.valueOf(royalty / 1000) + "%");
	}

	public void setRoyalty(int royalty) {
		super.setRoyalty(royalty);
		royaltyTextField.setText(String.valueOf(royalty / 1000) + "%");
	}

	public TextField getRoyaltyTextField() {
		return royaltyTextField;

	}

	/**
	 * collectionToABTE
	 * @param abList - The collection of AuthorBooks to be converted to an ObservableList of AuthorBookTableEntries
	 * @return - The converted ObservableList of AuthorBookTableEntries
	 */
	public static ObservableList<AuthorBookTableEntry> collectionToABTE(Collection<AuthorBook> abList) {
		ObservableList<AuthorBookTableEntry> abteList = FXCollections.observableArrayList();
		for (AuthorBook ab : abList) {
			AuthorBookTableEntry abte = new AuthorBookTableEntry(ab);
			abteList.add(abte);
		}
		return abteList;

	}
}
