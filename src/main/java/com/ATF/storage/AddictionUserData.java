package main.java.com.ATF.storage;

public class AddictionUserData {
    String fName;
    // 0 for starting the session, each question has a number. Once the answer to the queston is persisted, increment the questionPhase
    int questionPhase;
    //under18 or over18
    String ageType;

    String lgbt;
    String state;
    String city;
    String phoneNumber;

    // 12 step for religious and medically assisted for atheist
    String religiousMedical;

    String pregnantOrSpecalizedCare;

    private AddictionUserData () {
    }

    /**
     * Creates a new instance of {@link AddictionUserData} with initialized but empty player and
     * score information.
     *
     * @return
     */
    public static AddictionUserData newInstance() {
        AddictionUserData newInstance = new AddictionUserData();
        return newInstance;
    }

    public String getfName () { return fName; }

    public void setfName (String fName) { this.fName = fName; }

    public int getQuestionPhase () {
        return questionPhase;
    }

    public void setQuestionPhase (int questionPhase) {
        this.questionPhase = questionPhase;
    }

    public String getAgeType () {
        return ageType;
    }

    public void setAgeType (String ageType) {
        this.ageType = ageType;
    }

    public String getLgbt () {
        return lgbt;
    }

    public void setLgbt (String lgbt) {
        this.lgbt = lgbt;
    }

    public String getState () {
        return state;
    }

    public void setState (String state) {
        this.state = state;
    }

    public String getCity () {
        return city;
    }

    public void setCity (String city) {
        this.city = city;
    }

    public String getPhoneNumber () {
        return phoneNumber;
    }

    public void setPhoneNumber (String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getReligiousMedical () {
        return religiousMedical;
    }

    public void setReligiousMedical (String religiousMedical) {
        this.religiousMedical = religiousMedical;
    }

    public String getPregnantOrSpecalizedCare () {
        return pregnantOrSpecalizedCare;
    }

    public void setPregnantOrSpecalizedCare (String pregnantOrSpecalizedCare) { this.pregnantOrSpecalizedCare = pregnantOrSpecalizedCare; }

    @Override
    public String toString () {
        return "AddictionUserData{" +
                "fName='" + fName + '\'' +
                ", questionPhase=" + questionPhase +
                ", ageType='" + ageType + '\'' +
                ", lgbt='" + lgbt + '\'' +
                ", state='" + state + '\'' +
                ", city='" + city + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", religiousMedical='" + religiousMedical + '\'' +
                ", pregnantOrSpecalizedCare='" + pregnantOrSpecalizedCare + '\'' +
                '}';
    }
}
