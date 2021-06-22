package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import utilities.DBConnect;
import utilities.DBQuery;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class addCustomer implements Initializable {
    public TextField idTextBox;
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


    /***/
    @Override
    public void initialize(URL U, ResourceBundle resourceBundle) {
        firstLevelLabel.setText("State");

        //countryCombo.setPromptText("Country prompt");

        ObservableList<String> countryList = FXCollections.observableArrayList();

        try {
            //Get all the countries from the DB and add them to the result set
            String sql = "SELECT * FROM countries";
            DBQuery.setPreparedStatement(DBConnect.getConnection(), sql); //Creating the prepared statement object
            PreparedStatement ps = DBQuery.getPreparedStatement(); //referencing the prepared statement
            ps.execute();
            ResultSet allCountries = ps.getResultSet();
            while (allCountries.next()) {
                String temp = allCountries.getString("Country");
                //System.out.println(temp);
                countryList.add(temp);
            }
            //Iterate through the result set and add the items to the observable list


            //Create and observable list of all the countries from the DB
            // ObservableList<String> countryList = DBQuery.setPreparedStatement(DBConnect.getConnection(),sql);//from the DB


            countryCombo.setItems(countryList);


        } catch (Exception e) {
            System.out.println("There was a problem here.");

        }


    }

    public void cancelButtonClick(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(addCustomer.class.getResource("/views/homeScreen.fxml"));
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setTitle("DFC - Scheduler");
        stage.setScene(scene);
        stage.show();
    }
}
