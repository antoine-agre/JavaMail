package userInterface;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.IOException;
import java.util.Objects;

public class MainScreen {

    protected Scene scene;

    public MainScreen(){

        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(
                    getClass().getResource("/resources/fxml/mainScreen.fxml"),
                    "FXML file not loaded correctly."));

            this.scene = new Scene(root, 800, 450);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public Scene getScene() {
        return scene;
    }
}
