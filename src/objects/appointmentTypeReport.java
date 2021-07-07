package objects;

import java.time.Month;
import java.time.ZonedDateTime;

/**
 *
 */
public class appointmentTypeReport {
    private String type;
    private ZonedDateTime date;



    public  appointmentTypeReport(String type, ZonedDateTime date) {
        this.type = type;
        this.date = date;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }

    public ZonedDateTime getDate() {
        return date;
    }
}
