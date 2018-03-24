package client.GUI;

import client.ReceptionThread;
import helpers.Constants;
import helpers.EventPOP3;
import helpers.StatePOP3;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Observable;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MailController extends Observable implements Initializable {

    private static SSLSocket socket;
    private static SSLSocketFactory factory;
    private static InputStream inputStream;
    private static OutputStream outputStream;
    public int messageNumber;
    public Thread receptionThread;
    private AnchorPane root;
    private Scene scene;
    private String timbreADate;

    private StatePOP3 state = StatePOP3.AUTHORIZATION;

    @FXML
    private Button connectBtn, loginBtn, statBtn, retrBtn, logoutBtn;
    @FXML
    private TextField hostAdress, port, userName, password, mailNb;
    @FXML
    private ListView mailList;
    @FXML
    private TextArea console, mailContent;


    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    public void connect() {
        log("Trying to connect to " + hostAdress.getText() + " on port " + port.getText());
        setupTextAreas();
        if (socket != null && socket.isConnected()) {
            log("Already connected");
            return;
        }
        try {
            log("Connecting");
            factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            socket = (SSLSocket) factory.createSocket(InetAddress.getByName(getHostAdress()), Integer.parseInt(getPort()));
            socket.setEnabledCipherSuites(factory.getSupportedCipherSuites());
            log("Connected");
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();

            //Platform.runLater(new ReceptionThread(this));
            receptionThread = new Thread(new ReceptionThread(this));
            receptionThread.setDaemon(true);
            if (socket.isConnected()) {
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String recievedString = in.readLine();
                if (recievedString != null) {
                    String[] splitedString = recievedString.split(" ");
                    if (splitedString[0].contains(Constants.ok) && splitedString.length >= 5) {
                        timbreADate = splitedString[4];
                    }
                }
            }
            receptionThread.start();

        } catch (Exception e) {
            log("Failed to connect");
            e.printStackTrace();
        }

    }

    @FXML
    public void login(ActionEvent event) {
        if (socket != null && socket.isConnected() && state.equals(StatePOP3.AUTHORIZATION)) {
            try {
                log("Loging in your Mail Box");
                String sommeDeControle = new StringBuilder().append(getPassword()).append(timbreADate).toString();
                String passwordMD5 = new String(MessageDigest.getInstance("MD5").digest(sommeDeControle.getBytes()), StandardCharsets.UTF_8);
                sendToServer(EventPOP3.APOP, getUserName(), passwordMD5);
                state = StatePOP3.TRANSACTION;
                // Chargement de la fenêtre des mails si succès de l'authentification
                changeScreens(event);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void statMailInfo() {
        try {
            sendToServer(EventPOP3.STAT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void retrieveMail() {
        try {
            sendToServer(EventPOP3.RETR, mailNb.getText());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void logout(ActionEvent event) {
        try {
            if (socket != null && socket.isConnected()) {
                log("Disconnecting");
                sendToServer(EventPOP3.QUIT);
                socket.close();
                socket = null;
                state = StatePOP3.AUTHORIZATION;
                changeScreens(event);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void changeScreens(ActionEvent event) {
        try {
            Stage stage;
            if (event.getSource() == loginBtn) {

                stage = (Stage) loginBtn.getScene().getWindow();
                root = FXMLLoader.load(getClass().getResource("/client/GUI/mailScreen.fxml"));


            } else {
                stage = (Stage) logoutBtn.getScene().getWindow();
                root = FXMLLoader.load(getClass().getResource("/client/GUI/loginScreen.fxml"));
            }
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (Exception ex) {
            Logger.getLogger(MailController.class.getName()).log(Level.SEVERE, "Problème de connexion", ex);
        }
    }

    private void setupTextAreas() {
        console.setWrapText(true);
    }

    public void log(final String string) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if (scene != null) {
                    TextArea whereToLog = (TextArea) scene.lookup("#console");
                    whereToLog.appendText(string);
                    whereToLog.appendText("\n");
                } else {
                    console.appendText(string);
                    console.appendText("\n");
                }
                System.out.println(string);
            }
        });
    }

    public String getHostAdress() {
        return hostAdress.getText();
    }

    public String getPort() {
        return port.getText();
    }

    public String getUserName() {
        return userName.getText();
    }

    public String getPassword() {
        return password.getText();
    }

    public Socket getSocket() {
        return socket;
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public StatePOP3 getState() {
        return state;
    }

    public TextArea getConsole() {
        return console;
    }

    public void setState(StatePOP3 state) {
        this.state = state;
    }

    /**
     * UTILS
     */
    private void sendToServer(EventPOP3 eventPOP3, String... args) throws IOException {
        String separator = " ";
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(eventPOP3.getCmd());
        for (String arg : args) {
            stringBuilder.append(separator);
            stringBuilder.append(arg);
        }
        log("\n");
        log("Sending : " + stringBuilder.toString());
        stringBuilder.append("\r\n");
        outputStream.write(stringBuilder.toString().getBytes());
    }
}
