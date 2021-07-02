package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import utilities.*;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Class to control the login form.
 */
public class logincontroller implements Initializable {
    public TextField usernameTextBox;
    public TextField passTextBox;
    public Button loginButton;
    public Button exitButton;
    public Label tzLabel;
    public Label langLabel;
    public Label usernameLabel;
    public Label passLabel;
    public Label headerLabel;

    /**
     * Initilizes the login form by checking the current time zone for display and changing labels
     * based on user language settings.
     */
    @Override
    public void initialize(URL U, ResourceBundle resourceBundle) {
        //Get the current timezone and change the label.
        tzLabel.setText(timeZone.timeZoneName());

        langLabel.setText(Locale.getDefault().getLanguage());


        //String variable to store the users language setting.
        String userLang = Locale.getDefault().getLanguage();

        //if the language is french then change the all the labels.
        if (userLang.equals("fr")) {
            exitButton.setText("Sortir");
            loginButton.setText("Connexion");
            usernameLabel.setText("Nom d'utilisateur");
            passLabel.setText("Mot de passe");
            headerLabel.setText("Planification");
        }
    }


    /**
     * Gets the user ID and password input and checks them against the database.
     */
    public void loginButtonClick(ActionEvent actionEvent) throws SQLException, IOException {
        //Get the string value from the username and password text boxes
        String userName = usernameTextBox.getText();
        String userPass = passTextBox.getText();

        //check the username against the DB

        //Get the connection and set a statement object with that connection.

        String getUserTable = "SELECT * FROM users";


        Connection c = DBConnect.getConnection(); //Creating the connection to the database
        DBQuery.setPreparedStatement(c, getUserTable); //Setting up the prepared statement

        PreparedStatement statement = DBQuery.getPreparedStatement();

        //Uses a SQL statement to retrieve all the data from the users table and put them in a result set

        statement.execute(getUserTable);
        ResultSet rs = statement.getResultSet();

        //boolean flag to change if both username and password are correct
        boolean loginSuccess = false;

        //Loop through each username and password in the result set and compare to the text entered.
        while (rs.next()) {
            String userNameDB = rs.getString("User_Name");
            String passDB = rs.getString("Password");


            //If the user name and password entered match and entry then the login is successful.
            if (userName.equals(userNameDB) && userPass.equals(passDB)) {
                loginSuccess = true; //change the flag variable to true
                break; //exit the loop
            }


        }

        if (!loginSuccess) {
//Set a string to display an english error message.
            String loginError = "Incorrect username or password.";

            if (Locale.getDefault().getLanguage().equals("fr")) {
                //If the user language is french, change the error message.
                loginError = "Identifiant ou mot de passe incorrect.";
            }
            //Display the error message.
            displayMessages.errorMsg(loginError);
        }

        if (loginSuccess) {
            //If the login was successful, save the username for later use.
            userInfo.saveUsername = userName;

            //If login was successful, load the main screen
            returnHome.loadHome(actionEvent);
        }

    }


    /**
     * Exits the program  from the login screen when the exit button is pressed.
     */
    public void exitButtonClick(ActionEvent actionEvent) {
        ((Stage) (((Node) actionEvent.getSource()).getScene().getWindow())).close();
    }
}
