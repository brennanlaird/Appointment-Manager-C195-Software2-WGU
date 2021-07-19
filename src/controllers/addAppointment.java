package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import objects.apptType;
import utilities.*;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.TimeZone;

/**
 * This class controls the adding of appointments to the database.
 */
public class addAppointment implements Initializable {
    public TextField idTextBox;
    public TextField titleTextBox;
    public TextField descriptionTextBox;
    public TextField locationTextBox;

    public HBox datePick;
    public DatePicker datePicker;

    public Button saveButton;
    public Button cancelButton;

    public Label tzLabel;
    public Label restrictionLabel;

    public ComboBox customerCombo;
    public ComboBox contactCombo;
    public ComboBox startTimeCombo;
    public ComboBox endTimeCombo;
    public ComboBox typeComboBox;
    public ComboBox userCombo;


    /**
     * Sets up the user interface for the add appointment form.
     * Lamda expression is used to find the users current time zone and return it as a string. Implmented alongside a method
     * that can perform a similar function. Two methods were introduced to show how each could be implemented.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //Set up an observable list to display the customer names, Contact names, and user names.
        ObservableList<String> customerList = FXCollections.observableArrayList();
        ObservableList<String> contactList = FXCollections.observableArrayList();
        ObservableList<String> userList = FXCollections.observableArrayList();

        //Sets up an observable list to display the appointment times.
        ObservableList<String> timeList = FXCollections.observableArrayList();

        //Sets the time zone label to display the name of the users current time zone.
        tzLabel.setText(timeZone.timeZoneName());



        //Sets the combobox to display the available meeting types.
        typeComboBox.setItems(meetingTypes.getMeetTypesCombo());

        //Gets the default time zone to as a ZoneId object. Commented out to implement lamda.
        //ZoneId tzName = ZoneId.of(timeZone.timeZoneName());


        //Lamda expression to return the users time zone as a string.
        interfaces tz =  () -> {
            TimeZone systemTZ = TimeZone.getDefault();
            return systemTZ.getID();};



        //Adjusts the defined business hours to local time to restrict the inputs with the help of Lamda expression.
        ZonedDateTime startBusinessLocal = timeZone.startBusinessHours(ZoneId.of(tz.tzName()));
        ZonedDateTime endBusinessLocal = timeZone.endBusinessHours(ZoneId.of(tz.tzName()));

        //Date formatter to convert the zoned date times to only the times.
        DateTimeFormatter formatToString = DateTimeFormatter.ISO_LOCAL_TIME;

        //The zoned date times converted to string values for display.
        String startTimeLocal = startBusinessLocal.format(formatToString);
        String endTimeLocal = endBusinessLocal.format(formatToString);

        //Displays the local business times in the label.
        restrictionLabel.setText("This is from " + startTimeLocal.substring(0, Math.min(startTimeLocal.length(), 5))
                + " to " + endTimeLocal.substring(0, Math.min(endTimeLocal.length(), 5)) + " local time.");



        //Setting up variables for use in the loop
        ZonedDateTime addingTime = startBusinessLocal; //Sets the first time to the local start time.
        String addingTimeList = null;  //Empty string to be used for adding times to the list.
        boolean endTime = false; //flag to exit the loop when the end of business has been added to the list.

        //Loops through and increments the available meeting times and adds them to the display list.
        do {
            addingTimeList = addingTime.format(formatToString); //Converts the time to string
            timeList.add(addingTimeList); //Adds the converted time to the list

            //Adds one hour to the time. This could be adjusted to different increments.
            addingTime = addingTime.plusHours(1);

            //If the most recently added time equals the end of business, change the flag to exit the loop.
            if (addingTimeList.equals(endTimeLocal)) {
                endTime = true;
            }

        } while (!endTime);

        //Set the combo boxes to display the time list.
        startTimeCombo.setItems(timeList);
        endTimeCombo.setItems(timeList);

        //Populate the customer combo box from the DB.
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


        } catch (SQLException e) {
            displayMessages.errorMsg("Error retrieving data from the customer table." + e.getMessage());
        }

        //Populate the contacts combo box from the DB.
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

            //Sets the items to display in the contacts combo box
            contactCombo.setItems(contactList);


        } catch (SQLException e) {
            displayMessages.errorMsg("Error retrieving data from the contact table." + e.getMessage());
        }

        //Populate the user combobox from the DB
        try{
            //Get all the users from the DB and add them to the result set
            String sql = "SELECT * FROM users";
            DBQuery.setPreparedStatement(DBConnect.getConnection(), sql); //Creating the prepared statement object
            PreparedStatement ps = DBQuery.getPreparedStatement(); //referencing the prepared statement
            ps.execute(); //Runs the sql query
            ResultSet userRS = ps.getResultSet(); //Setting the results of the query to a result set

            //Iterate through the result set and add the items to the observable list
            while (userRS.next()) {
                String temp = userRS.getString("User_Name");
                userList.add(temp);
            }

            //Sets the items to display in the contacts combo box and sets the combo to the current username.
            userCombo.setItems(userList);
            userCombo.setValue(userInfo.saveUsername);
        } catch (SQLException e){
            displayMessages.errorMsg("Error retrieving data from the user table." + e.getMessage());
        }
    }


    /**
     * Performs error checking and then saves the appointment to the database.
     */
    public void saveButtonClick(ActionEvent actionEvent) throws IOException {

        //Assign the values entered to variables.
        String apptTitle = titleTextBox.getText();
        String apptDescription = descriptionTextBox.getText();
        String apptLocation = locationTextBox.getText();
        String currentUser = userInfo.saveUsername; //used for created by and last update by


        //Variables for flagging and entry error and passing message to the error message method.
        boolean entryError = false;
        String badEntryMsg = "";

        //Checks for a blank title
        if (apptTitle.equals("")) {
            badEntryMsg = badEntryMsg + "Appointment title cannot be blank.";
            entryError = true;
        }

        //Checks for a blank description.
        if (apptDescription.equals("")) {
            //If a previous entry was also bad, add a new line to the error string.
            if (entryError) {
                badEntryMsg = badEntryMsg + "\n";
            }
            badEntryMsg = badEntryMsg + "Appointment description cannot be blank.";
            entryError = true;
        }

        //Checks for a blank location.
        if (apptLocation.equals("")) {
            //If a previous entry was also bad, add a new line to the error string.
            if (entryError) {
                badEntryMsg = badEntryMsg + "\n";
            }
            badEntryMsg = badEntryMsg + "Appointment location cannot be blank.";
            entryError = true;
        }

        //Checking the date picker to ensure it isn't blank and the date is in the future or present.
        try {
            LocalDate formDate = datePicker.getValue();
            //Checks to ensure appointments are not being created in the past.
            if (formDate.compareTo(LocalDate.now()) < 0) {
                displayMessages.errorMsg("New appointments must be created for today or a future date.");
            }
        } catch (NullPointerException nullPointerException) {
            displayMessages.errorMsg("Date cannot be blank. Please pick a date.");
        }


        //Get the entered times and date.

        String startTime = (String) startTimeCombo.getSelectionModel().getSelectedItem();
        String endTime = (String) endTimeCombo.getSelectionModel().getSelectedItem();

        LocalDate formDate = datePicker.getValue();


        //Gets the default time zone to as a ZoneId object
        ZoneId tzName = ZoneId.of(timeZone.timeZoneName());

        //Formatter to combine the entered dates and times into a local time format.
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_TIME;

        //Converts the time strings to a local time object. Done in a try-catch as this will catch an empty combo for the times.
        LocalTime localTimeStart = null;
        LocalTime localTimeEnd = null;

        try {
            localTimeStart = LocalTime.parse(startTime, formatter);
            localTimeEnd = LocalTime.parse(endTime, formatter);
        } catch (NullPointerException e) {
            displayMessages.errorMsg("Appointment times cannot be blank. Please pick a start and end time.");
        }

        //Create a zoned date time object by combining date, local time, and time zone ID
        ZonedDateTime localStartTime = ZonedDateTime.of(formDate, localTimeStart, tzName);
        ZonedDateTime localEndTime = ZonedDateTime.of(formDate, localTimeEnd, tzName);

        //Convert the local time to UTC time.
        ZonedDateTime utcStart = localStartTime.withZoneSameInstant(ZoneId.of("UTC"));
        ZonedDateTime utcEnd = localEndTime.withZoneSameInstant(ZoneId.of("UTC"));

        //Convert the zoned date times in UTC back to local times to save to the database.
        LocalDateTime ldtStart = utcStart.toLocalDateTime();
        LocalDateTime ldtEnd = utcEnd.toLocalDateTime();


        //Error checking for the time entries.

        //Checks that the end time is after the start time.
        if (ldtEnd.isEqual(ldtStart) || ldtEnd.isBefore(ldtStart)) {
            if (entryError) {
                badEntryMsg = badEntryMsg + "\n";
            }

            badEntryMsg = badEntryMsg + "The end time must be after the start time.";
            entryError = true;
        }


        //Lamda expression to return the users time zone as a string.
        interfaces tz =  () -> {
            TimeZone systemTZ = TimeZone.getDefault();
            return systemTZ.getID();};

        //Business hours converted to local time.
        ZonedDateTime startTimeLocal = timeZone.startBusinessHours(ZoneId.of(tz.tzName()));
        ZonedDateTime endTimeLocal = timeZone.endBusinessHours(ZoneId.of(tz.tzName()));

        //Checks to ensure the start time is not outside business hours.
        if (ldtStart.isBefore(startTimeLocal.toLocalDateTime()) || ldtStart.isAfter(endTimeLocal.toLocalDateTime())) {
            if (entryError) {
                badEntryMsg = badEntryMsg + "\n";
            }
            badEntryMsg = badEntryMsg + "The start time must occur during defined business hours.";
            entryError = true;
        }

        //Checks to ensure the start time is not outside business hours.
        if (ldtEnd.isBefore(startTimeLocal.toLocalDateTime()) || ldtEnd.isAfter(endTimeLocal.toLocalDateTime())) {
            if (entryError) {
                badEntryMsg = badEntryMsg + "\n";
            }
            badEntryMsg = badEntryMsg + "The end time must occur during defined business hours.";
            entryError = true;
        }





//Check if the comboboxes for customer, contact, user, and type are empty. If they are, the booleans are true.
        boolean customerEmpty = customerCombo.getSelectionModel().isEmpty();
        boolean contactEmpty = contactCombo.getSelectionModel().isEmpty();
        boolean apptTypeEmpty = typeComboBox.getSelectionModel().isEmpty();
        boolean userEmpty = userCombo.getSelectionModel().isEmpty();

        //Create the error message for an empty customer box.
        if (customerEmpty) {
            if (entryError) {
                badEntryMsg = badEntryMsg + "\n";
            }

            badEntryMsg = badEntryMsg + "Select a customer to associate with the appointment.";
            entryError = true;
        }

//Create the error message for an empty contact box.
        if (contactEmpty) {
            if (entryError) {
                badEntryMsg = badEntryMsg + "\n";
            }

            badEntryMsg = badEntryMsg + "Select a contact to associate with the appointment.";
            entryError = true;
        }

        //Create the error message for an empty appointment type box.
        if (apptTypeEmpty) {
            if (entryError) {
                badEntryMsg = badEntryMsg + "\n";
            }

            badEntryMsg = badEntryMsg + "Select the appointment type.";
            entryError = true;
        }

        //Create the error message for an empty user box.
        if (userEmpty) {
            if (entryError) {
                badEntryMsg = badEntryMsg + "\n";
            }

            badEntryMsg = badEntryMsg + "Select the user from the dropdown box.";
            entryError = true;
        }

        //Get the customer and contact names and appointment type from the combo boxes.
        String customer = (String) customerCombo.getSelectionModel().getSelectedItem();

        String contact = (String) contactCombo.getSelectionModel().getSelectedItem();

        String apptType = (String) typeComboBox.getSelectionModel().getSelectedItem();

        String associatedUser = (String) userCombo.getSelectionModel().getSelectedItem();

        //If there are no entry error, proceed with pulling data using SQL queries.
        if (!entryError) {
            try {
                //Query for the Customer ID
                String sql = "SELECT Customer_ID FROM customers WHERE Customer_Name = ?";
                DBQuery.setPreparedStatement(DBConnect.getConnection(), sql); //Creating the prepared statement object
                PreparedStatement ps = DBQuery.getPreparedStatement(); //referencing the prepared statement
                ps.setString(1, customer);
                ps.executeQuery(); //Runs the sql query
                ResultSet customerID_RS = ps.getResultSet(); //Setting the results of the query to a result set
                customerID_RS.next(); //Moves to the first result in the result set

                int customerID = customerID_RS.getInt("Customer_ID");


                //Query for the User ID
                sql = "SELECT User_ID FROM users WHERE User_Name = ?";
                DBQuery.setPreparedStatement(DBConnect.getConnection(), sql); //Creating the prepared statement object
                PreparedStatement ps1 = DBQuery.getPreparedStatement(); //referencing the prepared statement
                ps1.setString(1, associatedUser);
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

                    //Only proceed if the overlap flag is false to prevent duplicate messages.
                    if (!overlapFlag) {
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
                        //If the the start is before and then end is after
                        if (utcStart.isBefore(dbZonedStart) && utcEnd.isAfter(dbZonedEnd)) {
                            overlapFlag = true;
                        }
                        //Error message if the overlap flag is raised.
                        if (overlapFlag) {
                            displayMessages.errorMsg("The times entered overlaps with another meeting. Please adjust the times and try again.");
                        }
                    }

                }


                //If no time overlap was found, proceed to insert.

                if (!overlapFlag) {
                    //SQL Insert query to insert the appointment into the DB
                    sql = "INSERT INTO appointments (Title, Description, Location, Type, Start, End, Created_By, Last_Updated_By, Customer_ID, User_ID, Contact_ID) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

                    DBQuery.setPreparedStatement(DBConnect.getConnection(), sql); //Creating the prepared statement object
                    PreparedStatement prepState = DBQuery.getPreparedStatement(); //referencing the prepared statement

                    //Sets the values for the various parameters in the SQL statement.
                    prepState.setString(1, apptTitle);
                    prepState.setString(2, apptDescription);
                    prepState.setString(3, apptLocation);
                    prepState.setString(4, apptType);
                    prepState.setObject(5, ldtStart);
                    prepState.setObject(6, ldtEnd);
                    prepState.setString(7, currentUser);
                    prepState.setString(8, currentUser);
                    prepState.setInt(9, customerID);
                    prepState.setInt(10, userID);
                    prepState.setInt(11, contactsID);

                    prepState.execute(); //Runs the sql query

                    //Returns to the home screen after saving the info to the DB
                    returnHome.loadHome(actionEvent);
                }
            } catch (SQLException e) {
                displayMessages.errorMsg("SQL Exception error encountered! " + e.getMessage());
            }
        } else {
            //This will display the error message if there were any entry errors found.
            displayMessages.errorMsg(badEntryMsg);
        }


    }

    /**
     * Returns to the main form without saving any of the user input.
     */
    public void cancelButtonClick(ActionEvent actionEvent) throws IOException {
        returnHome.loadHome(actionEvent);
    }


}
