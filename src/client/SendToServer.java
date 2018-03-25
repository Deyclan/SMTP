package client;

import java.io.*;
import java.net.Socket;
import java.util.List;

public class SendToServer implements Runnable {

    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;

    private String serverToJoin;
    private String from;
    private String[] destList;
    private String mailContent;

    public SendToServer(Socket socket, String serveurToJoin, String from, String[] destList, String mailContent) {
        try {
            this.socket = socket;
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.serverToJoin = serveurToJoin;
            this.from = from;
            this.destList = destList;
            this.mailContent = mailContent;

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            // ------------------------------------------------------------------------------
            /**
             *  Connexion au serveur
             */
            String receptionString = "";
            receptionString = read();
            String[] connexionServerMessage = receptionString.split(" ");
            String connectedServer;
            if (connexionServerMessage.length > 1 && connexionServerMessage[0].equals("220")){
                write("EHLO "+serverToJoin);
            }
            else {
                // TODO : A completer
                return;
            }

            // ------------------------------------------------------------------------------
            /**
             * Identification du serveur
             */
            receptionString = read();
            String[] serveurIdentification = receptionString.split(" ");
            if (serveurIdentification.length > 1 && serveurIdentification[0].equals("250")){
                write("MAIL FROM "+ from);
            }
            else {
                // TODO : A completer
                return;
            }

            // ------------------------------------------------------------------------------
            /**
             * Emetteur mail Envoyé
             */
            int destValidate = 0;
            for (String dest: destList) {
                write("RCPT TO "+dest);
                receptionString = read();
                String[] response = receptionString.split(" ");
                if (response[0].equals("250")){
                    destValidate++;
                }
            }
            if (destValidate == 0){
                write("RSET");
                // TODO : A completer
            }

            // ------------------------------------------------------------------------------
            /**
             * Recepteurs mail envoyé
             */
            receptionString = read();
            String[] reponsePostEnvoiRCPT = receptionString.split(" ");
            if (reponsePostEnvoiRCPT[0].equals("250")){
                write("DATA");
            }else {
                // TODO : A completer
                return;
            }

            // ------------------------------------------------------------------------------
            /**
             * Envoi données mail
             */
            receptionString = read();
            String[] reponseEnvoiDATA = receptionString.split(" ");
            if (reponseEnvoiDATA[0].equals("354")){
                write(mailContent);
                write(".");
            }else {
                // TODO : A completer
                return;
            }

            // ------------------------------------------------------------------------------
            /**
             * Contenu mail envoyé
             */
            receptionString = read();
            String[] reponseFinale = receptionString.split(" ");
            if (reponseFinale[0].equals("250")){
                write("QUIT");
            }else {
                write("RSET");
                return;
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {
            socket = null;
        }
    }

    private void write(String message) throws IOException {
        out.write(message + "\r\n");
        out.flush();
        System.out.println("Sending : "+message);
    }

    private String read() throws IOException {
        String s;
        if ((s = in.readLine()) != null) {
            return s;
        }
        return null;
    }
}
