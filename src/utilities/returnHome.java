package utilities;

import controllers.addCustomer;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * A class to load the home screen of the app. This enables a single line to be written to load the home screen.
 */
public class returnHome {
    /**
     * This method takes in an action event and loads the home screen of the app.
     * @param actionEvent The event which triggers the calling of the method, usually a button push.
     */
    public static void loadHome(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(addCustomer.class.getResource("/views/homeScreen.fxml"));
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setTitle("DFC - Scheduler");
        stage.setScene(scene);
        stage.show();
    }
}
