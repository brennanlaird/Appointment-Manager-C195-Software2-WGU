package utilities;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.time.ZonedDateTime;

/**
 * A utility class used to save all login attempts to a text file. The file will contain the user name and
 * the time stamp of the login attempt.
 */
public class logFile {
    /**
     * This method saves the user ID of the login attempt and the zoned date time of that attempt. The Zoned date time
     * is used as it could potentially capture location information of the user.
     *
     * @param user The user name that was typed into the login form.
     * @throws IOException
     */
    public static void logAttempt(String user) throws IOException {
        //create a new file writer and buffered reader to save to the text file.
        FileWriter logFile = new FileWriter("src/login_activity.txt", true);
        BufferedWriter bfWriter = new BufferedWriter(logFile);

        //Write the user name and login time to the file seperated by tabs.
        bfWriter.write(user);
        bfWriter.write("\t");
        bfWriter.write(ZonedDateTime.now().toString());
        bfWriter.write("\t");

        //Close the writer.
        bfWriter.close();


    }

    /**
     * This methods writes whether the login attempt was successful or not to the file.
     *
     * @param status The boolean status of the login attempt. True is successful and false is failed.
     */
    public static void logStatus(boolean status) throws IOException {
        //create a new file writer and buffered reader to save to the text file.
        FileWriter logFile = new FileWriter("src/login_activity.txt", true);
        BufferedWriter bfWriter = new BufferedWriter(logFile);

        //If statement to determine success or failure.
        if (status == true) {

            //Write success
            bfWriter.write("Success");
        } else {
            //write failed
            bfWriter.write("Failed");
        }
        //Append a new line to the file to start fresh for the next one.
        bfWriter.append(System.lineSeparator());
        //Close the writer.
        bfWriter.close();
    }

}
