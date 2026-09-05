package util.mail;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.io.IOException;
import java.util.Properties;

public class MailService {
    private static final String HOST;
    private static final String PORT;
    private static final String USERNAME;
    private static final String PASSWORD;
    private static final String FROM;

    static {
        Properties config = new Properties();
        try {
            config.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("mail.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        HOST = config.getProperty("smtp.host");
        PORT = config.getProperty("smtp.port");
        USERNAME = config.getProperty("smtp.username");
        PASSWORD = config.getProperty("smtp.password");
        FROM = config.getProperty("smtp.from");
    }

    private static Session buildSession() {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", HOST);
        props.put("mail.smtp.port", PORT);

        return Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(USERNAME, PASSWORD);
            }
        });
    }

    public static void sendAsync(String to, String subject, String body) {
        if (to == null || to.isBlank()) {
            return;
        }
        new Thread(() -> {
            try {
                send(to, subject, body);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static void sendWithAttachmentAsync(String to, String subject, String body, File attachment) {
        if (to == null || to.isBlank()) {
            return;
        }
        new Thread(() -> {
            try {
                sendWithAttachment(to, subject, body, attachment);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static void send(String to, String subject, String body) throws MessagingException {
        Message message = new MimeMessage(buildSession());
        message.setFrom(new InternetAddress(FROM));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        message.setSubject(subject);
        message.setText(body);

        Transport.send(message);
    }

    public static void sendWithAttachment(String to, String subject, String body, File attachment) throws MessagingException {
        Message message = new MimeMessage(buildSession());
        message.setFrom(new InternetAddress(FROM));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        message.setSubject(subject);

        MimeBodyPart textPart = new MimeBodyPart();
        textPart.setText(body);

        MimeBodyPart attachmentPart = new MimeBodyPart();
        attachmentPart.setDataHandler(new DataHandler(new FileDataSource(attachment)));
        attachmentPart.setFileName(attachment.getName());

        MimeMultipart multipart = new MimeMultipart();
        multipart.addBodyPart(textPart);
        multipart.addBodyPart(attachmentPart);
        message.setContent(multipart);

        Transport.send(message);
    }

}
