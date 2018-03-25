package helpers;

public enum EventSTMP {
    EHLO("EHLO"),
    MAIL_FROM("MAIL FROM"),
    RCPT_TO("RCPT TO"),
    DATA("DATA"),
    RST("RST"),
    QUIT("QUIT"),
    NOT_AN_EVENT("NOT AN EVENT");

    private String value;

    EventSTMP(final String nom) {
        this.value = nom;
    }

    @Override
    public String toString() {
        return this.value;
    }

    public static EventSTMP parse(String event) throws IllegalArgumentException {
        for (EventSTMP eventSMTP : values()) {
            if (eventSMTP.toString().equals(event)) {
                return eventSMTP;
            }
        }
        return NOT_AN_EVENT;
    }
}
