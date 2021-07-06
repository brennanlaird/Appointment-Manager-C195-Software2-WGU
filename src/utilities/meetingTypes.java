package utilities;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 */
public class meetingTypes {
    private static ObservableList<String> meetingTypes = FXCollections.observableArrayList();

    public static void populateAppointmentTypes() {
        meetingTypes.add("Project Team Meeting");
        meetingTypes.add("Stakeholder Meeting");
        meetingTypes.add("Change Control Meeting");
        meetingTypes.add("Project Status Meeting");
        meetingTypes.add("Project Review Meeting");
    }

    /**
     * This returns all the appointment types in the observable list.
     *
     * @return Returns the observable list of all appointment types.
     */
    public static ObservableList<String> getApptTypes() {
        return meetingTypes;

    }
}
