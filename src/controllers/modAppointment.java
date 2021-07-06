package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import objects.Appointment;
import utilities.*;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class modAppointment implements Initializable {
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

public  ComboBox typeComboBox;

    public ComboBox customerCombo;
    public ComboBox contactCombo;
    public ComboBox startTimeCombo;
    public ComboBox endTimeCombo;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //Sets the label to show the current time zone name.
        tzLabel.setText(timeZone.timeZoneName());
        typeComboBox.setItems(meetingTypes.getApptTypes());
    }

    /***/
    public void receiveAppt(Appointment modAppt) throws SQLException {

        //Set up an observable list to display the customer names and Contact names
        ObservableList<String> customerList = FXCollections.observableArrayList();
        ObservableList<String> contactList = FXCollections.observableArrayList();

        //Sets up an observable list to display the available times in the combo boxes.
        ObservableList<String> timeList = FXCollections.observableArrayList();

        //Sets the value of the text boxes to that of the passed in appointment
        idTextBox.setText(String.valueOf(modAppt.getId()));
        titleTextBox.setText(String.valueOf(modAppt.getTitle()));
        descriptionTextBox.setText(String.valueOf(modAppt.getDescription()));
        locationTextBox.setText(String.valueOf(modAppt.getLocation()));
        //typeTextBox.setText(String.valueOf(modAppt.getType()));
        typeComboBox.setValue(String.valueOf(modAppt.getType()));

        //Converts the passed in appointment start time to a local date then sets the date picker to display that date.
        LocalDate apptDate = modAppt.getStartTime().toLocalDate();
        datePicker.setValue(apptDate);


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

            if (addingTimeList.equals(endTimeLocal)) {
                endTime = true;
            }


        } while (!endTime);


        startTimeCombo.setItems(timeList);
        endTimeCombo.setItems(timeList);

        //Pulls the times from the passed object and adjusts to the users time zone and displays them in the time combos
        startTimeCombo.setValue(modAppt.getStartTime().withZoneSameInstant(tzName).toLocalTime());
        endTimeCombo.setValue(modAppt.getEndTime().withZoneSameInstant(tzName).toLocalTime());


        //Set up a query to pull customer names to display and select the name as passed in from the ID

        int tempID = modAppt.getCustomerID();
        String displayName = "";

        String sql = "SELECT * FROM customers";
        DBQuery.setPreparedStatement(DBConnect.getConnection(), sql); //Creating the prepared statement object
        PreparedStatement ps = DBQuery.getPreparedStatement(); //referencing the prepared statement
        ps.execute(); //Runs the sql query
        ResultSet allCustomers = ps.getResultSet(); //Setting the results of the query to a result set

        //Iterate through the result set and add the items to the observable list
        while (allCustomers.next()) {
            String temp = allCustomers.getString("Customer_Name");
            customerList.add(temp);
            if (tempID == allCustomers.getInt("Customer_ID")) {
                displayName = temp;
            }
        }


        customerCombo.setItems(customerList);
        customerCombo.setValue(displayName);

        //Set up a query to pull contact name as passed in from the ID
        tempID = modAppt.getContactID();
        displayName = "";

        sql = "SELECT * FROM contacts";
        DBQuery.setPreparedStatement(DBConnect.getConnection(), sql); //Creating the prepared statement object
        PreparedStatement ps1 = DBQuery.getPreparedStatement(); //referencing the prepared statement
        ps1.execute(); //Runs the sql query
        ResultSet allContacts = ps1.getResultSet(); //Setting the results of the query to a result set

        //Iterate through the result set and add the items to the observable list
        while (allContacts.next()) {
            String temp = allContacts.getString("Contact_Name");
            contactList.add(temp);
            if (tempID == allContacts.getInt("Contact_ID")) {
                displayName = temp;
            }
        }


        contactCombo.setItems(contactList);
        contactCombo.setValue(displayName);


    }


    public void clearFormButtonClick(ActionEvent actionEvent) {
        //Nothing here.
    }

    public void saveButtonClick(ActionEvent actionEvent) throws IOException, SQLException {
        int apptID = Integer.parseInt(idTextBox.getText());
        String apptTitle = titleTextBox.getText();
        String apptDescription = descriptionTextBox.getText();
        String apptLocation = locationTextBox.getText();
        //String apptType = typeTextBox.getText();
        String apptType = (String) typeComboBox.getSelectionModel().getSelectedItem();
        String currentUser = userInfo.saveUsername; //used for created by and last update by

        try {
            LocalDate formDate = datePicker.getValue();
            if (formDate.compareTo(LocalDate.now()) < 0) {
                displayMessages.errorMsg("New appointments must be created for today or a future date.");
            }
        } catch (NullPointerException nullPointerException) {
            displayMessages.errorMsg("Date cannot be blank. Please pick a date.");
        }

        System.out.println(startTimeCombo.getSelectionModel().getSelectedItem());



        String startTime = startTimeCombo.getSelectionModel().getSelectedItem().toString();
        String endTime =  endTimeCombo.getSelectionModel().getSelectedItem().toString();
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

        System.out.println("Local " + localEndTime + " UTC " + utcEnd);

        LocalDateTime ldtStart = utcStart.toLocalDateTime();
        LocalDateTime ldtEnd = utcEnd.toLocalDateTime();

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





        String updateApptSQL = "UPDATE appointments  " +
                "SET Title = ?, Description = ?, Location = ?, Type = ?, Start = ?, End = ?,  Last_Updated_By = ?, " +
                "Customer_ID = ?, User_ID = ?, Contact_ID = ? " +
                "WHERE Appointment_ID = ?";

        DBQuery.setPreparedStatement(DBConnect.getConnection(), updateApptSQL); //Creating the prepared statement object
        PreparedStatement prepState = DBQuery.getPreparedStatement(); //referencing the prepared statement

        prepState.setString(1, apptTitle);
        prepState.setString(2, apptDescription);
        prepState.setString(3, apptLocation);
        prepState.setString(4, apptType);

        prepState.setObject(5, ldtStart);
        prepState.setObject(6, ldtEnd);

        prepState.setString(7, currentUser);

        prepState.setInt(8, customerID);
        prepState.setInt(9, userID);
        prepState.setInt(10, contactsID);

        prepState.setInt(11, apptID);


        prepState.executeUpdate(); //Executes the update query


        returnHome.loadHome(actionEvent);
    }

    public void cancelButtonClick(ActionEvent actionEvent) throws IOException {
        returnHome.loadHome(actionEvent);
    }
}
