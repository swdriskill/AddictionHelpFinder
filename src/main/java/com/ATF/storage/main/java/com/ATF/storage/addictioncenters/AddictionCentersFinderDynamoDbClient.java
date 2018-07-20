package main.java.com.ATF.storage.main.java.com.ATF.storage.addictioncenters;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import main.java.com.ATF.storage.AddictionTreatmentFinderUserDataItem;

/**
 * Client for DynamoDB persistance layer for the Score Keeper skill.
 */
public class AddictionCentersFinderDynamoDbClient {
    private final AmazonDynamoDBClient dynamoDBClient;

    public AddictionCentersFinderDynamoDbClient (final AmazonDynamoDBClient dynamoDBClient) {
        this.dynamoDBClient = dynamoDBClient;
    }

    /**
     * Loads an item from DynamoDB by primary Hash Key. Callers of this method should pass in an
     * object which represents an item in the DynamoDB table item with the primary key populated.
     * 
     * @param tableItem
     * @return
     */
    public AddictionCentersFinderUserDataItem loadItem(final AddictionCentersFinderUserDataItem tableItem) {
        DynamoDBMapper mapper = createDynamoDBMapper();
        AddictionCentersFinderUserDataItem item = mapper.load(tableItem);
        return item;
    }

    /**
     * Stores an item to DynamoDB.
     * 
     * @param tableItem
     */
    public void saveItem(final AddictionCentersFinderUserDataItem tableItem) {
        DynamoDBMapper mapper = createDynamoDBMapper();
        mapper.save(tableItem);
    }

    /**
     * Creates a {@link DynamoDBMapper} using the default configurations.
     * 
     * @return
     */
    private DynamoDBMapper createDynamoDBMapper() {
        return new DynamoDBMapper(dynamoDBClient);
    }

    public AmazonDynamoDBClient getDynamoDBClient () {
        return dynamoDBClient;
    }
}
