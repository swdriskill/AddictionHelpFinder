package main.java.com.ATF.storage;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName = "AddictionCenters")
public class AddictionCenters {
    private String id;
    private String name;
    private String street1;
    private String street2;
    private String state;
    private String city;
    private String zip;
    private String county;
    private String phone;
    private int lgbt;
    private int alcoholDetox;
    private int children;

    @Override
    public String toString () {
        return "AddicionData{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", street1='" + street1 + '\'' +
                ", street2='" + street2 + '\'' +
                ", state='" + state + '\'' +
                ", city='" + city + '\'' +
                ", zip='" + zip + '\'' +
                ", county='" + county + '\'' +
                ", phone='" + phone + '\'' +
                ", lgbt=" + lgbt +
                ", alcoholDetox=" + alcoholDetox +
                ", children=" + children +
                '}';
    }

    public String getId () {
        return id;
    }

    public void setId (String id) {
        this.id = id;
    }

    public String getName () {
        return name;
    }

    public void setName (String name) {
        this.name = name;
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

    public String getState () {
        return state;
    }

    public void setState (String state) {
        this.state = state;
    }

    public String getCity () {
        return city;
    }

    public void setCity (String city) {
        this.city = city;
    }

    public String getZip () {
        return zip;
    }

    public void setZip (String zip) {
        this.zip = zip;
    }

    public String getCounty () {
        return county;
    }

    public void setCounty (String county) {
        this.county = county;
    }

    public String getPhone () {
        return phone;
    }

    public void setPhone (String phone) {
        this.phone = phone;
    }

    public int getLgbt () {
        return lgbt;
    }

    public void setLgbt (int lgbt) {
        this.lgbt = lgbt;
    }

    public int getAlcoholDetox () {
        return alcoholDetox;
    }

    public void setAlcoholDetox (int alcoholDetox) {
        this.alcoholDetox = alcoholDetox;
    }

    public int getChildren () {
        return children;
    }

    public void setChildren (int children) {
        this.children = children;
    }
}
