package objects;

/**
 *
 */
public class appointmentTypeReport {
    private String month;
    private int year;
    private int count;
    private String type;

    public appointmentTypeReport(String month, int year, int count, String type) {
        this.month = month;
        this.year = year;
        this.count = count;
        this.type = type;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getMonth() {
        return month;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getYear() {
        return year;
    }
}
