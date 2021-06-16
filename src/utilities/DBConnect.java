package utilities;

import java.sql.*;

/***/
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

    //Prints the URL as a test
    public static void printURL() {
        System.out.println(jdbcURL);
    }

    public static Connection startConnection() throws SQLException {
        //TO DO - Add try catch blocks to deal with errors instead of throwing the SQL Exception.
        dbConnect = DriverManager.getConnection(jdbcURL, dbUser, dbPass);
        //Statement statement = dbConnect.createStatement();
        //ResultSet testResult = statement.executeQuery("select * from countries");
        System.out.println("Connection successful.");
        return dbConnect;
    }

    //Returns the open connection. This way we do not have to open the connection each time.
    public static Connection getConnection(){
        return dbConnect;
    }

    public static void closeConnection() throws SQLException {
        //TO DO - Try-Catch to handle the SQL Exception
        dbConnect.close();
        System.out.println("Connection closed.");
    }


}
