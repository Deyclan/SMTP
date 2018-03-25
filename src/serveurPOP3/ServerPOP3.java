package serveurPOP3;

import data.ConnexionMySQL;
import data.Mail;
import helpers.ConsoleApp;
import helpers.ConsoleColor;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class ServerPOP3 extends ConsoleApp {

    private static final int port = 1025;
    private static SSLServerSocket serverSocket;
    private static SSLServerSocketFactory factory;

    //Tableau des utilisateurs et passwords
    private static Map<String, String> users;

    //Tableau des utilisateurs et de leurs mails associés
    private static Map<String, Map<Integer, Mail>> boitesMail;

    // Tableau permettant la jointure entre un user et sa boite mail
    private static Map<String, String> jointure;

    public static void main(String[] args) {

        users = new HashMap<>();
        boitesMail = new HashMap<>();
        initData();

        try {
            ConsoleApp.setConsoleColor(ConsoleColor.ANSI_RED);
            ConsoleApp.log("Starting serveurPOP3.serveurPOP3", ConsoleColor.ANSI_RED);
            factory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
            serverSocket = (SSLServerSocket) factory.createServerSocket(port);
            serverSocket.setEnabledCipherSuites(factory.getSupportedCipherSuites());
            ConsoleApp.log("InetAddress : " + serverSocket.getInetAddress(), ConsoleColor.ANSI_RED);
            ConsoleApp.log("Port :" + serverSocket.getLocalPort(), ConsoleColor.ANSI_RED);
            ConsoleApp.log("Waiting for client ... ");

            while (true) {
                SSLSocket inputClientSocket = (SSLSocket) serverSocket.accept();
                ConsoleApp.log("client " + inputClientSocket.getInetAddress() + " connected.", ConsoleColor.ANSI_GREEN);

                new Thread(new Connexion(inputClientSocket, users, boitesMail)).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void initData() {
        try {
            Connection connection = ConnexionMySQL.getConnexion();

            jointure = new HashMap<>();

            // Récupérer les infos de la table user
            Statement statementUser = connection.createStatement();
            String sqlUser = ("SELECT * FROM user;");
            ResultSet resultSetUser = statementUser.executeQuery(sqlUser);

            while (resultSetUser.next()) {
                String nom = resultSetUser.getString("nom");
                String password = resultSetUser.getString("password");
                String mail = resultSetUser.getString("mail");

                users.put(nom, password);
                jointure.put(mail, nom);
            }

            // Récupérer les infos de la table mail
            boitesMail = getBoitesMail();


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Map<String, Map<Integer, Mail>> getBoitesMail() {
        try {
            Map<String, Map<Integer, Mail>> boitesMail = new HashMap<>();

            Connection connection = ConnexionMySQL.getConnexion();
            Statement statementMail = connection.createStatement();

            String sqlMail = ("SELECT * FROM mail;");
            ResultSet resultSetMail = statementMail.executeQuery(sqlMail);

            while (resultSetMail.next()) {
                String mailUser = resultSetMail.getString("mailUser");
                String from = resultSetMail.getString("expediteur");
                String to = resultSetMail.getString("destinataire");
                String subject = resultSetMail.getString("subject");
                String date = resultSetMail.getString("dateMail");
                String message_id = resultSetMail.getString("message_id");
                String content = resultSetMail.getString("content");
                int num = resultSetMail.getInt("num");

                Mail mail = new Mail();
                mail.setFrom(from);
                mail.setTo(to);
                mail.setSubject(subject);
                mail.setDate(date);
                mail.setMessage_id(message_id);
                mail.setContent(content);

                String nomUser = jointure.get(mailUser);
                if (boitesMail.get(nomUser) == null) {
                    Map<Integer, Mail> ligneMail = new HashMap<>();
                    ligneMail.put(num, mail);
                    boitesMail.put(nomUser, ligneMail);
                } else {
                    boitesMail.get(nomUser).put(num, mail);
                }
            }
            return boitesMail;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
