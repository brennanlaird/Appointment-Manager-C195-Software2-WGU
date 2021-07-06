import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import utilities.DBConnect;
import utilities.DBQuery;
import utilities.meetingTypes;
import utilities.timeZone;

import java.sql.SQLException;
import java.sql.Statement;

public class Main extends Application {

    /**Shows the main screen of the application upon loading.*/
    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("/Views/login.fxml"));
        primaryStage.setTitle("Login");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    /**The main class the launches the program. This will establish the connection to the mysql database.*/
    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        //Starts the connection to the mysql DB
        DBConnect.startConnection();

        meetingTypes.populateAppointmentTypes();


        launch(args);

        //Closes the connection to the database.
        DBConnect.closeConnection();
    }
}
