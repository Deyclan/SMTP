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
                System.out.println("Error (EHLO). Message recieved :");
                System.out.println(receptionString);
                write("QUIT");
                return;
            }

            // ------------------------------------------------------------------------------
            /**
             * Identification du serveur
             */
            receptionString = read();
            String[] serveurIdentification = receptionString.split(" ");
            if (serveurIdentification.length > 1 && serveurIdentification[1].equals(serverToJoin)){
                write("MAIL FROM "+ from);
            }
            else {
                System.out.println("Error (MAIL FROM). Message recieved :");
                System.out.println(receptionString);
                write("QUIT");
                return;
            }

            // ------------------------------------------------------------------------------
            /**
             * Emetteur mail Envoyé
             */
            receptionString = read();
            String[] repMailFrom = receptionString.split(" ");
            if (repMailFrom.length > 1 && repMailFrom[0].equals("250")) {
                int destValidate = 0;
                for (String dest : destList) {
                    write("RCPT TO " + dest);
                    receptionString = read();
                    String[] response = receptionString.split(" ");
                    if (response[0].equals("250")) {
                        destValidate++;
                    }
                }
                if (destValidate == 0) {
                    write("RSET");
                }
            } else {
                System.out.println("Error (RCPT TO sending). Message recieved :");
                System.out.println(receptionString);
                write("QUIT");
                return;
            }

            // ------------------------------------------------------------------------------
            /**
             * Recepteurs mail envoyé
             */
            /*
            receptionString = read();
            String[] reponsePostEnvoiRCPT = receptionString.split(" ");
            if (reponsePostEnvoiRCPT[0].equals("250")){
                write("DATA");
            }else {
                System.out.println("Error (DATA). Message recieved :");
                System.out.println(receptionString);
                write("QUIT");
                return;
            }
            */
            write("DATA");

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
                System.out.println("Error (data sending). Message recieved :");
                System.out.println(receptionString);
                write("QUIT");
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
