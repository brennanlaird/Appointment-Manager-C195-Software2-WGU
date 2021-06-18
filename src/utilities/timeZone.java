package utilities;

import java.time.ZonedDateTime;
import java.util.TimeZone;
/**This class is used to deal with all things related to time zones.*/
public class timeZone {
    /**Returns the time zone ID to the calling code.
     * @return The ID of the system time zone.*/
    public static String timeZoneName() {
        //Sets a new time zone object based on the default time of the system.
        TimeZone systemTZ = TimeZone.getDefault();
        //System.out.println("Current time zone is: " + systemTZ.getDisplayName() + " and the user is located near: " + systemTZ.getID());
        return systemTZ.getID(); //returns the time zone ID
    }

}
