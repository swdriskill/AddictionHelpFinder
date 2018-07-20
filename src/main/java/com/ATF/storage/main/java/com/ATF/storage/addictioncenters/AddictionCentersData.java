package main.java.com.ATF.storage.main.java.com.ATF.storage.addictioncenters;

public class AddictionCentersData {
    String id;
    String name1;
    String name2;
    String street1;
    String street2;
    String city;
    String state;
    String zip;
    String zip4;
    String county;
    String phoneNumber;
    String lgbt;
    String adolescents;
    String twelveStep;

    private AddictionCentersData () {
    }

    /**
     * Creates a new instance of {@link main.java.com.ATF.storage.AddictionUserData} with initialized but empty player and
     * score information.
     *
     * @return
     */
    public static AddictionCentersData newInstance() {
        AddictionCentersData newInstance = new AddictionCentersData();
        return newInstance;
    }

    @Override
    public String toString () {
        return "AddictionCentersData{" +
                "id='" + id + '\'' +
                ", name1='" + name1 + '\'' +
                ", name2='" + name2 + '\'' +
                ", street1='" + street1 + '\'' +
                ", street2='" + street2 + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", zip='" + zip + '\'' +
                ", zip4='" + zip4 + '\'' +
                ", county='" + county + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", lgbt='" + lgbt + '\'' +
                ", adolescents='" + adolescents + '\'' +
                ", twelveStep='" + twelveStep + '\'' +
                '}';
    }

    public String getId () {
        return id;
    }

    public void setId (String id) {
        this.id = id;
    }

    public String getName1 () {
        return name1;
    }

    public void setName1 (String name1) {
        this.name1 = name1;
    }

    public String getName2 () {
        return name2;
    }

    public void setName2 (String name2) {
        this.name2 = name2;
    }

    public String getStreet1 () {
        return street1;
    }

    public void setStreet1 (String street1) {
        this.street1 = street1;
    }

    public String getStreet2 () {
        return street2;
    }

    public void setStreet2 (String street2) {
        this.street2 = street2;
    }

    public String getCity () {
        return city;
    }

    public void setCity (String city) {
        this.city = city;
    }

    public String getState () {
        return state;
    }

    public void setState (String state) {
        this.state = state;
    }

    public String getZip () {
        return zip;
    }

    public void setZip (String zip) {
        this.zip = zip;
    }

    public String getZip4 () {
        return zip4;
    }

    public void setZip4 (String zip4) {
        this.zip4 = zip4;
    }

    public String getCounty () {
        return county;
    }

    public void setCounty (String county) {
        this.county = county;
    }

    public String getPhoneNumber () {
        return phoneNumber;
    }

    public void setPhoneNumber (String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getLgbt () {
        return lgbt;
    }

    public void setLgbt (String lgbt) {
        this.lgbt = lgbt;
    }

    public String getAdolescents () {
        return adolescents;
    }

    public void setAdolescents (String adolescents) {
        this.adolescents = adolescents;
    }

    public String getTwelveStep () {
        return twelveStep;
    }

    public void setTwelveStep (String twelveStep) {
        this.twelveStep = twelveStep;
    }
}
