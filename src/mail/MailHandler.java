package mail;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

public class MailHandler {
    /**
     * Est responsable des interactions avec le serveur mail d'un client.
     */

    protected Session session;
    protected Store store;
    private String user;
    private String password;

    public MailHandler(String smtpServer, String user, String password) {
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", smtpServer); //smtp.gmail.com
        properties.put("mail.smtp.port", "587");

        this.session = Session.getInstance(properties);
        this.user = user;
        this.password = password;

//        Store store = session.getStore("imap");
//        store.connect();

    }

    public void testMail() {
        /**
         * Envoie un mail de test de l'utilisateur à lui-même, contenant la date et l'heure.
         */

        try {
            MimeMessage message = new MimeMessage(this.session);
            message.setFrom(this.user);
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(this.user));
            message.setText(
                    "Ceci est un e-mail de test de l'application JavaMail\n" +
                    "Date : " + DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now()));
            message.setSubject("JavaMail test mail");
            Transport.send(message, this.user, this.password);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }






}
