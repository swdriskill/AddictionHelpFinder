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
            String data = userData.getfName() + ":" + userData.getLName() + ":" + userData.getAgeType() + ":" +
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
            userData.setfName(userDataArray[0]);
            userData.setLName(userDataArray[1]);
            userData.setAgeType(userDataArray[2]);
            userData.setCity(userDataArray[3]);
            userData.setState(userDataArray[4]);
            userData.setLgbt(userDataArray[5]);
            userData.setPhoneNumber(userDataArray[6]);
            userData.setPregnantOrSpecalizedCare(userDataArray[7]);
            userData.setReligiousMedical(userDataArray[8]);
            userData.setQuestionPhase(new Integer(userDataArray[9]).intValue());

            log.debug("in UnMarshal  - : " + userData.toString());

            return userData;
        }
    }
}
