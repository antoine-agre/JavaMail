package userInterface;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;

import javax.mail.Message;
import java.io.IOException;
import java.util.Objects;

public class MainScreen {

    protected Scene scene;
    protected static Client client;

    public MainScreen(Client client){

        this.client = client;

        try {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(
                    getClass().getResource("/resources/fxml/mainScreen.fxml"),
                    "FXML file not loaded correctly."));
            Parent root = loader.load();
            MainScreenController controller = loader.getController();
            controller.client = this.client;

            this.scene = new Scene(root, 1366, 768);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public Scene getScene() {
        return scene;
    }
}
