import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/***/
public class DBConnect {
    //JDBC URL Parts
    //Protocol for DB connection
    private static final String protocol = "jdbc";

    //Vendor for DB Connection
    private static final String vendor = ":mysql:";

    //IP Address for DB Connection
    private static final String ipaddress = "//wgudb.ucertify.com/";

    //Database Name for DB Connection
    private static final String dbName = "WJ08AMo";

    //Concatenate the individual parts to form the full jdbc connection URL
    private static final String jdbcURL = protocol + vendor + ipaddress + dbName;

    //Driver Interface Reference
    private static final String jdbcDriver = "java.sql.Connection";
    private static  Connection dbConnect = null;

    //DB Username
    private static final String dbUser = "U08AMo";

    //DB Password
    private static final String dbPass = "53689231527";


    public static Connection startConnection() throws SQLException, ClassNotFoundException {
        Class.forName(jdbcDriver);
        dbConnect = DriverManager.getConnection(jdbcURL, dbUser, dbPass);
        System.out.println("This worked!");
        return dbConnect;
    }


}
