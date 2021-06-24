package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import objects.Customer;
import utilities.DBConnect;
import utilities.DBQuery;
import utilities.displayMessages;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;

public class homeController implements Initializable {

    public TableView customerTable;
    public TableColumn customerIDCol;
    public TableColumn customerNameCol;

    public Button addCustomerButton;
    public Button updateCustomerButton;
    public Button deleteCustomerButton;

    @Override
    /***/
    public void initialize(URL url, ResourceBundle resourceBundle) {

        ObservableList<Customer> allCustomers = FXCollections.observableArrayList();

        try {
            //Create a query based on the string variable to get the country ID
            String sql = "SELECT * FROM customers";
            DBQuery.setPreparedStatement(DBConnect.getConnection(), sql); //Creating the prepared statement object
            PreparedStatement ps = DBQuery.getPreparedStatement(); //referencing the prepared statement
            ps.executeQuery(); //Runs the sql query
            ResultSet customerRS = ps.getResultSet(); //Setting the results of the query to a result set

            while (customerRS.next()){
                int customerID = customerRS.getInt("Customer_ID");
                String customerName = customerRS.getString("Customer_Name");
                String customerAddress = customerRS.getString("Address");
                String customerPostCode = customerRS.getString("Postal_Code");
                String customerPhone = customerRS.getString("Phone");
                String customerCreator = customerRS.getString("Created_By");
                String customerUpdater = customerRS.getString("Last_Updated_By");
                Customer temp = new Customer(customerID, customerName, customerAddress, customerPostCode, customerPhone, customerCreator, customerUpdater);
                allCustomers.add(temp);
            }

        } catch (Exception SQLException) {
            System.out.println("Database Error");
        }

        //divIDRS.next(); //Moves to the first result in the result set
        customerTable.setItems(allCustomers);

        customerIDCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        customerNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
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
    /**Deletes the customer selected from the table after user confirmation and then adjusts the list of customers to display.*/
    public void deleteCustomerButtonClick(ActionEvent actionEvent) throws IOException {


    }

    /**
     * Exits the program  from the main screen when the exit button is pressed.
     */
    public void exitButtonClick(ActionEvent actionEvent) {
        ((Stage) (((Node) actionEvent.getSource()).getScene().getWindow())).close();
    }

    public void updateCustomerButtonClick(ActionEvent actionEvent) {
        //The try-catch block is used to avoid a null pointed error if the button is pushed with nothing selected.
        System.out.println("Debug me!");
        try {
            //Initializes the Modify Part controller
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/modCustomer.fxml"));
            Parent root = loader.load();
            modCustomerController mod = loader.getController();

            //Sends the selected part to the modify part controller
            mod.receiveCustomer((Customer) customerTable.getSelectionModel().getSelectedItem());

            //Shows the modify part controller after passing the data from the selected part
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setTitle("Modify Part");
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            //If nothing was selected when this button is pushed, an error is displayed.

            displayMessages.errorMsg("No customer was selected. Please select one to modify.");
        }

    }
}
