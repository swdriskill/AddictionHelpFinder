package main.java.com.ATF.utils.twilio;

import com.twilio.Twilio;
import com.twilio.http.TwilioRestClient;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.twiml.TwiMLException;
import com.twilio.twiml.VoiceResponse;
import com.twilio.twiml.voice.Dial;
import com.twilio.twiml.voice.Enqueue;
import com.twilio.twiml.voice.Say;
import com.twilio.type.PhoneNumber;
import com.twilio.twiml.voice.Number;

import main.java.com.ATF.utils.PropertyReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TwilioCallText {
    private static PropertyReader propertyReader = PropertyReader.getPropertyReader();
    private static final Logger log = LoggerFactory.getLogger(TwilioCallText.class);

    public static final String ACCOUNT_SID = propertyReader.getTwilioAccountSid();
    public static final String AUTH_TOKEN = propertyReader.getTwilioAuthToken();

    public void sendText(String from, String to, String textMessage) {

        // TODO: 7/17/18 fix the hardcoded details below 
        Twilio.init("ACff230bb14d5da16062cf9e8c433fa1fb", "9fcd8ab1ce3892292fcc2c028d88054d");
        Message message = Message.creator(
                new com.twilio.type.PhoneNumber(to),
                new com.twilio.type.PhoneNumber(from),
                textMessage)
                .create();

        log.debug("Text sent to - " + to);
        log.debug("Text sent from - " + from);
        log.debug("Text sent with message - " + textMessage);
    }

    public String connectCall(String addictionCenterNumber) {
        log.debug("Connect Call method = " + addictionCenterNumber);
        Number number = new Number.Builder(addictionCenterNumber).build();
        Dial dial = new Dial.Builder()
                .number(number)
                .build();
        log.debug("Connect Call method = " + 1);

        VoiceResponse response = new VoiceResponse.Builder()
                .say(new Say.Builder(
                        "Thanks for contacting the Addiction Treatement Center. Our " +
                                "next available representative will take your call.").build())
                .dial(dial)
                .build();
        log.debug("Connect Call method = " + 2);

        try {
            log.debug("Connect Call method = " + 3);

            return response.toXml();
        } catch (TwiMLException e) {
            e.printStackTrace();
        }

        return "";
    }

}
