package utilities;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/***/
public class DBQuery {

    private static Statement statement;

    //Creates the statement object from the database connection passed in.
    public static void setStatement (Connection dbConnection) throws SQLException {
        statement = dbConnection.createStatement();
    }

    //Returns the statement object.
    public static Statement getStatement(){
        return statement;
    }
}
