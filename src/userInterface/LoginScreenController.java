package userInterface;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.text.Text;

public class LoginScreenController {

    protected static Client client;
    @FXML private Text smtpInputError;
    @FXML private Text imapInputError;
    @FXML public Text emailAddressInputError;
    @FXML private Text passwordInputError;

    @FXML protected void handleSubmitButtonAction(ActionEvent event) {
//        imapInputError.setText("Je suis un champ IMAP !");
//        smtpInputError.setText("Je suis un champ SMTP !");
//        emailAddressInputError.setText("Je suis un champ adresse mail !");
//        passwordInputError.setText("Je suis un champ mot de passe !");
        client.switchToMain();
    }

}
