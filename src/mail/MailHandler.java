package mail;

import Cryptography.AES.AES;
import Cryptography.IBE.IBECipherText;
import Cryptography.IBE.IBEscheme;
import Cryptography.AES.AESFileEncryptor;
import it.unisa.dia.gas.jpbc.Element;

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
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }

    }


    public void sendMail(String recipient, String subject, String message, String attachementPath) throws MessagingException, IOException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {

        String AESKey = AES.randomString();

        MimeMessage mimeMessage = new MimeMessage(this.session);
        mimeMessage.setFrom(this.user);
        mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
        mimeMessage.setSubject(subject);

        Multipart myemailcontent=new MimeMultipart();
        MimeBodyPart bodypart=new MimeBodyPart();
        bodypart.setText(message);

        if(attachementPath!=null) {
            // First Attachment : the secret file encrypted with AES
            MimeBodyPart attachementfile=new MimeBodyPart();
            File Attachementfile=new File(attachementPath);
            File encryptedAttachementfile=new File(this.encryptedFilesFolder, "Encrypted_" + Attachementfile.getName());
            encryptedAttachementfile.createNewFile();
            AESFileEncryptor.fileEncrypt(Attachementfile,encryptedAttachementfile,AESKey);
            attachementfile.attachFile(encryptedAttachementfile);

            //Second Attachment : the file containing AES key infos encrypted using recipient IBE ID
            MimeBodyPart AESInfosFile=new MimeBodyPart();
            File AESinfos = AESdecryptionInfos(Attachementfile.getName(),AESKey);
            AESInfosFile.attachFile(AESinfos);

            myemailcontent.addBodyPart(attachementfile);
            myemailcontent.addBodyPart(AESInfosFile);
        }

        myemailcontent.addBodyPart(bodypart);
        mimeMessage.setContent(myemailcontent);
        Transport.send(mimeMessage,this.user,this.password);

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


    public File decryptAttachment(String attachmentPath,String AESInfoPath, Element privateKey) throws NoSuchPaddingException, IllegalBlockSizeException, IOException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        //Retrieve AES key infos
        Properties AESproperties = new Properties();
        try {
            AESproperties.load(new FileInputStream(AESInfoPath));
        } catch(IOException e) {e.printStackTrace();}
        byte[] u_bytes = AESproperties.getProperty("u").getBytes();
        Element u = this.pubParams.getG().newElementFromBytes(u_bytes);
        byte [] v = AESproperties.getProperty("v").getBytes();

        // Decrypt AES key using IBE private key
        IBECipherText C = new IBECipherText(u,v);
        byte [] AESprivateKeyBytes = this.pubParams.Decryption_Basic_IBE(this.pubParams.getP(),this.pubParams.getP(),privateKey,C);
        String AESprivateKey = new String(AESprivateKeyBytes);

        //Decrypt the file using AES key
        File attachmentFile = new File(attachmentPath);
        File decryptedAttachmentFile = new File(decryptedFilesFolder,"decrypted_"+attachmentFile.getName());
        AESFileEncryptor.fileDecrypt(attachmentFile, decryptedAttachmentFile,AESprivateKey);

        return decryptedAttachmentFile;
    }





    public void testMail(){
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
//                System.out.println("## Message : \n" + messageContent);
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
