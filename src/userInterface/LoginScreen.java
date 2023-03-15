package userInterface;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class LoginScreen{

    protected Scene scene;

    public LoginScreen(){

        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(
                    getClass().getResource("/resources/fxml/loginScreen.fxml"),
                    "FXML file not loaded correctly."));

            this.scene = new Scene(root, 600, 400);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public Scene getScene() {
        return scene;
    }
}
