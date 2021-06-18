package utilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

/***/
public class DBQuery {

    private static PreparedStatement statement;

    //Creates the statement object from the database connection passed in.
    public static void setPreparedStatement (Connection dbConnection, String sql) throws SQLException {
        statement = dbConnection.prepareStatement(sql);
    }

    //Returns the statement object.
    public static PreparedStatement getPreparedStatement(){
        return statement;
    }
}
