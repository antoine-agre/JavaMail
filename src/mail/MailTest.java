package mail;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class MailTest {
    public static void main(String[] args) {

        //Fichier de configuration à créer pour tester l'envoi de mail (!! ne pas commit !!)
        String configFilePath = "src/mail/testMailLogin.properties";
        Properties loginProperties = new Properties();

        try {
            loginProperties.load(new FileInputStream(configFilePath));
        } catch(IOException e) {e.printStackTrace();}

        System.out.println("user prop : " + loginProperties.getProperty("user"));
        System.out.println("pswd prop : " + loginProperties.getProperty("password"));

        System.out.println("Début des tests");
        MailHandler handler = new MailHandler(
                "smtp.gmail.com",
                loginProperties.getProperty("user"),
                loginProperties.getProperty("password")
        );
        handler.testMail();
        System.out.println("Fin des tests");
    }
}
