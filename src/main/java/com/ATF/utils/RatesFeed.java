package main.java.com.ATF.utils;

import java.util.ArrayList;
import java.util.List;

/*
 * Stores an RSS feed
 */

public class RatesFeed {

    final List<RatesFeedMessage> entries = new ArrayList<RatesFeedMessage>();

    public RatesFeed () {
    }


    public List<RatesFeedMessage> getMessages() {
        return entries;
    }

}
