package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import objects.Appointment;
import utilities.*;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class reportsController implements Initializable {
    public ToggleGroup reportType;
    public AnchorPane contactPane;

    public Button returnToMain;
    public RadioButton apptTypeButton;
    public RadioButton contactScheduleButton;
    public TableView contactTable;
    public TableColumn contactNameCol;
    public TableColumn apptIDCol;
    public TableColumn apptTitleCol;
    public TableColumn apptTypeCol;
    public TableColumn apptDescriptionCol;
    public TableColumn apptStartCol;
    public TableColumn apptEndCol;
    public TableColumn customerIDCol;
    public ComboBox contactNameCombo;
    public AnchorPane customerPane;
    public ComboBox customerCombo;
    public TableColumn apptIDColCustomer;
    public TableColumn apptTitleColCustomer;
    public TableColumn apptTypeColCustomer;
    public TableColumn apptDescriptionColCustomer;
    public TableColumn apptStartColCustomer;
    public TableColumn apptEndColCustomer;
    public TableColumn contactIDColCustomer;
    public RadioButton customerScheduleButton;
    public TableView customerTable;
    public AnchorPane apptTypePane;
    public ComboBox monthComboBox;
    public Label testLabel;
    public TableView currentMonthTable;
    public TableColumn apptTypeCol1;


    //Setup the observable lists for the combo boxes.
    ObservableList<String> contactList = FXCollections.observableArrayList();
    ObservableList<String> customerList = FXCollections.observableArrayList();
    ObservableList<Appointment> apptList = FXCollections.observableArrayList();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


        //Hide elements. These are only visible when the corresponding radio button is selected.
        contactPane.setVisible(false);
        customerPane.setVisible(false);
        apptTypePane.setVisible(false);



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

            //Sets the items to display in the country combo box
            contactNameCombo.setItems(contactList);


        } catch (Exception e) {
            System.out.println("There was a problem retrieving contacts.");

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


        } catch (Exception e) {
            System.out.println("There was a problem adding the customer data.");

        }



    }

    /**
     * Calls the return to home method to load the main screen.
     * @param actionEvent The button click action event.
     */
    public void returnToMainClick(ActionEvent actionEvent) throws IOException {
        returnHome.loadHome(actionEvent);
    }


    public void apptTypeButtonSelect(ActionEvent actionEvent) {
        contactPane.setVisible(false);

        customerPane.setVisible(false);

        apptTypePane.setVisible(true);


        LocalDate rightNow = LocalDate.now();
        testLabel.setText(String.valueOf(rightNow.getMonth()) + " " + rightNow.getYear());


        currentMonthTable.setItems(meetingTypes.getApptTypes());

        apptTypeCol1.setCellValueFactory(new PropertyValueFactory<>("apptType"));




    }

    public void contactScheduleButton(ActionEvent actionEvent) {
        contactPane.setVisible(true);
        contactNameCombo.setVisible(true);
        contactTable.setVisible(true);

        customerPane.setVisible(false);
        apptTypePane.setVisible(false);
    }

    public void customerScheduleButtonSelect(ActionEvent actionEvent) {
        customerPane.setVisible(true);
        contactPane.setVisible(false);
        apptTypePane.setVisible(false);
    }

    public void contactNameComboChange(ActionEvent actionEvent) {
        //Put the selected string from the combo into a variable
        String contactName = (String) contactNameCombo.getSelectionModel().getSelectedItem();

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
            System.out.println("Error getting Contact ID");
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

            //Declares a local date time as null to store the minimum start time.
            LocalDateTime minStart = null;
            //Declares a null appointment object to store the information about the next appointment.
            Appointment nextAppt = null;

            while (appointmentRS.next()) {
                int apptID = appointmentRS.getInt("Appointment_ID");
                String apptTitle = appointmentRS.getString("Title");
                String apptDescription = appointmentRS.getString("Description");
                String apptLocation = appointmentRS.getString("Location");
                String apptType = appointmentRS.getString("Type");


                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

                LocalDateTime startTime = LocalDateTime.parse(appointmentRS.getString("Start"), formatter);
                ZonedDateTime apptStart = ZonedDateTime.of(startTime, ZoneId.of("UTC"));

                LocalDateTime endTime = LocalDateTime.parse(appointmentRS.getString("End"), formatter);
                ZonedDateTime apptEnd = ZonedDateTime.of(endTime, ZoneId.of("UTC"));


                ZoneId tz = ZoneId.of(timeZone.timeZoneName());

                ZonedDateTime apptEndlocal = ZonedDateTime.ofInstant(apptEnd.toInstant(), tz);


                //System.out.println(tz);
                //System.out.println(apptEnd);

                //apptEnd.ofInstant(apptEnd.toInstant(),tz);

                //System.out.println("After conversion: " + apptEndlocal);

                String apptCreator = appointmentRS.getString("Created_By");
                String apptUpdater = appointmentRS.getString("Last_Updated_By");
                int apptCustomerID = appointmentRS.getInt("Customer_ID");
                int apptUserID = appointmentRS.getInt("User_ID");
                int apptContactID = appointmentRS.getInt("Contact_ID");

                String apptContact = convertID.convertContact(apptContactID);

                Appointment temp = new Appointment(apptID, apptTitle, apptDescription, apptLocation, apptType, apptStart,
                        apptEnd, apptCreator, apptUpdater, apptCustomerID, apptUserID, apptContactID, apptContact);
                apptList.add(temp);

            }
        }
            catch (SQLException e){
                System.out.println("Error getting appointments");
            }
        //Display the list in the table

        contactTable.setItems(apptList);

        //contactNameCol.setCellValueFactory(new PropertyValueFactory<>("contactID"));
        apptIDCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        apptTitleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        apptTypeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        apptDescriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        apptStartCol.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        apptEndCol.setCellValueFactory(new PropertyValueFactory<>("endTime"));
        customerIDCol.setCellValueFactory(new PropertyValueFactory<>("customerID"));




    }

    public void customerComboChange(ActionEvent actionEvent) {
        //Put the selected string from the combo into a variable
        String customerName = (String) customerCombo.getSelectionModel().getSelectedItem();

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
            System.out.println("Error getting Customer ID");
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

            //Declares a local date time as null to store the minimum start time.
            LocalDateTime minStart = null;
            //Declares a null appointment object to store the information about the next appointment.
            Appointment nextAppt = null;

            while (appointmentRS.next()) {
                int apptID = appointmentRS.getInt("Appointment_ID");
                String apptTitle = appointmentRS.getString("Title");
                String apptDescription = appointmentRS.getString("Description");
                String apptLocation = appointmentRS.getString("Location");
                String apptType = appointmentRS.getString("Type");


                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

                LocalDateTime startTime = LocalDateTime.parse(appointmentRS.getString("Start"), formatter);
                ZonedDateTime apptStart = ZonedDateTime.of(startTime, ZoneId.of("UTC"));

                LocalDateTime endTime = LocalDateTime.parse(appointmentRS.getString("End"), formatter);
                ZonedDateTime apptEnd = ZonedDateTime.of(endTime, ZoneId.of("UTC"));


                ZoneId tz = ZoneId.of(timeZone.timeZoneName());

                ZonedDateTime apptEndlocal = ZonedDateTime.ofInstant(apptEnd.toInstant(), tz);


                //System.out.println(tz);
                //System.out.println(apptEnd);

                //apptEnd.ofInstant(apptEnd.toInstant(),tz);

                //System.out.println("After conversion: " + apptEndlocal);

                String apptCreator = appointmentRS.getString("Created_By");
                String apptUpdater = appointmentRS.getString("Last_Updated_By");
                int apptCustomerID = appointmentRS.getInt("Customer_ID");
                int apptUserID = appointmentRS.getInt("User_ID");
                int apptContactID = appointmentRS.getInt("Contact_ID");

                String apptContact = convertID.convertContact(apptContactID);

                Appointment temp = new Appointment(apptID, apptTitle, apptDescription, apptLocation, apptType, apptStart,
                        apptEnd, apptCreator, apptUpdater, apptCustomerID, apptUserID, apptContactID, apptContact);
                apptList.add(temp);

            }
        }
        catch (SQLException e){
            System.out.println("Error getting appointments");
        }


        customerTable.setItems(apptList);

        //contactNameCol.setCellValueFactory(new PropertyValueFactory<>("contactID"));
        apptIDColCustomer.setCellValueFactory(new PropertyValueFactory<>("id"));
        apptTitleColCustomer.setCellValueFactory(new PropertyValueFactory<>("title"));
        apptTypeColCustomer.setCellValueFactory(new PropertyValueFactory<>("type"));
        apptDescriptionColCustomer.setCellValueFactory(new PropertyValueFactory<>("description"));
        apptStartColCustomer.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        apptEndColCustomer.setCellValueFactory(new PropertyValueFactory<>("endTime"));
        contactIDColCustomer.setCellValueFactory(new PropertyValueFactory<>("customerID"));




    }


    public void monthComboBoxChange(ActionEvent actionEvent) {
    }
}
