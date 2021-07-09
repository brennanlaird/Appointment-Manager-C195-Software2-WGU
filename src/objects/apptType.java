package objects;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class apptType {

    private String type;
    private int count;

    public static ObservableList<apptType> meetingTypes = FXCollections.observableArrayList();

    public apptType(String type, int count) {
        this.type = type;
        this.count = count;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }


    public static void resetCounts() {
        for (apptType c : meetingTypes
        ) {
            c.setCount(0);
        }
    }


    public static void createTypes() {
        meetingTypes.add(new apptType("Project Team Meeting", 0));
        meetingTypes.add(new apptType("Stakeholder Meeting", 0));
        meetingTypes.add(new apptType("Change Control Meeting", 0));
        meetingTypes.add(new apptType("Project Status Meeting", 0));
        meetingTypes.add(new apptType("Project Review Meeting", 0));
        meetingTypes.add(new apptType("Other", 0));
    }

}
