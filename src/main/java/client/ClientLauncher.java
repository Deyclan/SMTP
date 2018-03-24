package client;

import helpers.Constants;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.net.URL;
public class ClientLauncher extends Application {

    static AnchorPane root;
    private FXMLLoader fxmlLoader;
    private final URL loginURL = getClass().getResource("/client/GUI/loginScreen.fxml");

    public static void main(String[] args) {
        Application.launch(ClientLauncher.class, args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        fxmlLoader = new FXMLLoader(loginURL);
        root = fxmlLoader.load();
        primaryStage.setTitle("Messagerie");
        primaryStage.setScene(new Scene(root, Constants.frameDimension.width,Constants.frameDimension.height));
        primaryStage.show();
    }

}
