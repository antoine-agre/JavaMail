package mail;

import Cryptography.IBE.IBEscheme;
import Cryptography.AES.AESFileEncryptor;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

public class MailHandler {
    /**
     * Est responsable des interactions avec le serveur mail d'un client.
     * Utilise SMTP pour l'envoi de messages et IMAP pour la récupération des messages.
     */

    protected Session session;
    protected Store store;
    private String user;
    private String password;
    protected Message[] inbox;

    protected String encryptedFilesFolder;

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

        this.encryptedFilesFolder  = "Cryptography/EncryptedFiles/";

        try {
            this.store = this.session.getStore("imap");
            this.store.connect(this.user, this.password);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }

    }
    public Message encryptMail(String recipient, String subject, String message, String attachementPath, String secretKey) throws MessagingException, IOException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        MimeMessage mimeMessage = new MimeMessage(this.session);
        mimeMessage.setFrom(this.user);
        mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
        mimeMessage.setSubject(subject);

        Multipart myemailcontent=new MimeMultipart();
        MimeBodyPart bodypart=new MimeBodyPart();
        bodypart.setText(message);

        // Encrypt attachment using AES
        MimeBodyPart attachementfile=new MimeBodyPart();
        File Attachementfile=new File(attachementPath);
        File dir = new File(this.encryptedFilesFolder);
        File encryptedAttachementfile=new File(dir, "Encrypted_" + Attachementfile.getName());
        encryptedAttachementfile.createNewFile();
        AESFileEncryptor.fileEncrypt(Attachementfile,encryptedAttachementfile,secretKey);
        attachementfile.attachFile(encryptedAttachementfile);

        myemailcontent.addBodyPart(bodypart);
        myemailcontent.addBodyPart(attachementfile);
        mimeMessage.setContent(myemailcontent);
        Transport.send(mimeMessage,this.user,this.password);

        return mimeMessage;

        //Transport.send(mimeMessage,this.user,this.password);

    }
    public void sendMail(String recipient, String subject, String message, String attachementPath) throws MessagingException, NoSuchPaddingException, IllegalBlockSizeException, IOException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        String AESsecretKey = "secret2255";
        Message mimeMessage = encryptMail(recipient, subject, message, attachementPath, AESsecretKey);
        IBEscheme schema = new IBEscheme();
        message = message + "\nAES_SECRET_KEY: " + AESsecretKey;




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

    public void checkMails() {
        /**
         * Récupère les mails du dossier inbox
         */

        try {

            Folder folderInbox = this.store.getFolder("INBOX");
            folderInbox.open(Folder.READ_ONLY);
            this.inbox = folderInbox.getMessages();

//            for (int i = 0; i < this.inbox.length; i++) {
//                Message message = this.inbox[i];
//                Address[] fromAddress = message.getFrom();
//
//                String from = fromAddress[0].toString();
//                String subject = message.getSubject();
//                String sentDate = message.getSentDate().toString();
//                String contentType = message.getContentType();
//                String messageContent = "";
//                boolean messageSeen = message.getFlags().contains(Flags.Flag.SEEN);
//                String attachFiles = "";
//                if (contentType.contains("multipart")) {
//                    messageContent = "[multipart]";
//                }
//                else if (contentType.contains("text/plain") || contentType.contains("text/html")) {
//                    Object content = message.getContent();
//                    if (content != null) {
//                        messageContent = content.toString();
//                    }
//                }
//
//                //Print
//                System.out.println("# Message #" + (i+1));
//                System.out.println("## Seen : " + messageSeen);
//                System.out.println("## From : " + from);
//                System.out.println("## Subject : " + subject);
//                System.out.println("## Sent : " + sentDate);
//                System.out.println("## Content type : " + contentType);
////                System.out.println("## Message : \n" + messageContent);
//                System.out.println("## Message : \n" + message.getContent().toString());
//
//            }

            //Disconnect
            folderInbox.close(false);

        } catch (MessagingException /*| IOException*/ e) {
            throw new RuntimeException(e);
        }

    }

}
