package controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import model.*;


/**
 * BookListController
 * The Controller component for the Book List View, it handles input, and changes data/fields in booklist.fxml
 * @author Manuel Gomez IV
 */
public class BookListController {

	private static Logger logger = LogManager.getLogger(BookListController.class);
	private static ObservableList<Book> bookList;
    private static String searchString;
    private static int currentPage, totalRecords;
	@FXML
	private ListView<Book> listView;
    @FXML
    private Button first, prev, next, last, deleteButton;
    @FXML
    private TextField searchBar;
    @FXML
    private Label fetchedRecordLabel;

    /**
     * onSearch is used when a search term is entered and the enter key or search button are activated.
     * It updates Button states, text fields, and repopulates the book list based on the returned records.
     * @param event The event that triggered the onSearch method call.
     */
    @FXML
    void onSearch(ActionEvent event) {
    	searchString = searchBar.getText().trim();
		currentPage = 0;
		totalRecords = SingletonSwitcher.getInstance().getBTGate().getTotalBookCount(searchString);
		first.setDisable(true);
		prev.setDisable(true);
		if(totalRecords > (currentPage+1)*50){
		next.setDisable(false);
		last.setDisable(false);
		}else {
    	next.setDisable(true);
		last.setDisable(true);
		}
    	setFetchedRecordsLabel(currentPage);
    	bookList.clear();
		listView.getItems().clear();
		bookList = SingletonSwitcher.getInstance().getBTGate().getBooks(currentPage, searchString);
		listView.getItems().addAll(bookList);
		logger.info("Book List Refreshed");
    }
    
    /**
     * Constructor - Creates BookListController object and sets the static booklist field.
     * @param bookList An ObservableList of Book objects to be displayed
     */
	public BookListController(ObservableList<Book> bookList) {
		BookListController.bookList = bookList;
	}
	
	/**
	 * setUpGui
	 * Sets up initial values for label and button states. Calls addItems populate listview.
	 */
	public void setUpGui() {
		addItems();
		currentPage = 0;
		setFetchedRecordsLabel(currentPage);
		first.setDisable(true);
		prev.setDisable(true);
	}
	
	/**
	 * addItems
	 * Adds the book objects to the ListView, and sets listeners on them so that they can display the book detail view when 2x clicked.
	 */
	public void addItems() {
		for(int i = 0; i < bookList.size();i++) {
		listView.getItems().add(bookList.get(i));	
		}
		listView.setOnMouseClicked(new EventHandler<MouseEvent>() {
		    @Override
		    public void handle(MouseEvent event) {
		        if(event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
		        	logger.info("Double click detected, now displaying Book_Detail_view.");
		        	SingletonSwitcher.getInstance().ChangeView("BookDetailView", listView.getSelectionModel().getSelectedItem());
		         }    
		    }
		});
	}

	/**
	 * deleteBook
	 * Used to not only remove a book but to remove it from the database.
	 * Gives warning when removing a book that still has author entries as this is intentionally disallowed by the schema.
	 * Gives warning when attempting to delete a null book or no book.
	 * @param event - Event that triggered the deleteBook call
	 */
    @FXML
    void deleteBook(ActionEvent event) {
		Book book  = listView.getSelectionModel().getSelectedItem();
		if(book.getId() != 0) {
		if(SingletonSwitcher.getInstance().getBTGate().deleteMethod(listView.getSelectionModel().getSelectedItem())) {
		listView.getItems().remove(book);
		logger.info("Book ID: " + book.getId() + " has been deleted!");
		}else {
			Alert alert = new Alert(Alert.AlertType.ERROR, "Book still has existing authors, please delete its author entries to delete this book!", ButtonType.CLOSE);
    		alert.showAndWait();
		}
		}else {
			Alert alert = new Alert(Alert.AlertType.ERROR, "No book is selected!", ButtonType.CLOSE);
    		alert.showAndWait();
		}
	}
    
    /**
     * setFetchedRecordLabel
     * Sets the label displaying how many records were returned and which slice of these records are currently displayed.
     * @param pageNum The zero indexed current page of results being displayed in the listview.
     */
	public void setFetchedRecordsLabel(int pageNum) {
		totalRecords = SingletonSwitcher.getInstance().getBTGate().getTotalBookCount(searchString);
		if((currentPage+1)*50 < totalRecords) {
			fetchedRecordLabel.setText("Fetched records " + ((pageNum*50)+ 1) + " to " + ((pageNum+1)*50) + " of " + totalRecords);
		}else {
			fetchedRecordLabel.setText("Fetched records " + ((pageNum*50)+ 1) + " to " + totalRecords + " of " + totalRecords);
		}
		if(totalRecords == 0) {
			fetchedRecordLabel.setText("0 Records found");
		}
	}
	
	/**
	 * repopulateList
	 *  Used to repopulate the ListView, update button states, and update text labels.
	 * @param event The event that triggered the repopulateList call, used to determine behavior based on the source of the event.
	 */
    @FXML
    void repopulateList(ActionEvent event) {
		totalRecords = SingletonSwitcher.getInstance().getBTGate().getTotalBookCount(searchString);
    	switch(event.getSource().toString()) {
    	case"Button[id=first, styleClass=button]'First'":
    		currentPage = 0;
    		first.setDisable(true);
    		prev.setDisable(true);
    		if(totalRecords > (currentPage+1)*50){
    		next.setDisable(false);
    		last.setDisable(false);
    		}else {
        	next.setDisable(true);
    		last.setDisable(true);
    		}
    		break;
    	case"Button[id=next, styleClass=button]'Next'":
    		currentPage++;
    		prev.setDisable(false);
    		first.setDisable(false);
    		if(totalRecords > (currentPage+1)*50){
    		next.setDisable(false);
    		last.setDisable(false);
    		}else {
    		next.setDisable(true);
    		last.setDisable(true);
    		}
    		break;
    	case"Button[id=last, styleClass=button]'Last'":
    		currentPage = (int)Math.ceil((float)totalRecords/50)-1;
        	next.setDisable(true);
    		last.setDisable(true);
    		prev.setDisable(false);
    		first.setDisable(false);
    		break;
    	case"Button[id=prev, styleClass=button]'Prev'":
    		currentPage--;
    		last.setDisable(false);
    		next.setDisable(false);
    		if(currentPage == 0){
    		first.setDisable(true);
    		prev.setDisable(true);
    		}else {
        	next.setDisable(false);
    		last.setDisable(false);
    		}
    		break;
    	}
    	setFetchedRecordsLabel(currentPage);
    	bookList.clear();
		listView.getItems().clear();
		bookList = SingletonSwitcher.getInstance().getBTGate().getBooks(currentPage, searchString);
		listView.getItems().addAll(bookList);
		logger.info("Book List Refreshed");
    }
}
