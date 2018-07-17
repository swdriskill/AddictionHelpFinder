package main.java.com.ATF.storage.main.java.com.ATF.storage.addictioncenters;

import com.amazon.speech.speechlet.Session;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Contains the methods to interact with the persistence layer for ScoreKeeper in DynamoDB.
 */
public class AddictionCentersFinderDao {
    private final AddictionCentersDynamoDbClient dynamoDbClient;
    private static final Logger log = LoggerFactory.getLogger(AddictionCentersFinderDao.class);

    //AmazonDynamoDB client = AmazonDynamoDBClientBuilder.defaultClient();

    public AddictionCentersFinderDao (AddictionCentersDynamoDbClient dynamoDbClient) {
        this.dynamoDbClient = dynamoDbClient;
    }

    /**
     * Reads and returns the {@link AddictionCentersFinder} using user information from the session.
     * <p>
     * Returns null if the item could not be found in the database.
     * 
     * @param session
     * @return
     */
    public AddictionCentersFinder getUserData (Session session) {
        AddictionCentersDataItem item = new AddictionCentersDataItem();
        item.setId(session.getUser().getUserId());

        item = dynamoDbClient.loadItem(item);

        if (item == null) {
            return null;
        }

        return AddictionCentersFinder.newInstance(session, item.getAddictionCenterData());
    }

    /*public List<AddictionCenterData> getScanResult (String state, String city) {
        Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
        eav.put(":State", new AttributeValue().withS(state));
        eav.put(":City", new AttributeValue().withS(city));

        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
        scanExpression.withExpressionAttributeValues(eav);

        DynamoDBMapper mapper = null;

        List<AddictionCenterData> scanResult = mapper.scan(AddictionCenterData.class, scanExpression);

        return scanResult;
    }
*/

}
