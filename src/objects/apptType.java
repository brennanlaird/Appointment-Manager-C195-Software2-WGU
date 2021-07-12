package objects;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * This class is used to manage the reporting of appointments by type. It creates an object that stores the appointment
 * type and the count. The code will increment the count based on data provided by the user.
 */
public class apptType {

    private String type;
    private int count;

    public static ObservableList<apptType> meetingTypes = FXCollections.observableArrayList();

    //Constructor for the apptType class.
    public apptType(String type, int count) {
        this.type = type;
        this.count = count;
    }

    /**
     *
     * @return The type of appointment.
     */
    public String getType() {
        return type;
    }

    /**
     *
     * @param type The type of appointment to set.
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     *
     * @return The number of appointments of this type.
     */
    public int getCount() {
        return count;
    }

    /**
     *
     * @param count Set the number of appointments to this.
     */
    public void setCount(int count) {
        this.count = count;
    }

    /**
     * This methods resets all the appointment counts to zero. It is used when the date for the report is changed so
     * the count can be re-done for the new date.
     */
    public static void resetCounts() {
        for (apptType c : meetingTypes
        ) {
            c.setCount(0);
        }
    }

    /**
     * This method creates the meeting types that are then used to store the count data for a given date period.
     */
    public static void createTypes() {
        meetingTypes.add(new apptType("Project Team Meeting", 0));
        meetingTypes.add(new apptType("Stakeholder Meeting", 0));
        meetingTypes.add(new apptType("Change Control Meeting", 0));
        meetingTypes.add(new apptType("Project Status Meeting", 0));
        meetingTypes.add(new apptType("Project Review Meeting", 0));
        meetingTypes.add(new apptType("Other", 0));
    }

}
