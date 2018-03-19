package serveur;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServeurMain {

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(25);
            while(!serverSocket.isClosed()){
                Socket socket = serverSocket.accept();
                new Thread(new Connexion(socket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
