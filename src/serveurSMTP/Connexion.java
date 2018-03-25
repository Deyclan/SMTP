package serveurSMTP;

import data.Mail;
import helpers.EventSTMP;
import helpers.StateSMTP;

import java.io.*;
import java.net.Socket;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Connexion implements Runnable {

    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;
    private Connection connection;

    private StateSMTP etat;
    private String serveur;

    private List<String> users;

    public Connexion(Socket socket, String nomServeur, Connection connection, List<String> users) {
        this.socket = socket;
        this.serveur = nomServeur;
        this.connection = connection;
        this.users = users;
        this.etat = StateSMTP.Debut;
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

            List<Mail> mailsToMany = new ArrayList<>();
            mailsToMany.add(mail);

            while (!socket.isClosed()) {
                if ((messageRecu = in.readLine()) != null) {

                    System.out.println("client : " + messageRecu);

                    String[] elementsMessage = messageRecu.split(" ");
                    String evenement = recupEvenement(elementsMessage);

                    EventSTMP event = EventSTMP.parse(evenement);
                    switch (event) {
                        case EHLO:
                            switch (etat) {
                                case Debut:
                                    etat = StateSMTP.Attente;
                                    write("250 " + serveur);
                                    break;
                                default:
                                    write("500 Commande ignorée");
                                    break;
                            }
                            break;
                        case MAIL_FROM:
                            switch (etat) {
                                case Attente:
                                    mail.setFrom(elementsMessage[2]);
                                    etat = StateSMTP.Mail_cree;
                                    write("250 OK");
                                    break;
                                default:
                                    write("500 Commande ignorée");
                                    break;
                            }
                            break;
                        case RCPT_TO:
                            switch (etat) {
                                case Mail_cree:
                                    if (users.contains(elementsMessage[2])) {
                                        etat = StateSMTP.Destinataire_attribue;
                                        if (mail.getTo() == null) {
                                            mail.setTo(elementsMessage[2]);
                                        } else {
                                            Mail mailTmp = new Mail();
                                            mailTmp.setTo(elementsMessage[2]);
                                            mailsToMany.add(mailTmp);
                                        }

                                        write("250 OK");
                                    } else {
                                        write("550 No such user");
                                    }
                                    break;
                                case Destinataire_attribue:
                                    if (users.contains(elementsMessage[2])) {
                                        write("250 OK");
                                        mail.setTo(elementsMessage[2]);
                                    } else {
                                        write("550 No such user");
                                    }
                                    break;
                                default:
                                    write("500 Commande ignorée");
                                    break;
                            }
                            break;
                        case DATA:
                            switch (etat) {
                                case Destinataire_attribue:
                                    etat = StateSMTP.Ecriture_mail;
                                    write("354");
                                    break;
                                default:
                                    write("500 Commande ignorée");
                                    break;
                            }
                            break;
                        case RST:
                            switch (etat) {
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
                            switch (etat) {
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
                            switch (etat) {
                                case Ecriture_mail:
                                    if (!messageRecu.equals(".")) {
                                        if (mail.getSubject() == null) {
                                            mail.setSubject(messageRecu);
                                        } else {
                                            if (mail.getContent() == null) {
                                                mail.setContent(messageRecu + "\n");
                                            } else {
                                                mail.setContent(mail.getContent() + messageRecu + "\n");
                                            }
                                        }
                                    } else {
                                        for (Mail iterMail : mailsToMany) {
                                            if (iterMail != mail) {
                                                iterMail.setFrom(mail.getFrom());
                                                iterMail.setContent(mail.getContent());
                                                iterMail.setDate(mail.getDate());
                                                iterMail.setMessage_id(mail.getMessage_id());
                                                iterMail.setSubject(mail.getSubject());
                                            }


                                            try {
                                                // On récupère le num max
                                                Statement statementMaxNum = connection.createStatement();
                                                String sqlMail = ("SELECT max(num) AS 'num' FROM mail WHERE mailUser = '" + iterMail.getTo() + "';");
                                                ResultSet resultSetMail = statementMaxNum.executeQuery(sqlMail);

                                                if (resultSetMail.next()) {
                                                    int num = resultSetMail.getInt("num") + 1;
                                                    iterMail.setNum(num);
                                                    iterMail.setMessage_id("<" + iterMail.getTo().substring(0, 1).toLowerCase() + num + "@" + serveur + ">");
                                                }

                                                // Insertion dans mysql
                                                Statement statement = connection.createStatement();
                                                String sql = "INSERT INTO mail (mailUser, expediteur, destinataire, subject, dateMail, content, num, message_id) VALUES ("
                                                        + "'" + iterMail.getTo() + "'" + ","
                                                        + "'" + iterMail.getFrom() + "'" + ","
                                                        + "'" + iterMail.getTo() + "'" + ","
                                                        + "'" + iterMail.getSubject() + "'" + ","
                                                        + "'" + iterMail.getDate() + "'" + ","
                                                        + "'" + iterMail.getContent() + "'" + ","
                                                        + "'" + iterMail.getNum() + "'" + ","
                                                        + "'" + iterMail.getMessage_id() + "'" + ");";
                                                statement.execute(sql);
                                            } catch (SQLException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        etat = StateSMTP.Attente;
                                    }
                                    break;
                                default:
                                    write("500 Commande ignorée");
                                    break;
                            }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void reset() throws IOException {
        etat = StateSMTP.Attente;
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
        System.out.println("serveurPOP3 : " + message);
    }
}
