package objects;

import java.time.ZonedDateTime;

/**
 * This class sets up appointment objects.
 */
public class Appointment {
    private String date;
    private int id;
    private String title;
    private String description;
    private String location;
    private String type;
    private ZonedDateTime startTime;
    private ZonedDateTime endTime;
    private String createdBy;
    private String lastUpdateBy;
    private int customerID;
    private int userID;
    private int contactID;
    private String contact;
    private String sTime;
    private String eTime;

//Constructor for the appointment class.
    public Appointment(int id, String title, String description, String location, String type,
                       ZonedDateTime startTime, ZonedDateTime endTime, String createdBy, String lastUpdateBy,
                       int customerID, int userID, int contactID, String contact, String date, String sTime, String eTime) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.location = location;
        this.type = type;
        this.startTime = startTime;
        this.endTime = endTime;
        this.createdBy = createdBy;
        this.lastUpdateBy = lastUpdateBy;
        this.customerID = customerID;
        this.userID = userID;
        this.contactID = contactID;
        this.contact = contact;
        this.date = date;
        this.sTime = sTime;
        this.eTime = eTime;
    }

    /**
     *
     * @return The appointment ID
     */
    public int getId() {
        return id;
    }

    /**
     *
     * @param id of the appointment to set.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     *
     * @return The title of the appointment.
     */
    public String getTitle() {
        return title;
    }

    /**
     *
     * @param title of the appointmentto set.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     *
     * @return The description of the appointment.
     */
    public String getDescription() {
        return description;
    }

    /**
     *
     * @param description of the appointment to set.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     *
     * @return The location of the appointment.
     */
    public String getLocation() {
        return location;
    }

    /**
     *
     * @param location of the appointment to set.
     */
    public void setLocation(String location) {
        this.location = location;
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
     * @param type of appointment to set.
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     *
     * @return The start time of the appointment.
     */
    public ZonedDateTime getStartTime() {
        return startTime;
    }

    /**
     *
     * @param startTime the start time of the appointment to set.
     */
    public void setStartTime(ZonedDateTime startTime) {
        this.startTime = startTime;
    }

    /**
     *
     * @return The end time of the appointment
     */
    public ZonedDateTime getEndTime() {
        return endTime;
    }

    /**
     *
     * @param endTime the end time of the appointment to set.
     */
    public void setEndTime(ZonedDateTime endTime) {
        this.endTime = endTime;
    }

    /**
     *
     * @return The user who originally created the appointment.
     */
    public String getCreatedBy() {
        return createdBy;
    }

    /**
     *
     * @param createdBy Set the user who created the appointment.
     */
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    /**
     *
     * @return The user who last updated the appointment.
     */
    public String getLastUpdateBy() {
        return lastUpdateBy;
    }

    /**
     *
     * @param lastUpdateBy Set the user who last updated the appointment.
     */
    public void setLastUpdateBy(String lastUpdateBy) {
        this.lastUpdateBy = lastUpdateBy;
    }

    /**
     *
     * @return The ID of the customer associated with the appointment.
     */
    public int getCustomerID() {
        return customerID;
    }

    /**
     *
     * @param customerID Set the ID of the customer associated with the appointment.
     */
    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    /**
     *
     * @return The user ID associated with the appointment.
     */
    public int getUserID() {
        return userID;
    }

    /**
     *
     * @param userID Set the user ID associated with the appointment.
     */
    public void setUserID(int userID) {
        this.userID = userID;
    }

    /**
     *
     * @return The ID of the contact associated with the appointment.
     */
    public int getContactID() {
        return contactID;
    }

    /**
     *
     * @param contactID Sets the contact ID associated with the appointment.
     */
    public void setContactID(int contactID) {
        this.contactID = contactID;
    }

    /**
     *
     * @return The name of the contact associated with the appointment.
     */
    public String getContact() {
        return contact;
    }

    /**
     *
     * @param contact Sets the contact name associated with the appointment.
     */
    public void setContact(String contact) {
        this.contact = contact;
    }


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSTime() {
        return sTime;
    }

    public void setSTime(String sTime) {
        this.sTime = sTime;
    }

    public String getETime() {
        return eTime;
    }


    public void setETime(String eTime) {
        this.eTime = eTime;
    }
}