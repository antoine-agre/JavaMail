package userInterface;

import com.sun.tools.javac.Main;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class Client extends Application {
    /**
     * Représente l'application client.
     */

    LoginScreen loginScreen;
    MainScreen mainScreen;
    Stage stage;

    public static void main(String[] args) {launch(args);}

    @Override
    public void start(Stage stage) {

        this.loginScreen = new LoginScreen();
        Scene loginScene = this.loginScreen.getScene();

        this.mainScreen = new MainScreen();
        Scene mainScene = this.mainScreen.getScene();

        //////

        LoginScreenController.client = this;

        //////

        this.stage = stage;
        this.stage.setTitle("JavaMail");
        this.stage.setScene(loginScene); //
        this.stage.show();
    }

    public void switchToMain() {
        this.stage.setScene(this.mainScreen.getScene());
    }

}