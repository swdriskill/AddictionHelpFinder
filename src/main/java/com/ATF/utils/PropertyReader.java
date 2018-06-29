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
    private String question2 = "";
    private String question3 = "";
    private String question4 = "";
    private String question5 = "";
    private String question6 = "";
    private String question7 = "";
    private String question8 = "";


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
            question2 = skillProperties.getProperty("question-2");
            question3 = skillProperties.getProperty("question-3");
            question4 = skillProperties.getProperty("question-4");
            question5 = skillProperties.getProperty("question-5");
            question6 = skillProperties.getProperty("question-6");
            question7 = skillProperties.getProperty("question-7");
            question8 = skillProperties.getProperty("question-8");

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

    public String getQuestion2 () {
        return question2;
    }

    public String getQuestion3 () {
        return question3;
    }

    public String getQuestion4 () {
        return question4;
    }

    public String getQuestion5 () {
        return question5;
    }

    public String getQuestion6 () {
        return question6;
    }

    public String getQuestion7 () {
        return question7;
    }

    public String getQuestion8 () {
        return question8;
    }

    public static PropertyReader getPropertyReader () {
        if (propertyReader == null) {
            propertyReader = new PropertyReader();
        }
        return propertyReader;
    }

}
