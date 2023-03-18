package userInterface;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import mail.EMail;

import java.io.File;
import java.util.Date;

public class SendScreenController {

    protected Client client;
    protected File choosenFile = null;
    @FXML protected TextField recipientField;
    @FXML protected TextField subjectField;
    @FXML protected TextArea textBodyField;
    @FXML protected Button chooseFileButton;
    @FXML protected Label choosenFileLabel;

    @FXML
    public void initialize() {
        /**
         * Executée à la création du controller, mais après que les attributs @FXML aient été remplis (contrairement
         * au constructeur).
         */
    }

    @FXML protected void handleSendAction(ActionEvent event) {
        if (choosenFile == null) {
            client.mailHandler.sendMail(
                    recipientField.getText(),
                    subjectField.getText(),
                    textBodyField.getText());
        } else {
            client.mailHandler.sendMail(
                    recipientField.getText(),
                    subjectField.getText(),
                    textBodyField.getText(),
                    choosenFile);
        }

        client.sendScreen.getStage().close();
    }

    @FXML protected void handleFileChoice() {
        FileChooser fileChooser = new FileChooser();
        choosenFile = fileChooser.showOpenDialog(client.sendScreen.getStage());
        choosenFileLabel.setText(choosenFile.getName());
    }

}
