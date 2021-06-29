package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import utilities.returnHome;

import java.io.IOException;

public class addAppointment {
    public TextField idTextBox;
    public TextField titleTextBox;
    public TextField descriptionTextBox;
    public TextField locationTextBox;
    public HBox datePick;
    public Button clearFormButton;
    public Button saveButton;
    public Button cancelButton;
    public Label tzLabel;

    public void clearFormButtonClick(ActionEvent actionEvent) {
    }

    public void saveButtonClick(ActionEvent actionEvent) throws IOException {


        returnHome.loadHome(actionEvent);
    }

    /**
     * Returns to the main form without saving any of the user input.
     */
    public void cancelButtonClick(ActionEvent actionEvent) throws IOException {
        returnHome.loadHome(actionEvent);
    }
}
