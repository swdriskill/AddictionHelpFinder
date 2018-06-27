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
import com.amazon.speech.ui.Card;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.Reprompt;
import com.amazon.speech.ui.SimpleCard;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import main.java.com.ATF.storage.*;
import main.java.com.ATF.utils.PropertyReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;

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

    public SpeechletResponse getCityStateIntentResponse (Intent intent, Session session, SkillContext skillContext) {

        Integer stepInSession = (Integer) session.getAttribute("SESSION-STEP");
        AddictionUserData addictionUserData = skillContext.getAddictionUserData();

        log.debug("getUserAgeIntentResponse STEP in Session - " + stepInSession.intValue());

        if (stepInSession == null) {
            return getAskSpeechletResponse(propertyReader.getWelcomeMessage(), propertyReader.getSpeechReprompt());
        }
        int iStepInSession = stepInSession.intValue();
        return getAskSpeechletResponse(propertyReader.getQuestion3underCityState(), propertyReader.getQuestion3underCityState());
    }

    public SpeechletResponse getCaptureCityStateIntentResponse (Intent intent, Session session, SkillContext skillContext) {

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
        String city = intent.getSlot(SLOT_USER_CITY).getValue();

        if (city == null) {
            String speechText = "Sorry, I did not hear that" + propertyReader.getQuestion3underCityState();
            return getAskSpeechletResponse(speechText, speechText);
        }

        String state = intent.getSlot(SLOT_USER_STATE).getValue();

        if (state == null) {
            String speechText = "Sorry, I did not hear that" + propertyReader.getQuestion3underCityState();
            return getAskSpeechletResponse(speechText, speechText);
        }

        log.debug("in getUserAgeIntentResponse userAge inputted is - " + city + "  -  " + state);

        addictionUserData.setCity(city);
        addictionUserData.setState(state);

        log.debug("In getUserAgeIntentResponse Logging Addiction contents - : " + addictionUserData.toString());

        addictionUserData.setQuestionPhase(iStepInSession+1); //set the state of the next question

        log.debug("In getUserAgeIntentResponse addiction data - " + addictionUserData.getAgeType());
        skillContext.setAddictionUserData(addictionUserData);
        session.setAttribute("SESSION-STEP", new Integer(addictionUserData.getQuestionPhase()));
        session.setAttribute("SESSION-USER_AGE", addictionUserData.getAgeType());
        session.setAttribute("SESSION-CITY", addictionUserData.getCity());
        session.setAttribute("SESSION-STATE", addictionUserData.getState());
// UBER TERMINATION OF FLOW todo
// SEARCH FOR THE PROVIDER IN CITY/STATE
// TEXT
// CONNECT CALL
        return getAskSpeechletResponse("in search", propertyReader.getSpeechReprompt());
    }

    public SpeechletResponse getConnectIntentResponse (Intent intent, Session session, SkillContext skillContext) {
        log.debug("In getConnectIntentResponse");

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
        AddictionTreatmentFinder game = addictionTreatmentFinderDao.getScoreKeeperGame(session);

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
    public SpeechletResponse getNewUserIntentResponse (Intent intent, Session session, SkillContext skillContext) {
        Integer stepInSession = (Integer) session.getAttribute("SESSION-STEP");
        AddictionUserData addictionUserData = null;

        if (stepInSession == null) {
            return getAskSpeechletResponse(propertyReader.getWelcomeMessage(), propertyReader.getSpeechReprompt());
        }

        int iStepInSession = stepInSession.intValue();
        String overAgeUserExperienceBegin = (String) session.getAttribute("SESSION-START_OVERAGE_USER");
        log.debug("Entered getNewUserIntentResponse With STEP in Session value of - " + stepInSession.intValue());
        log.debug("Entered getNewUserIntentResponse With overAgeUserExperienceBegin Session value of - " + overAgeUserExperienceBegin);

        if (iStepInSession == 0) {

            addictionUserData = skillContext.getAddictionUserData();
            if (addictionUserData == null) {
                addictionUserData = AddictionUserData.newInstance();
            }
            addictionUserData.setQuestionPhase(iStepInSession+1); //set the state ofspeech-welcome the next question
            log.debug("In 0 Addiction data - " + addictionUserData.toString());
            session.setAttribute("SESSION-STEP", new Integer(addictionUserData.getQuestionPhase()));
            skillContext.setAddictionUserData(addictionUserData);

            return getAskSpeechletResponse(propertyReader.getQuestion0(), propertyReader.getSpeechReprompt());
        } else if (iStepInSession == 1) {
            addictionUserData = skillContext.getAddictionUserData();
            addictionUserData.setQuestionPhase(iStepInSession+1); //set the state of the next question

            log.debug("In 1 Addiction data - " + addictionUserData.toString());
            skillContext.setAddictionUserData(addictionUserData);
            session.setAttribute("SESSION-STEP", new Integer(addictionUserData.getQuestionPhase()));
            return getAskSpeechletResponse(propertyReader.getQuestion1(), propertyReader.getSpeechReprompt());
        } else {
            return getTellSpeechletResponse(propertyReader.getFatalError());
        }


/*
        AddictionTreatmentFinder ongoingSession = addictionTreatmentFinderDao.getScoreKeeperGame(session);

        if (game == null) {
            return getAskSpeechletResponse("New game started. Who's your first player?",
                    "Please tell me who\'s your first player?");
        }

        // Reset current game
        game.resetScores();
        addictionTreatmentFinderDao.saveScoreKeeperGame(game);

        String speechText =
                "New game started with " + game.getNumberOfPlayers() + " existing player"
                        + (game.getNumberOfPlayers() != 1 ? "" : "s") + ".";

        if (skillContext.needsMoreHelp()) {
            String repromptText =
                    "You can give a player points, add another player, reset all players or "
                            + "exit. What would you like?";
            speechText += repromptText;
            return getAskSpeechletResponse(speechText, repromptText);
        } else {
            return getTellSpeechletResponse(speechText);
        }
*/
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

        log.debug("in getUserAgeIntentResponse userAge inputted is - " + userAge);
        if (userAge == null) {
            return getAskSpeechletResponse(propertyReader.getQuestion1(), propertyReader.getQuestion1());
        }
        addictionUserData.setAgeType(userAge);

        log.debug("In getUserAgeIntentResponse Logging Addiction contents - : " + addictionUserData.toString());

        addictionUserData.setQuestionPhase(iStepInSession+1); //set the state of the next question

        log.debug("In getUserAgeIntentResponse addiction data - " + addictionUserData.getAgeType());
        skillContext.setAddictionUserData(addictionUserData);
        session.setAttribute("SESSION-STEP", new Integer(addictionUserData.getQuestionPhase()));
        session.setAttribute("SESSION-USER_AGE", addictionUserData.getAgeType());

        if (userAge.equals("under 18")) {
            return getAskSpeechletResponse(propertyReader.getQuestion2under(), propertyReader.getQuestion2under());
        } else if (userAge.equals("over 18")) {
            session.setAttribute("SESSION-START_OVERAGE_USER", addictionUserData.getAgeType());
            return getAskSpeechletResponse(propertyReader.getQuestion2over(), propertyReader.getQuestion2over());
        }

        return getAskSpeechletResponse(propertyReader.getQuestion1(), propertyReader.getSpeechReprompt());
/*
        // Load the previous game
        AddictionTreatmentFinder game = addictionTreatmentFinderDao.getScoreKeeperGame(session);
        if (game == null) {
            game = AddictionTreatmentFinder.newInstance(session, AddictionHelperData.newInstance());
        }

        game.addPlayer(newUserName);

        // Save the updated game
        addictionTreatmentFinderDao.saveScoreKeeperGame(game);

        String speechText = newUserName + " has joined your game. ";
        String repromptText = null;

        if (skillContext.needsMoreHelp()) {
            if (game.getNumberOfPlayers() == 1) {
                speechText += "You can say, I am done adding players. Now who's your next player?";

            } else {
                speechText += "Who is your next player?";
            }
            repromptText = AddictionTreatmentFinderTextUtil.NEXT_HELP;
        }

        if (repromptText != null) {
            return getAskSpeechletResponse(speechText, repromptText);
        } else {
            return getTellSpeechletResponse(speechText);
        }
*/


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
        // add a player to the current game,
        // terminate or continue the conversation based on whether the intent
        // is from a one shot command or not.
        String newUserName =
                AddictionTreatmentFinderTextUtil.getUserName(intent.getSlot(SLOT_USER_NAME).getValue());
        if (newUserName == null) {
            String speechText = "OK. Who do you want to add?";
            return getAskSpeechletResponse(speechText, speechText);
        }

        // Load the previous game
        AddictionTreatmentFinder game = addictionTreatmentFinderDao.getScoreKeeperGame(session);
        if (game == null) {
            game = AddictionTreatmentFinder.newInstance(session, AddictionHelperData.newInstance());
        }

        game.addPlayer(newUserName);

        // Save the updated game
        addictionTreatmentFinderDao.saveScoreKeeperGame(game);

        String speechText = newUserName + " has joined your game. ";
        String repromptText = null;

        if (skillContext.needsMoreHelp()) {
            if (game.getNumberOfPlayers() == 1) {
                speechText += "You can say, I am done adding players. Now who's your next player?";

            } else {
                speechText += "Who is your next player?";
            }
            repromptText = AddictionTreatmentFinderTextUtil.NEXT_HELP;
        }

        if (repromptText != null) {
            return getAskSpeechletResponse(speechText, repromptText);
        } else {
            return getTellSpeechletResponse(speechText);
        }
    }

    /**
     * Creates and returns response for the add score intent.
     *
     * @param intent
     *            {@link Intent} for this request
     * @param session
     *            {@link Session} for this request
     * @param skillContext
     *            {@link SkillContext} for this request
     * @return response for the add score intent
     */
    public SpeechletResponse getAddScoreIntentResponse(Intent intent, Session session,
                                                       SkillContext skillContext) {
        String playerName =
                AddictionTreatmentFinderTextUtil.getUserName(intent.getSlot(SLOT_USER_NAME).getValue());
        if (playerName == null) {
            String speechText = "Sorry, I did not hear the player name. Please say again?";
            return getAskSpeechletResponse(speechText, speechText);
        }

        int score = 0;
        try {
            score = Integer.parseInt(intent.getSlot(SLOT_SCORE_NUMBER).getValue());
        } catch (NumberFormatException e) {
            String speechText = "Sorry, I did not hear the points. Please say again?";
            return getAskSpeechletResponse(speechText, speechText);
        }

        AddictionTreatmentFinder game = addictionTreatmentFinderDao.getScoreKeeperGame(session);
        if (game == null) {
            return getTellSpeechletResponse("A game has not been started. Please say New Game to "
                    + "start a new game before adding scores.");
        }

        if (game.getNumberOfPlayers() == 0) {
            String speechText = "Sorry, no player has joined the game yet. What can I do for you?";
            return getAskSpeechletResponse(speechText, speechText);
        }

        // Update score
        if (!game.addScoreForPlayer(playerName, score)) {
            String speechText = "Sorry, " + playerName + " has not joined the game. What else?";
            return getAskSpeechletResponse(speechText, speechText);
        }

        // Save game
        addictionTreatmentFinderDao.saveScoreKeeperGame(game);

        // Prepare speech text. If the game has less than 3 players, skip reading scores for each
        // player for brevity.
        String speechText = score + " for " + playerName + ". ";
        if (game.getNumberOfPlayers() > MAX_PLAYERS_FOR_SPEECH) {
            speechText += playerName + " has " + game.getScoreForPlayer(playerName) + " in total.";
        } else {
            speechText += getAllScoresAsSpeechText(game.getAllScoresInDescndingOrder());
        }

        return getTellSpeechletResponse(speechText);
    }

    /**
     * Creates and returns response for the tell scores intent.
     *
     * @param intent
     *            {@link Intent} for this request
     * @param session
     *            {@link Session} for this request
     * @return response for the tell scores intent
     */
    public SpeechletResponse getTellScoresIntentResponse(Intent intent, Session session) {
        // tells the scores in the leaderboard and send the result in card.
        AddictionTreatmentFinder game = addictionTreatmentFinderDao.getScoreKeeperGame(session);

        if (game == null || !game.hasPlayers()) {
            return getTellSpeechletResponse("Nobody has joined the game.");
        }

        SortedMap<String, Long> sortedScores = game.getAllScoresInDescndingOrder();
        String speechText = getAllScoresAsSpeechText(sortedScores);
        Card leaderboardScoreCard = getLeaderboardScoreCard(sortedScores);

        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
        speech.setText(speechText);

        return SpeechletResponse.newTellResponse(speech, leaderboardScoreCard);
    }

    /**
     * Creates and returns response for the reset players intent.
     *
     * @param intent
     *            {@link Intent} for this request
     * @param session
     *            {@link Session} for this request
     * @return response for the reset players intent
     */
    public SpeechletResponse getResetPlayersIntentResponse(Intent intent, Session session) {
        // Remove all players
        AddictionTreatmentFinder game =
                AddictionTreatmentFinder.newInstance(session, AddictionHelperData.newInstance());
        addictionTreatmentFinderDao.saveScoreKeeperGame(game);
        session.setAttribute("SESSION-STEP", new Integer(0));
        session.setAttribute("USER-ADDICTION-DATA", AddictionUserData.newInstance());

        String speechText = "New game started without players. Who do you want to add first?";
        return getAskSpeechletResponse(speechText, speechText);
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
        return skillContext.needsMoreHelp() ? getAskSpeechletResponse(
                AddictionTreatmentFinderTextUtil.COMPLETE_HELP + " So, how can I help?",
                AddictionTreatmentFinderTextUtil.NEXT_HELP)
                : getTellSpeechletResponse(AddictionTreatmentFinderTextUtil.COMPLETE_HELP);
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

        return skillContext.needsMoreHelp() ? getTellSpeechletResponse("Okay. Whenever you're "
                + "ready, you can start giving points to the players in your game.")
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

    /**
     * Converts a {@link Map} of scores into text for speech. The order of the entries in the text
     * is determined by the order of entries in {@link Map#entrySet()}.
     *
     * @param scores
     *            A {@link Map} of scores
     * @return a speech ready text containing scores
     */
    private String getAllScoresAsSpeechText(Map<String, Long> scores) {
        StringBuilder speechText = new StringBuilder();
        int index = 0;
        for (Entry<String, Long> entry : scores.entrySet()) {
            if (scores.size() > 1 && index == scores.size() - 1) {
                speechText.append(" and ");
            }
            String singularOrPluralPoints = entry.getValue() == 1 ? " point, " : " points, ";
            speechText
                    .append(entry.getKey())
                    .append(" has ")
                    .append(entry.getValue())
                    .append(singularOrPluralPoints);
            index++;
        }

        return speechText.toString();
    }

    /**
     * Creates and returns a {@link Card} with a formatted text containing all scores in the game.
     * The order of the entries in the text is determined by the order of entries in
     * {@link Map#entrySet()}.
     *
     * @param scores
     *            A {@link Map} of scores
     * @return leaderboard text containing all scores in the game
     */
    private Card getLeaderboardScoreCard(Map<String, Long> scores) {
        StringBuilder leaderboard = new StringBuilder();
        int index = 0;
        for (Entry<String, Long> entry : scores.entrySet()) {
            index++;
            leaderboard
                    .append("No. ")
                    .append(index)
                    .append(" - ")
                    .append(entry.getKey())
                    .append(" : ")
                    .append(entry.getValue())
                    .append("\n");
        }

        SimpleCard card = new SimpleCard();
        card.setTitle("Leaderboard");
        card.setContent(leaderboard.toString());
        return card;
    }
}
