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
    protected Client client;

    public LoginScreen(Client client){

        this.client = client;

        try {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(
                    getClass().getResource("/resources/fxml/loginScreen.fxml"),
                    "FXML file not loaded correctly."));
            Parent root = loader.load();
            LoginScreenController controller = loader.getController();
            controller.client = this.client;

            this.scene = new Scene(root, 800, 400);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public Scene getScene() {
        return scene;
    }
}
