package serveurSMTP;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServeurMain {

    public static void main(String[] args) {
        System.out.println("Quel serveurSMTP voulez-vous lancer ? \n - ait.com \n - gstaad.fr");

        try {
            ServerSocket serverSocket = new ServerSocket(25);
            while(!serverSocket.isClosed()){
                Socket socket = serverSocket.accept();
                new Thread(new Connexion(socket,"ait.com")).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
