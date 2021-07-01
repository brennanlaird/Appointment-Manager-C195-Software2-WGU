package utilities;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class convertID {

    public static String convertDivision(int divID) throws SQLException {
        //Create a query based on the string variable to get the country ID
        String sql = "SELECT * FROM first_level_divisions WHERE Division_ID = ?";
        DBQuery.setPreparedStatement(DBConnect.getConnection(), sql); //Creating the prepared statement object
        PreparedStatement ps = DBQuery.getPreparedStatement(); //referencing the prepared statement
        ps.setString(1, String.valueOf(divID));
        ps.execute(); //Runs the sql query
        ResultSet divRS = ps.getResultSet(); //Setting the results of the query to a result set

        divRS.next();
        return divRS.getString("Division");


    }

    public static String convertCountry(int divID) throws SQLException {
        String sql = "SELECT * FROM first_level_divisions WHERE Division_ID = ?";
        DBQuery.setPreparedStatement(DBConnect.getConnection(), sql); //Creating the prepared statement object
        PreparedStatement ps = DBQuery.getPreparedStatement(); //referencing the prepared statement
        ps.setInt(1, divID);
        ps.executeQuery(); //Runs the sql query
        ResultSet divRS = ps.getResultSet(); //Setting the results of the query to a result set

        divRS.next();

        int countryID = divRS.getInt("COUNTRY_ID");



        sql = "SELECT * FROM countries WHERE Country_ID = ?";
        DBQuery.setPreparedStatement(DBConnect.getConnection(), sql); //Creating the prepared statement object
        PreparedStatement ps1 = DBQuery.getPreparedStatement(); //referencing the prepared statement
        ps1.setInt(1, countryID);
        ps1.executeQuery(); //Runs the sql query
        ResultSet countryRS = ps1.getResultSet(); //Setting the results of the query to a result set

        countryRS.next();

        return countryRS.getString("Country");

    }

    public static String convertContact(int id) throws SQLException {
        //Query for the Contact ID
        String sql = "SELECT * FROM contacts WHERE Contact_ID = ?";
        DBQuery.setPreparedStatement(DBConnect.getConnection(), sql); //Creating the prepared statement object
        PreparedStatement ps = DBQuery.getPreparedStatement(); //referencing the prepared statement
        ps.setString(1, String.valueOf(id));
        ps.execute(); //Runs the sql query
        ResultSet contactID_RS = ps.getResultSet();

        contactID_RS.next();

        return contactID_RS.getString("Contact_Name");
    }


}
