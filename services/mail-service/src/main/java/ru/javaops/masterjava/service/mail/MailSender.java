package ru.javaops.masterjava.service.mail;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import ru.javaops.masterjava.config.Configs;

import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Slf4j
public class MailSender {
    private static final String HOST_PROPERTY = "host";
    private static final String PORT_PROPERTY = "port";
    private static final String USER_NAME_PROPERTY = "username";
    private static final String PASSWORD_PROPERTY = "password";
    private static final String USE_SSL_PROPERTY = "useSSL";
    private static final String USE_TLS_PROPERTY = "useTLS";
    private static final String DEBUG_PROPERTY = "debug";
    private static final String FROM_NAME_PROPERTY = "fromName";

    private static final String MAIL_SMTP_AUTH_PROPERTY = "mail.smtp.auth";
    private static final String MAIL_SMTP_HOST_PROPERTY = "mail.smtp.host";
    private static final String MAIL_SMTP_PORT_PROPERTY = "mail.smtp.port";
    private static final String MAIL_SMTP_SSL_ENABLE_PROPERTY = "mail.smtp.ssl.enable";
    private static final String MAIL_SMTP_START_TLS_ENABLE_PROPERTY = "mail.smtp.starttls.enable";

    private static final InternetAddress senderInternetAddress;
    private static final Session mailSession;

    static {
        val mailConfig = Configs.getConfig("mail.conf", "mail");

        // sender internet address initializing
        val senderAddress = mailConfig.getString(USER_NAME_PROPERTY);
        try {
            senderInternetAddress = new InternetAddress(senderAddress, mailConfig.getString(FROM_NAME_PROPERTY));
        } catch (UnsupportedEncodingException e) {
            log.error("Unsupported sender address: {}", senderAddress);
            throw new RuntimeException(e);
        }

        val properties = new Properties();
        properties.setProperty(MAIL_SMTP_AUTH_PROPERTY, Boolean.TRUE.toString());
        properties.setProperty(MAIL_SMTP_HOST_PROPERTY, mailConfig.getString(HOST_PROPERTY));
        properties.setProperty(MAIL_SMTP_PORT_PROPERTY, mailConfig.getString(PORT_PROPERTY));
        properties.setProperty(MAIL_SMTP_SSL_ENABLE_PROPERTY, mailConfig.getString(USE_SSL_PROPERTY));
        properties.setProperty(MAIL_SMTP_START_TLS_ENABLE_PROPERTY, mailConfig.getString(USE_TLS_PROPERTY));

        mailSession = Session.getInstance(
                properties,
                new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(
                                senderInternetAddress.getAddress(),
                                mailConfig.getString(PASSWORD_PROPERTY)
                        );
                    }
                });

        mailSession.setDebug(mailConfig.getBoolean(DEBUG_PROPERTY));
    }

    static void sendMail(List<UserAddress> to,
                         List<UserAddress> cc,
                         String subject,
                         String body) throws UnsupportedEncodingException, MessagingException {
        log.info("Send mail to '"
                 + to
                 + "' cc '"
                 + cc
                 + "' subject '"
                 + subject
                 + (log.isDebugEnabled() ? "\nbody=" + body : ""));

        Message message = new MimeMessage(mailSession);
        message.setFrom(senderInternetAddress);
        message.setRecipients(Message.RecipientType.TO, getMailAddresses(to));
        message.setRecipients(Message.RecipientType.CC, getMailAddresses(cc));
        message.setSubject(subject);
        message.setText(body);
        Transport.send(message);
    }

    private static javax.mail.Address[] getMailAddresses(
            List<UserAddress> userAddresses
    ) throws UnsupportedEncodingException {
        List<Address> mailAddresses = new ArrayList<>(userAddresses.size());
        for (UserAddress userAddress : userAddresses) {
            mailAddresses.add(new InternetAddress(userAddress.getEmail(), userAddress.getName()));
        }
        return mailAddresses.toArray(new javax.mail.Address[0]);
    }
}
