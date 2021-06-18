package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import utilities.DBConnect;
import utilities.DBQuery;
import utilities.displayMessages;
import utilities.timeZone;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

/***/
public class logincontroller implements Initializable {
    public TextField usernameTextBox;
    public TextField passTextBox;
    public Button loginButton;
    public Button exitButton;
    public Label tzLabel;

    /***/
    @Override
    public void initialize(URL U, ResourceBundle resourceBundle) {
        //Get the current timezone and change the label.
        tzLabel.setText(timeZone.timeZoneName());
    }


    /***/
    public void loginButtonClick(ActionEvent actionEvent) throws SQLException {
        //get the string value from the username and password text boxes
        String userName = usernameTextBox.getText();
        String userPass = passTextBox.getText();

        //check the username against the DB

        //Get the connection and set a statement object with that connection.

        String getUserTable = "SELECT * FROM users";


        Connection c = DBConnect.getConnection();
        DBQuery.setPreparedStatement(c, getUserTable);

        PreparedStatement statement = DBQuery.getPreparedStatement();

        //Uses a SQL statement to retrieve all the data from the users table and put them in a result set

        statement.execute(getUserTable);
        ResultSet rs = statement.getResultSet();

        boolean loginSuccess = false;

        while (rs.next()) {
            String userNameDB = rs.getString("User_Name");
            String passDB = rs.getString("Password");

            //System.out.println(userNameDB + " : " + passDB);

            if (userName.equals(userNameDB) && userPass.equals(passDB)) {
                loginSuccess = true;
                break;
            }



        }

        if (!loginSuccess) {displayMessages.errorMsg("Incorrect user name or password.");}


    }


    /**
     * Exits the program  from the login screen when the exit button is pressed.
     */
    public void exitButtonClick(ActionEvent actionEvent) {
        ((Stage) (((Node) actionEvent.getSource()).getScene().getWindow())).close();
    }
}