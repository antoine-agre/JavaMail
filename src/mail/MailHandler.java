package mail;

import Cryptography.AES.AES;
import Cryptography.AES.AESFileEncryptor;
import Cryptography.IBE.IBECipherText;
import Cryptography.IBE.IBEscheme;
import it.unisa.dia.gas.jpbc.Element;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.DirectoryChooser;
import userInterface.Client;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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
    protected Folder folderInbox;
    protected String user;
    protected String password;
//    protected ArrayList<EMail> eMailList = new ArrayList<EMail>();
    protected ObservableList<EMail> eMailList = FXCollections.observableArrayList();
    //Cette initialisation sert uniquer de test
    //TODO à changer
    protected IBEscheme pubParams =  new IBEscheme();

    protected File encryptedFilesFolder ;
    protected File decryptedFilesFolder ;


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

        this.encryptedFilesFolder  = new File("EncryptedFiles/");
        this.encryptedFilesFolder.mkdirs();
        this.decryptedFilesFolder = new File("DecryptedFiles/");
        this.decryptedFilesFolder.mkdirs();

        try {
            this.store = this.session.getStore("imap");
            this.store.connect(this.user, this.password);
            this.folderInbox = this.store.getFolder("INBOX");
            this.folderInbox.open(Folder.READ_ONLY);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }

//        try {
//            this.store = this.session.getStore("imap");
//            this.store.connect(this.user, this.password);
//        } catch (MessagingException e) {
//            throw new RuntimeException(e);
//        }

    }

    //Create file that contains information about AES key
    public File AESdecryptionInfos(String name, String AESKey) throws IOException {
        IBECipherText cipher = this.pubParams.Encryption_Basic_IBE(this.pubParams.getP(), this.pubParams.getPpub(), this.user,AESKey);
        Element u = cipher.getU();
        byte[] v = cipher.getV();
        File AESInfos=new File(this.encryptedFilesFolder, "AES_" + name.replaceFirst("[.][^.]+$",".properties"));
        AESInfos.createNewFile();

        FileOutputStream outputStream = new FileOutputStream(AESInfos);
        byte[] inputBytesU = u.toBytes();
        outputStream.write("u:".getBytes());
        outputStream.write(inputBytesU);
        outputStream.write("\nv:".getBytes());
        outputStream.write(v);

        return AESInfos;
    }


    public File decryptAttachment(File attachmentFile, File AESInfoFile, Element privateKey) throws NoSuchPaddingException, IllegalBlockSizeException, IOException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        //Retrieve AES key infos
        Properties AESproperties = new Properties();
        try {
            AESproperties.load(new FileInputStream(AESInfoFile));
        } catch(IOException e) {e.printStackTrace();}
        byte[] u_bytes = AESproperties.getProperty("u").getBytes();
        Element u = this.pubParams.getG().newElementFromBytes(u_bytes);
        byte [] v = AESproperties.getProperty("v").getBytes();

        // Decrypt AES key using IBE private key
        IBECipherText C = new IBECipherText(u,v);
        byte [] AESprivateKeyBytes = this.pubParams.Decryption_Basic_IBE(this.pubParams.getP(),this.pubParams.getP(),privateKey,C);
        String AESprivateKey = new String(AESprivateKeyBytes);

        //Decrypt the file using AES key
//        File attachmentFile = new File(attachmentPath);
        String newFileName = attachmentFile.getName();
        newFileName = newFileName.substring(0, newFileName.length() - 8);
        File decryptedAttachmentFile = new File(decryptedFilesFolder, newFileName);
        AESFileEncryptor.fileDecrypt(attachmentFile, decryptedAttachmentFile,AESprivateKey);

        return decryptedAttachmentFile;
    }

    /**
     * Envoie un mail de test de l'utilisateur à lui-même, contenant la date et l'heure.
     */
    public void testMail() {
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

    /**
     * Envoie un mail de test de l'utilisateur à lui-même, contenant la date et l'heure.
     */
    public void sendMail(String recipientAddress, String subject, String textContent) {

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

    public void sendMail(String recipientAddress, String subject, String textContent, File attachment) {

        try {
            //Random AES Session key
            String AESKey = AES.randomString();

            //Header
            MimeMessage message = new MimeMessage(this.session);
            message.setFrom(this.user);
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipientAddress));
            message.setSubject(subject);

            //Corps et pièce jointe
            Multipart multipart = new MimeMultipart(); //

            MimeBodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText(textContent);
            multipart.addBodyPart(messageBodyPart);

            messageBodyPart = new MimeBodyPart();
            File encryptedAttachment = new File(this.encryptedFilesFolder, attachment.getName() + ".chiffre");
            encryptedAttachment.createNewFile();
            AESFileEncryptor.fileEncrypt(attachment, encryptedAttachment, AESKey);
            messageBodyPart.attachFile(encryptedAttachment);
//            DataSource source = new FileDataSource(attachment);
//            messageBodyPart.setDataHandler(new DataHandler(source));
//            messageBodyPart.setFileName(attachment.getName());
            multipart.addBodyPart(messageBodyPart);

            messageBodyPart = new MimeBodyPart();
            File AESInfos = AESdecryptionInfos(attachment.getName(), AESKey);
            messageBodyPart.attachFile(AESInfos);
            multipart.addBodyPart(messageBodyPart);

            message.setContent(multipart);

            //Envoi
            Transport.send(message, this.user, this.password);
        } catch (MessagingException | IOException | NoSuchPaddingException | IllegalBlockSizeException |
                 NoSuchAlgorithmException | BadPaddingException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Récupère les mails du dossier inbox
     */
    public void checkMails() {

        try {

//            Store store = this.session.getStore("imap");
//            store.connect(this.user, this.password);
//
//            Folder folderInbox = store.getFolder("INBOX");
//            folderInbox.open(Folder.READ_ONLY);
            Message[] inbox = this.folderInbox.getMessages();
            this.eMailList.clear();

            for (Message message : inbox) {
                EMail email = new EMail(message);
                this.eMailList.add(email);
            }

            //Disconnect
//            folderInbox.close(false);
//            store.close();

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }

    }

    public void downloadAttachment(Client client, EMail eMail) {
        try {

//            Message[] inbox = folderInbox.getMessages();

            DirectoryChooser dirChooser = new DirectoryChooser();
            File choosenDir = dirChooser.showDialog(client.getStage());
            eMail.getAttachmentPart().saveFile(choosenDir + "/" + eMail.getFileName());

            //Disconnect
//            folderInbox.close(false);
//            store.close();

        } catch (MessagingException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void downloadEncryptedAttachment(Client client, EMail eMail) {
        try {

//            Message[] inbox = folderInbox.getMessages();

            DirectoryChooser dirChooser = new DirectoryChooser();
            File choosenDir = dirChooser.showDialog(client.getStage());

            eMail.getAttachmentPart().saveFile("DecryptedFiles/" + eMail.getAttachmentPart().getFileName());
            eMail.getPropertiesPart().saveFile("DecryptedFiles/" + eMail.getPropertiesPart().getFileName());

            File attachmentFile = new File("DecryptedFiles/" + eMail.getAttachmentPart().getFileName());
            File propertiesFile = new File("DecryptedFiles/" + eMail.getPropertiesPart().getFileName());

            File decryptedFile = decryptAttachment(attachmentFile, propertiesFile, pubParams.generate_private_key_ID(this.user));

            Files.copy(decryptedFile.toPath(), Path.of(choosenDir.toString() + "/" + decryptedFile.getName()))  ;
//            decryptedFile.
//            eMail.getAttachmentPart().saveFile(choosenDir + "/" + eMail.getFileName());

            //Disconnect
//            folderInbox.close(false);
//            store.close();

        } catch (MessagingException | IOException e) {
            throw new RuntimeException(e);
        } catch (NoSuchPaddingException e) {
            throw new RuntimeException(e);
        } catch (IllegalBlockSizeException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (BadPaddingException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
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
