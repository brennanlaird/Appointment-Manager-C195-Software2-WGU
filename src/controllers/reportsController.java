package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import objects.Appointment;
import objects.appointmentTypeReport;
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

/**
 * Implements three different reports based on the data.
 */
public class reportsController implements Initializable {
    public ToggleGroup reportType;
    public RadioButton apptTypeButton;
    public RadioButton contactScheduleButton;
    public RadioButton customerScheduleButton;

    public AnchorPane apptTypePane;
    public AnchorPane contactPane;
    public AnchorPane customerPane;

    public Button returnToMain;

    public TableView contactTable;
    public TableColumn apptIDCol;
    public TableColumn apptTitleCol;
    public TableColumn apptTypeCol;
    public TableColumn apptDescriptionCol;
    public TableColumn apptStartCol;
    public TableColumn apptEndCol;
    public TableColumn customerIDCol;

    public ComboBox contactNameCombo;

    public TableView customerTable;
    public TableColumn apptIDColCustomer;
    public TableColumn apptTitleColCustomer;
    public TableColumn apptTypeColCustomer;
    public TableColumn apptDescriptionColCustomer;
    public TableColumn apptStartColCustomer;
    public TableColumn apptEndColCustomer;
    public TableColumn contactIDColCustomer;

    public ComboBox customerCombo;

    public ComboBox monthComboBox;
    public ComboBox yearCombo;

    public TableView testApptView;
    public TableColumn testApptViewCol;
    public TableColumn testApptViewCol2;

    public Label tableHeader;


    //Setup the observable lists for the combo boxes.
    ObservableList<String> contactList = FXCollections.observableArrayList();
    ObservableList<String> customerList = FXCollections.observableArrayList();
    ObservableList<Appointment> apptList = FXCollections.observableArrayList();
    ObservableList<appointmentTypeReport> apptTypeList = FXCollections.observableArrayList();
    ObservableList<Month> availableMonths = FXCollections.observableArrayList();
    ObservableList<Year> availableYears = FXCollections.observableArrayList();

    /**
     * Sets up the meeting types from the class and hides the anchor panes until a button is clicked to show them.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


        //Hide elements. These are only visible when the corresponding radio button is selected.
        contactPane.setVisible(false);
        customerPane.setVisible(false);
        apptTypePane.setVisible(false);


        //Setting the meeting type buckets.
        apptType.meetingTypes.clear();
        apptType.createTypes();


        try {
            //Get all the contacts from the DB and add them to the result set
            String sql = "SELECT * FROM contacts";
            DBQuery.setPreparedStatement(DBConnect.getConnection(), sql); //Creating the prepared statement object
            PreparedStatement ps = DBQuery.getPreparedStatement(); //referencing the prepared statement
            ps.execute(); //Runs the sql query
            ResultSet allContacts = ps.getResultSet(); //Setting the results of the query to a result set

            //Iterate through the result set and add the items to the observable list
            while (allContacts.next()) {
                String temp = allContacts.getString("Contact_Name");
                contactList.add(temp);
            }

            //Sets the items to display in the contacts combo box
            contactNameCombo.setItems(contactList);


        } catch (SQLException e) {
            displayMessages.errorMsg("SQL Exception error encountered! " + e.getMessage());
        }


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


            //Sets the items to display in the customer combo box
            customerCombo.setItems(customerList);


        } catch (SQLException e) {
            displayMessages.errorMsg("SQL Exception error encountered! " + e.getMessage());

        }


    }

    /**
     * Calls the return to home method to load the main screen.
     *
     * @param actionEvent The button click action event.
     */
    public void returnToMainClick(ActionEvent actionEvent) throws IOException {
        returnHome.loadHome(actionEvent);
    }

    /**
     * @param actionEvent The button click action event.
     */
    public void apptTypeButtonSelect(ActionEvent actionEvent) {
        //Set the visibility of the panes.
        contactPane.setVisible(false);
        customerPane.setVisible(false);
        apptTypePane.setVisible(true);


        //Set the table to display the appointment types and counts from the apptType observable list.
        testApptView.setItems(apptType.meetingTypes);
        testApptViewCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        testApptViewCol2.setCellValueFactory(new PropertyValueFactory<>("count"));

        monthComboBox.setItems(availableMonths);
        yearCombo.setItems(availableYears);


        //Clear the list to avoid displaying extra appointments.
        apptTypeList.clear();
        apptList.clear();

        //Get the appointments and create the objects
        try {
            //Create a query to get all the appointments from the DB
            String sql = "SELECT * FROM appointments";
            DBQuery.setPreparedStatement(DBConnect.getConnection(), sql); //Creating the prepared statement object
            PreparedStatement ps = DBQuery.getPreparedStatement(); //referencing the prepared statement

            ps.executeQuery(); //Runs the sql query
            ResultSet appointmentRS = ps.getResultSet(); //Setting the results of the query to a result set


            while (appointmentRS.next()) {
                int apptID = appointmentRS.getInt("Appointment_ID");
                String apptTitle = appointmentRS.getString("Title");
                String apptDescription = appointmentRS.getString("Description");
                String apptLocation = appointmentRS.getString("Location");
                String apptType = appointmentRS.getString("Type");

                //Date time formatter that matches the pattern from the database.
                //This is used to parse the strings from the database into date time objects in Java.
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

                //Parse the start time into a local date time and then change it to a zoned date time
                LocalDateTime startTime = LocalDateTime.parse(appointmentRS.getString("Start"), formatter);
                ZonedDateTime apptStart = ZonedDateTime.of(startTime, ZoneId.of("UTC"));

                //Parse the end time into a local date time and then change it to a zoned date time
                LocalDateTime endTime = LocalDateTime.parse(appointmentRS.getString("End"), formatter);
                ZonedDateTime apptEnd = ZonedDateTime.of(endTime, ZoneId.of("UTC"));

                //Get the name of the users current time zone.
                ZoneId tz = ZoneId.of(timeZone.timeZoneName());


                ZonedDateTime apptEndlocal = ZonedDateTime.ofInstant(apptEnd.toInstant(), tz);


                //Create a new object and add it to the list.
                appointmentTypeReport temp = new appointmentTypeReport(apptType, apptStart);
                apptTypeList.add(temp);

                //Get the month and year of the current appointment start time.
                Month apptMonth = apptStart.getMonth();
                Year apptYear = Year.of(apptStart.getYear());

                //If not already on the list, add the month to the list for the combo box.
                if (!availableMonths.contains(apptMonth)) {
                    availableMonths.add(apptMonth);
                }
                //If not already on the list, add the year to the list for the combo box.
                if (!availableYears.contains(apptYear)) {
                    availableYears.add(apptYear);
                }

                //Code to add a new appointment object. Left in for potential future use.
/*
                Appointment temp = new Appointment(apptID, apptTitle, apptDescription, apptLocation, apptType, apptStart,
                        apptEnd, apptCreator, apptUpdater, apptCustomerID, apptUserID, apptContactID, apptContact);
                apptList.add(temp);
*/
            }


            //Sets the month and year combo boxes to select the first items.
            monthComboBox.getSelectionModel().select(0);
            yearCombo.getSelectionModel().select(0);


        } catch (SQLException e) {
            displayMessages.errorMsg("SQL Exception error encountered! " + e.getMessage());
        }


    }

    /**
     * Sets the contact schedule pane as visible so the controls in that anchor pane can be interacted with. All the
     * other panes are set to invisible.
     *
     * @param actionEvent The button click action event.
     */
    public void contactScheduleButton(ActionEvent actionEvent) {
        //Sets the elements in the contact pane as visible.
        contactPane.setVisible(true);
        contactNameCombo.setVisible(true);
        contactTable.setVisible(true);

        //Sets the other panes as not visible.
        customerPane.setVisible(false);
        apptTypePane.setVisible(false);
    }

    /**
     * Sets the customer schedule pane as visible so the controls in that anchor pane can be interacted with. All the
     * other panes are set to invisible.
     *
     * @param actionEvent The button click action event.
     */
    public void customerScheduleButtonSelect(ActionEvent actionEvent) {
        //Sets the customer anchor pane to visible.
        customerPane.setVisible(true);

        //Sets the other anchor panes to invisible.
        contactPane.setVisible(false);
        apptTypePane.setVisible(false);
    }

    /**
     * Displays all the appointments associated with the contact selected from the combo box.
     *
     * @param actionEvent The button click action event.
     */
    public void contactNameComboChange(ActionEvent actionEvent) {
        //Put the selected string from the combo into a variable
        String contactName = (String) contactNameCombo.getSelectionModel().getSelectedItem();

        //Sets the contacts ID as zero outside the try-catch block.
        int contactsID = 0;

        //Clear the appointment list to prevent stray appointments from appearing.
        apptList.clear();


        //Get the contact ID from the DB
        try {
            //Query for the Contact ID
            String sql = "SELECT Contact_ID FROM contacts WHERE Contact_Name = ?";
            DBQuery.setPreparedStatement(DBConnect.getConnection(), sql); //Creating the prepared statement object
            PreparedStatement ps = DBQuery.getPreparedStatement(); //referencing the prepared statement
            ps.setString(1, contactName);
            ps.executeQuery(); //Runs the sql query
            ResultSet contactID_RS = ps.getResultSet();

            contactID_RS.next();

            contactsID = contactID_RS.getInt("Contact_ID");

        } catch (SQLException e) {
            displayMessages.errorMsg("SQL Exception error encountered! " + e.getMessage());
        }

        //Get the appointment IDs that match the contact name from the DB and add those appointments to the list
        try {
            //Create a query to get all the appointments from the DB
            String sql = "SELECT * FROM appointments WHERE Contact_ID = ?";
            DBQuery.setPreparedStatement(DBConnect.getConnection(), sql); //Creating the prepared statement object
            PreparedStatement ps = DBQuery.getPreparedStatement(); //referencing the prepared statement
            ps.setString(1, String.valueOf(contactsID));
            ps.executeQuery(); //Runs the sql query
            ResultSet appointmentRS = ps.getResultSet(); //Setting the results of the query to a result set

            while (appointmentRS.next()) {
                int apptID = appointmentRS.getInt("Appointment_ID");
                String apptTitle = appointmentRS.getString("Title");
                String apptDescription = appointmentRS.getString("Description");
                String apptLocation = appointmentRS.getString("Location");
                String apptType = appointmentRS.getString("Type");

                //Date time formatter that matches the pattern from the database.
                //This is used to parse the strings from the database into date time objects in Java.
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

                //Parse the start time into a local date time and then change it to a zoned date time
                LocalDateTime startTime = LocalDateTime.parse(appointmentRS.getString("Start"), formatter);
                ZonedDateTime apptStart = ZonedDateTime.of(startTime, ZoneId.of("UTC"));

                //Parse the end time into a local date time and then change it to a zoned date time
                LocalDateTime endTime = LocalDateTime.parse(appointmentRS.getString("End"), formatter);
                ZonedDateTime apptEnd = ZonedDateTime.of(endTime, ZoneId.of("UTC"));

                //Get the name of the users current time zone.
                ZoneId tz = ZoneId.of(timeZone.timeZoneName());


                ZonedDateTime apptEndlocal = ZonedDateTime.ofInstant(apptEnd.toInstant(), tz);


                String apptCreator = appointmentRS.getString("Created_By");
                String apptUpdater = appointmentRS.getString("Last_Updated_By");
                int apptCustomerID = appointmentRS.getInt("Customer_ID");
                int apptUserID = appointmentRS.getInt("User_ID");
                int apptContactID = appointmentRS.getInt("Contact_ID");

                String apptContact = convertID.convertContact(apptContactID);

                //Create a new appointment and add it to the list.
                Appointment temp = new Appointment(apptID, apptTitle, apptDescription, apptLocation, apptType, apptStart,
                        apptEnd, apptCreator, apptUpdater, apptCustomerID, apptUserID, apptContactID, apptContact);
                apptList.add(temp);

            }
        } catch (SQLException e) {
            displayMessages.errorMsg("SQL Exception error encountered! " + e.getMessage());
        }
        //Display the list in the table

        contactTable.setItems(apptList);

        //Set up the columns for the table.
        apptIDCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        apptTitleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        apptTypeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        apptDescriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        apptStartCol.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        apptEndCol.setCellValueFactory(new PropertyValueFactory<>("endTime"));
        customerIDCol.setCellValueFactory(new PropertyValueFactory<>("customerID"));


    }

    /**
     * Displays all the appointments associated with the customer selected from the combo box.
     *
     * @param actionEvent The button click action event.
     */
    public void customerComboChange(ActionEvent actionEvent) {
        //Put the selected string from the combo into a variable
        String customerName = (String) customerCombo.getSelectionModel().getSelectedItem();

        //Sets the customer ID as zero outside the try-catch block.
        int customerID = 0;

        //Clear the appointment list to prevent stray appointments from appearing.
        apptList.clear();

        //Get the customer ID from the DB
        try {
            //Query for the Contact ID
            String sql = "SELECT Customer_ID FROM customers WHERE Customer_Name = ?";
            DBQuery.setPreparedStatement(DBConnect.getConnection(), sql); //Creating the prepared statement object
            PreparedStatement ps = DBQuery.getPreparedStatement(); //referencing the prepared statement
            ps.setString(1, customerName);
            ps.executeQuery(); //Runs the sql query
            ResultSet customerID_RS = ps.getResultSet();

            customerID_RS.next();

            customerID = customerID_RS.getInt("Customer_ID");

        } catch (SQLException e) {
            displayMessages.errorMsg("SQL Exception error encountered! " + e.getMessage());
        }


        //Get the appointment IDs that match the contact name from the DB and add those appointments to the list
        try {
            //Create a query to get all the appointments from the DB
            String sql = "SELECT * FROM appointments WHERE Customer_ID = ?";
            DBQuery.setPreparedStatement(DBConnect.getConnection(), sql); //Creating the prepared statement object
            PreparedStatement ps = DBQuery.getPreparedStatement(); //referencing the prepared statement
            ps.setString(1, String.valueOf(customerID));
            ps.executeQuery(); //Runs the sql query
            ResultSet appointmentRS = ps.getResultSet(); //Setting the results of the query to a result set


            while (appointmentRS.next()) {
                int apptID = appointmentRS.getInt("Appointment_ID");
                String apptTitle = appointmentRS.getString("Title");
                String apptDescription = appointmentRS.getString("Description");
                String apptLocation = appointmentRS.getString("Location");
                String apptType = appointmentRS.getString("Type");

                //Date time formatter that matches the pattern from the database.
                //This is used to parse the strings from the database into date time objects in Java.
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

                //Parse the start time into a local date time and then change it to a zoned date time
                LocalDateTime startTime = LocalDateTime.parse(appointmentRS.getString("Start"), formatter);
                ZonedDateTime apptStart = ZonedDateTime.of(startTime, ZoneId.of("UTC"));

                //Parse the end time into a local date time and then change it to a zoned date time
                LocalDateTime endTime = LocalDateTime.parse(appointmentRS.getString("End"), formatter);
                ZonedDateTime apptEnd = ZonedDateTime.of(endTime, ZoneId.of("UTC"));

                //Get the name of the users current time zone.
                ZoneId tz = ZoneId.of(timeZone.timeZoneName());

                ZonedDateTime apptEndlocal = ZonedDateTime.ofInstant(apptEnd.toInstant(), tz);


                String apptCreator = appointmentRS.getString("Created_By");
                String apptUpdater = appointmentRS.getString("Last_Updated_By");
                int apptCustomerID = appointmentRS.getInt("Customer_ID");
                int apptUserID = appointmentRS.getInt("User_ID");
                int apptContactID = appointmentRS.getInt("Contact_ID");

                //Convert the contact ID into a string of the cantacts name.
                String apptContact = convertID.convertContact(apptContactID);

                //Create an appointment object and add it to the list.
                Appointment temp = new Appointment(apptID, apptTitle, apptDescription, apptLocation, apptType, apptStart,
                        apptEnd, apptCreator, apptUpdater, apptCustomerID, apptUserID, apptContactID, apptContact);
                apptList.add(temp);

            }
        } catch (SQLException e) {
            displayMessages.errorMsg("SQL Exception error encountered! " + e.getMessage());
        }

        //Sets the table to display the items added to the list.
        customerTable.setItems(apptList);

        //Sets up the table columns.
        apptIDColCustomer.setCellValueFactory(new PropertyValueFactory<>("id"));
        apptTitleColCustomer.setCellValueFactory(new PropertyValueFactory<>("title"));
        apptTypeColCustomer.setCellValueFactory(new PropertyValueFactory<>("type"));
        apptDescriptionColCustomer.setCellValueFactory(new PropertyValueFactory<>("description"));
        apptStartColCustomer.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        apptEndColCustomer.setCellValueFactory(new PropertyValueFactory<>("endTime"));
        contactIDColCustomer.setCellValueFactory(new PropertyValueFactory<>("customerID"));


    }

    /**
     * Gets the information from both the month and year combo boxes when the month box is changed and sends the values
     * to the method to update the table.
     *
     * @param actionEvent The button click action event.
     */
    public void monthComboBoxChange(ActionEvent actionEvent) {

        //Get the selections from the combo boxes.
        Month selectedMonth = (Month) monthComboBox.getSelectionModel().getSelectedItem();
        Year selectedYear = (Year) yearCombo.getSelectionModel().getSelectedItem();

        //Pass the selections to the method that updates the table.

        if (selectedYear == null || selectedMonth == null) {
            updateTypeTable(Month.JANUARY, Year.now());
        } else {
            updateTypeTable(selectedMonth, selectedYear);
        }
    }

    /**
     * Gets the information from both the month and year combo boxes when the year box is changed and sends the values
     * to the method to update the table.
     *
     * @param actionEvent The button click action event.
     */
    public void yearComboChange(ActionEvent actionEvent) {
        //Get the selections from the combo boxes.
        Month selectedMonth = (Month) monthComboBox.getSelectionModel().getSelectedItem();
        Year selectedYear = (Year) yearCombo.getSelectionModel().getSelectedItem();

        //Pass the selections to the method that updates the table.
        updateTypeTable(selectedMonth, selectedYear);

    }

    /**
     * This method counts the occurrences of appointment types based on the month and year selected by the user. The month
     * and year are passed in from other methods.
     *
     * @param month The month selected by the user from the combo box.
     * @param year  The year selected by the user from the combo box.
     */
    public void updateTypeTable(Month month, Year year) {

        //Reset the counts back to 0
        apptType.resetCounts();

        //Change the table header based on the selection.
        tableHeader.setText(month + " " + year + " Appointments");

        //Loop to iterate through all the appointment types found
        for (int i = 0; i < apptTypeList.size(); i++) {

            //Set the month and year variables of the current appointment type object.
            Month apptMonth = apptTypeList.get(i).getDate().getMonth();

            Year apptYear = Year.of(apptTypeList.get(i).getDate().getYear());


            //If the passed in month and year selected from the combo boxes matches the current month and year of
            //the appointment type, continue.
            if (month.equals(apptMonth) && year.equals(apptYear)) {


                //if the type matches then set the count in the appType object to  count + 1
                if (apptTypeList.get(i).getType().equals("Project Team Meeting")) {
                    apptType.meetingTypes.get(0).setCount(apptType.meetingTypes.get(0).getCount() + 1);
                } else if (apptTypeList.get(i).getType().equals("Stakeholder Meeting")) {
                    apptType.meetingTypes.get(1).setCount(apptType.meetingTypes.get(1).getCount() + 1);
                } else if (apptTypeList.get(i).getType().equals("Change Control Meeting")) {
                    apptType.meetingTypes.get(2).setCount(apptType.meetingTypes.get(2).getCount() + 1);
                } else if (apptTypeList.get(i).getType().equals("Project Status Meeting")) {
                    apptType.meetingTypes.get(3).setCount(apptType.meetingTypes.get(3).getCount() + 1);
                } else if (apptTypeList.get(i).getType().equals("Project Review Meeting")) {
                    apptType.meetingTypes.get(4).setCount(apptType.meetingTypes.get(4).getCount() + 1);
                } else {
                    apptType.meetingTypes.get(5).setCount(apptType.meetingTypes.get(5).getCount() + 1);
                }

            }


        }
        //Refresh the table with the updated data.
        testApptView.refresh();

    }


}
