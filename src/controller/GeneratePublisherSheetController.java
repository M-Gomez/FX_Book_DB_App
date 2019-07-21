package controller;

import java.io.File;

import ExcelGenerator.ExcelSheetGenerator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import model.Publisher;
import model.SingletonSwitcher;

/**
 * GeneratePublisherSheetController
 * @author Manuel Gomez IV
 * The Controller associated with GeneratePublisherSheetView.fxml
 */
public class GeneratePublisherSheetController {

    @FXML
    private ComboBox<Publisher> publisherCombobox;
    @FXML
    private TextField directoryTextField;
    private File saveDir;

    /**
     * openDirChooser
     * Opens the FileChooser that selects where the .csv file will be saved
     * @param event - The event that called openDirChooser
     */
    @FXML
    void openDirChooser(ActionEvent event) {
    	FileChooser saveChooser = new FileChooser();
    	saveChooser.setInitialFileName("PublisherReportSheet");
    	FileChooser.ExtensionFilter ef = new FileChooser.ExtensionFilter("Comma Separated Value Files (*.csv)","*.csv");
    	saveChooser.getExtensionFilters().add(ef);
    	saveChooser.setSelectedExtensionFilter(ef);
    	try {
    	saveDir = saveChooser.showSaveDialog(SingletonSwitcher.getInstance().getStage());
    	directoryTextField.setText(saveDir.getAbsolutePath());
    	}catch(Exception e) {
    	
    	}
    }
    
    /**
     * generatePublisherReport
     * Starts .csv generation in a seperate thread to prevent freezing for large data sets
     * @param event - The event that called generatePublisherReport
     */
    @FXML
    void generatePublisherReport(ActionEvent event) {
    	if(publisherCombobox.getSelectionModel().getSelectedItem() != null) {
    		if(saveDir == null) {
    		Alert alert = new Alert(Alert.AlertType.ERROR, "No directory selected, please choose a location to save the report.", ButtonType.CLOSE);
    		alert.showAndWait();    		
    		}else{
    		ExcelSheetGenerator sheetGen = new ExcelSheetGenerator(saveDir, publisherCombobox.getSelectionModel().getSelectedItem());
    		sheetGen.start();
    		}
    	}else {
    		Alert alert = new Alert(Alert.AlertType.ERROR, "No publisher selected, please choose a publisher to generate a report for.", ButtonType.CLOSE);
    		alert.showAndWait();   
    	}
    }

    /**
     * setUpGui
     * Sets up UI elements for GeneratePublisherSheetView.fxml
     */
	public void setUpGui() {
		publisherCombobox.getItems().addAll(SingletonSwitcher.getInstance().getPTGate().fetchPublishers());
		publisherCombobox.getItems().remove(new Publisher());
	}
}
