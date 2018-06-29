/**
 Copyright 2014-2015 Amazon.com, Inc. or its affiliates. All Rights Reserved.

 Licensed under the Apache License, Version 2.0 (the "License"). You may not use this file except in compliance with the License. A copy of the License is located at

 http://aws.amazon.com/apache2.0/

 or in the "license" file accompanying this file. This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */
package main.java.com.ATF.skill;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.LaunchRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.Reprompt;
import com.amazon.speech.ui.SimpleCard;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import main.java.com.ATF.storage.AddictionTreatmentFinder;
import main.java.com.ATF.storage.AddictionTreatmentFinderDao;
import main.java.com.ATF.storage.AddictionTreatmentFinderDynamoDbClient;
import main.java.com.ATF.storage.AddictionUserData;
import main.java.com.ATF.utils.PropertyReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.Map;

/**
 * The {@link AddictionTreatmentFinderManager} receives various events and intents and manages the flow of the
 * game.
 */
public class AddictionTreatmentFinderManager {
    private static PropertyReader propertyReader = PropertyReader.getPropertyReader();
    private static final Logger log = LoggerFactory.getLogger(AddictionTreatmentFinderManager.class);

    /**
     * Intent slot for player name.
     */
    private static final String SLOT_USER_NAME = "UserName";

    private static final String SLOT_USER_AGE = "UserAge";
    private static final String SLOT_USER_CITY = "City";
    private static final String SLOT_USER_STATE = "State";
    private static final String SLOT_USER_MEDICAL_RELIGIOUS_CHOICE = "ReligiousMedicalChoice";


    /**
     * Intent slot for player score.
     */
    private static final String SLOT_SCORE_NUMBER = "ScoreNumber";


    /**
     * Maximum number of players for which scores must be announced while adding a score.
     */
    private static final int MAX_PLAYERS_FOR_SPEECH = 3;

    private final AddictionTreatmentFinderDao addictionTreatmentFinderDao;

    public AddictionTreatmentFinderManager (final AmazonDynamoDBClient amazonDynamoDbClient) {
        AddictionTreatmentFinderDynamoDbClient dynamoDbClient =
                new AddictionTreatmentFinderDynamoDbClient(amazonDynamoDbClient);
        addictionTreatmentFinderDao = new AddictionTreatmentFinderDao(dynamoDbClient);
    }



    public SpeechletResponse getConnectIntentResponse (Intent intent, Session session, SkillContext skillContext) {
        log.debug("In getConnectIntentResponse");

        // Load the previous game
        AddictionTreatmentFinder treatmentFinder = addictionTreatmentFinderDao.getUserData(session);

        if (treatmentFinder == null) {
            treatmentFinder = AddictionTreatmentFinder.newInstance(session, skillContext.getAddictionUserData());
        }

        log.debug("in getConnectIntentResponse 1 " + skillContext.getAddictionUserData().toString());
        treatmentFinder.setUserData(skillContext.getAddictionUserData());

        log.debug("in getConnectIntentResponse 2 " + skillContext.getAddictionUserData().toString());
        log.debug("in getConnectIntentResponse 3 " + treatmentFinder);
        log.debug("in getConnectIntentResponse 4 " + treatmentFinder.getUserData());

        log.debug("About to save - " + treatmentFinder.getUserData().toString());


        // Save the updated game
        addictionTreatmentFinderDao.setUserData(treatmentFinder);

        return getAskSpeechletResponse(propertyReader.getSpeechConnect(), propertyReader.getSpeechReprompt());
    }



    /**
     * Creates and returns response for Launch request.
     *
     * @param request
     *            {@link LaunchRequest} for this request
     * @param session
     *            Speechlet {@link Session} for this request
     * @return response for launch request
     */
    public SpeechletResponse getLaunchResponse(LaunchRequest request, Session session) {
        // Speak welcome message and ask user questions
        // based on whether there are players or not.

/* Commenting till i know better TODO
        String speechText, repromptText;
        AddictionTreatmentFinder game = addictionTreatmentFinderDao.getUserData(session);

        if (game == null || !game.hasPlayers()) {
            speechText = "ScoreKeeper, Let's start your game. Who's your first player?";
            repromptText = "Please tell me who is your first player?";
        } else if (!game.hasScores()) {
            speechText =
                    "ScoreKeeper, you have " + game.getNumberOfPlayers()
                            + (game.getNumberOfPlayers() == 1 ? " player" : " players")
                            + " in the game. You can give a player points, add another player,"
                            + " reset all players or exit. Which would you like?";
            repromptText = AddictionTreatmentFinderTextUtil.COMPLETE_HELP;
        } else {
            speechText = "ScoreKeeper, What can I do for you?";
            repromptText = AddictionTreatmentFinderTextUtil.NEXT_HELP;
        }

        return getAskSpeechletResponse(speechText, repromptText);
*/
        return getAskSpeechletResponse(propertyReader.getWelcomeMessage(), propertyReader.getSpeechReprompt());
    }

    /**
     * Creates and returns response for the new game intent.
     *
     * @param session
     *            {@link Session} for the request
     * @param skillContext
     *            {@link SkillContext} for this request
     * @return response for the new game intent.
     */
    public SpeechletResponse getStartInteractionIntentResponse (Intent intent, Session session, SkillContext skillContext) {
        Integer stepInSession = (Integer) session.getAttribute("SESSION-STEP");
        AddictionUserData addictionUserData = null;

        if (stepInSession == null) {
            return getAskSpeechletResponse(propertyReader.getWelcomeMessage(), propertyReader.getSpeechReprompt());
        }

        int iStepInSession = stepInSession.intValue();
        log.debug("Entered getStartInteractionIntentResponse With STEP in Session value of - " + stepInSession.intValue());

        if (iStepInSession == 0) {
            addictionUserData = skillContext.getAddictionUserData();
            if (addictionUserData == null) {
                addictionUserData = AddictionUserData.newInstance();
            }
            addictionUserData.setQuestionPhase(iStepInSession+1); //set the state ofspeech-welcome the next question

            log.debug("In 0 Addiction data - " + addictionUserData.toString());

            session.setAttribute("SESSION-STEP", new Integer(addictionUserData.getQuestionPhase()));
            skillContext.setAddictionUserData(addictionUserData);

            return getAskSpeechletResponse(propertyReader.getQuestion0(), propertyReader.getQuestion0());

        } else if (iStepInSession == 1) {
            addictionUserData = skillContext.getAddictionUserData();
            addictionUserData.setQuestionPhase(iStepInSession+1); //set the state of the next question
            session.setAttribute("SESSION-STEP", new Integer(addictionUserData.getQuestionPhase()));

            log.debug("In 1 Addiction data - " + addictionUserData.toString());

            skillContext.setAddictionUserData(addictionUserData);
            return getAskSpeechletResponse(propertyReader.getQuestion1(), propertyReader.getQuestion1());
        } else if (iStepInSession == 4) {
            addictionUserData = skillContext.getAddictionUserData();

//            String yesNoResponse = intent.getSlot(SLOT_USER_RESPONSE).getValue();
//            Iterator keys = intent.getSlots().keySet().iterator();
//            while (keys.hasNext()) {
//                log.debug("Yahoo" + (String) keys.next());
//
//            }
//
//            if (yesNoResponse == null) {
//                String speechText = "Sorry, I did not hear that. " + propertyReader.getQuestion3();
//                return getAskSpeechletResponse(speechText, speechText);
//            }
            addictionUserData.setLgbt("yes");
            addictionUserData.setQuestionPhase(iStepInSession + 1); //set the state of the next question
            session.setAttribute("SESSION-STEP", new Integer(addictionUserData.getQuestionPhase()));
            session.setAttribute("SESSION-USER-LGBT", addictionUserData.getLgbt());
            log.debug("In 4 Addiction data - " + addictionUserData.toString());

            skillContext.setAddictionUserData(addictionUserData);
            return getAskSpeechletResponse(addictionUserData.getfName() +", " + propertyReader.getQuestion4(), propertyReader.getQuestion4());
        } else if (iStepInSession == 5) {
            addictionUserData = skillContext.getAddictionUserData();

//            String yesNoResponse = intent.getSlot(SLOT_USER_RESPONSE).getValue();
//
//            if (yesNoResponse == null) {
//                String speechText = "Sorry, I did not hear that" + propertyReader.getQuestion4();
//                return getAskSpeechletResponse(speechText, speechText);
//            }
            addictionUserData.setPregnantOrSpecalizedCare("yes");
            addictionUserData.setQuestionPhase(iStepInSession + 1); //set the state of the next question
            session.setAttribute("SESSION-STEP", new Integer(addictionUserData.getQuestionPhase()));
            session.setAttribute("SESSION-USER-MEDICAL-ASSISTANCE", addictionUserData.getPregnantOrSpecalizedCare());
            log.debug("In 5 Addiction data - " + addictionUserData.toString());

            skillContext.setAddictionUserData(addictionUserData);
            return getAskSpeechletResponse(addictionUserData.getfName() +", " + propertyReader.getQuestion5(), propertyReader.getQuestion5());
        } else if (iStepInSession == 6) {
            // ToDO
            String userAge = (String) session.getAttribute("SESSION-USER_AGE");
            addictionUserData = AddictionUserData.newInstance();
            addictionUserData.setAgeType(userAge);
            addictionUserData.setQuestionPhase(iStepInSession+1); //set the state of the next question
            addictionUserData.setLgbt("lgbt");

            log.debug("In 1 Addiction data - " + addictionUserData.toString());
            skillContext.setAddictionUserData(addictionUserData);
            session.setAttribute("SESSION-USER_AGE", addictionUserData.getAgeType());
            session.setAttribute("SESSION-STEP", new Integer(addictionUserData.getQuestionPhase()));
            session.setAttribute("SESSION-LGBT", addictionUserData.getLgbt());
            return getAskSpeechletResponse("I am here", "I am here");
        } else {
            return getTellSpeechletResponse(propertyReader.getFatalError());
        }
    }

    public SpeechletResponse getReligiousMedicalChoiceIntentResponse (Intent intent, Session session,
                                                       SkillContext skillContext) {
        Integer stepInSession = (Integer) session.getAttribute("SESSION-STEP");
        AddictionUserData addictionUserData = skillContext.getAddictionUserData();

        log.debug("getReligiousMedicalChoiceIntentResponse STEP in Session - " + stepInSession.intValue());
        if (stepInSession == null) {
            return getAskSpeechletResponse(propertyReader.getWelcomeMessage(), propertyReader.getSpeechReprompt());
        }
        int iStepInSession = stepInSession.intValue();

        String religiousMedicalChoice = intent.getSlot(SLOT_USER_MEDICAL_RELIGIOUS_CHOICE).getValue();

        if (religiousMedicalChoice == null) {
            String speechText = "Sorry, I did not hear that" + propertyReader.getQuestion5();
            return getAskSpeechletResponse(speechText, speechText);
        }

        addictionUserData.setReligiousMedical(religiousMedicalChoice);

        log.debug("In getReligiousMedicalChoiceIntentResponse Logging Addiction contents - : " + addictionUserData.toString());

        addictionUserData.setQuestionPhase(iStepInSession+1); //set the state of the next question

        skillContext.setAddictionUserData(addictionUserData);
        session.setAttribute("SESSION-STEP", new Integer(addictionUserData.getQuestionPhase()));
        session.setAttribute("SESSION-USER_MEDICAL_CHOICE", addictionUserData.getReligiousMedical());

        return getAskSpeechletResponse(addictionUserData.getfName()+", " + propertyReader.getQuestion6(), propertyReader.getQuestion6());

    }


    public SpeechletResponse getUserAgeIntentResponse (Intent intent, Session session,
                                                       SkillContext skillContext) {

        Integer stepInSession = (Integer) session.getAttribute("SESSION-STEP");
        AddictionUserData addictionUserData = skillContext.getAddictionUserData();

        log.debug("getUserAgeIntentResponse STEP in Session - " + stepInSession.intValue());

        if (stepInSession == null) {
            return getAskSpeechletResponse(propertyReader.getWelcomeMessage(), propertyReader.getSpeechReprompt());
        }
        int iStepInSession = stepInSession.intValue();

        // add a player to the current game,
        // terminate or continue the conversation based on whether the intent
        // is from a one shot command or not.
        String userAge = intent.getSlot(SLOT_USER_AGE).getValue();

        if (userAge == null) {
            String speechText = "Sorry, I did not hear that" + propertyReader.getQuestion1();
            return getAskSpeechletResponse(speechText, speechText);
        }

        String newUserName = addictionUserData.getfName();
        log.debug("in getUserAgeIntentResponse userAge inputted is - " + userAge);
        if (userAge == null) {
            return getAskSpeechletResponse(newUserName+", " + propertyReader.getQuestion1(), propertyReader.getQuestion1());
        }
        addictionUserData.setAgeType(userAge);

        log.debug("In getUserAgeIntentResponse Logging Addiction contents - : " + addictionUserData.toString());

        addictionUserData.setQuestionPhase(iStepInSession+1); //set the state of the next question

        log.debug("In getUserAgeIntentResponse addiction data - " + addictionUserData.getAgeType());
        skillContext.setAddictionUserData(addictionUserData);
        session.setAttribute("SESSION-STEP", new Integer(addictionUserData.getQuestionPhase()));
        session.setAttribute("SESSION-USER_AGE", addictionUserData.getAgeType());

        return getAskSpeechletResponse(newUserName+", " + propertyReader.getQuestion3(), propertyReader.getQuestion3());
    }


    /**
     * Creates and returns response for the add player intent.
     *
     * @param intent
     *            {@link Intent} for this request
     * @param session
     *            Speechlet {@link Session} for this request
     * @param skillContext
     * @return response for the add player intent.
     */
    public SpeechletResponse getAddUserIntentResponse (Intent intent, Session session,
                                                       SkillContext skillContext) {

        Integer stepInSession = (Integer) session.getAttribute("SESSION-STEP");
        AddictionUserData addictionUserData = skillContext.getAddictionUserData();

        log.debug("getAddUserIntentResponse STEP in Session - " + stepInSession.intValue());

        if (stepInSession == null) {
            return getAskSpeechletResponse(propertyReader.getWelcomeMessage(), propertyReader.getSpeechReprompt());
        }
        int iStepInSession = stepInSession.intValue();

        String newUserName =
                AddictionTreatmentFinderTextUtil.getUserName(intent.getSlot(SLOT_USER_NAME).getValue());
        if (newUserName == null) {
            String speechText = "I did not catch that. Can you tell me your name please?";
            return getAskSpeechletResponse(speechText, speechText);
        }

        log.debug("in getAddUserIntentResponse name is + " + newUserName);

        addictionUserData.setQuestionPhase(iStepInSession+1); //set the state of the next question
        session.setAttribute("SESSION-STEP", new Integer(addictionUserData.getQuestionPhase()));
        session.setAttribute("SESSION-USER-NAME", newUserName);
        addictionUserData.setfName(newUserName);

        log.debug("In getAddUserIntentResponse Addiction data - " + addictionUserData.toString());

        skillContext.setAddictionUserData(addictionUserData);
        return getAskSpeechletResponse(newUserName+", " + propertyReader.getQuestion2(), propertyReader.getQuestion2());
    }

    /**
     * Creates and returns response for the help intent.
     *
     * @param intent
     *            {@link Intent} for this request
     * @param session
     *            {@link Session} for this request
     * @param skillContext
     *            {@link SkillContext} for this request
     * @return response for the help intent
     */
    public SpeechletResponse getHelpIntentResponse(Intent intent, Session session,
                                                   SkillContext skillContext) {


        return skillContext.needsMoreHelp() ? getAskSpeechletResponse(propertyReader.getSpeechHelp(), propertyReader.getSpeechHelp())
                : getTellSpeechletResponse(propertyReader.getSpeechHelp());
    }

    /**
     * Creates and returns response for the exit intent.
     *
     * @param intent
     *            {@link Intent} for this request
     * @param session
     *            {@link Session} for this request
     * @param skillContext
     *            {@link SkillContext} for this request
     * @return response for the exit intent
     */
    public SpeechletResponse getExitIntentResponse(Intent intent, Session session,
                                                   SkillContext skillContext) {
        session.setAttribute("SESSION-STEP", new Integer(0));
        session.setAttribute("USER-ADDICTION-DATA", AddictionUserData.newInstance());

        return skillContext.needsMoreHelp() ? getTellSpeechletResponse(propertyReader.getGoodBye())
                : getTellSpeechletResponse("");
    }

    /**
     * Returns an ask Speechlet response for a speech and reprompt text.
     *
     * @param speechText
     *            Text for speech output
     * @param repromptText
     *            Text for reprompt output
     * @return ask Speechlet response for a speech and reprompt text
     */
    private SpeechletResponse getAskSpeechletResponse(String speechText, String repromptText) {
        // Create the Simple card content.
        SimpleCard card = new SimpleCard();
        card.setTitle("Session");
        card.setContent(speechText);

        // Create the plain text output.
        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
        speech.setText(speechText);

        // Create reprompt
        PlainTextOutputSpeech repromptSpeech = new PlainTextOutputSpeech();
        repromptSpeech.setText(repromptText);
        Reprompt reprompt = new Reprompt();
        reprompt.setOutputSpeech(repromptSpeech);

        return SpeechletResponse.newAskResponse(speech, reprompt, card);
    }

    /**
     * Returns a tell Speechlet response for a speech and reprompt text.
     *
     * @param speechText
     *            Text for speech output
     * @return a tell Speechlet response for a speech and reprompt text
     */
    private SpeechletResponse getTellSpeechletResponse(String speechText) {
        // Create the Simple card content.
        SimpleCard card = new SimpleCard();
        card.setTitle("Session");
        card.setContent(speechText);

        // Create the plain text output.
        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
        speech.setText(speechText);

        return SpeechletResponse.newTellResponse(speech, card);
    }
}
