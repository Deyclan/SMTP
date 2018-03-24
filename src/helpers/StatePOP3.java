package helpers;

public enum StatePOP3 {

    AUTHORIZATION("AUTHORIZATION"),
    TRANSACTION("TRANSACTION");

    private String name = "";

    StatePOP3(String name){
        this.name = name;
    }

    public String getName(){
        return this.name;
    }

}
