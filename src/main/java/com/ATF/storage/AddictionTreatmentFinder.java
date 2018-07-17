package main.java.com.ATF.storage;

import com.amazon.speech.speechlet.Session;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Represents a score keeper game.
 */
public final class AddictionTreatmentFinder {
    private Session session;
    private AddictionUserData userData;


    private AddictionTreatmentFinder () {
    }

    /**
     * Creates a new instance of {@link AddictionTreatmentFinder} with the provided {@link Session} and
     * {@link AddictionUserData}.
     * <p>
     * To create a new instance of {@link AddictionUserData}, see
     * {@link AddictionUserData#newInstance()}
     * 
     * @param session
     * @param userData
     * @return
     * @see AddictionUserData#newInstance()
     */
    public static AddictionTreatmentFinder newInstance(Session session, AddictionUserData userData) {
        AddictionTreatmentFinder treatementFinder = new AddictionTreatmentFinder();
        treatementFinder.setSession(session);
        treatementFinder.setUserData(userData);
        return treatementFinder;
    }


    public void setSession(Session session) {
        this.session = session;
    }

    public Session getSession() {
        return session;
    }

    public AddictionUserData getUserData() {
        return userData;
    }

    public void setUserData(AddictionUserData lUserData) {
        this.userData = lUserData;
    }

/*
    public void addUser(String userName) {
        userData.getPlayers().add(playerName);
    }


    public long getScoreForPlayer(String playerName) {
        return gameData.getScores().get(playerName);
    }

    public boolean addScoreForPlayer(String playerName, long score) {
        if (!hasPlayer(playerName)) {
            return false;
        }

        long currentScore = 0L;
        if (gameData.getScores().containsKey(playerName)) {
            currentScore = gameData.getScores().get(playerName);
        }

        gameData.getScores().put(playerName, Long.valueOf(currentScore + score));
        return true;
    }

    public void resetScores() {
        for (String playerName : gameData.getPlayers()) {
            gameData.getScores().put(playerName, Long.valueOf(0L));
        }
    }


    public SortedMap<String, Long> getAllScoresInDescndingOrder() {
		Map<String, Long> scores = gameData.getScores();

		for (String playerName : gameData.getPlayers()) {
			if (!gameData.getScores().containsKey(playerName)) {
				scores.put(playerName, Long.valueOf(0L));
			}
		}

        SortedMap<String, Long> sortedScores =
                new TreeMap<String, Long>(new ScoreComparator(scores));
        sortedScores.putAll(gameData.getScores());
        return sortedScores;
    }


    private static final class ScoreComparator implements Comparator<String>, Serializable {
        private static final long serialVersionUID = 7849926209990327190L;
        private final Map<String, Long> baseMap;

        private ScoreComparator(Map<String, Long> baseMap) {
            this.baseMap = baseMap;
        }

        @Override
        public int compare(String a, String b) {
            int longCompare = Long.compare(baseMap.get(b), baseMap.get(a));
            return longCompare != 0 ? longCompare : a.compareTo(b);
        }
    }
*/

}
