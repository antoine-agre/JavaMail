package mail;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Properties;

public class MailHandler {
    /**
     * Est responsable des interactions avec le serveur mail d'un client.
     * Utilise SMTP pour l'envoi de messages et IMAP pour la récupération des messages.
     */

    protected Session session;
    protected Store store;
    protected String user;
    protected String password;
//    protected ArrayList<EMail> eMailList = new ArrayList<EMail>();
    protected ObservableList<EMail> eMailList = FXCollections.observableArrayList();

    public MailHandler(String smtpServer, String imapServer, String user, String password) {
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", smtpServer); //smtp.gmail.com
        properties.put("mail.smtp.port", "587");

        properties.put("mail.imap.host", imapServer); //imap.gmail.com
        properties.put("mail.imap.port", "993");
        properties.setProperty("mail.imap.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        properties.setProperty("mail.imap.socketFactory.fallback", "false");
        properties.setProperty("mail.imap.socketFactory.port", "993");

        this.session = Session.getInstance(properties);
        this.user = user;
        this.password = password;

        try {
            this.store = this.session.getStore("imap");
            this.store.connect(this.user, this.password);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
        checkMails();

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

    public void sendMail(String recipientAddress, String subject, String textContent) {
        /**
         * Envoie un mail de test de l'utilisateur à lui-même, contenant la date et l'heure.
         */

        try {
            MimeMessage message = new MimeMessage(this.session);
            message.setFrom(this.user);
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipientAddress));
            message.setText(textContent);
            message.setSubject(subject);
            Transport.send(message, this.user, this.password);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    public void checkMails() {
        /**
         * Récupère les mails du dossier inbox
         */

        try {

            Folder folderInbox = this.store.getFolder("INBOX");
            folderInbox.open(Folder.READ_ONLY);
            Message[] inbox = folderInbox.getMessages();
            this.eMailList.clear();

            for (int i = 0; i < inbox.length; i++) {
                Message message = inbox[i];
                EMail email = new EMail(message);
                this.eMailList.add(email);
            }

            //Disconnect
            folderInbox.close(false);

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }

    }

    public ObservableList<EMail> getEMailList() {
        return eMailList;
    }

    public String getUser() {
        return user;
    }
}
