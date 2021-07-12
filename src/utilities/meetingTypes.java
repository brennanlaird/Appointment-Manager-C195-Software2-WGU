package utilities;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import objects.apptType;

/**
 * This class sets up the different meeting types and populates observable lists with the information.
 */
public class meetingTypes {

    //Observable lists to store the different meeting types and display them between different forms.
    private static ObservableList<apptType> meetingTypes = FXCollections.observableArrayList();
    private static ObservableList<String> meetTypesCombo = FXCollections.observableArrayList();


    /**
     * Populates the observable list with the defined meeting types.
     */
    public static void populatemeetingTypeCombo() {

        meetTypesCombo.add("Project Team Meeting");
        meetTypesCombo.add("Stakeholder Meeting");
        meetTypesCombo.add("Change Control Meeting");
        meetTypesCombo.add("Project Status Meeting");
        meetTypesCombo.add("Project Review Meeting");
        meetTypesCombo.add("Other");

    }


    /**
     * This returns all the appointment types in the observable list.
     *
     * @return Returns the observable list of all appointment types.
     */
    public static ObservableList<String> getMeetTypesCombo() {
        return meetTypesCombo;
    }
}
