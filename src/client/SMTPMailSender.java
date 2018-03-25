package client;

import helpers.EventSTMP;
import javafx.application.Platform;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class SMTPMailSender implements Runnable {

    private String fromAdress;
    private String toAdress;
    private String content;

    public SMTPMailSender(String fromAdress, String toAdress, String content) {
        try {
            this.fromAdress = fromAdress;
            this.toAdress = toAdress;
            this.content = content;
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try{
            List<String> domaines = new ArrayList<>();
            String[] destinataireList = toAdress.split(";");
            for (String destinataire: destinataireList) {
                String[] dest = destinataire.split("@");
                if (dest.length > 0 && !domaines.contains(dest[1])){
                    domaines.add(dest[1]);
                    System.out.println("DEBUG : destinataire = "+destinataire + " | dest = " + dest[1]);
                }
            }
            for (String domaine : domaines){
                String adresse;
                switch (domaine){
                    case "ait.com":
                        adresse = "127.0.0.1";
                        break;
                    case "gstaad.fr":
                        adresse = "127.0.0.1";
                        break;
                    default:return;
                }
                System.out.println("DEBUG : domaine = "+domaine+" | addresse = "+adresse);
                Platform.runLater(new SendToServer(new Socket(adresse, 25), domaine, fromAdress, destinataireList, content)); //TODO : implémenter les adresses des serveurs
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
