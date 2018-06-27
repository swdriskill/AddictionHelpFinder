package main.java.com.ATF.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by vshyba on 25/04/16.
 */
public class ActiveCallsService {
    public static Map<String, String> localPersistence = new HashMap<String, String>();

    public static void saveNewConference(String agentId, String conferenceId) {
        localPersistence.put(agentId, conferenceId);
    }

    public static String getConferenceFromAgentID(String agentId) {
        return localPersistence.get(agentId);
    }
}
