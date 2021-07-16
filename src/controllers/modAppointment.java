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


/**
 * This class controls the modification of appointments pulled from the database.
 */
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

    /**
     * Sets up the user interface for the modify appointment form.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //Sets the label to show the current time zone name.
        tzLabel.setText(timeZone.timeZoneName());
        typeComboBox.setItems(meetingTypes.getMeetTypesCombo());
    }

    /**
     * This takes the appointment object and populates the UI with appropriate items from the DB.
     * @param modAppt The appointment object selected from the main screen.

     */
    public void receiveAppt(Appointment modAppt)  {

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

        //Set the items for the start and end time lists.
        startTimeCombo.setItems(timeList);
        endTimeCombo.setItems(timeList);

        //Pulls the times from the passed object and adjusts to the users time zone and displays them in the time combos
        startTimeCombo.setValue(modAppt.getStartTime().toLocalTime().format(formatToString));
        endTimeCombo.setValue(modAppt.getEndTime().toLocalTime().format(formatToString));


        //Set up a query to pull customer names to display and select the name as passed in from the ID

        //Try-catch for SQL errors.
        try {
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

            //Set the combobox to display the names list and set the value to the one passed in.
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

            //Set the combobox to display the contact list and set the value to the one passed in.
            contactCombo.setItems(contactList);
            contactCombo.setValue(displayName);
        } catch (SQLException e){
            //Displays an error if a SQL exception is caught.
            displayMessages.errorMsg("SQL Exception error encountered! " + e.getMessage());
        }

    }


    /**
     * Performs error checking and then updates the a modified appointment in the database.

     */
    public void saveButtonClick(ActionEvent actionEvent) throws IOException {
        int apptID = Integer.parseInt(idTextBox.getText());

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

        if (ldtEnd.isEqual(ldtStart) || ldtEnd.isBefore(ldtStart)) {
            if (entryError) {
                badEntryMsg = badEntryMsg + "\n";
            }

            badEntryMsg = badEntryMsg + "The end time must be after the start time.";
            entryError = true;

        }

//Check if the comboboxes for customer, contact and type are empty. If they are, the booleans are true.
        boolean customerEmpty = customerCombo.getSelectionModel().isEmpty();
        boolean contactEmpty = contactCombo.getSelectionModel().isEmpty();
        boolean apptTypeEmpty = typeComboBox.getSelectionModel().isEmpty();

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

        //Get the customer and contact names and appointment type from the combo boxes.
        String customer = (String) customerCombo.getSelectionModel().getSelectedItem();

        String contact = (String) contactCombo.getSelectionModel().getSelectedItem();

        String apptType = (String) typeComboBox.getSelectionModel().getSelectedItem();


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

                    //Only proceed if the overlap flag is false to prevent duplicate messages.
                    if (!overlapFlag) {
                        //This ensures the appointment is not compared to itself.
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
                }


                //Creates a local date time to be passed to the database to show the last update time.
                ZonedDateTime rn = ZonedDateTime.of(LocalDateTime.now(), tzName); //Current time in the users time zone.
                rn = rn.withZoneSameInstant(ZoneId.of("UTC")); //Convert user time to UTC
                LocalDateTime rightNow = rn.toLocalDateTime(); //Convert UTC zoned object to local time object for passing to DB

                //If no time overlap was found, proceed to insert.
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
