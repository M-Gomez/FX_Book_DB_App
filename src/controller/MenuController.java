package controller;

import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import model.Book;
import model.SingletonSwitcher;

/**
 * MenuController The controller component for the Menu Bar displayed over the main window
 * @author Manuel Gomez IV
 */
public class MenuController {

	private static Logger logger = LogManager.getLogger(MenuController.class);
	@FXML
	private MenuItem quitExecution, bookListButton, addBookButton, openSheetViewButton;
	@FXML
	private BorderPane contentPane;

	/**
	 * viewBookList Displays the booklist.fxml view
	 * @param event - The event that called viewBookList
	 */
	@FXML
	void viewBookList(ActionEvent event) {
		logger.info("Opening Booklist View");
		SingletonSwitcher.getInstance().ChangeView("BookList");
	}

	/**
	 * viewBookDetail Displays the bookdetailview.fxml view with a blank book object
	 * @param event - The event that called viewBookDetail
	 */
	@FXML
	void viewBookDetail(ActionEvent event) {
		logger.info("Opening Book Detail View");
		SingletonSwitcher.getInstance().ChangeView("BookDetailView", new Book());

	}

	/**
	 * viewAddBook Displays a modified BookDetailView that is used to create new Book entries
	 * @param event - The event that called viewAddBook
	 */
	@FXML
	void viewAddBook(ActionEvent event) {
		logger.info("Opening Add Book View");
		SingletonSwitcher.getInstance().ChangeView("AddBookView", new Book());

	}

	/**
	 * viewSheetView
	 * Displays generatePublisherSheetView.fxml
	 * @param event - The event that called viewSheetView
	 */
	@FXML
	void viewSheetView(ActionEvent event) {
		SingletonSwitcher.getInstance().ChangeView("SheetGeneratorView");
	}

	/**
	 * quitExecution
	 * quits execution of the program, closes DB connections, and closes UI
	 * @param event - The event that called quitExecution
	 */
	@FXML
	void quitExecution(ActionEvent event) {
		SingletonSwitcher.getInstance().getStage().close();
		try {
			SingletonSwitcher.getInstance().getBTGate().conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.exit(0);
	}

	/**
	 * getContentPane
	 * @return - The content pane to display the main content of the application in
	 */
	public BorderPane getContentPane() {
		return contentPane;
	}
}
