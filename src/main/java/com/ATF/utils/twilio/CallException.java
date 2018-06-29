package main.java.com.ATF.utils.twilio;

public class CallException extends RuntimeException {

    public CallException(Throwable e) {
        super(e);
    }
}
