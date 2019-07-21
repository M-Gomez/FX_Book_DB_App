package controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import model.AuditTableGateway;
import model.AuditTrailEntry;
import model.Book;
import model.SingletonSwitcher;

/**
 * AuditTrailController
 * The controller component for the AuditTrailView
 * @author Manuel Gomez IV
 */
public class AuditTrailController {
	
	private Book book;
	private static Logger logger = LogManager.getLogger(AuditTrailController.class);
    @FXML
    private Label audit_trail_title;
    @FXML
    private ListView<AuditTrailEntry> auditTrailList;

    /**
     * back
     * Swaps views in the main stage to the BookDetailView for this book
     * @param event - The event that called back
     */
    @FXML
    void back(ActionEvent event) {
    	SingletonSwitcher.getInstance().ChangeView("BookDetailView", book);
    }
    
    /**
     * Constructor
     * Sets atg and book fields so that audit history can be displayed, and BookDetailView can be displayed if necessary
     * @param book - The book to view the audit history for
     * @param atg - The AuditTableGateway used to access the DB
     */
	public AuditTrailController(Book book, AuditTableGateway atg) {
		this.book = book;
	}
	
	/**
	 * setUpGUI
	 * Sets display values for the AuditTrailView
	 */
	public void setUpGUI() {
		auditTrailList.getItems().addAll(book.getAuditTrail());
		audit_trail_title.setText("Audit Trail for " + book.getTitle());
	}
}
