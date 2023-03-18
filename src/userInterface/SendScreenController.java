package userInterface;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import mail.EMail;

import java.util.Date;

public class SendScreenController {

    protected static Client client;
    @FXML protected TextField recipientField;
    @FXML protected TextField subjectField;
    @FXML protected TextArea textBodyField;

    @FXML
    public void initialize() {
        /**
         * Executée à la création du controller, mais après que les attributs @FXML aient été remplis (contrairement
         * au constructeur).
         */
    }

    @FXML protected void handleSendAction(ActionEvent event) {
        client.mailHandler.sendMail(
                recipientField.getText(),
                subjectField.getText(),
                textBodyField.getText());
        client.sendScreen.getStage().close();
    }

}
