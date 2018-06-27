package main.java.com.ATF.storage;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Model representing an item of the ScoreKeeperUserData table in DynamoDB for the ScoreKeeper
 * skill.
 */
@DynamoDBTable(tableName = "AddictionHelperUserData")
public class AddictionTreatmentFinderUserDataItem {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private String customerId;

    private AddictionHelperData userData;

    @DynamoDBHashKey(attributeName = "CustomerId")
    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    @DynamoDBAttribute(attributeName = "Data")
    @DynamoDBMarshalling(marshallerClass = AddictionTreatmentFinderDataMarshaller.class)
    public AddictionHelperData getUserData () {
        return userData;
    }

    public void setUserData (AddictionHelperData userData) {
        this.userData = userData;
    }

    /**
     * A {@link DynamoDBMarshaller} that provides marshalling and unmarshalling logic for
     * {@link AddictionHelperData} values so that they can be persisted in the database as String.
     */
    public static class AddictionTreatmentFinderDataMarshaller implements
            DynamoDBMarshaller<AddictionHelperData> {

        @Override
        public String marshall(AddictionHelperData gameData) {
            try {
                return OBJECT_MAPPER.writeValueAsString(gameData);
            } catch (JsonProcessingException e) {
                throw new IllegalStateException("Unable to marshall user data", e);
            }
        }

        @Override
        public AddictionHelperData unmarshall(Class<AddictionHelperData> clazz, String value) {
            try {
                return OBJECT_MAPPER.readValue(value, new TypeReference<AddictionHelperData>() {
                });
            } catch (Exception e) {
                throw new IllegalStateException("Unable to unmarshall user data value", e);
            }
        }
    }
}
