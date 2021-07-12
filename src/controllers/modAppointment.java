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

    public ComboBox typeComboBox;

    public ComboBox customerCombo;
    public ComboBox contactCombo;
    public ComboBox startTimeCombo;
    public ComboBox endTimeCombo;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //Sets the label to show the current time zone name.
        tzLabel.setText(timeZone.timeZoneName());
        typeComboBox.setItems(meetingTypes.getMeetTypesCombo());
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
        String endTime = endTimeCombo.getSelectionModel().getSelectedItem().toString();
        LocalDate formDate = datePicker.getValue();

        //Gets the default time zone to as a ZoneId object
        ZoneId tzName = ZoneId.of(timeZone.timeZoneName());


        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_TIME;
        LocalTime localTimeStart = LocalTime.parse(startTime, formatter);
        LocalTime localTimeEnd = LocalTime.parse(endTime, formatter);

        ZonedDateTime localStartTime = ZonedDateTime.of(formDate, localTimeStart, tzName);
        ZonedDateTime localEndTime = ZonedDateTime.of(formDate, localTimeEnd, tzName);


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


        //Check for overlapping appointments.

        //Get all the appointments and add them to a result set.
        sql = "SELECT * FROM appointments";
        DBQuery.setPreparedStatement(DBConnect.getConnection(), sql); //Creating the prepared statement object
        PreparedStatement ps3 = DBQuery.getPreparedStatement(); //referencing the prepared statement
        ps3.executeQuery(); //Runs the sql query
        ResultSet allAppointmentsRS = ps3.getResultSet();

        //Variable to act as a flag to determine if an overlap was found.
        boolean overlapFlag = false;

        while (allAppointmentsRS.next()) {

            //Formatter to parse the string from the DB into date and times.
            DateTimeFormatter formatFromDB = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            //Get the start and end times of the current appointment as strings.
            String dbStart = allAppointmentsRS.getString("Start");
            String dbEnd = allAppointmentsRS.getString("End");

            //Convert the strings from the DB to Local Time types.
            LocalTime dbLocalStart = LocalTime.parse(dbStart, formatFromDB);
            LocalTime dbLocalEnd = LocalTime.parse(dbEnd, formatFromDB);

            //Convert the strings to local date types.
            LocalDate dbStartDate = LocalDate.parse(dbStart, formatFromDB);
            LocalDate dbEndDate = LocalDate.parse(dbEnd, formatFromDB);

            //Create zoneddatetime objects from the time and date from the DB.
            ZonedDateTime dbZonedStart = ZonedDateTime.of(dbStartDate, dbLocalStart, ZoneId.of("UTC"));
            ZonedDateTime dbZonedEnd = ZonedDateTime.of(dbEndDate, dbLocalEnd, ZoneId.of("UTC"));

            if (apptID != allAppointmentsRS.getInt("Appointment_ID")) {

                //If the date and time of the start times match, then raise the overlap flag
                if (dbZonedStart.equals(utcStart)) {
                    overlapFlag = true;
                }

                //If the end date and time are equal, there is an overlap.
                if (dbZonedEnd.equals(utcEnd)) {
                    overlapFlag = true;
                }

                //If the start time is between times of another meeting.
                if (utcStart.isAfter(dbZonedStart) && utcStart.isBefore(dbZonedEnd)) {
                    overlapFlag = true;
                }

                //If the end time falls between another meeting.
                if (utcEnd.isAfter(dbZonedStart) && utcEnd.isBefore(dbZonedEnd)) {
                    overlapFlag = true;
                }

                //Error message if the overlap flag is raised.
                if (overlapFlag) {
                    displayMessages.errorMsg("The times entered overlaps with another meeting. Please adjust the times and try again.");
                }

            }
        }


        //Creates a local date time to be passed to the database to show the last update time.
        ZonedDateTime rn = ZonedDateTime.of(LocalDateTime.now(), tzName); //Current time in the users time zone.
        rn = rn.withZoneSameInstant(ZoneId.of("UTC")); //Convert user time to UTC
        LocalDateTime rightNow = rn.toLocalDateTime(); //Convert UTC zoned object to local time object for passing to DB


        if (!overlapFlag) {
            String updateApptSQL = "UPDATE appointments  " +
                    "SET Title = ?, Description = ?, Location = ?, Type = ?, Start = ?, End = ?, Last_Update = ?,  Last_Updated_By = ?, " +
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

            prepState.setObject(7, rightNow);
            prepState.setString(8, currentUser);

            prepState.setInt(9, customerID);
            prepState.setInt(10, userID);
            prepState.setInt(11, contactsID);

            prepState.setInt(12, apptID);


            prepState.executeUpdate(); //Executes the update query


            returnHome.loadHome(actionEvent);
        }
    }

    public void cancelButtonClick(ActionEvent actionEvent) throws IOException {
        returnHome.loadHome(actionEvent);
    }
}
