package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import objects.Customer;

import java.io.IOException;

public class modCustomerController {
    public TextField idTextBox;
    public TextField customerNameBox;
    public TextField addressTextBox;
    public TextField address2TextBox;
    public TextField postCodeTextBox;
    public TextField phoneTextBox;

    public ComboBox countryCombo;
    public ComboBox firstLevelCombo;

    public Button clearFormButton;
    public Button saveButton;
    public Button cancelButton;

    public Label postCodeLabel;
    public Label firstLevelLabel;
    public Label countryLabel;
    public Label phoneLabel;

    public void receiveCustomer(Customer selectedItem) {
        idTextBox.setText(String.valueOf(selectedItem.getId()));
        customerNameBox.setText(String.valueOf(selectedItem.getName()));

        if (selectedItem.getAddress().contains("|") ){

            String[] addressSplit = selectedItem.getAddress().split("\\|");

            addressTextBox.setText(String.valueOf(addressSplit[0]));
            address2TextBox.setText(String.valueOf(addressSplit[1]));
        } else {
            addressTextBox.setText(selectedItem.getAddress());
        }
        postCodeTextBox.setText(String.valueOf(selectedItem.getPostCode()));
        phoneTextBox.setText(String.valueOf(selectedItem.getphone()));


    }

    /**
     * Returns to the main form without saving any of the user input.
     */
    public void cancelButtonClick(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(addCustomer.class.getResource("/views/homeScreen.fxml"));
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setTitle("DFC - Scheduler");
        stage.setScene(scene);
        stage.show();
    }

    public void countryComboChange(ActionEvent actionEvent) {
        System.out.println("Country combo change fired.");
    }

    public void clearFormButtonClick(ActionEvent actionEvent) {
    }

    public void saveButtonClick(ActionEvent actionEvent) throws IOException {




        Parent root = FXMLLoader.load(addCustomer.class.getResource("/views/homeScreen.fxml"));
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setTitle("DFC - Scheduler");
        stage.setScene(scene);
        stage.show();
    }

}
