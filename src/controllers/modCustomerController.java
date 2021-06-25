package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import utilities.DBConnect;
import utilities.DBQuery;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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


        //Sets the first level label to state by default
        firstLevelLabel.setText("State");


        //Set up an observable list to display the country data and first level data for combo boxes
        ObservableList<String> countryList = FXCollections.observableArrayList();


        try {
            //Get all the countries from the DB and add them to the result set
            String sql = "SELECT * FROM countries";
            DBQuery.setPreparedStatement(DBConnect.getConnection(), sql); //Creating the prepared statement object
            PreparedStatement ps = DBQuery.getPreparedStatement(); //referencing the prepared statement
            ps.execute(); //Runs the sql query
            ResultSet allCountries = ps.getResultSet(); //Setting the results of the query to a result set

            //Iterate through the result set and add the items to the observable list
            while (allCountries.next()) {
                String temp = allCountries.getString("Country");
                countryList.add(temp);
            }


            //Sets the items to display in the country combo box
            countryCombo.setItems(countryList);




        } catch (Exception e) {
            System.out.println("There was a problem here.");

        }




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

    /**
     * Changes the label for the first level division based on the user selection in the country combo box
     * and then populates the first level division combo box.
     */
    public void countryComboChange(ActionEvent actionEvent) throws SQLException {

        //Activates the first level combo and label when a country is selected.
        firstLevelLabel.setDisable(false);
        firstLevelCombo.setDisable(false);

        //Put the selected string form the combo into a variable
        String countrySelected = (String) countryCombo.getSelectionModel().getSelectedItem();

        //If-else block to set the label based on the country selected.
        if (countrySelected.equals("U.S")) {
            //set to state for US
            firstLevelLabel.setText("State");
        } else if (countrySelected.equals("Canada")) {
            //set to province for Canada
            firstLevelLabel.setText("Province");
        } else {
            //set to region for all others
            firstLevelLabel.setText("Region");
        }


        //Populate the first level division combo box based on the country selected.

        //Create a query based on the string variable to get the country ID
        String sql = "SELECT Country_ID FROM countries WHERE Country = ?";
        DBQuery.setPreparedStatement(DBConnect.getConnection(), sql); //Creating the prepared statement object
        PreparedStatement ps = DBQuery.getPreparedStatement(); //referencing the prepared statement
        ps.setString(1, countrySelected);
        ps.executeQuery(); //Runs the sql query
        ResultSet countryID = ps.getResultSet(); //Setting the results of the query to a result set
        countryID.next(); //Moves to the first result in the result set

        //Assigns the country ID to an integer variable
        int id = countryID.getInt("Country_ID");

        //Creates an observable list to display the selected divisions in the combo box
        ObservableList<String> firstLevelList = FXCollections.observableArrayList();


        //Create another query to pull the first level divisions from the DB where the country ID matches
        String sql1 = "SELECT * FROM first_level_divisions WHERE COUNTRY_ID = ?";
        DBQuery.setPreparedStatement(DBConnect.getConnection(), sql1); //Creating the prepared statement object
        PreparedStatement ps1 = DBQuery.getPreparedStatement(); //referencing the prepared statement
        ps1.setString(1, String.valueOf(id)); //Getting the string representation of the id
        ps1.executeQuery(); //Runs the sql query
        ResultSet firstLevelDivs = ps1.getResultSet(); //Setting the results of the query to a result set

        //iterates through the result set and adds the items to the observable list
        while (firstLevelDivs.next()) {
            String temp = firstLevelDivs.getString("Division");
            firstLevelList.add(temp);
        }
        //Sets the combo box to display the items in the observable list
        firstLevelCombo.setItems(firstLevelList);



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
