import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import utilities.DBConnect;
import utilities.DBQuery;
import utilities.timeZone;

import java.sql.SQLException;
import java.sql.Statement;

public class Main extends Application {

    /***/
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




        //Inserting country data as a test.
/*
        //Create the statement object
        DBQuery.setPreparedStatement(DBConnect.getConnection(), );

        //Get the statement object
        Statement statement = DBQuery.getStatement();

        //SQL statement as a string
        String sql = "INSERT INTO countries(Country, Create_Date, Created_By, Last_Updated_By) VALUES('Wakanda', '2021-06-15 11:00:00', 'Tchaka', 'Tchalla')";

        //Execute the SQL statement
        statement.execute(sql);

        if (statement.getUpdateCount()>0){
            System.out.println("Inserted " + statement.getUpdateCount() + " thing.");
        } else {
            System.out.println("Nothing changed.");
        }
*/

        launch(args);

        DBConnect.closeConnection();
    }
}
