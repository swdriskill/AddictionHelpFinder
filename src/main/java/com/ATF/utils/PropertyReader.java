package main.java.com.ATF.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyReader {
    private static final Logger log = LoggerFactory.getLogger(PropertyReader.class);

    private static PropertyReader propertyReader;
    private String skillName = "";
    private boolean propertyRead = false;
    private String fatalError = "";
    private String welcomeMessage = "";
    private String speechHelp = "";
    private String goodBye = "";
    private String speechReprompt= "";
    private String speechSorry = "";
    private String speechConnect = "";
    private String speechNo = "";
    private String question0 = "";
    private String question1 = "";
    private String question2under = "";
    private String question3underCityState = "";
    private String questionunderphonecall = "";
    private String questionunderphonetext = "";

    private String question2over = "";
    private String question2overlgbt = "";

    private String question3over = "";

    private String question4over = "";



    private String skillId = "";


    private PropertyReader() {
        Properties skillProperties = new Properties();
        InputStream input = null;

        try {
            input = new FileInputStream("skill.properties");
            // load a properties file

            skillProperties.load(input);
            skillName = skillProperties.getProperty("skill");
            skillId = skillProperties.getProperty("skill-id");
            fatalError = skillProperties.getProperty("speech-fatal-error");
            welcomeMessage = skillProperties.getProperty("speech-welcome");
            speechConnect = skillProperties.getProperty("speech-connect");
            speechHelp = skillProperties.getProperty("speech-help");
            goodBye = skillProperties.getProperty("speech-goodbye");
            speechSorry = skillProperties.getProperty("speech-sorry");
            speechReprompt = skillProperties.getProperty("speech-reprompt");
            speechNo = skillProperties.getProperty("speech-no");
            question0 = skillProperties.getProperty("question-0");
            question1 = skillProperties.getProperty("question-1");
            question2under = skillProperties.getProperty("question-2-Under");
            question3underCityState = skillProperties.getProperty("question-3-Under-city-state");
            questionunderphonecall = skillProperties.getProperty("question-Under-phone-call");
            questionunderphonetext = skillProperties.getProperty("question-Under-phone-text");

            question2over = skillProperties.getProperty("question-2-Over");
            question2overlgbt = skillProperties.getProperty("question-2-Over-LGBT");

            question3over = skillProperties.getProperty("question-3-Over");

            question4over = skillProperties.getProperty("question-4-Over");

            log.debug("Coming from LOG 4 J - The skill name is :- " + skillName);


            propertyRead = true;


        } catch (IOException ioException) {
            propertyRead = false;
            log.error("Coming from LOG 4 J - Skill Property file not loaded");
        }

    }

    public String getSkillId () {
        return skillId;
    }

    public String getSpeechReprompt () {
        return speechReprompt;
    }

    public String getSpeechSorry () {
        return speechSorry;
    }

    public String getGoodBye () {
        return goodBye;
    }

    public String getSpeechHelp () {
        return speechHelp;
    }

    public String getFatalError () {
        return fatalError;
    }

    public String getWelcomeMessage () {
        return welcomeMessage;
    }

    public boolean isPropertyRead () {
        return propertyRead;
    }

    public String getSkillName () {
        return skillName;
    }

    public String getSpeechNo () {
        return speechNo;
    }

    public String getQuestion0 () {
        return question0;
    }

    public String getQuestion1 () {
        return question1;
    }

    public String getSpeechConnect () {
        return speechConnect;
    }

    public String getQuestion2under () {
        return question2under;
    }

    public String getQuestion2over () {
        return question2over;
    }

    public String getQuestion2overlgbt () {
        return question2overlgbt;
    }

    public String getQuestion3over () {
        return question3over;
    }

    public String getQuestion4over () {
        return question4over;
    }

    public String getQuestion3underCityState () {
        return question3underCityState;
    }

    public String getQuestionunderphonecall () {
        return questionunderphonecall;
    }

    public String getQuestionunderphonetext () {
        return questionunderphonetext;
    }

    public static PropertyReader getPropertyReader () {
        if (propertyReader == null) {
            propertyReader = new PropertyReader();
        }
        return propertyReader;
    }

}
