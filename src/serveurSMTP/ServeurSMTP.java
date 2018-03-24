package serveurSMTP;

import data.ConnexionMySQL;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ServeurSMTP {

    public static void main(String[] args) {

        Scanner reader = new Scanner(System.in);
        Connection connection = ConnexionMySQL.getConnexion();

        System.out.println("Quel serveur SMTP voulez-vous lancer ? \n 1 - ait.com \n 2 - gstaad.fr");
        int choix = reader.nextInt();

        String serverChoisi;
        List<String> users = new ArrayList<>();

        try {
            if (choix == 1) {
                serverChoisi = "ait.com";
            } else {
                serverChoisi = "gstaad.fr";
            }

            Statement statement = connection.createStatement();
            String sql = ("SELECT * FROM serveur WHERE nom = '" + serverChoisi + "';");
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                String adresseMail = resultSet.getString("adresseMail");
                users.add(adresseMail);
            }

            ServerSocket serverSocket = new ServerSocket(25);
            while (!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                new Thread(new Connexion(socket, serverChoisi, connection, users)).start();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        reader.close();
    }

}
