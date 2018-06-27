package test.java.com.ATF.utils;

import main.java.com.ATF.utils.NewsFeed;
import main.java.com.ATF.utils.NewsFeedMessage;
import main.java.com.ATF.utils.NewsRSSFeedParser;
import main.java.com.ATF.utils.StringUtils;

import java.io.IOException;
import java.util.ArrayList;

public class ReadTest {
    public static void main(String[] args) throws IOException {
        StringUtils stringUtils = new StringUtils();

        //Test RSS Feed
        //RSSFeedParser parser = new RSSFeedParser(
        //        "http://s3.amazonaws.com/alexa-java-skill-vikas/news.rss");

        //Prod RSS Feed
        NewsRSSFeedParser parser = new NewsRSSFeedParser
                ("https://www.bisnow.com/rss/chicago");

        NewsFeed newsFeed = parser.readFeed();
        StringBuilder builder = new StringBuilder();
        ArrayList subString = new ArrayList();

        for (NewsFeedMessage message : newsFeed.getMessages()) {
            subString = new ArrayList();
            System.out.println(message.getEncodedContent());
        }
        System.out.println(builder.toString());


        stringUtils.splitByLength("bobjoecat", 3);
        stringUtils.splitString("This is a message that needs to be split over multiple lines because it is too long. The result must be a list of strings with a maximum length provided as input. Will this procedure work? I hope so!",40);




    }
}
