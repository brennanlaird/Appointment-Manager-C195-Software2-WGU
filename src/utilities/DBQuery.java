package utilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


/**
 * This class Creates and returns prepared statement objects.
 */
public class DBQuery {

    private static PreparedStatement statement;


    /**
     * Creates the statement object from the database connection passed in.
     * @param dbConnection The connection to the database.
     * @param sql The string of the sql query that will be executed.
     */
    public static void setPreparedStatement (Connection dbConnection, String sql) {
        //Sets up a prepared statement.
        try{
            statement = dbConnection.prepareStatement(sql);
        }
        catch (SQLException sqlException){
            //Display a message with the error code if caught.
            displayMessages.errorMsg("Database error " + sqlException.getErrorCode());
        }

    }

    /**
     * Returns the statement object.
     * @return The statement object.
     */
    public static PreparedStatement getPreparedStatement(){
        return statement;
    }



}
