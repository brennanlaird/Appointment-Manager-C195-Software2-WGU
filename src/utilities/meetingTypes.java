package utilities;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import objects.apptType;

/**
 *
 */
public class meetingTypes {
    private static ObservableList<apptType> meetingTypes = FXCollections.observableArrayList();
    private static ObservableList<String> meetTypesCombo = FXCollections.observableArrayList();

    public static void populatemeetingTypeCombo () {

        meetTypesCombo.add("Project Team Meeting");
        meetTypesCombo.add("Stakeholder Meeting");
        meetTypesCombo.add("Change Control Meeting");
        meetTypesCombo.add("Project Status Meeting");
        meetTypesCombo.add("Project Review Meeting");
        meetTypesCombo.add("Other");

        //meetTypesCombo.add
    }


    public static void populateAppointmentTypes() {
        String ptm = "Project Team Meeting";
        int count = 0;

        apptType projectTeamMeeting = new apptType(ptm, count);

        meetingTypes.add(projectTeamMeeting);
        meetingTypes.add(new apptType("Stakeholder Meeting",0));
        meetingTypes.add(new apptType("Change Control Meeting",0));
        meetingTypes.add(new apptType("Project Status Meeting",0));
        meetingTypes.add(new apptType("Project Review Meeting",0));
        meetingTypes.add(new apptType("Other",0));
    }

    /**
     * This returns all the appointment types in the observable list.
     *
     * @return Returns the observable list of all appointment types.
     */
    public static ObservableList<apptType> getApptTypes() {
        return meetingTypes;

    }

    public static ObservableList<String> getMeetTypesCombo(){
        return meetTypesCombo;
    }
}
