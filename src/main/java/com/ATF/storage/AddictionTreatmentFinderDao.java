package main.java.com.ATF.storage;

import com.amazon.speech.speechlet.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Contains the methods to interact with the persistence layer for ScoreKeeper in DynamoDB.
 */
public class AddictionTreatmentFinderDao {
    private final AddictionTreatmentFinderDynamoDbClient dynamoDbClient;
    private static final Logger log = LoggerFactory.getLogger(AddictionTreatmentFinderDao.class);


    public AddictionTreatmentFinderDao (AddictionTreatmentFinderDynamoDbClient dynamoDbClient) {
        this.dynamoDbClient = dynamoDbClient;
    }

    /**
     * Reads and returns the {@link AddictionTreatmentFinder} using user information from the session.
     * <p>
     * Returns null if the item could not be found in the database.
     * 
     * @param session
     * @return
     */
    public AddictionTreatmentFinder getUserData (Session session) {
        AddictionTreatmentFinderUserDataItem item = new AddictionTreatmentFinderUserDataItem();
        item.setCustomerId(session.getUser().getUserId());

        item = dynamoDbClient.loadItem(item);

        if (item == null) {
            return null;
        }

        return AddictionTreatmentFinder.newInstance(session, item.getUserData());
    }

    /**
     * Saves the {@link AddictionTreatmentFinder} into the database.
     * 
     * @param treatmentFinder
     */
    public void setUserData(AddictionTreatmentFinder treatmentFinder) {
        AddictionTreatmentFinderUserDataItem item = new AddictionTreatmentFinderUserDataItem();
        item.setCustomerId(treatmentFinder.getSession().getUser().getUserId());
        item.setUserData(treatmentFinder.getUserData());

        log.debug("In setUserData  + " + item.getCustomerId());
        log.debug("In setUserData  + " + item.getUserData().toString());

        dynamoDbClient.saveItem(item);
    }
}
