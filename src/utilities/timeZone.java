package utilities;

import java.time.ZoneId;
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

    /**Returns the defined start of business in the default local time zone.
     * @param userZone The default time zone of the user.
     * @return The zoned date time of the start of business in the local time zone.*/
    public static ZonedDateTime startBusinessHours(ZoneId userZone){
        //Declares the start of business hours in Eastern time
        ZonedDateTime startBusiness = ZonedDateTime.of(2000,1,1,8,0,0,0, ZoneId.of("US/Eastern"));

        //Updates the start time based on the users time zone
        ZonedDateTime updateStartTime = startBusiness.withZoneSameInstant(userZone);

        //returns the updated time to the calling code.
        return updateStartTime;

            }
    /**Returns the defined end of business in the default local time zone.
     * @param userZone The default time zone of the user.
     * @return The zoned date time of the end of business in the local time zone.*/
    public static ZonedDateTime endBusinessHours(ZoneId userZone){
        //Declares the start of business hours in Eastern time
        ZonedDateTime endBusiness = ZonedDateTime.of(2000,1,1,20,0,0,0, ZoneId.of("US/Eastern"));

        //Updates the start time based on the users time zone
        ZonedDateTime updateEndTime = endBusiness.withZoneSameInstant(userZone);

        //returns the updated time to the calling code.
        return updateEndTime;
    }
}
