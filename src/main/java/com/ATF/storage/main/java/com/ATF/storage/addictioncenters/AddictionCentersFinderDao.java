package main.java.com.ATF.storage.main.java.com.ATF.storage.addictioncenters;

import com.amazon.speech.speechlet.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.DescribeTableRequest;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.amazonaws.services.dynamodbv2.model.PutItemResult;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.amazonaws.services.dynamodbv2.model.TableDescription;
import com.amazonaws.services.dynamodbv2.util.Tables;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Contains the methods to interact with the persistence layer for ScoreKeeper in DynamoDB.
 */
public class AddictionCentersFinderDao {
    private final AddictionCentersFinderDynamoDbClient dynamoDbClient;
    private static final Logger log = LoggerFactory.getLogger(AddictionCentersFinderDao.class);


    public AddictionCentersFinderDao (AddictionCentersFinderDynamoDbClient dynamoDbClient) {
        this.dynamoDbClient = dynamoDbClient;
    }

    /**
     * Reads and returns the {@link AddictionCenterFinder} using user information from the session.
     * <p>
     * Returns null if the item could not be found in the database.
     * 
     * @param session
     * @return
     */

    public AddictionCenterFinder getAddictionCentersData (Session session) {
        AddictionCentersFinderUserDataItem item = new AddictionCentersFinderUserDataItem();

        String city = (String) session.getAttribute("SESSION-CITY");
        String state = (String) session.getAttribute("SESSION-STATE");
        double random = Math.random();
        item.setId(city+":"+state+":"+random+"");
//todo Should fail as id is not proper
        item = dynamoDbClient.loadItem(item);

        if (item == null) {
            return null;
        }

        return AddictionCenterFinder.newInstance(session, item.getAddictionCentersData());
    }

    public List<AddictionCentersData> scanCityState(Session session, String tableName) {
        // Scan for centers in city and state
        String city = (String) session.getAttribute("SESSION-CITY");
        String state = (String) session.getAttribute("SESSION-STATE");
        String scanKey = city+":"+state+":";
        log.debug("Scan Key: " + scanKey);

        HashMap<String, Condition> scanFilter = new HashMap<String, Condition>();
        Condition condition = new Condition()
                .withComparisonOperator(ComparisonOperator.BEGINS_WITH.toString())
                .withAttributeValueList(new AttributeValue().withS(scanKey));
        scanFilter.put("Id", condition);
        log.debug("Scan condition: " + condition.toString());

        ScanRequest scanRequest = new ScanRequest(tableName).withScanFilter(scanFilter);
        log.debug("Scan Request: " + scanRequest.getSelect());

        ScanResult scanResult = dynamoDbClient.getDynamoDBClient().scan(scanRequest);

        log.debug("Scan Result: " + scanResult);
        log.debug("Scan Result: " + scanResult.getItems());
        log.debug("Scan Result: " + scanResult.getItems().get(0));

        //todo implement scan functionality
        return new ArrayList<>();
    }

    /**
     * Saves the {@link AddictionCenterFinder} into the database.
     * 
     * @param addictionCenterFinder
     */
    public void setAddictionCentersData(AddictionCenterFinder addictionCenterFinder, Session session) {
        AddictionCentersFinderUserDataItem item = new AddictionCentersFinderUserDataItem();

        String city = (String) session.getAttribute("SESSION-CITY");
        String state = (String) session.getAttribute("SESSION-STATE");
        double random = Math.random();
        item.setId(city+":"+state+":"+random+"");

        //item.setCustomerId(treatmentFinder.getSession().getUser().getUserId());
        item.setAddictionCentersData(addictionCenterFinder.getAddictionCentersData());

        log.debug("In setUserData  + " + item.getId());
        log.debug("In setUserData  + " + item.getAddictionCentersData().toString());

        dynamoDbClient.saveItem(item);
    }

    public void loadCentersTable(AddictionCentersData addictionCentersData) {
        AddictionCentersFinderUserDataItem item = new AddictionCentersFinderUserDataItem();

        String city = addictionCentersData.getCity();
        String state = addictionCentersData.getState();
        double random = Math.random();

        item.setId(city+":"+state+":"+random+"");
        item.setAddictionCentersData(addictionCentersData);

        log.debug("In setUserData  + " + item.getId());
        log.debug("In setUserData  + " + addictionCentersData.toString());

        dynamoDbClient.saveItem(item);
    }
}
