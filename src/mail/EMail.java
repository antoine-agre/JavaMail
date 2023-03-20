package mail;

import javax.mail.*;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import java.io.IOException;
import java.util.Date;

public class EMail{
    /**
     * Représente un email et permet d'exposer son contenu pour la vue en liste du client.
     */

    protected String fromAddress;
    protected String subject;
    protected String content;
    protected Boolean hasAttachment;
    protected Boolean isEncrypted = false;
    protected String fileName = "";
    protected MimeBodyPart attachmentPart;
    protected MimeBodyPart propertiesPart;
    protected Date date;

    public EMail(String fromAddress, String subject, String content, Boolean hasAttachment) {
        this.fromAddress = fromAddress;
        this.subject = subject;
        this.content = content;
        this.hasAttachment = hasAttachment;
    }

    public EMail(Message message) {

        try {
            Address[] fromAddress = message.getFrom();

            this.fromAddress = fromAddress[0].toString();
            this.subject = message.getSubject();
            this.date = message.getReceivedDate();
            //placeholder :
            this.hasAttachment = false;

            //Analyse du contenu
            this.content = parseTextFromMessage(message);

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    private String parseTextFromMessage(Message message) {
        String result = "";
        try {

            if (message.isMimeType("text/plain")) {
                result = message.getContent().toString();
            }
            else if (message.isMimeType("multipart/*")) {
                MimeMultipart mimeMultipart = (MimeMultipart) message.getContent();
                result = parseTextFromMimeMultipart(mimeMultipart);
            }
            return result;

        } catch (MessagingException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String parseTextFromMimeMultipart(MimeMultipart mimeMultipart) {
        try {

            int count = mimeMultipart.getCount();
            if (count == 0) {throw new MessagingException("Multipart with no body parts not supported");}
            if (mimeMultipart.getContentType().contains("multipart/alternative")) { //Peut causer problème
                BodyPart part = mimeMultipart.getBodyPart(0);
                if(mimeMultipart.getBodyPart(0).isMimeType("text/plain")) {
                    return part.getContent().toString();
                }
                else {
                    return "ERROR";
                }
            }
            String result = "";
            for (int i = 0; i < count; i++) {
                BodyPart bodyPart = mimeMultipart.getBodyPart(i);
                result = parseTextFromBodyPart(bodyPart);
            }
            return result;

        } catch (MessagingException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String parseTextFromBodyPart(BodyPart bodyPart) {
        String result = "";
        try {
            if (bodyPart.isMimeType("text/plain")) {
                result = bodyPart.getContent().toString();
            }
            else if (bodyPart.isMimeType("text/html")) {
                result = "[HTML Content]";
            }
            else if (Part.ATTACHMENT.equalsIgnoreCase(bodyPart.getDisposition())) {
                this.hasAttachment = true;
                if (bodyPart.getFileName().endsWith(".chiffre")) {
                    this.isEncrypted = true;
                    this.attachmentPart = (MimeBodyPart) bodyPart;
                    this.fileName = bodyPart.getFileName();
                } else if (bodyPart.getFileName().endsWith(".properties")) {
                    this.propertiesPart = (MimeBodyPart) bodyPart;
                } else {
                    this.attachmentPart = (MimeBodyPart) bodyPart;
                    this.fileName = bodyPart.getFileName();
                }
            }
            else if (bodyPart.getContent() instanceof MimeMultipart) {
                result = parseTextFromMimeMultipart((MimeMultipart) bodyPart.getContent());
            }
            return result;
        } catch (MessagingException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getFromAddress() {
        return fromAddress;
    }

    public String getSubject() {
        return subject;
    }

    public String getContent() {
        return content;
    }

    public Boolean getHasAttachment() {
        return hasAttachment;
    }

    public Boolean getEncrypted() {
        return isEncrypted;
    }

    public MimeBodyPart getPropertiesPart() {
        return propertiesPart;
    }

    public Date getDate() {
        return date;
    }

    public String getFileName() {
        return fileName;
    }

    public MimeBodyPart getAttachmentPart() {
        return attachmentPart;
    }
}
