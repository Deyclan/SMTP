package serveurPOP3;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import data.Mail;
import helpers.ConsoleApp;
import helpers.ConsoleColor;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;
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

    public static void main(String[] args) {

        Gson gson = new Gson();
        users = new HashMap<>();
        boitesMail = new HashMap<>();
        initData(gson);

        try {
            ConsoleApp.setConsoleColor(ConsoleColor.ANSI_RED);
            ConsoleApp.log("Starting serveurPOP3.serveurPOP3", ConsoleColor.ANSI_RED);
            factory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
            serverSocket = (SSLServerSocket) factory.createServerSocket(port);
            serverSocket.setEnabledCipherSuites(factory.getSupportedCipherSuites());
            ConsoleApp.log("InetAddress : " + serverSocket.getInetAddress(), ConsoleColor.ANSI_RED);
            ConsoleApp.log("Port :" + serverSocket.getLocalPort(), ConsoleColor.ANSI_RED);
            ConsoleApp.log("Waiting for clientSMTP ... ");

            while (true) {
                SSLSocket inputClientSocket = (SSLSocket) serverSocket.accept();
                ConsoleApp.log("clientPOP3 " + inputClientSocket.getInetAddress() + " connected.", ConsoleColor.ANSI_GREEN);

                new Thread(new Connexion(inputClientSocket, users, boitesMail)).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void initData(Gson gson) {
                try {
                    FileReader file = new FileReader("src/data/database.json");
                    JsonObject json = gson.fromJson(file, JsonObject.class);
                    // Users
                    Type usersType = new TypeToken<Map<String, String>>() {
            }.getType();
            users = gson.fromJson(json.get("users").getAsJsonObject(), usersType);

            // Boites mail
            Type boitesMailType = new TypeToken<Map<String, Map<Integer, Mail>>>() {
            }.getType();
            boitesMail = gson.fromJson(json.get("boitesMail").getAsJsonObject(), boitesMailType);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

}
