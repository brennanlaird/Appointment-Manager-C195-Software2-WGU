package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import objects.Appointment;
import objects.Customer;
import utilities.*;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ResourceBundle;

/**
 * The controller for the main screen of the application. Contains controls to access all other forms and a table to
 * view and filter upcoming appoitnments.
 */
public class homeController implements Initializable {

    public TableView customerTable;
    public TableColumn customerIDCol;
    public TableColumn customerNameCol;
    public TableColumn customerCountryCol;
    public TableColumn customerRegionCol;


    public Button addCustomerButton;
    public Button updateCustomerButton;
    public Button deleteCustomerButton;

    public TableView apptTable;
    public TableColumn apptIDCol;
    public TableColumn apptTitleCol;
    public TableColumn apptDescriptionCol;
    public TableColumn apptLocationCol;
    public TableColumn apptContactCol;
    public TableColumn apptTypeCol;
    public TableColumn apptStartCol;
    public TableColumn apptEndCol;
    public TableColumn apptCustomerIDCol;

    public Button addAppointmentButton;
    public Button updateAppointmentButton;
    public Button deleteAppointment;
    public Button exitButton;
    public Button reportsButton;

    public RadioButton viewAllRadio;
    public RadioButton viewWeekRadio;
    public RadioButton viewMonthRadio;
    public ToggleGroup apptFilter;



    //Setting up observable lists for displaying data.
    ObservableList<Customer> allCustomers = FXCollections.observableArrayList();
    ObservableList<Appointment> allAppointments = FXCollections.observableArrayList();


    @Override
    /***/
    public void initialize(URL url, ResourceBundle resourceBundle) {

        //Retrieves all the customers from the DB and adds them to the observable list.
        try {
            //Create a query to get the all the customers
            String sql = "SELECT * FROM customers";
            DBQuery.setPreparedStatement(DBConnect.getConnection(), sql); //Creating the prepared statement object
            PreparedStatement ps = DBQuery.getPreparedStatement(); //referencing the prepared statement
            ps.executeQuery(); //Runs the sql query
            ResultSet customerRS = ps.getResultSet(); //Setting the results of the query to a result set

            //Loops through each customer in the result set and creates a customer object.
            while (customerRS.next()) {
                int customerID = customerRS.getInt("Customer_ID");
                String customerName = customerRS.getString("Customer_Name");
                String customerAddress = customerRS.getString("Address");
                String customerPostCode = customerRS.getString("Postal_Code");
                String customerPhone = customerRS.getString("Phone");
                String customerCreator = customerRS.getString("Created_By");
                String customerUpdater = customerRS.getString("Last_Updated_By");
                int customerDivID = customerRS.getInt("Division_ID");
                String customerDiv = convertID.convertDivision(customerDivID);
                String customerCountry = convertID.convertCountry(customerDivID);
                Customer temp = new Customer(customerID, customerName, customerAddress, customerPostCode, customerPhone, customerCreator, customerUpdater, customerDivID, customerDiv, customerCountry);
                //Add the temporary customer object to the observable list.
                allCustomers.add(temp);

            }

        } catch (SQLException e) {
            displayMessages.errorMsg("Error retrieving data. Please check the database connection.");
            //System.out.println("Database Error from the customers.");
        }

        //Retrieves all the appointments from the database and adds them to the list to display.
        try {
            //Create a query to get all the appointments from the DB
            String sql = "SELECT * FROM appointments";
            DBQuery.setPreparedStatement(DBConnect.getConnection(), sql); //Creating the prepared statement object
            PreparedStatement ps = DBQuery.getPreparedStatement(); //referencing the prepared statement
            ps.executeQuery(); //Runs the sql query
            ResultSet appointmentRS = ps.getResultSet(); //Setting the results of the query to a result set

            //Declares a local date time as null to store the minimum start time.
            LocalDateTime minStart = null;
            //Declares a null appointment object to store the information about the next appointment.
            Appointment nextAppt = null;

            //Loops through each appointment in the result set and creates a new appointment object for each one.
            while (appointmentRS.next()) {
                int apptID = appointmentRS.getInt("Appointment_ID");
                String apptTitle = appointmentRS.getString("Title");
                String apptDescription = appointmentRS.getString("Description");
                String apptLocation = appointmentRS.getString("Location");
                String apptType = appointmentRS.getString("Type");

                //Create a date time formatter to change the UTC time from the DB
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

                //Get the start time from the result set by parsing the string based on the formatter.
                LocalDateTime startTime = LocalDateTime.parse(appointmentRS.getString("Start"), formatter);

                ZonedDateTime apptStart = ZonedDateTime.of(startTime, ZoneId.of("UTC"));

                //Create variables to store the appointment end time as a local date time based on the formatter and
                //as a zoned date time.
                LocalDateTime endTime = LocalDateTime.parse(appointmentRS.getString("End"), formatter);
                ZonedDateTime apptEnd = ZonedDateTime.of(endTime, ZoneId.of("UTC"));

                //Call the method to get and store the time zone ID of the current user
                ZoneId tz = ZoneId.of(timeZone.timeZoneName());

                //ZonedDateTime apptEndlocal = ZonedDateTime.ofInstant(apptEnd.toInstant(), tz);


                String apptCreator = appointmentRS.getString("Created_By");
                String apptUpdater = appointmentRS.getString("Last_Updated_By");
                int apptCustomerID = appointmentRS.getInt("Customer_ID");
                int apptUserID = appointmentRS.getInt("User_ID");
                int apptContactID = appointmentRS.getInt("Contact_ID");

                String apptContact = convertID.convertContact(apptContactID);

                Appointment temp = new Appointment(apptID, apptTitle, apptDescription, apptLocation, apptType, apptStart,
                        apptEnd, apptCreator, apptUpdater, apptCustomerID, apptUserID, apptContactID, apptContact);
                allAppointments.add(temp);

                //Store the minimum start time that is next and in the future.
                if (minStart == null && startTime.isAfter(LocalDateTime.now())) {
                    minStart = startTime;
                    nextAppt = temp;
                } else if (minStart !=  null && startTime.isBefore(minStart) && startTime.isAfter(LocalDateTime.now())) {
                    minStart = startTime;
                    nextAppt = temp;
                }

            }

            if (!loginFlag.loginCheck) {
                displayMessages.apptUpcoming(nextAppt.getId(), minStart);
                loginFlag.loginCheck = true;
            }



        } catch (Exception SQLException) {
            System.out.println("Database Error from the appointments.");
        }


        customerTable.setItems(allCustomers);

        customerIDCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        customerNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        customerCountryCol.setCellValueFactory(new PropertyValueFactory<>("country"));
        customerRegionCol.setCellValueFactory(new PropertyValueFactory<>("division"));

        apptTable.setItems(allAppointments);

        apptIDCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        apptTitleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        apptDescriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        apptLocationCol.setCellValueFactory(new PropertyValueFactory<>("location"));
        apptContactCol.setCellValueFactory(new PropertyValueFactory<>("contact"));
        apptTypeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        apptStartCol.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        apptEndCol.setCellValueFactory(new PropertyValueFactory<>("endTime"));
        apptCustomerIDCol.setCellValueFactory(new PropertyValueFactory<>("customerID"));


    }


    /**
     * Loads the add customer form
     */
    public void addCustomerButtonClick(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(addCustomer.class.getResource("/views/addCustomer.fxml"));
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setTitle("DFC - Add Customer");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Deletes the customer selected from the table after user confirmation and then adjusts the list of customers to display.
     */
    public void deleteCustomerButtonClick(ActionEvent actionEvent) throws IOException, SQLException {

        Customer deleteCheck = (Customer) customerTable.getSelectionModel().getSelectedItem();


        String sql = "SELECT * FROM appointments WHERE Customer_ID = ?";
        DBQuery.setPreparedStatement(DBConnect.getConnection(), sql); //Creating the prepared statement object
        PreparedStatement ps = DBQuery.getPreparedStatement(); //referencing the prepared statement
        ps.setString(1, String.valueOf(deleteCheck.getId())); //Getting the string representation of the id
        ps.execute(); //Runs the sql query
        ResultSet customerID_RS = ps.getResultSet();

        if (customerID_RS.next() == true) {
            displayMessages.errorMsg("This customer has active appointments. Please delete those prior to deleting the customer.");

        } else {


            if (deleteCheck == null) {

                displayMessages.errorMsg("No customer was selected. Please select one to delete.");

            } else {
                //Sets a dialog to ensure the user wants to delete
                var deleteConfirm = new Alert(Alert.AlertType.CONFIRMATION, "", ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
                deleteConfirm.setTitle("Confirm Delete");
                deleteConfirm.setContentText("Are you sure you want to delete the selected item?");
                deleteConfirm.showAndWait();
                //If the user presses yes, the customer is deleted from the customer table
                if (deleteConfirm.getResult() == ButtonType.YES) {

                    //Run a delete query

                    sql = "DELETE FROM customers WHERE Customer_ID = ?";
                    DBQuery.setPreparedStatement(DBConnect.getConnection(), sql); //Creating the prepared statement object
                    PreparedStatement ps1 = DBQuery.getPreparedStatement(); //referencing the prepared statement
                    ps1.setString(1, String.valueOf(deleteCheck.getId())); //Getting the string representation of the id
                    ps1.executeUpdate(); //Runs the sql query


                    //Update the table view
                    allCustomers.remove(deleteCheck);

                    //Show a dialog that the delete was successful.
                    displayMessages.infoMsg("Customer record deleted successfully.");

                }
            }
        }

    }

    /**
     * Exits the program  from the main screen when the exit button is pressed.
     */
    public void exitButtonClick(ActionEvent actionEvent) {
        ((Stage) (((Node) actionEvent.getSource()).getScene().getWindow())).close();
    }

    /***/
    public void updateCustomerButtonClick(ActionEvent actionEvent) {
        //The try-catch block is used to avoid a null pointed error if the button is pushed with nothing selected.

        try {
            //Initializes the Modify Customer controller
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/modCustomer.fxml"));
            Parent root = loader.load();
            modCustomerController mod = loader.getController();

            //Sends the selected customer to the modify customer controller
            mod.receiveCustomer((Customer) customerTable.getSelectionModel().getSelectedItem());

            //Shows the modify customer controller after passing the data from the selected customer
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setTitle("Modify Customer");
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            //If nothing was selected when this button is pushed, an error is displayed.

            displayMessages.errorMsg("No customer was selected. Please select one to modify.");
        }

    }

    /**
     * Loads the add appointment form when the add appointment button has been clicked.
     */
    public void addAppointmentButtonClick(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(addCustomer.class.getResource("/views/addAppointment.fxml"));
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setTitle("DFC - Add Appointment");
        stage.setScene(scene);
        stage.show();
    }

    /***/
    public void updateAppointmentButtonClick(ActionEvent actionEvent) {
        //The try-catch block is used to avoid a null pointed error if the button is pushed with nothing selected.

        try {
            //Initializes the Modify Appointment controller
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/modAppointment.fxml"));
            Parent root = loader.load();
            modAppointment mod = loader.getController();

            //Sends the selected part to the modify part controller
            mod.receiveAppt((Appointment) apptTable.getSelectionModel().getSelectedItem());

            //Shows the modify Appointment controller after passing the data from the selected Appointment
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setTitle("Modify Appointment");
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            //If nothing was selected when this button is pushed, an error is displayed.

            displayMessages.errorMsg("No appointment was selected. Please select one to modify.");
        }

    }


    /**
     * Deletes the selected appointment from the database and updates the display to remove it from view.
     */
    public void deleteAppointmentClick(ActionEvent actionEvent) throws SQLException {


        Appointment deleteCheck = (Appointment) apptTable.getSelectionModel().getSelectedItem();

        if (deleteCheck == null) {

            displayMessages.errorMsg("No appointment was selected. Please select one to delete.");

        } else {
            //Sets a dialog to ensure the user wants to delete
            var deleteConfirm = new Alert(Alert.AlertType.CONFIRMATION, "", ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
            deleteConfirm.setTitle("Confirm Delete");
            deleteConfirm.setContentText("Are you sure you want to delete the selected item?");
            deleteConfirm.showAndWait();
            //If the user presses yes, the part is deleted from the part table
            if (deleteConfirm.getResult() == ButtonType.YES) {

                //Run a delete query

                String sql = "DELETE FROM appointments WHERE Appointment_ID = ?";
                DBQuery.setPreparedStatement(DBConnect.getConnection(), sql); //Creating the prepared statement object
                PreparedStatement ps = DBQuery.getPreparedStatement(); //referencing the prepared statement
                ps.setString(1, String.valueOf(deleteCheck.getId())); //Getting the string representation of the id
                ps.executeUpdate(); //Runs the sql query


                //Update the table view
                allAppointments.remove(deleteCheck);
            }
            //Show a dialog that the delete was successful.
            displayMessages.apptCanceled(deleteCheck.getId());
        }
    }


    public void viewAllRadioClick(ActionEvent actionEvent) {
        apptTable.setItems(allAppointments);
    }

    public void viewWeekRadioClick(ActionEvent actionEvent) {
        LocalDate rightNow = LocalDate.now();
        LocalDate oneWeek = LocalDate.now().plus(7, ChronoUnit.DAYS);
        FilteredList<Appointment> weeklyAppt = new FilteredList<>(allAppointments, i -> (i.getEndTime().toLocalDate().compareTo(oneWeek)) < 0 && (rightNow.compareTo(i.getEndTime().toLocalDate()) < 0));
        apptTable.setItems(weeklyAppt);

    }

    public void viewMonthRadioClick(ActionEvent actionEvent) {
        LocalDate rightNow = LocalDate.now();
        LocalDate oneMonth = LocalDate.now().plus(31, ChronoUnit.DAYS);
        FilteredList<Appointment> monthlyAppt = new FilteredList<>(allAppointments, i -> (i.getEndTime().toLocalDate().compareTo(oneMonth)) < 0 && (rightNow.compareTo(i.getEndTime().toLocalDate()) < 0));
        apptTable.setItems(monthlyAppt);
    }

    /**
     *
     * @param actionEvent
     */
    public void reportsButtonClick(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(addCustomer.class.getResource("/views/reports.fxml"));
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setTitle("DFC - Reports");
        stage.setScene(scene);
        stage.show();
    }
}
