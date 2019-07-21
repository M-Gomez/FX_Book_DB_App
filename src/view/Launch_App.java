package view;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.AuditTableGateway;
import model.BookTableGateway;
import model.JSONReader;
import model.PublisherTableGateway;
import model.SingletonSwitcher;
import org.apache.logging.log4j.Logger;
import controller.MenuController;
import org.apache.logging.log4j.LogManager;

/**
 * Launch_App
 * Acts as the main executable for the application
 * Does a large amount of the setup/initialization for other parts of the program
 * Displays the initial application state
 * @author Manuel Gomez IV
 */
public class Launch_App extends Application {
	private static Logger logger = LogManager.getLogger(Launch_App.class);

	/**
	 * main
	 * Calls launch, this is a very very complex main method, the intricacies of which elude even its creator
	 */
	public static void main(String[] args) {
		launch(args);
	}

	/**
	 * start Does a large amount of the setup/initialization for other parts of the program
	 * Displays the initial application state
	 * Adds listener for quitting the application
	 */
	@Override
	public void start(Stage stage) throws Exception {
		Platform.setImplicitExit(false);
		JSONReader.start();
		BookTableGateway btGate = new BookTableGateway();
		PublisherTableGateway ptGate = new PublisherTableGateway();
		AuditTableGateway atGate = new AuditTableGateway();
		btGate.setPTGate(ptGate);
		btGate.setATGate(atGate);
		SingletonSwitcher.getInstance().setBTGate(btGate);
		SingletonSwitcher.getInstance().setPTGate(ptGate);
		SingletonSwitcher.getInstance().setATGate(atGate);
		MenuController menuController = new MenuController();
		FXMLLoader loader = new FXMLLoader(getClass().getResource("menu.fxml"));
		loader.setController(menuController);
		Parent root = loader.load();
		Scene scene = new Scene(root, 820, 560);
		SingletonSwitcher.getInstance().setStage(stage);
		SingletonSwitcher.getInstance().setPane(menuController.getContentPane());
		stage.setTitle("Menu");
		stage.setScene(scene);
		stage.setOnHiding(event -> {
			logger.info("Quitting Application");
			System.exit(0);
		});
		stage.show();
		logger.info("Stage succesfully shown!");
	}
}
