package main.java.com.ATF.utils.twilio;

import main.java.com.ATF.utils.UndefinedEnvironmentVariableException;

import java.util.Map;

/**
 * Class that holds methods to obtain configuration parameters from the environment.
 */
public class AppSetup {
  private Map<String, String> env;

  public AppSetup() {
    this.env = System.getenv();
  }

  public String getAccountSid() throws UndefinedEnvironmentVariableException {
    return "ACff230bb14d5da16062cf9e8c433fa1fb";
    //todo
  }

  public String getAuthToken() throws UndefinedEnvironmentVariableException {
    return "9fcd8ab1ce3892292fcc2c028d88054d";
  }

  public String getTwilioNumber() throws UndefinedEnvironmentVariableException {
    return "+114139236428";
  }

}
