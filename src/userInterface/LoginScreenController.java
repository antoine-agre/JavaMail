package userInterface;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import mail.MailHandler;

public class LoginScreenController {

    protected static Client client;
    @FXML private TextField smtpInput;
    @FXML private Text smtpInputError;
    @FXML private TextField imapInput;
    @FXML private Text imapInputError;
    @FXML public TextField emailAddressInput;
    @FXML public Text emailAddressInputError;
    @FXML private TextField passwordInput;
    @FXML private Text passwordInputError;

    @FXML protected void handleSubmitButtonAction(ActionEvent event) {
        client.connectClient(
                smtpInput.getText(),
                imapInput.getText(),
                emailAddressInput.getText(),
                passwordInput.getText());
    }

}
