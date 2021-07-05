package utilities;

import javafx.scene.control.Alert;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

/**This class is used to display different types of messages based on the users input. Parameters can be passed in as
 * so different messages can be displayed using the same code.*/
public class displayMessages {

    /**Code to display an error message when an input error is found. Message is passed from calling code so
     * this method can be used to display multiple types of errors.
     * @param msg The error message string to display.*/
    public static void errorMsg(String msg) {
        var alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    /**
     * Displays an informational message.
     * @param msg The string to display on the message box.
     */
    public static void infoMsg(String msg) {
        var alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    /**
     * Displays a custom informational message with the appointment ID when an appointment is cancelled.
     * @param id The ID of the appointment that was cancelled/deleted.
     */
    public static void apptCanceled(int id){
        var alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText("Appointment ID " + id + " has been cancelled.");
        alert.showAndWait();
    }

    /**
     * This displays a message if an appointment is detected within the next 15-minutes or displays that one was not found.
     * @param id The id of the upcoming appointment.
     * @param apptDateTime The start time of the next meeting as a local date time type.
     */
    public static void apptUpcoming (int id, LocalDateTime apptDateTime){
//Create a variable to store right now.
        LocalDateTime rightNow = LocalDateTime.now();
//Determine the number of minutes between right now and the next appointment.
        long minutes = ChronoUnit.MINUTES.between(rightNow,apptDateTime);
//If the next appointment is more than 15 minutes away, display that in an info box.
        if (minutes > 15) {
            infoMsg("No appointment within the next 15 minutes.");
            System.out.println(minutes);
        } else {
            //Display the id and time of the next available appointment.
            infoMsg("Appointment " + id + " is at " + apptDateTime.toLocalTime() + " and is within 15 minutes.");
            System.out.println(minutes);
        }



    }
}
