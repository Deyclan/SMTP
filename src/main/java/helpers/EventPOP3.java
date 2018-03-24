package helpers;

public enum EventPOP3 {

    APOP("APOP"),
    STAT("STAT"),
    LIST("LIST"),
    RETR("RETR"),
    DELE("DELE"),
    QUIT("QUIT");

    private String cmd = "";

    EventPOP3(String cmd){
        this.cmd = cmd;
    }

    public String getCmd() {
        return cmd;
    }
}
