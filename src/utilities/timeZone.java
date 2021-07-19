package utilities;

import java.time.*;
import java.util.TimeZone;

/**
 * This class is used to deal with all things related to time zones and time zone conversions..
 */
public class timeZone {
    /**
     * Returns the time zone ID to the calling code.
     *
     * @return The ID of the system time zone.
     */
    public static String timeZoneName() {
        //Sets a new time zone object based on the default time of the system.
        TimeZone systemTZ = TimeZone.getDefault();
        //System.out.println("Current time zone is: " + systemTZ.getDisplayName() + " and the user is located near: " + systemTZ.getID());
        return systemTZ.getID(); //returns the time zone ID
    }

    /**
     * Returns the defined start of business in the default local time zone.
     *
     * @param userZone The default time zone of the user.
     * @return The zoned date time of the start of business in the local time zone.
     */
    public static ZonedDateTime startBusinessHours(ZoneId userZone, ZonedDateTime dateToKeep) {
        //Declares the start of business hours in Eastern time
        ZonedDateTime startBusiness = ZonedDateTime.of(2000, 1, 1, 8, 0, 0, 0, ZoneId.of("US/Eastern"));

        startBusiness = startBusiness.with(LocalDate.of(dateToKeep.getYear(),dateToKeep.getMonth(),dateToKeep.getDayOfMonth()));



        //System.out.println("Date to Keep: " + dateToKeep.with(LocalTime.of(8,0)));

        //Updates the start time based on the users time zone
        ZonedDateTime updateStartTime = startBusiness.withZoneSameInstant(userZone);
        //ZonedDateTime updateStartTime = dateToKeep.with(LocalTime.of(8,0));
        //returns the updated time to the calling code.
        return updateStartTime;

    }

    /**
     * Returns the defined end of business in the default local time zone.
     *
     * @param userZone The default time zone of the user.
     * @return The zoned date time of the end of business in the local time zone.
     */
    public static ZonedDateTime endBusinessHours(ZoneId userZone, ZonedDateTime dateToKeep) {
        //Declares the start of business hours in Eastern time
        ZonedDateTime endBusiness = ZonedDateTime.of(2000, 1, 1, 22, 0, 0, 0, ZoneId.of("US/Eastern"));


        endBusiness = endBusiness.with(LocalDate.of(dateToKeep.getYear(),dateToKeep.getMonth(),dateToKeep.getDayOfMonth()));

        //Updates the start time based on the users time zone
        ZonedDateTime updateEndTime = endBusiness.withZoneSameInstant(userZone);

        //returns the updated time to the calling code.
        return updateEndTime;
    }

    /**
     * This method takes a local date time object and converts it to the users local time from UTC.
     * @param inputTime The local date time object in UTC time from the database.
     * @return The local time in the users default time zone.
     */
    public static ZonedDateTime convertToLocal(LocalDateTime inputTime){

        ZonedDateTime localTime = ZonedDateTime.of(inputTime, ZoneId.of("UTC")).withZoneSameInstant(ZoneId.of(timeZoneName()));

        return localTime;


    }

}

