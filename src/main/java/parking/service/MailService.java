package parking.service;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.Session;
import java.util.Properties;
import javax.mail.*;

public class MailService {
    static Properties mailServerProperties;
    static Session getMailSession;
    static MimeMessage mailMessage;

    public static void sendEmail(String email, String subject, String message) throws MessagingException {
        mailServerProperties = System.getProperties();
        mailServerProperties.put("mail.smtp.host", "localhost");

        getMailSession = Session.getDefaultInstance(mailServerProperties, null);
        mailMessage = new MimeMessage(getMailSession);
        mailMessage.setFrom(new InternetAddress("parkinger@gmail.com"));
        mailMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
        mailMessage.setSubject(subject);
        mailMessage.setText("the message text");

        Transport.send(mailMessage);
    }

}

