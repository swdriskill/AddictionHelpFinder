package main.java.com.ATF.storage.main.java.com.ATF.storage.addictioncenters;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import main.java.com.ATF.storage.AddictionUserData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Model representing an item of the ScoreKeeperUserData table in DynamoDB for the ScoreKeeper
 * skill.
 */
@DynamoDBTable(tableName = "SkillDB")
public class AddictionCentersDataItem {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final Logger log = LoggerFactory.getLogger(AddictionCentersDataItem.class);

    private String id;

    private AddictionCenterData addictionCenterData;

    @DynamoDBHashKey(attributeName = "Id")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @DynamoDBAttribute(attributeName = "Data")
    @DynamoDBMarshalling(marshallerClass = AddictionCentersDataMarshaller.class)
    public AddictionCenterData getAddictionCenterData () {
        return addictionCenterData;
    }

    public void setAddictionCenterData (AddictionCenterData addictionCenterData) {
        this.addictionCenterData = addictionCenterData;
    }

    /**
     * A {@link DynamoDBMarshaller} that provides marshalling and unmarshalling logic for
     * {@link AddictionCenterData} values so that they can be persisted in the database as String.
     */
    public static class AddictionCentersDataMarshaller implements
            DynamoDBMarshaller<AddictionCenterData> {

        @Override
        public String marshall(AddictionCenterData addictionCenterData) {
            /*String data = userData.getfName() + ":" + userData.getAgeType() + ":" +
                    userData.getCity() + ":" + userData.getState()+ ":" + userData.getLgbt()+ ":" +
                    userData.getPhoneNumber() + ":" + userData.getPregnantOrSpecalizedCare() + ":" +
                    userData.getReligiousMedical()+ ":" + userData.getQuestionPhase();*/
            String data = "Empty Bag";

            log.debug("in Marshal  - : " + data);
            return data;
        }

        @Override
        public AddictionCenterData unmarshall(Class<AddictionCenterData> clazz, String value) {
            /*String[] userDataArray = value.split(",");
            AddictionCenterData userData = AddictionUserData.newInstance();
            String fName = userDataArray[0];
            String ageType = userDataArray[1];
            String cityType = userDataArray[2];
            String stateType = userDataArray[3];
            String lgbt = userDataArray[4];
            String phone = userDataArray[5];
            String pregnantOrSpecalizedCare = userDataArray[6];
            String religiousMedical = userDataArray[7];
            String questionPhase = userDataArray[8];

            if (fName == null) { fName = ""; }
            if (ageType == null) { ageType = ""; }
            if (cityType == null) { cityType = ""; }
            if (stateType == null) { stateType = ""; }
            if (lgbt == null) { lgbt = ""; }
            if (phone == null) { phone = ""; }
            if (pregnantOrSpecalizedCare == null) { pregnantOrSpecalizedCare = ""; }
            if (religiousMedical == null) { religiousMedical = ""; }
            if (questionPhase == null) { questionPhase = "0"; }

            userData.setfName(fName);
            userData.setAgeType(ageType);
            userData.setCity(cityType);
            userData.setState(stateType);
            userData.setLgbt(lgbt);
            userData.setPhoneNumber(phone);
            userData.setPregnantOrSpecalizedCare(pregnantOrSpecalizedCare);
            userData.setReligiousMedical(religiousMedical);
            userData.setQuestionPhase(new Integer(questionPhase).intValue());
*/
            AddictionCenterData addictionCenterData = new AddictionCenterData();
            log.debug("in UnMarshal  - : " + addictionCenterData.toString());

            return addictionCenterData;
        }
    }
}
