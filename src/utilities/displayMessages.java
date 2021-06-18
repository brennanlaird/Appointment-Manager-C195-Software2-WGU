package utilities;

import javafx.scene.control.Alert;

/**This class is used to display different types of messages based on the users input. Parameters can be passed in as
 * so different messages can be displayed using the same code.*/
public class displayMessages {

    /**Code to display an error message when an input error is found. Message is passed from calling code so
     * this method can be used to display multiple types of errors.
     * @param msg The error message string to display.*/
    public static void errorMsg(String msg) {
        var alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
