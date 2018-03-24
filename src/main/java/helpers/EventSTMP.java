package helpers;

public enum EventSTMP {
    EHLO("EHLO"),
    MAIL_FROM("MAIL FROM"),
    RCPT_TO("RCPT TO"),
    DATA("DATA"),
    RST("RST"),
    QUIT("QUIT");

    private String value;

    EventSTMP(final String nom){
        this.value = nom;
    }

    @Override
    public String toString() {
        return this.value;
    }
}
