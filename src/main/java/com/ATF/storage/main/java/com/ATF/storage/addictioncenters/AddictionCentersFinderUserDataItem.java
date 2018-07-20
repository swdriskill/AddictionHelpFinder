package main.java.com.ATF.storage.main.java.com.ATF.storage.addictioncenters;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import main.java.com.ATF.storage.AddictionTreatmentFinderDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Model representing an item of the ScoreKeeperUserData table in DynamoDB for the ScoreKeeper
 * skill.
 */
@DynamoDBTable(tableName = "AlcoholHelpCenters")
public class AddictionCentersFinderUserDataItem {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final Logger log = LoggerFactory.getLogger(AddictionCentersFinderUserDataItem.class);

    private String id;

    private AddictionCentersData addictionCentersData;

    @DynamoDBHashKey(attributeName = "Id")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @DynamoDBAttribute(attributeName = "Data")
    @DynamoDBMarshalling(marshallerClass = AddictionCenterFinderDataMarshaller.class)
    public AddictionCentersData getAddictionCentersData () {
        return addictionCentersData;
    }

    public void setAddictionCentersData (AddictionCentersData addictionCentersData) {
        this.addictionCentersData = addictionCentersData;
    }

    /**
     * A {@link DynamoDBMarshaller} that provides marshalling and unmarshalling logic for
     * {@link AddictionCentersData} values so that they can be persisted in the database as String.
     */
    public static class AddictionCenterFinderDataMarshaller implements
            DynamoDBMarshaller<AddictionCentersData> {

        @Override
        public String marshall(AddictionCentersData addictionCentersData) {
            String data = addictionCentersData.getName1() + ":" + addictionCentersData.getName2() + ":" +
                    addictionCentersData.getStreet1() + ":" + addictionCentersData.getStreet2() + ":" +
                    addictionCentersData.getCity() + ":" + addictionCentersData.getState() + ":" +
                    addictionCentersData.getZip() + ":" + addictionCentersData.getZip4() + ":" +
                    addictionCentersData.getCounty() + ":" + addictionCentersData.getPhoneNumber() + ":" +
                    addictionCentersData.getLgbt() + ":" + addictionCentersData.getAdolescents() + ":" +
                    addictionCentersData.getTwelveStep();

                    log.debug("in Marshal  - : " + data);
            return data;
        }

        @Override
        public AddictionCentersData unmarshall(Class<AddictionCentersData> clazz, String value) {
            String[] userDataArray = value.split(":");
            AddictionCentersData addictionCentersData = AddictionCentersData.newInstance();

            String name1 = userDataArray[0];
            String name2 = userDataArray[1];
            String street1 = userDataArray[2];
            String street2 = userDataArray[3];
            String city = userDataArray[4];
            String state = userDataArray[5];
            String zip = userDataArray[6];
            String zip4 = userDataArray[7];
            String county = userDataArray[8];
            String phoneNumber = userDataArray[9];
            String lgbt = userDataArray[10];
            String adolescents = userDataArray[11];
            String twelveStep = userDataArray[12];


            if (name1 == null) {name1 = ""; }
            if (name2 == null) {name2 = ""; }
            if (street1 == null) {street1 = ""; }
            if (street2 == null) {street2 = ""; }
            if (city == null) {city = ""; }
            if (state == null) {state = ""; }
            if (zip == null) {zip = ""; }
            if (zip4 == null) {zip4 = ""; }
            if (county == null) {county = ""; }
            if (phoneNumber == null) {phoneNumber = ""; }
            if (lgbt == null) {lgbt = ""; }
            if (adolescents == null) {adolescents = ""; }
            if (twelveStep == null) {twelveStep = ""; }

            addictionCentersData.setName1(name1);
            addictionCentersData.setName2(name2);
            addictionCentersData.setStreet1(street1);
            addictionCentersData.setStreet2(street2);
            addictionCentersData.setCity(city);
            addictionCentersData.setState(state);
            addictionCentersData.setZip(zip);
            addictionCentersData.setZip4(zip4);
            addictionCentersData.setCounty(county);
            addictionCentersData.setPhoneNumber(phoneNumber);
            addictionCentersData.setLgbt(lgbt);
            addictionCentersData.setAdolescents(adolescents);
            addictionCentersData.setTwelveStep(twelveStep);

            log.debug("in UnMarshal  - : " + addictionCentersData.toString());

            return addictionCentersData;
        }
    }
}
