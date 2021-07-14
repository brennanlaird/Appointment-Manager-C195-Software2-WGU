package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import utilities.*;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;


/**
 * Class to control the adding of customers to the database.
 */
public class addCustomer implements Initializable {
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


    /**
     * Sets up the country combobox by pulling all the available countries from the database and setting them to display
     * in the observable list associated with the combobox.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
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


        } catch (SQLException e) {
            displayMessages.errorMsg("SQL Exception error encountered! " + e.getMessage());

        }


    }

    /**
     * Returns to the main form without saving any of the user input.
     */
    public void cancelButtonClick(ActionEvent actionEvent) throws IOException {
        returnHome.loadHome(actionEvent);
    }

    /**
     * Changes the label for the first level division based on the user selection in the country combo box
     * and then populates the first level division combo box.
     */
    public void countryComboChange(ActionEvent actionEvent) throws SQLException {

        //Activates the first level combo and label when a country is selected.
        firstLevelLabel.setDisable(false);
        firstLevelCombo.setDisable(false);

        //Put the selected string from the combo into a variable
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
        try {
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
        } catch (SQLException e) {
            displayMessages.errorMsg("SQL Exception error encountered! " + e.getMessage());
        }

    }

    /**
     * Clears all the user inputs on the form back to blank or their default values.
     */
    public void clearFormButtonClick(ActionEvent actionEvent) {
        //Clears the form
//Crashes because the combo box change is called and can't deal with a blank result.
        //countryCombo.getSelectionModel().clearSelection();
        //firstLevelCombo.getSelectionModel().clearSelection();

        customerNameBox.setText("");
        addressTextBox.setText("");
        address2TextBox.setText("");
        postCodeTextBox.setText("");
        phoneTextBox.setText("");

    }

    /**
     * Performs error checking and then saves the customer information to the database.
     */
    public void saveButtonClick(ActionEvent actionEvent) throws IOException {
        //Retrieve the values of all the text boxes.

        String customerName = customerNameBox.getText();
        String customerAddress = addressTextBox.getText() + "|" + address2TextBox.getText();
        String customerPhone = phoneTextBox.getText();
        String customerPostCode = postCodeTextBox.getText();

        //Variables for entry validation.
        boolean entryError = false;
        String badEntryMsg = "";


        //Create an error message and flag for a blank customer field.
        if (customerName.equals("")) {
            badEntryMsg = badEntryMsg + "Customer name cannot be blank.";
            entryError = true;
        }


        //Create an error message and flag for a blank address field.
        // This only checks the first field as the second is optional.
        if (addressTextBox.getText().equals("")) {
            if (entryError) {
                badEntryMsg = badEntryMsg + "\n";
            }


            badEntryMsg = badEntryMsg + "Customer address cannot be blank.";
            entryError = true;
        }

        //Create an error message and flag for a blank phone field.

        if (customerPhone.equals("")) {
            if (entryError) {
                badEntryMsg = badEntryMsg + "\n";
            }
            badEntryMsg = badEntryMsg + "Customer phone number cannot be blank.";
            entryError = true;
        }

        //Create an error message and flag for a blank postal code field.
        if (customerPostCode.equals("")) {
            if (entryError) {
                badEntryMsg = badEntryMsg + "\n";
            }
            badEntryMsg = badEntryMsg + "Customer postal code cannot be blank.";
            entryError = true;
        }


        //Boolean to determine if the country combo is blank.
        boolean countryBlank = countryCombo.getSelectionModel().isEmpty();

        //Error and flag for an empty country combo box.
        if (countryBlank) {
            if (entryError) {
                badEntryMsg = badEntryMsg + "\n";
            }
            badEntryMsg = badEntryMsg + "Please select a country.";
            entryError = true;
        }


        //Boolean to determine if the first level div combo is blank.
        boolean fldBlank = firstLevelCombo.getSelectionModel().isEmpty();

        //Error and flag for an empty first level division combo box.
        if (fldBlank) {
            if (entryError) {
                badEntryMsg = badEntryMsg + "\n";
            }
            badEntryMsg = badEntryMsg + "Please select a state, province, or region.";
            entryError = true;
        }


        //Get the division ID of the selected first level division
        //Put the selected string from the combo into a variable
        String divSelected = (String) firstLevelCombo.getSelectionModel().getSelectedItem();


        //If no entry errors were found, proceed with trying to add info to the DB.
        if (!entryError) {


            try {
                //Create a query based on the string variable to get the country ID
                String sql = "SELECT Division_ID FROM first_level_divisions WHERE Division = ?";
                DBQuery.setPreparedStatement(DBConnect.getConnection(), sql); //Creating the prepared statement object
                PreparedStatement ps = DBQuery.getPreparedStatement(); //referencing the prepared statement
                ps.setString(1, divSelected);
                ps.executeQuery(); //Runs the sql query
                ResultSet divIDRS = ps.getResultSet(); //Setting the results of the query to a result set
                divIDRS.next(); //Moves to the first result in the result set

                //Assigns the country ID to an integer variable
                int divId = divIDRS.getInt("Division_ID");

                String currentUser = userInfo.saveUsername;

                String insertCustomerSQL = "INSERT INTO customers (Customer_Name, Address, Postal_Code, Phone, Created_By, Last_Updated_By, Division_ID) VALUES (?, ?, ?, ?, ?, ?, ?)";

                DBQuery.setPreparedStatement(DBConnect.getConnection(), insertCustomerSQL); //Creating the prepared statement object
                PreparedStatement prepState = DBQuery.getPreparedStatement(); //referencing the prepared statement

                prepState.setString(1, customerName);
                prepState.setString(2, customerAddress);
                prepState.setString(3, customerPostCode);
                prepState.setString(4, customerPhone);
                prepState.setString(5, currentUser);
                prepState.setString(6, currentUser);
                prepState.setInt(7, divId);

                prepState.execute(); //Runs the sql query


                //Return to the main screen after adding the customer
                returnHome.loadHome(actionEvent);
            } catch (SQLException e) {
                displayMessages.errorMsg("SQL Exception error encountered! " + e.getMessage());
            }
        } else {
            //Displays an error if there were bad entries found.
            displayMessages.errorMsg(badEntryMsg);
        }

    }
}
