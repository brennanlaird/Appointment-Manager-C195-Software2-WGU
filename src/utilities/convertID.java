package utilities;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * The convert ID utility is used to convert the ID's passed from calling methods to determine the associated strings.
 * The methods are provided an ID from an object and uses prepared statements to lookup the associated string values.
 */
public class convertID {
    /**
     * This converts the first level division ID in to the string name of the associated division.
     *
     * @param divID The ID number of a first level division.
     * @return The name of the first level division from the database.
     */
    public static String convertDivision(int divID) {
        //Create a query based on the first level division ID variable to get the division name.

        //Try-Catch blocks used to deal with a potential SQL error.
        try {
            String sql = "SELECT * FROM first_level_divisions WHERE Division_ID = ?";
            DBQuery.setPreparedStatement(DBConnect.getConnection(), sql); //Creating the prepared statement object
            PreparedStatement ps = DBQuery.getPreparedStatement(); //referencing the prepared statement
            ps.setString(1, String.valueOf(divID));
            ps.execute(); //Runs the sql query
            ResultSet divRS = ps.getResultSet(); //Setting the results of the query to a result set

            //Moves to the first item found in the result set.
            divRS.next();

            //Returns the string value found by the query.
            return divRS.getString("Division");
        } catch (SQLException sqlException) {
            //Displays an error message if a SQL exception error occurs.
            displayMessages.errorMsg("Database error. Please try again.");
        }

        //Returns a blank string. This will return if the try block misses.
        return "";

    }

    /**
     * The convert country uses two prepared statements to return the country id after being passes the first level division ID.
     *
     * @param divID The ID number of a first level division.
     * @return The name of the country from the database as a string.
     */
    public static String convertCountry(int divID){
        //First a query is run on the first level division ID to get the country ID.

        try {
            String sql = "SELECT * FROM first_level_divisions WHERE Division_ID = ?";
            DBQuery.setPreparedStatement(DBConnect.getConnection(), sql); //Creating the prepared statement object
            PreparedStatement ps = DBQuery.getPreparedStatement(); //referencing the prepared statement
            ps.setInt(1, divID);
            ps.executeQuery(); //Runs the sql query
            ResultSet divRS = ps.getResultSet(); //Setting the results of the query to a result set
            //Moves to the first value in the result set.
            divRS.next();
            //Assigns the result of the query to an integer variable to be used in the next query.
            int countryID = divRS.getInt("COUNTRY_ID");

            //Second query is run to get the country name based on the country ID returned from the first query.
            sql = "SELECT * FROM countries WHERE Country_ID = ?";
            DBQuery.setPreparedStatement(DBConnect.getConnection(), sql); //Creating the prepared statement object
            PreparedStatement ps1 = DBQuery.getPreparedStatement(); //referencing the prepared statement
            ps1.setInt(1, countryID);
            ps1.executeQuery(); //Runs the sql query
            ResultSet countryRS = ps1.getResultSet(); //Setting the results of the query to a result set

            //Moves to the first value in the result set.
            countryRS.next();
            //Returns the string value found by the query.
            return countryRS.getString("Country");
        } catch (SQLException sqlException) {
            //Displays an error message if a SQL exception error occurs.
            displayMessages.errorMsg("Database error. Please try again.");
        }
        //Returns a blank string. This will return if the try block misses.
        return "";
    }

    /**
     * This method uses the contact ID to return the contact name as a string value.
     *
     * @param id The id of the contact.
     * @return The contact name as a string value.,
     */
    public static String convertContact(int id) {
        //Query for the Contact ID
        try {
            String sql = "SELECT * FROM contacts WHERE Contact_ID = ?";
            DBQuery.setPreparedStatement(DBConnect.getConnection(), sql); //Creating the prepared statement object
            PreparedStatement ps = DBQuery.getPreparedStatement(); //referencing the prepared statement
            ps.setString(1, String.valueOf(id));
            ps.execute(); //Runs the sql query
            ResultSet contactID_RS = ps.getResultSet();
            //Moves to the first value in the result set
            contactID_RS.next();
            //Returns the string value found by the query.
            return contactID_RS.getString("Contact_Name");
        } catch (SQLException sqlException) {
            //Displays an error message if a SQL exception error occurs.
            displayMessages.errorMsg("Database error. Please try again.");
        }
        //Returns a blank string. This will return if the try block misses.
        return "";
    }


}
