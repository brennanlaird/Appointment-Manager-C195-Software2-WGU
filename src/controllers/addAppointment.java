package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import utilities.*;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.TimeZone;

public class addAppointment implements Initializable {
    public TextField idTextBox;
    public TextField titleTextBox;
    public TextField descriptionTextBox;
    public TextField locationTextBox;
    public TextField typeTextBox;

    public HBox datePick;
    public DatePicker datePicker;

    public Button clearFormButton;
    public Button saveButton;
    public Button cancelButton;

    public Label tzLabel;


    public ComboBox customerCombo;
    public ComboBox contactCombo;
    public ComboBox startTimeCombo;
    public ComboBox endTimeCombo;




    /**Sets up the combo boxes and the spinner boxes for the add appointment form.*/
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //Set up an observable list to display the customer names and Contact names
        ObservableList<String> customerList = FXCollections.observableArrayList();
        ObservableList<String> contactList = FXCollections.observableArrayList();

        ObservableList<String> timeList = FXCollections.observableArrayList();

        //Sets the time zone label to display the name of the users current time zone.
        tzLabel.setText(timeZone.timeZoneName());


        //Gets the default time zone to as a ZoneId object
        ZoneId tzName = ZoneId.of(timeZone.timeZoneName());

        //Adjusts the defined business hours to local time to restrict the inputs.
        ZonedDateTime startBusinessLocal = timeZone.startBusinessHours(tzName);
        ZonedDateTime endBusinessLocal = timeZone.endBusinessHours(tzName);





        //Date formatter to convert the zoned date times to only the times.
        DateTimeFormatter formatToString = DateTimeFormatter.ISO_LOCAL_TIME;

        //The zoned date times converted to string values for display.
        String startTimeLocal = startBusinessLocal.format(formatToString);
        String endTimeLocal = endBusinessLocal.format(formatToString);

        ZonedDateTime addingTime = startBusinessLocal;
        String addingTimeList = null;
        boolean endTime = false;

        do {
            addingTimeList = addingTime.format(formatToString);
            timeList.add(addingTimeList);

            addingTime = addingTime.plusHours(1);

            if (addingTimeList.equals(endTimeLocal)) {endTime=true;}


        } while (!endTime);


        startTimeCombo.setItems(timeList);
        endTimeCombo.setItems(timeList);

        try {
            //Get all the customers from the DB and add them to the result set
            String sql = "SELECT * FROM customers";
            DBQuery.setPreparedStatement(DBConnect.getConnection(), sql); //Creating the prepared statement object
            PreparedStatement ps = DBQuery.getPreparedStatement(); //referencing the prepared statement
            ps.execute(); //Runs the sql query
            ResultSet customerRS = ps.getResultSet(); //Setting the results of the query to a result set

            //Iterate through the result set and add the items to the observable list
            while (customerRS.next()) {
                String temp = customerRS.getString("Customer_Name");
                customerList.add(temp);
            }


            //Sets the items to display in the country combo box
            customerCombo.setItems(customerList);


        } catch (Exception e) {
            System.out.println("There was a problem adding the customer data.");

        }


        try {
            //Get all the contacts from the DB and add them to the result set
            String sql = "SELECT * FROM contacts";
            DBQuery.setPreparedStatement(DBConnect.getConnection(), sql); //Creating the prepared statement object
            PreparedStatement ps = DBQuery.getPreparedStatement(); //referencing the prepared statement
            ps.execute(); //Runs the sql query
            ResultSet contactsRS = ps.getResultSet(); //Setting the results of the query to a result set

            //Iterate through the result set and add the items to the observable list
            while (contactsRS.next()) {
                String temp = contactsRS.getString("Contact_Name");
                contactList.add(temp);
            }


            //Sets the items to display in the country combo box
            contactCombo.setItems(contactList);


        } catch (Exception e) {
            System.out.println("There was a problem adding the contact data.");

        }
    }

    public void clearFormButtonClick(ActionEvent actionEvent) {
        //idTextBox.setText("");
        titleTextBox.setText("");
        descriptionTextBox.setText("");
        locationTextBox.setText("");
        typeTextBox.setText("");
    }

    public void saveButtonClick(ActionEvent actionEvent) throws IOException, SQLException {

        String apptTitle = titleTextBox.getText();
        String apptDescription = descriptionTextBox.getText();
        String apptLocation = locationTextBox.getText();
        String apptType = typeTextBox.getText();
        String currentUser = userInfo.saveUsername; //used for created by and last update by

        try {
            LocalDate formDate = datePicker.getValue();
            if (formDate.compareTo(LocalDate.now()) < 0) {
                displayMessages.errorMsg("New appointments must be created for today or a future date.");
            }
        } catch (NullPointerException nullPointerException) {
            displayMessages.errorMsg("Date cannot be blank. Please pick a date.");
        }


        String startTime = (String) startTimeCombo.getSelectionModel().getSelectedItem();
        String endTime = (String) endTimeCombo.getSelectionModel().getSelectedItem();
        LocalDate formDate = datePicker.getValue();

        //Gets the default time zone to as a ZoneId object
        ZoneId tzName = ZoneId.of(timeZone.timeZoneName());


        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_TIME;
        LocalTime localTimeStart = LocalTime.parse(startTime,formatter);
        LocalTime localTimeEnd = LocalTime.parse(endTime,formatter);

        ZonedDateTime localStartTime = ZonedDateTime.of(formDate,localTimeStart,tzName);
        ZonedDateTime localEndTime = ZonedDateTime.of(formDate,localTimeEnd,tzName);


        ZonedDateTime utcStart = localStartTime.withZoneSameInstant(ZoneId.of("UTC"));
        ZonedDateTime utcEnd = localEndTime.withZoneSameInstant(ZoneId.of("UTC"));

        System.out.println("Local " + localStartTime + " UTC " + utcStart);



        String customer = (String) customerCombo.getSelectionModel().getSelectedItem();
        String contact = (String) contactCombo.getSelectionModel().getSelectedItem();


        //Query for the Customer ID
        String sql = "SELECT Customer_ID FROM customers WHERE Customer_Name = ?";
        DBQuery.setPreparedStatement(DBConnect.getConnection(), sql); //Creating the prepared statement object
        PreparedStatement ps = DBQuery.getPreparedStatement(); //referencing the prepared statement
        ps.setString(1, customer);
        ps.executeQuery(); //Runs the sql query
        ResultSet customerID_RS = ps.getResultSet(); //Setting the results of the query to a result set
        customerID_RS.next(); //Moves to the first result in the result set

        int customerID = customerID_RS.getInt("Customer_ID");



        //Query for the User ID - This can be done on initilization?
        sql = "SELECT User_ID FROM users WHERE User_Name = ?";
        DBQuery.setPreparedStatement(DBConnect.getConnection(), sql); //Creating the prepared statement object
        PreparedStatement ps1 = DBQuery.getPreparedStatement(); //referencing the prepared statement
        ps1.setString(1, currentUser);
        ps1.executeQuery(); //Runs the sql query
        ResultSet userID_RS = ps1.getResultSet();

        userID_RS.next();

        int userID = userID_RS.getInt("User_ID");


        //Query for the Contact ID
        sql = "SELECT Contact_ID FROM contacts WHERE Contact_Name = ?";
        DBQuery.setPreparedStatement(DBConnect.getConnection(), sql); //Creating the prepared statement object
        PreparedStatement ps2 = DBQuery.getPreparedStatement(); //referencing the prepared statement
        ps2.setString(1, contact);
        ps2.executeQuery(); //Runs the sql query
        ResultSet contactID_RS = ps2.getResultSet();

        contactID_RS.next();

        int contactsID = contactID_RS.getInt("Contact_ID");





        //SQL Insert query to insert the appointment into the DB
        sql = "INSERT INTO appointments (Title, Description, Location, Type, Start, End, Created_By, Last_Updated_By, Customer_ID, User_ID, Contact_ID) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        DBQuery.setPreparedStatement(DBConnect.getConnection(), sql); //Creating the prepared statement object
        PreparedStatement prepState = DBQuery.getPreparedStatement(); //referencing the prepared statement

        prepState.setString(1, apptTitle);
        prepState.setString(2, apptDescription);
        prepState.setString(3, apptLocation);
        prepState.setString(4, apptType);

        prepState.setObject(5, utcStart);
        prepState.setObject(6, utcEnd);

        prepState.setString(7, currentUser);
        prepState.setString(8, currentUser);


        prepState.setInt(9, customerID);
        prepState.setInt(10, userID);
        prepState.setInt(11, contactsID);

        prepState.execute(); //Runs the sql query




        //Returns to the home screen after saving the info to the DB
        returnHome.loadHome(actionEvent);
    }

    /**
     * Returns to the main form without saving any of the user input.
     */
    public void cancelButtonClick(ActionEvent actionEvent) throws IOException {
        returnHome.loadHome(actionEvent);
    }


}
