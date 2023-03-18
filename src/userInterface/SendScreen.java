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

    public static void main(String[] args) {launch(args);}

    @Override
    public void start(Stage primaryStage) {

        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(
                    getClass().getResource("/resources/fxml/sendScreen.fxml"),
                    "FXML file not loaded correctly."));

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
