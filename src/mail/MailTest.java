package mail;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.mail.MessagingException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;

public class MailTest {
    public static void main(String[] args) {

        //Fichier de configuration à créer pour tester l'envoi de mail (!! ne pas commit !!)
        String configFilePath = "src/mail/credentials.properties";
        Properties loginProperties = new Properties();

        try {
            loginProperties.load(new FileInputStream(configFilePath));
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("user prop : " + loginProperties.getProperty("user"));
        System.out.println("pswd prop : " + loginProperties.getProperty("password"));

        System.out.println("Envoi en cours ...");
        MailHandler handler = new MailHandler(
                "smtp.gmail.com",
                "imap.gmail.com",
                loginProperties.getProperty("user"),
                loginProperties.getProperty("password")
        );

        File file = new File("/home/issa/Courses/AdvCrypto/JavaMail/EncryptedFiles/file1.txt");
        handler.sendMail("aaaaaa", "Test de mail", "Test de mail", file);

        System.out.println("Mail envoté");

    }
}
