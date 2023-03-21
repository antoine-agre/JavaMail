package userInterface;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import mail.MailHandler;

public class LoginScreenController {

    protected Client client;
    @FXML private TextField smtpInput;
    @FXML private Text smtpInputError;
    @FXML private TextField imapInput;
    @FXML private Text imapInputError;
    @FXML public TextField emailAddressInput;
    @FXML public Text emailAddressInputError;
    @FXML private TextField passwordInput;
    @FXML private Text passwordInputError;
    @FXML protected Label keyStatusLabel;
    @FXML protected Button keyRequestButton;
    @FXML protected HBox codeHBox;
    @FXML protected TextField codeInputField;
    @FXML protected Button codeOkButton;
    protected boolean hasKey = false;

    @FXML protected void handleKeyRequest(ActionEvent event) {
        this.keyRequestButton.setVisible(false);
        this.codeHBox.setVisible(true);
    }

    @FXML protected void handleOkButton(ActionEvent event) {
        this.codeHBox.setVisible(false);
        this.keyStatusLabel.setText("Reçue");
        this.keyStatusLabel.setTextFill(Color.GREEN);
    }

    @FXML protected void handleSubmitButtonAction(ActionEvent event) {
        client.connectClient(
                smtpInput.getText(),
                imapInput.getText(),
                emailAddressInput.getText(),
                passwordInput.getText());
    }

}
