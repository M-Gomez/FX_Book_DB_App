package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import model.Author;
import model.AuthorBook;
import model.SingletonSwitcher;

/**
 * AddAuthorController
 * The control component for addauthorview.fxml
 * @author Manuel Gomez IV
 */
public class AddAuthorController {
	private boolean newAuthor; // True if the author being added is new, and does not exist in the database.
	private BookDetailController bdcParent;
	@FXML
	private BorderPane newWindowContentPane;
	@FXML
	private HBox existingAuthorBox;
	@FXML
	private ComboBox<Author> existingAuthorCombobox;
	@FXML
	private TextField exisitingRoyaltyField, firstName, lastName, website, newAuthorRoyalty;
	@FXML
	private VBox newAuthorBox;
	@FXML
	private DatePicker dob;
	@FXML
	private ComboBox<String> gender;

	/**
	 * Constructor Sets the newAuthor and bdcParent fields
	 * @param newAuthor - boolean conveying whether the author exists as an author in the database or is a new Author
	 * @param bdcParent - The parent BDC component that created this AddAuthorController
	 */
	public AddAuthorController(boolean newAuthor, BookDetailController bdcParent) {
		this.newAuthor = newAuthor;
		this.bdcParent = bdcParent;
	}

	/**
	 * getContentPane
	 * @return - The contentPane FXML field so that it can be used outside this class
	 */
	public BorderPane getContentPane() {
		return newWindowContentPane;
	}

	/**
	 * initialize Sets starting values/states for the fields/input in the view.
	 */
	public void initialize() {
		ObservableList<Author> entriesToRemove = FXCollections.observableArrayList();
		gender.getItems().addAll("Male", "Female");
		if (!newAuthor) {
			existingAuthorCombobox.getItems().addAll(SingletonSwitcher.getInstance().getBTGate().getAuthors());
			for (Author auth : existingAuthorCombobox.getItems()) {
				for (AuthorBook tableEntry : bdcParent.getAuthorTable().getItems()) {
					if (tableEntry.getAuthor().getId() == (auth.getId())) {
						entriesToRemove.add(auth);
					}
				}
			}
			existingAuthorCombobox.getItems().removeAll(entriesToRemove);
		}
	}

	/**
	 * removeAuthorBox
	 * Removes unnecessary/unwanted input fields based on whether a new author is being created and added, or an existing author
	 * @param string - String determining whether a new author is being created and added, or an existing author
	 */
	public void removeAuthorBox(String string) {
		switch (string) {
		case "New":
			newAuthorBox.setVisible(false);
			break;
		case "Existing":
			existingAuthorBox.setVisible(false);
			break;
		}
	}

	/**
	 * saveAuthor Saves author data to the book if valid, if invalid alerts are sent.
	 * @param event - The event that called saveAuthor
	 */
	@FXML
	void saveAuthor(ActionEvent event) {
		try {
			Author author;
			if (bdcParent.getBook().getId() != 0) {
				if (newAuthor) {
					if (firstName.getText() != "" && lastName.getText() != "" && gender.getValue() != null
							&& dob.getValue() != null) {
						author = new Author(0, firstName.getText(), lastName.getText(), dob.getValue(),
								gender.getValue(), website.getText());
						SingletonSwitcher.getInstance().getBTGate().addAuthor(author);
						SingletonSwitcher.getInstance().getBTGate().addAuthorToBook(author, bdcParent.getBook(),
								Float.parseFloat(newAuthorRoyalty.getText()));
						SingletonSwitcher.getInstance().getATGate().insertAuditTrailEntry(bdcParent.getBook(), author,
								true);
						bdcParent.updateAuthorList();
						bdcParent.saveAuthorChanges();
						firstName.setText("");
						lastName.setText("");
						gender.getSelectionModel().clearSelection();
						dob.getEditor().clear();
						newAuthorRoyalty.setText("");
					} else {
						Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid Values entered, save aborted!",
								ButtonType.CLOSE);
						alert.showAndWait();
					}
				} else {
					SingletonSwitcher.getInstance().getBTGate().addAuthorToBook(
							existingAuthorCombobox.getSelectionModel().getSelectedItem(), bdcParent.getBook(),
							Float.parseFloat(exisitingRoyaltyField.getText()));
					bdcParent.updateAuthorList();
					SingletonSwitcher.getInstance().getATGate().insertAuditTrailEntry(bdcParent.getBook(),
							(existingAuthorCombobox.getSelectionModel().getSelectedItem()), true);
					existingAuthorCombobox.getItems()
							.remove(existingAuthorCombobox.getSelectionModel().getSelectedItem());
					existingAuthorCombobox.getSelectionModel().clearSelection();
					bdcParent.saveAuthorChanges();
				}
			} else {
				Alert alert = new Alert(Alert.AlertType.ERROR,
						"Book has not yet been initialized, please save book to db before adding authors. Save has been aborted!",
						ButtonType.CLOSE);
				alert.showAndWait();
			}
		} catch (Exception e) {
			Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid Values entered, save aborted!", ButtonType.CLOSE);
			alert.showAndWait();
		}
	}
}