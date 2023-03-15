package userInterface;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class MainScreenController {

    @FXML protected VBox mailListVBox;

    public void addTestLine() {

        try {
            Parent testMailLine = FXMLLoader.load(
                    getClass().getResource("/resources/fxml/mailLine.fxml"));
            mailListVBox.getChildren().add(testMailLine);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}
