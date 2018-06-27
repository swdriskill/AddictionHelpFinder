package main.java.com.ATF.storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Contains player and score data to represent a score keeper game.
 */
public class AddictionHelperData {
    private List<String> players;
    private Map<String, Long> scores;

    public AddictionHelperData () {
        // public no-arg constructor required for DynamoDBMapper marshalling
    }

    /**
     * Creates a new instance of {@link AddictionHelperData} with initialized but empty player and
     * score information.
     * 
     * @return
     */
    public static AddictionHelperData newInstance() {
        AddictionHelperData newInstance = new AddictionHelperData();
        newInstance.setPlayers(new ArrayList<String>());
        newInstance.setScores(new HashMap<String, Long>());
        return newInstance;
    }

    public List<String> getPlayers() {
        return players;
    }

    public void setPlayers(List<String> players) {
        this.players = players;
    }

    public Map<String, Long> getScores() {
        return scores;
    }

    public void setScores(Map<String, Long> scores) {
        this.scores = scores;
    }

    @Override
    public String toString() {
        return "[AddictionHelperData players: " + players + "] scores: " + scores + "]";
    }
}
