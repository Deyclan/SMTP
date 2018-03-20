package serveur;

import helpers.Event;
import helpers.State;

import java.io.*;
import java.net.Socket;
import java.util.List;

public class Connexion implements Runnable {

    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;

    private String etat;
    private String serveur;

    private List<String> users;

    public Connexion(Socket socket, String nomServeur) {
        this.socket = socket;
        this.serveur = nomServeur;
        this.etat = State.Debut.toString();
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            // Envoi du message de bienvenu
            write("220 " + serveur + " Simple Mail Transfer Protocol");

            String messageRecu;
            Mail mail = new Mail();
            while (!socket.isClosed()) {
                if ((messageRecu = in.readLine()) != null) {

                    String[] elementsMessage = messageRecu.split(" ");
                    String evenement = recupEvenement(elementsMessage);

                    switch (Event.valueOf(evenement)) {
                        case EHLO:
                            switch (State.valueOf(etat)) {
                                case Debut:
                                    write("250 " + serveur);
                                    etat = State.Attente.toString();
                                    break;
                                default:
                                    write("500 Commande ignorée");
                                    break;
                            }
                            break;
                        case MAIL_FROM:
                            switch (State.valueOf(etat)) {
                                case Attente:
                                    mail.setFrom(elementsMessage[2]);
                                    etat = State.Ecriture_mail.toString();
                                    break;
                                default:
                                    write("500 Commande ignorée");
                                    break;
                            }
                            break;
                        case RCPT_TO:
                            switch (State.valueOf(etat)) {
                                case Mail_cree:
                                    if(users.contains(elementsMessage[2])){
                                        write("250 OK");
                                        etat = State.Destinataire_attribue.toString();
                                        mail.getTo().add(elementsMessage[2]);
                                    }else{
                                        write("550 No such user");
                                    }
                                    break;
                                case Destinataire_attribue:
                                    if(users.contains(elementsMessage[2])){
                                        write("250 OK");
                                        mail.getTo().add(elementsMessage[2]);
                                    }else{
                                        write("550 No such user");
                                    }
                                    break;
                                default:
                                    write("500 Commande ignorée");
                                    break;
                            }
                            break;
                        case DATA:
                            switch (State.valueOf(etat)) {
                                case Destinataire_attribue:
                                    // TODO
                                    break;
                                case Ecriture_mail:
                                    // TODO
                                    break;
                                default:
                                    write("500 Commande ignorée");
                                    break;
                            }
                            break;
                        case RST:
                            switch (State.valueOf(etat)) {
                                case Attente:
                                    reset();
                                    break;
                                case Mail_cree:
                                    reset();
                                    break;
                                case Destinataire_attribue:
                                    reset();
                                    break;
                                case Ecriture_mail:
                                    reset();
                                    break;
                                default:
                                    write("500 Commande ignorée");
                                    break;
                            }
                            break;
                        case QUIT:
                            switch (State.valueOf(etat)) {
                                case Debut:
                                    quit();
                                    break;
                                case Attente:
                                    quit();
                                    break;
                                case Mail_cree:
                                    quit();
                                    break;
                                case Destinataire_attribue:
                                    quit();
                                    break;
                                case Ecriture_mail:
                                    quit();
                                    break;
                                default:
                                    write("500 Commande ignorée");
                                    break;
                            }
                            break;
                        default:
                            write("500 Commande ignorée");
                            break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void reset() throws IOException {
        etat = State.Attente.toString();
        write("250 OK");
    }

    private void quit() throws IOException {
        write("221");
        socket.close();
    }

    private String recupEvenement(String[] elementsMessage) {
        String evenement = elementsMessage[0];

        if (evenement.equals("MAIL") || evenement.equals("RCPT")) {
            evenement += " " + elementsMessage[1];
        }
        return evenement;
    }

    private void write(String message) throws IOException {
        out.write(message + "\r\n");
        out.flush();
        System.out.println("Server : " + message);
    }
}
