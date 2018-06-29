package main.java.com.ATF.storage;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Model representing an item of the ScoreKeeperUserData table in DynamoDB for the ScoreKeeper
 * skill.
 */
@DynamoDBTable(tableName = "AddictionHelperUserData")
public class AddictionTreatmentFinderUserDataItem {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final Logger log = LoggerFactory.getLogger(AddictionTreatmentFinderDao.class);

    private String customerId;

    private AddictionUserData userData;

    @DynamoDBHashKey(attributeName = "CustomerId")
    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    @DynamoDBAttribute(attributeName = "Data")
    @DynamoDBMarshalling(marshallerClass = AddictionTreatmentFinderDataMarshaller.class)
    public AddictionUserData getUserData () {
        return userData;
    }

    public void setUserData (AddictionUserData userData) {
        this.userData = userData;
    }

    /**
     * A {@link DynamoDBMarshaller} that provides marshalling and unmarshalling logic for
     * {@link AddictionUserData} values so that they can be persisted in the database as String.
     */
    public static class AddictionTreatmentFinderDataMarshaller implements
            DynamoDBMarshaller<AddictionUserData> {

        @Override
        public String marshall(AddictionUserData userData) {
            String data = userData.getfName() + ":" + userData.getAgeType() + ":" +
                    userData.getCity() + ":" + userData.getState()+ ":" + userData.getLgbt()+ ":" +
                    userData.getPhoneNumber() + ":" + userData.getPregnantOrSpecalizedCare() + ":" +
                    userData.getReligiousMedical()+ ":" + userData.getQuestionPhase();

            log.debug("in Marshal  - : " + data);
            return data;
        }

        @Override
        public AddictionUserData unmarshall(Class<AddictionUserData> clazz, String value) {
            String[] userDataArray = value.split(",");
            AddictionUserData userData = AddictionUserData.newInstance();
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

            log.debug("in UnMarshal  - : " + userData.toString());

            return userData;
        }
    }
}
