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
import main.java.com.ATF.storage.main.java.com.ATF.storage.addictioncenters.AddictionCenterFinder;
import main.java.com.ATF.storage.main.java.com.ATF.storage.addictioncenters.AddictionCentersData;
import main.java.com.ATF.storage.main.java.com.ATF.storage.addictioncenters.AddictionCentersFinderDao;
import main.java.com.ATF.storage.main.java.com.ATF.storage.addictioncenters.AddictionCentersFinderDynamoDbClient;
import main.java.com.ATF.utils.PropertyReader;
import main.java.com.ATF.utils.twilio.TwilioCallText;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Array;
import java.util.*;

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
    private static final String SLOT_PHONE_NUMBER = "PhoneNo";

    /**
     * Intent slot for player score.
     */
    private static final String SLOT_SCORE_NUMBER = "ScoreNumber";


    /**
     * Maximum number of players for which scores must be announced while adding a score.
     */
    private static final int MAX_PLAYERS_FOR_SPEECH = 3;

    private final AddictionTreatmentFinderDao addictionTreatmentFinderDao;
    private final AddictionCentersFinderDao addictionCentersFinderDao;


    public AddictionTreatmentFinderManager (final AmazonDynamoDBClient amazonDynamoDbClient) {
        AddictionTreatmentFinderDynamoDbClient dynamoDbClient =
                new AddictionTreatmentFinderDynamoDbClient(amazonDynamoDbClient);
        AddictionCentersFinderDynamoDbClient addictionCentersDynamoDbClient =
                new AddictionCentersFinderDynamoDbClient(amazonDynamoDbClient);


        addictionTreatmentFinderDao = new AddictionTreatmentFinderDao(dynamoDbClient);
        addictionCentersFinderDao = new AddictionCentersFinderDao(addictionCentersDynamoDbClient);
    }



    public SpeechletResponse getConnectIntentResponse (Intent intent, Session session, SkillContext skillContext) {
        log.debug("In getConnectIntentResponse");

        Integer stepInSession = (Integer) session.getAttribute("SESSION-STEP");
        AddictionUserData addictionUserData = null;

        if (stepInSession == null) {
            return getAskSpeechletResponse(propertyReader.getWelcomeMessage(), propertyReader.getSpeechReprompt());
        }

        int iStepInSession = stepInSession.intValue();
        log.debug("Entered getConnectIntentResponse With STEP in Session value of - " + stepInSession.intValue());

        addictionUserData = skillContext.getAddictionUserData();

        log.debug("in getConnectIntentResponse 1 " + skillContext.getAddictionUserData().toString());

        AddictionTreatmentFinder treatmentFinder = addictionTreatmentFinderDao.getUserData(session);
        if (treatmentFinder == null) {
            treatmentFinder = AddictionTreatmentFinder.newInstance(session, addictionUserData);
        }
        treatmentFinder.setUserData(addictionUserData);

        String prompt = "Thanks for using the skill. You will recieve a call shortly.";

        String ifNationalFacilityCase = (String) session.getAttribute("SESSION-NATIONAL-FACILITY");
        log.debug("in getConnectIntentResponse 2 " + ifNationalFacilityCase);

        if (ifNationalFacilityCase != null && ifNationalFacilityCase.equalsIgnoreCase("yes")) {
            TwilioCallText twilioCallText = new TwilioCallText();
            twilioCallText.connectCall("+19149537025");

           //todo Connection the call with National Facility Number
            log.debug("TODo - Connect the call with National Facility Number");
        } else {


            List<AddictionCentersData> addictionCenterDataList = addictionCentersFinderDao.scanCityState(session, propertyReader.getAlcoholCentersTable());

            for (AddictionCentersData addictionCenterData : addictionCenterDataList) {
                log.debug("TODo - Search the center with state and city, connect the call");
                log.debug(addictionCenterData.toString());
            }

            //todo search of the center with state, city data
            //todo place the call
        }

        // Save the updated game
        addictionTreatmentFinderDao.setUserData(treatmentFinder, session);

//        return getAskSpeechletResponse(propertyReader.getSpeechConnect(), propertyReader.getSpeechReprompt());
        return getTellSpeechletResponse(treatmentFinder.getUserData().getfName() + " " + prompt);

    }

    public SpeechletResponse getSearchIntentResponse (Intent intent, Session session, SkillContext skillContext) {
        log.debug("In getConnectIntentResponse");

        // Load the previous game
        AddictionTreatmentFinder treatmentFinder = addictionTreatmentFinderDao.getUserData(session);

        if (treatmentFinder == null) {
            treatmentFinder = AddictionTreatmentFinder.newInstance(session, skillContext.getAddictionUserData());
        }

        log.debug("in getSearchIntentResponse 1 " + skillContext.getAddictionUserData().toString());
        treatmentFinder.setUserData(skillContext.getAddictionUserData());

        log.debug("in getSearchIntentResponse 2 " + skillContext.getAddictionUserData().toString());
        log.debug("in getSearchIntentResponse 3 " + treatmentFinder);
        log.debug("in getSearchIntentResponse 4 " + treatmentFinder.getUserData());

        String prompt = "Thanks for using the skill. You will recieve a text with details shortly.";

        String ifNationalFacilityCase = (String) session.getAttribute("SESSION-NATIONAL-FACILITY");
        if (ifNationalFacilityCase != null && ifNationalFacilityCase.equalsIgnoreCase("yes")) {
            TwilioCallText twilioCallText = new TwilioCallText();
            twilioCallText.sendText(propertyReader.getTwilioNumber(), skillContext.getAddictionUserData().getPhoneNumber(), propertyReader.getTextMessageContent());

            log.debug("Text the details of with National Facility Number");
        } else {
            // TODO: 7/19/18  
            /*AddictionCentersFinder addictionCentersFinder = addictionTreatmentFinderDao.getUserData(session);
            if (treatmentFinder == null) {
                treatmentFinder = AddictionTreatmentFinder.newInstance(session, addictionUserData);
            }
            
            AddictionCentersFinder addictionCentersFinder = new AddictionCentersFinder()();
            addictionCentersFinderDao.setAddictionData();*/
//Todo remove after loading date
            AddictionCentersData addictionCentersData = AddictionCentersData.newInstance();

//Populate the data List
            ArrayList list = new ArrayList();
            list.add("Shelby County Treatment Center::750 Highway 31 South::Alabaster:Alabama:35007::Shelby:205-216-0200:1::1");
            list.add("Chilton County Treatment Center::2100 Holiday Inn Drive::Clanton:Alabama:35046::Chilton:205-755-4300:1::1");
            list.add("Cullman Area Mental Health Authority::1909 Commerce Avenue::Cullman:Alabama:35055::Cullman:256-734-4688:::1");
            list.add("Lighthouse Inc::925 Convent Road NE::Cullman:Alabama:35055::Cullman:256-739-2777:1::1");
            list.add("Bridge Inc:Recovery Center for Teens/Cullman:402 Arnold Street NE:Suite 104:Cullman:Alabama:35055::Cullman:256-775-8301::1:1");
            list.add("Cullman County Treatment Center::1912 Commerce Avenue NW::Cullman:Alabama:35056::Cullman:256-739-5595:::1");
            list.add("Hope House Inc::1000 Lincoln Avenue:Suite B:Oneonta:Alabama:35121::Blount:205-625-4673::1:1");
            list.add("Bradford Health Services:Warrior:1189 Allbritton Road::Warrior:Alabama:35180::Jefferson:800-333-1865:::1");
            list.add("University of Alabama at Birmingham:Beacon Addiction Treatment Center:401 Beacon Parkway West:Suite 150:Birmingham:Alabama:35209::Jefferson:205-917-3733 x205:1:1:1");
            list.add("Zukoski Outpatient::601 Princeton Avenue SW::Birmingham:Alabama:35211::Jefferson:205-785-5787:1::1");
            list.add("Alcohol and Drug Abuse:Treatment Centers Inc/Pearson Hall:2701 Jefferson Avenue SW::Birmingham:Alabama:35211::Jefferson:205-923-6552 x12:::1");
            list.add("Bradford Health Services:Birmingham Regional Office/Jefferson:300 Century Park South:Suite 100:Birmingham:Alabama:35226::Jefferson:205-942-3200::1:1");
            list.add("Tuscaloosa Treatment Center::1001 Mimosa Park Road::Tuscaloosa:Alabama:35405::Tuscaloosa:205-752-5857:::");
            list.add("Northwest Alabama MH Center:Awakening Recovery:593 Highway 78 West:Parkland Shopping Center:Jasper:Alabama:35501::Walker:205-295-2336::1:1");
            list.add("Walker Recovery Center Inc::2195 North Airport Road::Jasper:Alabama:35504::Walker:205-221-1799:::1");
            list.add("Northwest Alabama MH Center:Start Program:2584 Highway 96::Fayette:Alabama:35555::Fayette:205-442-7049::1:1");
            list.add("Northwest Alabama MH Center:Begin Again:123 2nd Avenue NW::Fayette:Alabama:35555::Fayette:205-932-3216:::1");
            list.add("Marion County Mental Health::427 Smokey Bear Road::Hamilton:Alabama:35570::Marion:205-921-2186:::1");
            list.add("Northwest Alabama MH Center::141 2nd Avenue NW::Vernon:Alabama:35592::Lamar:205-695-9183:::1");
            list.add("Northwest Alabama MH Center:Winfield Outpatient Program:260 Baker Street::Winfield:Alabama:35594::Fayette:205-487-2124::1:1");
            list.add("Marwin Counseling Services::1065 U.S. Highway 43::Winfield:Alabama:35594:1427:Marion:205-487-0359:::1");
            list.add("Bradford Health Services:Florence Outreach:303 East College Street:Suite A:Florence:Alabama:35630:5709:Lauderdale:256-760-0200::1:1");
            list.add("Health Connect America::614 South Court Street::Florence:Alabama:35630::Lauderdale:256-712-2822::1:1");
            list.add("Bradford Health Services:Madison Residential Facility:1600 Browns Ferry Road::Madison:Alabama:35758::Madison:800-879-7272::1:1");
            list.add("AIDS Action Coalition::600 Saint Clair Avenue:Building 9 Suite 23:Huntsville:Alabama:35801::Madison:256-536-4700 x121:1::1");
            list.add("Wellstone Huntsville MHC:New Horizons Recovery Center:4040 Memorial Parkway South:Suite C:Huntsville:Alabama:35802::Madison:256-532-4141:::1");
            list.add("Pathfinder Inc::3104 Ivy Avenue SW::Huntsville:Alabama:35805::Madison:256-534-7644:::1");
            list.add("Huntsville Recovery Inc::4040 Independence Drive::Huntsville:Alabama:35816::Madison:256-721-1940:::1");
            list.add("New Life for Women::102 Centurion Way::Gadsden:Alabama:35904::Etowah:256-413-0200:::1");
            list.add("Mountain View Hospital:Outpatient Services:3001 Scenic Highway::Gadsden:Alabama:35904::Etowah:800-245-3645:::");
            list.add("Bridge Inc::3232 Lay Springs Road::Gadsden:Alabama:35904::Etowah:256-546-6324:1:1:1");
            list.add("Bridge Inc:Fort Payne Recovery Center:100 7th Street NE::Fort Payne:Alabama:35967::DeKalb:256-845-7767:::");
            list.add("A Nu Direction::500 Hospital Drive::Wetumpka:Alabama:36092::Elmore:334-567-4311 x152:::1");
            list.add("Chemical Addictions Program Inc :(CAP)/Pegasus:1151 Air Base Boulevard::Montgomery:Alabama:36108::Montgomery:334-269-2150:::");
            list.add("Lighthouse Counseling Center Inc:Intensive Outpatient Unit:111 Coliseum Boulevard::Montgomery:Alabama:36109:2707:Montgomery:334-286-5980:::");
            list.add("Montgomery Metro Treatment Center::6001 East Shirley Lane::Montgomery:Alabama:36117::Montgomery:334-244-1618:::");
            list.add("Highland Health Systems:New Directions:1640 Coleman Road::Oxford:Alabama:36203::Calhoun:256-236-8003:::1");
            list.add("MedMark Treatment Centers:Oxford:118 East Choccolocco Street::Oxford:Alabama:36203::Calhoun:256-831-4601:::1");
            list.add("Bradford Health Services:Oxford Anniston:1713 Hamric Drive East::Oxford:Alabama:36203::Calhoun:256-237-4209:1:1:1");
            list.add("AltaPointe Health Systems:SA Level 1 Outpatient:88217 Highway 9::Lineville:Alabama:36266::Clay:256-396-2150:::1");
            list.add("Southeast Intervention Group Inc:Herring Houses/Step By Step Recov:101 North Herring Street::Dothan:Alabama:36303::Houston:334-699-3175:::1");
            list.add("Lyster Army Health Clinic:Behavioral Health/SUDCC:301 Andrews Avenue::Fort Rucker:Alabama:36362::Dale:334-255-7028:::1");
            list.add("Gulf Coast Treatment Center::12271 Interchange Road::Grand Bay:Alabama:36541::Mobile:251-865-0123:::1");
            list.add("CED Mental Health Center:Substance Abuse Services:200 Dean Buttram Senior Avenue::Centre:Alabama:35960::Cherokee:256-927-3601:::1");
            list.add("CED Fellowship House Inc::4209 Brooke Avenue::Gadsden:Alabama:35904::Etowah:256-413-3470:1::1");
            list.add("Sunrise Lodge:Substance Abuse Treatment Center:1163 Washington Avenue SW::Russellville:Alabama:35653::Franklin:256-332-0078:::1");
            list.add("WellStone Inc:Nova Center for Youth and Family:1900 Golf Road SW::Huntsville:Alabama:35802::Madison:256-705-6493::1:");
            list.add("Bradford Health Services:Boaz Regional Office:703 Medical Center Parkway::Boaz:Alabama:35957::Marshall:256-593-6373:::1");
            list.add("Lighthouse of Tallapoosa County Inc:Substance Abuse Rehab Program/Resid:36 Franklin Street::Alexander City:Alabama:35010::Tallapoosa:256-234-4894:::1");
            list.add("Northwest Alabama Treatment Center::4204 Edmonton Drive::Bessemer:Alabama:35022::Jefferson:205-425-1200:1::1");
            list.add("Chilton Shelby Mental Health Center::151 Hamilton Lane::Calera:Alabama:35040::Shelby:205-668-4308::1:1");
            list.add("Chilton Shelby Mental Health Center::110 Medical Center Drive::Clanton:Alabama:35045::Chilton:205-755-5933::1:1");
            list.add("New Pathways Inc::1508 Bunt Drive::Pell City:Alabama:35125::Saint Clair:205-814-1423::1:1");
            list.add("Aletheia House::201 Finley Avenue West::Birmingham:Alabama:35204::Jefferson:205-324-6502:::1");
            list.add("Fellowship House Inc::1625 12th Avenue South::Birmingham:Alabama:35205::Jefferson:205-933-2430:::1");
            list.add("Tri County Treatment Center::5605 Clifford Circle::Birmingham:Alabama:35210::Jefferson:205-836-3345:::");
            list.add("Birmingham Metro Treatment Center::151 Industrial Drive::Birmingham:Alabama:35211::Jefferson:205-941-1799:::1");
            list.add("Department of :Veterans Affairs Medical Center:700 South 19th Street::Birmingham:Alabama:35233::Jefferson:205-933-8101:::1");
            list.add("Indian Rivers Mental Health Center:A Womans Place:2209 9th Street::Tuscaloosa:Alabama:35401::Tuscaloosa:205-391-3131:::1");
            list.add("Indian Rivers Mental Health Center:Substance Abuse Services:2209 9th Street::Tuscaloosa:Alabama:35401::Tuscaloosa:205-391-3131 x1206:::");
            list.add("Phoenix House Inc::700 35th Avenue::Tuscaloosa:Alabama:35401::Tuscaloosa:205-758-3867:1::1");
            list.add("Veterans Affairs Medical Center:Comprehensive OP Substance Abuse:3701 Loop Road East::Tuscaloosa:Alabama:35404::Tuscaloosa:205-554-2000 x2767:::1");
            list.add("Bridge Inc:Recovery Ctr for Teens/Tuscaloosa:6001 12th Avenue East::Tuscaloosa:Alabama:35405::Tuscaloosa:205-344-6483::1:1");
            list.add("West Alabama Mental Health Center:Greene County :301 Prairie Avenue::Eutaw:Alabama:35462::Greene:205-372-3106::1:1");
            list.add("West Alabama Mental Health Center:Sumter County:1121 North Washington Street::Livingston:Alabama:35470::Sumter:205-652-6731:1:1:1");
            list.add("Bradford Health Services:Tuscaloosa Regional Office:515 Energy Center Boulevard::Northport:Alabama:35473::Tuscaloosa:205-750-0227::1:1");
            list.add("Northwest Alabama MH Center::71 Carraway Drive::Haleyville:Alabama:35565::Winston:205-486-4111:::1");
            list.add("Marion County Treatment Center::1879 Military Street South::Hamilton:Alabama:35570::Marion:205-921-3799:::1");
            list.add("Family Life Center Inc::110 Johnston Street SE::Decatur:Alabama:35601::Morgan:256-355-3703:::1");
            list.add("Family Life Center::410 South Jefferson Street::Athens:Alabama:35611::Limestone:256-216-3917:::1");
            list.add("Riverbend Center for Mental Health:Substance Abuse Services:635 West College Street::Florence:Alabama:35630::Lauderdale:256-764-3431 x205::1:1");
            list.add("Freedom House::54 Wheeler Hills Road::Rogersville:Alabama:35652::Lauderdale:256-247-1222:::1");
            list.add("Shoals Treatment Center Inc::3430 North Jackson Highway::Sheffield:Alabama:35660::Colbert:256-383-6646:::1");
            list.add("Family Life Center::250 Sun Temple Drive:Suite B-3:Madison:Alabama:35758::Madison:256-601-8846:::1");
            list.add("Family Life Center::211 South Market Street::Scottsboro:Alabama:35768::Jackson:256-574-3448:::1");
            list.add("Stevenson Recovery Center::196 County Road 85::Stevenson:Alabama:35772::Jackson:256-437-2728:::1");
            list.add("Huntsville Metro Treatment Center::2227 Drake Avenue:Suite 19:Huntsville:Alabama:35805::Madison:256-881-1311:1::1");
            list.add("Bradford Health Services:Huntsville Regional Office:220 Providence Main Street NW:Suite 200:Huntsville:Alabama:35806:4831:Madison:256-517-7002::1:1");
            list.add("Substance Use Disorder Clinical Care::Building 4100 Goss Road::Huntsville:Alabama:35898:5000:Madison:256-876-7256:::1");
            list.add("Gadsden Treatment Center::1121 Gardner Street::Gadsden:Alabama:35901::Etowah:256-549-0807:::1");
            list.add("Cherokee/Etowah/DeKalb MH Center:Substance Abuse Servs Men/Women:425 5th Avenue NW::Attalla:Alabama:35954::Etowah:256-492-7800:::1");
            list.add("Rapha Treatment Center::677 West Covington Avenue::Attalla:Alabama:35954::Etowah:256-538-7458:::1");
            list.add("Family Life Center::677 West Covington Avenue::Attalla:Alabama:35954::Etowah:256-538-7458:::1");
            list.add("Family Life Center::141 Main Street::Centre:Alabama:35960::Cherokee:256-927-4722:::1");
            list.add("CED Mental Health Center::301 14th Street NW::Fort Payne:Alabama:35967:3155:DeKalb:256-845-4571:::1");
            list.add("Family Life Center::300 Gault Avenue South::Fort Payne:Alabama:35967::De Kalb:256-997-9356:::1");
            list.add("Family Life Center::432 Gunter Avenue::Guntersville:Alabama:35976::Marshall:256-582-1471:::1");
            list.add("Cedar Lodge:A Program of MLBH:22165 U.S. Highway 431::Guntersville:Alabama:35976::Marshall:256-582-4465 x203:::1");
            list.add("SpectraCare:Outpatient:133 North Orange Avenue::Eufaula:Alabama:36027::Barbour:800-951-4357:1::1");
            list.add("East Central Mental Health Inc:Substance Abuse Program:200 Cherry Street::Troy:Alabama:36081::Pike:334-566-6022:::1");
            list.add("Central Alabama Veterans:Healthcare System:2400 Hospital Road::Tuskegee:Alabama:36083::Macon:334-727-0550:::1");
            list.add("Chemical Addictions Program Inc:Capital Recovery Center (CRC):1155 Air Base Boulevard::Montgomery:Alabama:36108::Montgomery:334-323-3214:::");
            list.add("Chemical Addictions Program Inc:(CAP)/Adol Intensive Outpt Prog:1153 Air Base Boulevard::Montgomery:Alabama:36108::Montgomery:334-269-2150::1:1");
            list.add("Bradford Health Services:Montgomery Regional Office:386 Saint Lukes Drive::Montgomery:Alabama:36117::Montgomery:800-873-2887::1:1");
            list.add("Anniston Fellowship House Inc::106 East 22nd Street::Anniston:Alabama:36201::Calhoun:256-236-7229:::1");
            list.add("Health Services Center Inc:CORE Calhoun County:801 Noble Street:Suite 100:Anniston:Alabama:36201::Calhoun:256-237-5993:::1");
            list.add("RMC Jacksonville::1701 Pelham Road South::Jacksonville:Alabama:36265::Calhoun:256-782-4330:::1");
            list.add("SpectraCare:Adult Outpatient Services:1672 Columbia Highway::Dothan:Alabama:36303::Houston:800-951-4357:1::1");
            list.add("SpectraCare:The Haven:831 John D Odom Road::Dothan:Alabama:36303::Houston:334-951-4357:1::1");
            list.add("SpectraCare:Henry County/OP:219 Dothan Road::Abbeville:Alabama:36310::Henry:800-951-4357:1::1");
            list.add("SpectraCare:Geneva County/Outpatient:1203 West Maple Street::Geneva:Alabama:36340::Geneva:800-951-4357:1:1:1");
            list.add("MedMark Treatment Centers:Dothan:9283 U.S. Highway 84 West::Newton:Alabama:36352::Houston:334-692-4455:::1");
            list.add("SpectraCare:Dale County:134 Katherine Avenue::Ozark:Alabama:36360::Dale:800-951-4357:::1");
            list.add("Shoulder::31214 Coleman Lane::Spanish Fort:Alabama:36527::Baldwin:251-626-2199:::1");
            list.add("Dauphin Way Lodge::1009 Dauphin Street::Mobile:Alabama:36604::Mobile:251-438-1625:::1");
            list.add("Mobile Metro Treatment Center:New Season:1924 Dauphin Island Parkway:Suite C:Mobile:Alabama:36605::Mobile:251-476-5733:1::");
            list.add("Bridge Inc:Mobile Addictions Treatment Center:722 Downtowner Loop West::Mobile:Alabama:36609::Mobile:251-338-1780 x7052::1:1");
            list.add("ECD Program Inc::808 Downtowner Loop West::Mobile:Alabama:36609::Mobile:251-341-9504:::1");
            list.add("Franklin Primary Health Center Inc::510 South Wilson Avenue::Mobile:Alabama:36610::Mobile:251-434-8195:1::");
            list.add("AltaPointe Health Systems Inc:AltaPointe Medication Assisted Trt:4211 Government Boulevard::Mobile:Alabama:36693::Mobile:251-666-2569:::1");
            list.add("Bridge Inc Addiction Treatment Center:Westwood:3401 Newman Road::Mobile:Alabama:36695::Mobile:251-633-0475:1:1:1");
            list.add("Bradford Health Services:Mobile Regional Facility:1000 Hillcrest Road:Suite 304:Mobile:Alabama:36695::Mobile:800-333-0906::1:1");
            list.add("Serenity Care Inc::1951 Dawes Road::Mobile:Alabama:36695::Mobile:251-635-1942:::1");
            list.add("East Alabama Mental Health:Opelika Addiction Center:2300 Center Hills Drive::Opelika:Alabama:36801::Lee:334-742-2130:::1");
            list.add("TEARS Inc::1011 South Railroad Street::Phenix City:Alabama:36867::Russell:334-291-6363::1:");
            list.add("East Alabama Mental Health:Russell County Clinic:3170 Martin Luther King Parkway South::Phenix City:Alabama:36869::Russell:334-298-2405:::1");
            list.add("West Alabama Mental Health Center::401 Rodgers Street::Butler:Alabama:36904::CHOCTAW:334-289-2410:1::1");

//Populate the data List
            String value = null;

            for (int i=0; i<list.size(); i++) {
                value = (String) list.get(i);
                addictionCentersData = unmarshall(value);
                log.debug("Fefore saving        " + addictionCentersData.toString());
                addictionCentersFinderDao.loadCentersTable(addictionCentersData);

            }
//Todo remove after loading date


            List<AddictionCentersData> addictionCenterDataList = addictionCentersFinderDao.scanCityState(session, propertyReader.getAlcoholCentersTable());

            for (AddictionCentersData addictionCenterData : addictionCenterDataList) {
                log.debug("TODo - Search the center with state and city, connect the call");
                log.debug(addictionCenterData.toString());
            }

            log.debug("TODo - Search the center with state, city data and Text the details of the same");
            //todo search of the center with state, city data
            //todo text the details
        }

        // Save th data
        addictionTreatmentFinderDao.setUserData(treatmentFinder, session);


//        return getAskSpeechletResponse(propertyReader.getSpeechConnect(), propertyReader.getSpeechReprompt());
        return getTellSpeechletResponse(treatmentFinder.getUserData().getfName() + ". " + prompt);
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
        AddictionCentersFinder game = addictionTreatmentFinderDao.getUserData(session);

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

    public SpeechletResponse getStopIntentResponse (Intent intent, Session session, SkillContext skillContext) {
        String isFalseNo = (String) session.getAttribute("SESSION-NO-DONT-EXIT");

        if (isFalseNo != null && isFalseNo.equalsIgnoreCase("yes")) {
            //session.removeAttribute("SESSION-NO-DONT-EXIT");
            log.debug("Landed in the STOP intent in case of a false no");

            Integer stepInSession = (Integer) session.getAttribute("SESSION-STEP");
            AddictionUserData addictionUserData = null;

            if (stepInSession == null) {
                return getAskSpeechletResponse(propertyReader.getWelcomeMessage(), propertyReader.getSpeechReprompt());
            }

            int iStepInSession = stepInSession.intValue();
            log.debug("Entered getStopIntentResponse With STEP in Session value of - " + stepInSession.intValue());

            if (iStepInSession == 4) {
                addictionUserData = skillContext.getAddictionUserData();

                addictionUserData.setLgbt("no");
                addictionUserData.setQuestionPhase(iStepInSession + 1); //set the state of the next question
                session.setAttribute("SESSION-STEP", new Integer(addictionUserData.getQuestionPhase()));
                session.setAttribute("SESSION-USER-LGBT", addictionUserData.getLgbt());
                log.debug("In 4 Addiction data - " + addictionUserData.toString());

                skillContext.setAddictionUserData(addictionUserData);
                return getAskSpeechletResponse(addictionUserData.getfName() +", " + propertyReader.getQuestion4(), propertyReader.getQuestion4());
            } else if (iStepInSession == 5) {
                addictionUserData = skillContext.getAddictionUserData();

                addictionUserData.setPregnantOrSpecalizedCare("no");
                addictionUserData.setQuestionPhase(iStepInSession + 1); //set the state of the next question
                session.setAttribute("SESSION-STEP", new Integer(addictionUserData.getQuestionPhase()));
                session.setAttribute("SESSION-USER-MEDICAL-ASSISTANCE", addictionUserData.getPregnantOrSpecalizedCare());
                log.debug("In 5 Addiction data - " + addictionUserData.toString());
                session.removeAttribute("SESSION-NO-DONT-EXIT");

                skillContext.setAddictionUserData(addictionUserData);
                return getAskSpeechletResponse(addictionUserData.getfName() +", " + propertyReader.getQuestion5(), propertyReader.getQuestion5());
            }

        }
        return getExitIntentResponse(intent, session, skillContext);
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

            addictionUserData.setLgbt("yes");
            addictionUserData.setQuestionPhase(iStepInSession + 1); //set the state of the next question
            session.setAttribute("SESSION-STEP", new Integer(addictionUserData.getQuestionPhase()));
            session.setAttribute("SESSION-USER-LGBT", addictionUserData.getLgbt());
            log.debug("In 4 Addiction data - " + addictionUserData.toString());
            session.setAttribute("SESSION-NATIONAL-FACILITY", "yes");
            session.setAttribute("SESSION-NO-DONT-EXIT", "yes");

            skillContext.setAddictionUserData(addictionUserData);
            return getAskSpeechletResponse(addictionUserData.getfName() +", " + propertyReader.getQuestion4(), propertyReader.getQuestion4());
        } else if (iStepInSession == 5) {
            addictionUserData = skillContext.getAddictionUserData();

            addictionUserData.setPregnantOrSpecalizedCare("yes");
            addictionUserData.setQuestionPhase(iStepInSession + 1); //set the state of the next question
            session.setAttribute("SESSION-STEP", new Integer(addictionUserData.getQuestionPhase()));
            session.setAttribute("SESSION-USER-MEDICAL-ASSISTANCE", addictionUserData.getPregnantOrSpecalizedCare());
            log.debug("In 5 Addiction data - " + addictionUserData.toString());
            session.setAttribute("SESSION-NATIONAL-FACILITY", "yes");
            session.setAttribute("SESSION-NO-DONT-EXIT", "yes");

            skillContext.setAddictionUserData(addictionUserData);
            return getAskSpeechletResponse(addictionUserData.getfName() +", " + propertyReader.getQuestion5(), propertyReader.getQuestion5());
        } else if (iStepInSession == 7) {
            /*addictionUserData = skillContext.getAddictionUserData();

            String phoneNumber = "";
            Map slots = intent.getSlots();
            Collection keys = slots.values();
            Iterator keysIterator = keys.iterator();
            while (keysIterator.hasNext()) {
                String slotName = (String) slots.get(keysIterator.next());
                log.debug("getCapturePhoneIntentResponse iterating over keys " + slotName);
            }
            String phoneNumberOne = intent.getSlot(SLOT_PHONE_NUMBER).getValue();

            addictionUserData.setQuestionPhase(iStepInSession + 1); //set the state of the next question
            session.setAttribute("SESSION-STEP", new Integer(addictionUserData.getQuestionPhase()));

            skillContext.setAddictionUserData(addictionUserData);*/
            return getAskSpeechletResponse(addictionUserData.getfName() +", " + "I am here", "I am here");
        } else {
            return getTellSpeechletResponse(propertyReader.getFatalError());
        }
    }

    public SpeechletResponse getCaptureCityStateIntentResponse (Intent intent, Session session,
                                                                      SkillContext skillContext) {
        Integer stepInSession = (Integer) session.getAttribute("SESSION-STEP");
        AddictionUserData addictionUserData = skillContext.getAddictionUserData();

        log.debug("getCaptureCityStateIntentResponse STEP in Session - " + stepInSession.intValue());
        if (stepInSession == null) {
            return getAskSpeechletResponse(propertyReader.getWelcomeMessage(), propertyReader.getSpeechReprompt());
        }
        int iStepInSession = stepInSession.intValue();
        String city = intent.getSlot(SLOT_USER_CITY).getValue();
        String state = intent.getSlot(SLOT_USER_STATE).getValue();

        if (city == null || state == null) {
            String speechText = "Sorry, I did not hear that" + propertyReader.getQuestion7();
            return getAskSpeechletResponse(speechText, speechText);
        }

        addictionUserData.setCity(city);
        addictionUserData.setState(state);

        log.debug("In getCaptureCityStateIntentResponse Logging Addiction contents - : " + addictionUserData.toString());

        addictionUserData.setQuestionPhase(iStepInSession+1); //set the state of the next question

        skillContext.setAddictionUserData(addictionUserData);
        session.setAttribute("SESSION-STEP", new Integer(addictionUserData.getQuestionPhase()));
        session.setAttribute("SESSION-CITY", addictionUserData.getCity());
        session.setAttribute("SESSION-STATE", addictionUserData.getState());

        return getAskSpeechletResponse(addictionUserData.getfName()+", " + propertyReader.getQuestion9(), propertyReader.getQuestion9());
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
        session.setAttribute("SESSION-USER-MEDICAL-CHOICE", addictionUserData.getReligiousMedical());

        return getAskSpeechletResponse(addictionUserData.getfName()+", " + propertyReader.getQuestion8(), propertyReader.getQuestion8());

    }

    public SpeechletResponse getCapturePhoneIntentResponse (Intent intent, Session session,
                                                       SkillContext skillContext) {
        Integer stepInSession = (Integer) session.getAttribute("SESSION-STEP");
        AddictionUserData addictionUserData = skillContext.getAddictionUserData();

        log.debug("getCapturePhoneIntentResponse STEP in Session - " + stepInSession.intValue());

        if (stepInSession == null) {
            return getAskSpeechletResponse(propertyReader.getWelcomeMessage(), propertyReader.getSpeechReprompt());
        }
        int iStepInSession = stepInSession.intValue();


        Map slots = intent.getSlots();
        Collection keys = slots.values();
        Iterator keysIterator = keys.iterator();
        while (keysIterator.hasNext()) {
            String slotName = (String) slots.get(keysIterator.next());
            log.debug("getCapturePhoneIntentResponse iterating over keys " + slotName);
        }
        String phoneNumber = intent.getSlot(SLOT_PHONE_NUMBER).getValue();

        if (phoneNumber == null) {
            String speechText = "Sorry, I did not hear that" + propertyReader.getQuestion8();
            return getAskSpeechletResponse(speechText, speechText);
        }

        String userName = addictionUserData.getfName();
        log.debug("in getCapturePhoneIntentResponse phoneNumber inputted is - " + phoneNumber);
        if (phoneNumber == null) {
            return getAskSpeechletResponse(userName+", " + propertyReader.getQuestion8(), propertyReader.getQuestion8());
        }
        addictionUserData.setPhoneNumber(phoneNumber);

        log.debug("In getCapturePhoneIntentResponse Logging Addiction contents - : " + addictionUserData.toString());

        addictionUserData.setQuestionPhase(iStepInSession+1); //set the state of the next question

        log.debug("In getCapturePhoneIntentResponse addiction data - " + addictionUserData.getAgeType());
        skillContext.setAddictionUserData(addictionUserData);
        session.setAttribute("SESSION-STEP", new Integer(addictionUserData.getQuestionPhase()));
        session.setAttribute("SESSION-USER-AGE", addictionUserData.getAgeType());
        session.setAttribute("SESSION-USER-PHONE", addictionUserData.getPhoneNumber());

        String sessionNationalFacility = (String) session.getAttribute("SESSION-NATIONAL-FACILITY");
        String prompt = null;
        if (sessionNationalFacility != null && sessionNationalFacility.equalsIgnoreCase("yes")) {
            prompt = propertyReader.getQuestion6();
        } else {
            prompt = propertyReader.getQuestion7();
        }
//        kjgfhgckjhhfjh
        // Get number (08)

        // If the session has "SESSION-NATIONAL-FACILITY" load Q6 and in connect/search send them SESSION-NATIONAL-FACILITY number

        // if the session does not have SESSION-NATIONAL-FACILITY, load Q7, Q9

        // Q6 and Q9 will land the user at ConnectCallIntent and SearchCallIntent
        return getAskSpeechletResponse(userName+", " + prompt, prompt);
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
        session.setAttribute("SESSION-USER-AGE", addictionUserData.getAgeType());
        if (userAge.equalsIgnoreCase("Under 18")) {
            session.setAttribute("SESSION-NATIONAL-FACILITY", "yes");
        }
        session.setAttribute("SESSION-NO-DONT-EXIT", "yes");
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
        skillContext.setAddictionUserData(AddictionUserData.newInstance());

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

    private AddictionCentersData unmarshall(String value) {
        String[] userDataArray = value.split(":");
        AddictionCentersData addictionCentersData = AddictionCentersData.newInstance();

        String name1 = userDataArray[0];
        String name2 = userDataArray[1];
        String street1 = userDataArray[2];
        String street2 = userDataArray[3];
        String city = userDataArray[4];
        String state = userDataArray[5];
        String zip = userDataArray[6];
        String zip4 = userDataArray[7];
        String county = userDataArray[8];
        String phoneNumber = userDataArray[9];
        String lgbt = userDataArray[10];
        String adolescents = userDataArray[11];
        String twelveStep = userDataArray[12];


        if (name1 == null) {name1 = ""; }
        if (name2 == null) {name2 = ""; }
        if (street1 == null) {street1 = ""; }
        if (street2 == null) {street2 = ""; }
        if (city == null) {city = ""; }
        if (state == null) {state = ""; }
        if (zip == null) {zip = ""; }
        if (zip4 == null) {zip4 = ""; }
        if (county == null) {county = ""; }
        if (phoneNumber == null) {phoneNumber = ""; }
        if (lgbt == null) {lgbt = ""; }
        if (adolescents == null) {adolescents = ""; }
        if (twelveStep == null) {twelveStep = ""; }

        addictionCentersData.setName1(name1);
        addictionCentersData.setName2(name2);
        addictionCentersData.setStreet1(street1);
        addictionCentersData.setStreet2(street2);
        addictionCentersData.setCity(city);
        addictionCentersData.setState(state);
        addictionCentersData.setZip(zip);
        addictionCentersData.setZip4(zip4);
        addictionCentersData.setCounty(county);
        addictionCentersData.setPhoneNumber(phoneNumber);
        addictionCentersData.setLgbt(lgbt);
        addictionCentersData.setAdolescents(adolescents);
        addictionCentersData.setTwelveStep(twelveStep);

        log.debug("in UnMarshal  - : " + addictionCentersData.toString());

        return addictionCentersData;
    }
}
