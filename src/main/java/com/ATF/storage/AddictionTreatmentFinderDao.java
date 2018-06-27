package main.java.com.ATF.storage;

import com.amazon.speech.speechlet.Session;

/**
 * Contains the methods to interact with the persistence layer for ScoreKeeper in DynamoDB.
 */
public class AddictionTreatmentFinderDao {
    private final AddictionTreatmentFinderDynamoDbClient dynamoDbClient;

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
    public AddictionTreatmentFinder getScoreKeeperGame(Session session) {
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
     * @param game
     */
    public void saveScoreKeeperGame(AddictionTreatmentFinder game) {
        AddictionTreatmentFinderUserDataItem item = new AddictionTreatmentFinderUserDataItem();
        item.setCustomerId(game.getSession().getUser().getUserId());
        item.setUserData(game.getGameData());

        dynamoDbClient.saveItem(item);
    }
}
