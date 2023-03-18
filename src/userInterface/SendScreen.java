package userInterface;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class SendScreen extends Application {

    protected Scene scene;
    protected Stage stage;
    protected Client client;

    public SendScreen(Client client) {
        this.client = client;
    }

    public static void main(String[] args) {launch(args);}

    @Override
    public void start(Stage primaryStage) {

        try {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(
                    getClass().getResource("/resources/fxml/sendScreen.fxml"),
                    "FXML file not loaded correctly."));
            Parent root = loader.load();
            SendScreenController controller = loader.getController();
            controller.client = this.client;

            scene = new Scene(root, 900, 600);

            primaryStage.setTitle("Nouveau mail");
            primaryStage.setScene(scene);
            primaryStage.initModality(Modality.NONE);
            primaryStage.show();
            stage = primaryStage;

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public Scene getScene() {
        return scene;
    }

    public Stage getStage() {
        return stage;
    }
}
