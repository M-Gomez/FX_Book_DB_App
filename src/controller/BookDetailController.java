package controller;

import java.util.ArrayList;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import model.AuthorBook;
import model.AuthorBookTableEntry;
import model.Book;
import model.BookTableGateway;
import model.Publisher;
import model.PublisherTableGateway;
import model.SingletonSwitcher;

/**
 * BookDetailController
 * The controller component for BookDetailView.fxml
 * @author Manuel Gomez IV
 */
public class BookDetailController {

	private static Logger logger = LogManager.getLogger(BookListController.class);
	private Book book;
	private boolean isBlankDetailView, unsavedChanges;
	private BookTableGateway btg;
	private PublisherTableGateway ptg;
	@FXML
	private TextField titleField, yearPublishedField, ISBNField, summaryField;
	@FXML
	private DatePicker dateAddedSelector;
	@FXML
	private ComboBox<Publisher> publisherComboBox;
	@FXML
	private Button auditTrailButton, addAuthorButton, newAuthorButton, delAuthorButton;
	@FXML
	private TableView<AuthorBookTableEntry> authorTable;

	/**
	 * saveChanges
	 * attempts to save book changes and if successful save AuthorBookTableEntry changes
	 * @param event - The event that called saveChanges
	 */
	@FXML
	void saveChanges(ActionEvent event) {
		try {
			if (saveBookChanges()) {
				saveAuthorChanges();
			}
		} catch (Exception e) {
			Alert alert = new Alert(Alert.AlertType.ERROR,
					"Unable to save, please ensure all fields are entered properly!", ButtonType.CLOSE);
			alert.showAndWait();
		}
	}

	/**
	 * saveAuthorChanges
	 * saves changes made to AuthorBookTableEntry and updates it in the DB
	 */
	public void saveAuthorChanges() {
		for (AuthorBookTableEntry abte : authorTable.getItems()) {
			String s = abte.getRoyaltyTextField().getText();
			int newRoyalty = Math.round(Float.parseFloat(s.replace("%", "")) * 1000);
			if (newRoyalty != abte.getRoyalty()) {
				btg.updateAuthorRoyalty(abte.getAuthor(), book,  newRoyalty);
				SingletonSwitcher.getInstance().getATGate().insertAuditTrailEntryForRoyaltyChanges(book,
						abte.getAuthor().toString() + "'s royalty ", abte.getRoyalty(), newRoyalty);
				abte.setRoyalty(newRoyalty);
			}
		}
	}

	/**
	 * deleteAuthor
	 * Removes a selected author from a book, and deletes the corresponding AuthorBookTableEntry from the DB
	 * @param event - The event that called deleteAuthor
	 */
	@FXML
	void deleteAuthor(ActionEvent event) {
		if (authorTable.getSelectionModel().getSelectedItem() != null) {
			AuthorBook authorBook = authorTable.getSelectionModel().getSelectedItem();
			btg.removeAuthorBook(authorBook.getAuthor(), book);
			SingletonSwitcher.getInstance().getATGate().insertAuditTrailEntry(book, authorBook.getAuthor(), false);
			authorTable.getItems().remove(authorBook);
		} else {
			Alert alert = new Alert(Alert.AlertType.ERROR, "No author record selected!", ButtonType.CLOSE);
			alert.showAndWait();
		}
	}

	/**
	 * openNewAuthorView
	 * Opens the interface to add an author to a book
	 * @param event - the event that called openNewAuthorView
	 */
	@FXML
	void openNewAuthorView(ActionEvent event) {
		SingletonSwitcher.getInstance().openAddAuthorView(true, this);
	}

	/**
	 * saveBookChanges
	 * Saves changes made to the book if they are valid, and updates the database
	 * @return - True if save is successful, false if unsucessful
	 */
	public boolean saveBookChanges() {
		Book buffer;
		if (isBlankDetailView) {
			book = new Book();
		}
		if (book.setTitle(titleField.getText()) && book.setYearPublished(Integer.parseInt(yearPublishedField.getText()))
				&& book.setIsbn(ISBNField.getText()) && book.setSummary(summaryField.getText())) {
			if (book.getId() == 0) {
				book.setPublisher(publisherComboBox.getSelectionModel().getSelectedItem());
				buffer = btg.insertBook(book);
				book = new Book(buffer);
				isBlankDetailView = false;
				auditTrailButton.setDisable(false);
				logger.info("Book Details Saved");
				unsavedChanges = false;
				return true;
			} else {
				book.setPublisher(publisherComboBox.getSelectionModel().getSelectedItem());
				buffer = btg.updateBook(book);
				book = new Book(buffer);
				logger.info("Book Details Saved");
				unsavedChanges = false;
				return true;
			}
		} else {
			Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid Values entered, save aborted!", ButtonType.CLOSE);
			alert.showAndWait();
			try {
				throw new Exception();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		}
	}

	/**
	 * onExitAttempt
	 * Ensures that if any unsaved changes were made the user is alerted
	 * @return - True if save is successful, false if unsucessful
	 */
	public boolean onExitAttempt() { // Returns true if event needs to be consumed.
		for (AuthorBookTableEntry abte : authorTable.getItems()) {
			String s = abte.getRoyaltyTextField().getText();
			int newRoyalty = Math.round(Float.parseFloat(s.replace("%", "")) * 1000);
			if (newRoyalty != abte.getRoyalty()) {
				unsavedChanges = true;
			}
		}
		if (unsavedChanges) {
			Alert saveAlert = new Alert(Alert.AlertType.CONFIRMATION, null, ButtonType.YES, ButtonType.NO,
					ButtonType.CANCEL);
			saveAlert.setTitle("Unsaved Changes!");
			saveAlert.setHeaderText("You have unsaved changes, would you like to save them?");
			Optional<ButtonType> selection = saveAlert.showAndWait();
			switch (selection.get().getButtonData().toString()) {
			case "CANCEL_CLOSE":
				return true;
			case "NO":
				logger.info("Quitting Application");
				return false;
			case "YES":
				if (saveBookChanges()) {
					saveAuthorChanges();
				}
				logger.info("Quitting Application");
				return false;
			default:
				logger.info("Quitting Application");
				return false;
			}
		} else {
			logger.info("Quitting Application");
			return false;
		}
	}

	/**
	 * setUp
	 * sets up display field values, and adds listeners to fields to alert controller to unsaved changes
	 */
	public void setUp() {
		publisherComboBox.getItems().clear();
		if (isBlankDetailView) {
			auditTrailButton.setDisable(true);
		}
		titleField.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue o, String str1, String str2) {
				unsavedChanges = true;
			}
		});
		yearPublishedField.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue o, String str1, String str2) {
				unsavedChanges = true;
			}
		});
		ISBNField.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue o, String str1, String str2) {
				unsavedChanges = true;
			}
		});
		summaryField.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue o, String str1, String str2) {
				unsavedChanges = true;
			}
		});
		publisherComboBox.valueProperty().addListener(new ChangeListener<Publisher>() {
			@Override
			public void changed(ObservableValue o, Publisher p1, Publisher p2) {
				unsavedChanges = true;
			}
		});
		ArrayList<Publisher> publishers = ptg.fetchPublishers();
		for (int i = 0; i < publishers.size(); i++) { // Not sure why any of this was necessary but it kept making duplicates without it
			if (!publisherComboBox.getItems().contains((publishers.get(i)))) {
				publisherComboBox.getItems().add((Publisher) publishers.get(i));
			}
		}
		updateAuthorList();

		titleField.setText(book.getTitle());
		if (book.getDateAdded() != null) {
			yearPublishedField.setText(String.valueOf(book.getYearPublished()));
		}
		ISBNField.setText(book.getIsbn());
		summaryField.setText(book.getSummary());
		if (book.getDateAdded() != null) {
			dateAddedSelector.setValue(book.getDateAdded().toLocalDate());
		}
		dateAddedSelector.setDisable(true);
		dateAddedSelector.setStyle("-fx-opacity: 1");
		dateAddedSelector.getEditor().setStyle("-fx-opacity: 1");
		publisherComboBox.getSelectionModel().select(book.getPublisher());
		unsavedChanges = false;
	}

	/**
	 * Constructor
	 * Sets appropriate values within this object
	 * @param book - Book to display details of
	 * @param btg - BookTableGateway to be used
	 * @param ptg - PublisherTableGateway to be used
	 * @param displayBlankView - Determines whether to display a blank BookDetailView or not
	 */
	public BookDetailController(Book book, BookTableGateway btg, PublisherTableGateway ptg, Boolean displayBlankView) {
		logger.info("BookDetailController created");
		this.btg = btg;
		this.ptg = ptg;
		this.book = book;
		this.isBlankDetailView = displayBlankView;
	}

	/**
	 * viewAuditTrail
	 * Changes current view to AuditTrailView.fxml
	 * @param event - The event that called viewAuditTrail
	 */
	@FXML
	void viewAuditTrail(ActionEvent event) {
		SingletonSwitcher.getInstance().ChangeView("AuditTrailView", book);
	}

	/**
	 * addExistingAuthor
	 * Opens AddAuthorView.fxml with instructions to add an existing author
	 */
	@FXML
	void addExistingAuthor() {
		SingletonSwitcher.getInstance().openAddAuthorView(false, this);
	}

	/**
	 * updateAuthorList
	 * Updates the list of Authors attached to this Book that is being displayed
	 */
	public void updateAuthorList() {
		ObservableList<AuthorBook> aList = FXCollections.observableArrayList();
		authorTable.getItems().clear();
		authorTable.getColumns().clear();
		aList = SingletonSwitcher.getInstance().getBTGate().getAuthorsForBook(book);
		ObservableList<AuthorBookTableEntry> abteList = FXCollections.observableArrayList();
		abteList = AuthorBookTableEntry.collectionToABTE(aList);
		authorTable.getItems().addAll(abteList);
		TableColumn<AuthorBookTableEntry, String> author = new TableColumn<>();
		author.setText("Author");
		TableColumn royalty = new TableColumn<>();
		royalty.setText("Royalty");
		author.setCellValueFactory(a -> new SimpleStringProperty(a.getValue().getAuthor().toString()));
		royalty.setCellValueFactory(new PropertyValueFactory<AuthorBookTableEntry, String>("royaltyTextField"));
		authorTable.getColumns().addAll(author, royalty);
		author.setPrefWidth(300);
		royalty.setPrefWidth(120);
	}

	/**
	 * getBook
	 * @return - The Book OBJ this controller is based on
	 */
	public Book getBook() {
		return book;
	}

	/**
	 * getAuthorTable
	 * @return - The TableView of AuthorBookTableEntries currently being displayed
	 */
	public TableView<AuthorBookTableEntry> getAuthorTable() {
		return authorTable;
	}
}
