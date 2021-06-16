package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/***/
public class logincontroller implements Initializable {
    public TextField usernameTextBox;
    public TextField passTextBox;
    public Button loginButton;
    public Button exitButton;

    /***/
    @Override
    public void initialize (URL U, ResourceBundle resourceBundle){

    }




    /***/
    public void loginButtonClick(ActionEvent actionEvent) {
        //get the string value from the username text box
        String userName = usernameTextBox.getText();

        //check the username against the DB


    }


    /**Exits the program  from the login screen when the exit button is pressed.*/
    public void exitButtonClick(ActionEvent actionEvent) {
        ((Stage) (((Node) actionEvent.getSource()).getScene().getWindow())).close();
    }
}
