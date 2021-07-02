package utilities;

import java.sql.*;

/**This class is used to manage the connection to the database. The initial variables are static and used to build the
 * connection string. */
public class DBConnect {
    //JDBC URL Parts
    //Protocol for DB connection
    private static final String protocol = "jdbc";

    //Vendor for DB Connection
    private static final String vendor = ":mysql:";

    //IP Address for DB Connection
    private static final String ipaddress = "//wgudb.ucertify.com:3306/";

    //Database Name for DB Connection
    private static final String dbName = "WJ08AMo";

    //Concatenate the individual parts to form the full jdbc connection URL
    private static final String jdbcURL = protocol + vendor + ipaddress + dbName;
    //Can comment out the line which best deals with the time zone issue. Currently using 8.0.25
    //private static final String jdbcURL = protocol + vendor + ipaddress + dbName + "?connectionTimeZone=SERVER";


    //Driver Interface Reference
    private static final String jdbcDriver = "com.mysql.jdbc.Driver";
    private static  Connection dbConnect = null;

    //DB Username
    private static final String dbUser = "U08AMo";

    //DB Password
    private static final String dbPass = "53689231527";

    /**
     * Opens a connection to the database.
     * @return The connection to the database.
     */
    public static Connection startConnection()  {
        //Try catch will catch a sql error and display an error message with the error code.
        try {
            //Sets up the connection object using the static variables defined in the class.
            dbConnect = DriverManager.getConnection(jdbcURL, dbUser, dbPass);
        }
        catch (SQLException sqlException) {
            displayMessages.errorMsg("Database error " + sqlException.getErrorCode());
        }
        //System.out.println("Connection successful.");
        return dbConnect;
    }

    /**
     * Returns the open connection so the connection does not have to be opened each time.
     * @return The connection to the database.
     */
    public static Connection getConnection(){
        return dbConnect;
    }

    /**
     * Closes the connection to the database.
     */
    public static void closeConnection()  {
        try {
            dbConnect.close();
        }
        catch (Exception e){
            //Ignore the catch, this method is only called on program exit.
        }

        //System.out.println("Connection closed.");
    }


}
