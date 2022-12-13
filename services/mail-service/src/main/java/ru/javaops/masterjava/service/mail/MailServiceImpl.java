package ru.javaops.masterjava.service.mail;

import lombok.extern.slf4j.Slf4j;

import javax.jws.WebService;
import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import java.util.List;

@Slf4j
@WebService(endpointInterface = "ru.javaops.masterjava.service.mail.MailService")
public class MailServiceImpl implements MailService {
    public void sendMail(List<UserAddress> to, List<UserAddress> cc, String subject, String body) {
        try {
            MailSender.sendMail(to, cc, subject, body);
        } catch (MessagingException | UnsupportedEncodingException e) {
            log.error(e.getMessage());
        }
    }
}