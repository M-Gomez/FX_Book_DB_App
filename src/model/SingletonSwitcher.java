package model;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.mysql.cj.jdbc.MysqlDataSource;
import controller.AddAuthorController;
import controller.AuditTrailController;
import controller.BookDetailController;
import controller.BookListController;
import controller.GeneratePublisherSheetController;
import controller.MenuController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;


/**
 * SingletonSwitcher
 * Acts as a means of easily switching between views.
 * Also stores code for establishing connections to DB for easy access in gateways in order to remove redundancies
 * Also contains refs. to Author/Book/Publisher TableGateways
 * @author Manuel Gomez IV
 */
public class SingletonSwitcher {

	private static Logger logger = LogManager.getLogger(MenuController.class);
	private static SingletonSwitcher sSwitcher;
	private static Stage stage;
	private BookTableGateway btGate;
	private PublisherTableGateway ptGate;
	private AuditTableGateway atGate;
	private BorderPane contentPane;

	/**
	 * Returns the only instance of this SingletonSwitcher, or creates it if it does not already exist
	 */
	public static SingletonSwitcher getInstance() {
		if (sSwitcher == null) {
			sSwitcher = new SingletonSwitcher();
		}
		return sSwitcher;
	}
	
	/**
	 * openAddAuthorView
	 * Opens AddAuthorView in a new, smaller window
	 * @param isNewAuthor - Boolean relaying whether this is an author that doesn't exist yet in the DB
	 * @param bdc - The BookDetailController that acts as a parent to this view
	 */
	public void openAddAuthorView(boolean isNewAuthor, BookDetailController bdc) {
		FXMLLoader loader;
		Parent newPane;
		Stage addWindow = new Stage();
		AddAuthorController addController = new AddAuthorController(isNewAuthor, bdc);
		loader = new FXMLLoader(getClass().getResource("../view/addauthorview.fxml"));
		loader.setController(addController);
		try {
			newPane = loader.load();
			Scene scene = new Scene(newPane, 600, 400);
			addWindow.setScene(scene);
		} catch (IOException e) {
			e.printStackTrace();
		}
		addWindow.initOwner(stage);
		addWindow.initModality(Modality.APPLICATION_MODAL);
		if (isNewAuthor) {
			addController.removeAuthorBox("Existing");
		} else {
			addController.removeAuthorBox("New");
		}
		addWindow.showAndWait();
	}

	/**
	 * establishDBConnection
	 * @return - a Connection to the MySQL DB
	 */
	public Connection establishDBConnection() {
		MysqlDataSource ds = new MysqlDataSource();
		ds.setURL(JSONReader.getDb_address());
		ds.setUser(JSONReader.getUsername());
		ds.setPassword(JSONReader.getPassword());
		try {
			return ds.getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * ChangeView
	 * Changes views, the new view depends on the viewName
	 * @param viewName - The name associated with the view to be swapped to
	 */
	public void ChangeView(String viewName) {
		try {
			FXMLLoader loader;
			BorderPane newPane;
			switch (viewName) {
			case "BookList":
				ObservableList<Book> bookList = FXCollections.observableArrayList();
				bookList.addAll(btGate.getBooks(0, null));

				BookListController blController = new BookListController(bookList);
				loader = new FXMLLoader(getClass().getResource("../view/booklist.fxml"));
				loader.setController(blController);
				newPane = loader.load();
				contentPane.setCenter(newPane);
				blController.setUpGui();
				stage.setOnHiding(event -> {
					logger.info("Quitting Application");
				});
				break;
			case "SheetGeneratorView":
				GeneratePublisherSheetController excelController = new GeneratePublisherSheetController();
				loader = new FXMLLoader(getClass().getResource("../view/generatepublishersheetview.fxml"));
				loader.setController(excelController);
				newPane = loader.load();
				contentPane.setCenter(newPane);
				excelController.setUpGui();
				break;
			}
		} catch (IOException e) {
			logger.error(e.getStackTrace());
		}
	}

	/**
	 * 
	/**
	 * ChangeView
	 * Changes views, the new view depends on the viewName, and a book that will be associated with the new view
	 * @param viewName - The name associated with the view to be swapped to
	 * @param book - The book to associate the new view with
	 */
	public void ChangeView(String viewName, Book book) {
		try {
			FXMLLoader loader;
			BorderPane newPane;
			BookDetailController bdc;
			AuditTrailController atc;
			switch (viewName) {
			case "BookDetailView":
				bdc = new BookDetailController(book, btGate, ptGate, false);
				loader = new FXMLLoader(getClass().getResource("../view/bookdetailview.fxml"));
				loader.setController(bdc);
				newPane = loader.load();
				contentPane.setCenter(newPane);
				bdc.setUp();
				stage.setOnCloseRequest(event -> {
					if (bdc.onExitAttempt()) {
						event.consume();
					}
					;
				});
				break;
			case "AddBookView":
				bdc = new BookDetailController(book, btGate, ptGate, true);
				loader = new FXMLLoader(getClass().getResource("../view/bookdetailview.fxml"));
				loader.setController(bdc);
				newPane = loader.load();
				contentPane.setCenter(newPane);
				bdc.setUp();
				stage.setOnCloseRequest(event -> {
					if (bdc.onExitAttempt()) {
						event.consume();
					};
				});
				break;
			case "AuditTrailView":
				atc = new AuditTrailController(book, atGate);
				loader = new FXMLLoader(getClass().getResource("../view/AuditTrailView.fxml"));
				loader.setController(atc);
				newPane = loader.load();
				contentPane.setCenter(newPane);
				atc.setUpGUI();
				stage.setOnHiding(event -> {
					logger.info("Quitting Application");
				});
				break;
			}
		} catch (IOException e) {
			logger.error(e.getStackTrace());
		}
	}

	public void setBTGate(BookTableGateway btGate) {
		this.btGate = btGate;
	}

	public void setPane(BorderPane root) {
		this.contentPane = root;
	}

	public void setATGate(AuditTableGateway atGate) {
		this.atGate = atGate;
	}

	public AuditTableGateway getATGate() {
		return atGate;
	}

	public Stage getStage() {
		return stage;
	}

	public void setStage(Stage stage) {
		SingletonSwitcher.stage = stage;
	}

	public BookTableGateway getBTGate() {
		return btGate;
	}

	public void setPTGate(PublisherTableGateway ptGate) {
		this.ptGate = ptGate;
	}

	public PublisherTableGateway getPTGate() {
		return ptGate;
	}

}
