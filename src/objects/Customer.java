package objects;

/**
 * Class to set up and store customer objects.
 */
public class Customer {
    private int id;
    private String name;
    private String address;
    private String postCode;
    private String phone;
    private String createBy;
    private String lastUpdateBy;
    private int divisionID;
    private String division;
    private String country;




    //Constructor for the customer class
    public Customer(int id, String name, String address, String postCode, String phone, String createBy, String lastUpdateBy, int divisionID, String division, String country) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.postCode = postCode;
        this.phone = phone;
        this.createBy = createBy;
        this.lastUpdateBy = lastUpdateBy;
        this.divisionID = divisionID;
        this.division = division;
        this.country = country;
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }


    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the address
     */
    public String getAddress() {
        return address;
    }
    /**
     * @param address the address to set
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * @return the post code
     */
    public String getPostCode() {
        return postCode;
    }

    /**
     * @param postCode the post code to set
     */
    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }

    /**
     * @return the phone
     */
    public String getphone() {
        return phone;
    }

    /**
     * @param phone the phone to set
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * @return the username that created the entry
     */
    public String getCreateBy() {
        return createBy;
    }

    /**
     * @param createBy set the user that created the entry
     */
    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }


    /**
     * @return the username that last updated the entry
     */
    public String getLastUpdateBy() {
        return lastUpdateBy;
    }

    /**
     * @param lastUpdateBy set the username to last update the entry
     */
    public void setLastUpdateBy(String lastUpdateBy) {
        this.lastUpdateBy = lastUpdateBy;
    }

    /**
     * @return the division id
     */
    public int getdivisionID() {
        return divisionID;
    }

    /**
     * @param divisionID the id to set
     */
    public void setdivisionID(int divisionID) {
        this.divisionID = divisionID;
    }

    /**
     *
     * @return The division name.
     */
    public String getDivision() {
        return division;
    }

    /**
     *
     * @param division the division name to set.
     */
    public void setDivision(String division) {
        this.division = division;
    }

    /**
     *
     * @return The country string.
     */
    public String getCountry() {
        return country;
    }

    /**
     *
     * @param country The country name to set.
     */
    public void setCountry(String country) {
        this.country = country;
    }
}


