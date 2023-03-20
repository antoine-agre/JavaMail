package userInterface;

import com.sun.tools.javac.Main;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import mail.MailHandler;

import java.io.IOException;
import java.util.Objects;

public class Client extends Application {
    /**
     * Représente l'application client.
     */

    protected LoginScreen loginScreen;
    protected MainScreen mainScreen;
    protected SendScreen sendScreen;
    protected Stage stage;
    protected MailHandler mailHandler;

    public static void main(String[] args) {launch(args);}

    @Override
    public void start(Stage stage) {

        this.loginScreen = new LoginScreen(this);
        Scene loginScene = this.loginScreen.getScene();

        this.mainScreen = new MainScreen(this);
        Scene mainScene = this.mainScreen.getScene();

        this.stage = stage;
        this.stage.setTitle("JavaMail");
        this.stage.setScene(loginScene); //
        this.stage.show();
    }

    public void connectClient(String smtp, String imap, String login, String password) {
        this.mailHandler = new MailHandler(smtp, imap, login, password);
        switchToMain();
    }

    public void switchToMain() {
        this.stage.setScene(this.mainScreen.getScene());
//        this.stage.sizeToScene();
        this.stage.centerOnScreen();
    }

    public void composeMail() {
        sendScreen = new SendScreen(this);
        Stage composeWindow = new Stage();
        sendScreen.start(composeWindow);
    }

    public Stage getStage() {
        return stage;
    }
}