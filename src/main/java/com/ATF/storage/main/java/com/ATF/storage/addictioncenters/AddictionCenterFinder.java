package main.java.com.ATF.storage.main.java.com.ATF.storage.addictioncenters;

import com.amazon.speech.speechlet.Session;

/**
 * Represents a score keeper game.
 */
public final class AddictionCenterFinder {
    private Session session;
    private AddictionCentersData addictionCentersData;


    private AddictionCenterFinder () {
    }

    /**
     * Creates a new instance of {@link AddictionCenterFinder} with the provided {@link Session} and
     * {@link AddictionCentersData}.
     * <p>
     * To create a new instance of {@link AddictionCentersData}, see
     * {@link AddictionCentersData#newInstance()}
     * 
     * @param session
     * @param addictionCentersData
     * @return
     * @see AddictionCentersData#newInstance()
     */
    public static AddictionCenterFinder newInstance(Session session, AddictionCentersData addictionCentersData) {
        AddictionCenterFinder addictionCenterFinder = new AddictionCenterFinder();
        addictionCenterFinder.setSession(session);
        addictionCenterFinder.setAddictionCentersData(addictionCentersData);
        return addictionCenterFinder;
    }


    public void setSession(Session session) {
        this.session = session;
    }

    public Session getSession() {
        return session;
    }

    public AddictionCentersData getAddictionCentersData() {
        return addictionCentersData;
    }

    public void setAddictionCentersData(AddictionCentersData addictionCentersData) {
        this.addictionCentersData = addictionCentersData;
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
