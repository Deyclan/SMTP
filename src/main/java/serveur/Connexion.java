package serveur;

import java.net.Socket;

public class Connexion implements Runnable {

    Socket socket;
    String etat = "Début";

    public Connexion(Socket socket) {
        this.socket = socket;
    }

    public void run() {

    }
}
