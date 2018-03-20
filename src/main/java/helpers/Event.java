package helpers;

public enum Event {
    EHLO("EHLO"),
    MAIL_FROM("MAIL FROM"),
    RCPT_TO("RCPT TO"),
    DATA("DATA"),
    RST("RST"),
    QUIT("QUIT");

    private String value;

    Event(final String nom){
        this.value = nom;
    }

    @Override
    public String toString() {
        return this.value;
    }
}
