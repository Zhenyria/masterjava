package ru.javaops.masterjava.service.mail;

import ru.javaops.web.WebStateException;

import javax.jws.WebService;
import java.util.List;
import java.util.Set;

@WebService(endpointInterface = "ru.javaops.masterjava.service.mail.MailService",
            targetNamespace = "http://mail.javaops.ru/",
            wsdlLocation = "mailService.wsdl")
public class MailServiceImpl implements MailService {

    @Override
    public String sendToGroup(Set<Addressee> to,
                              Set<Addressee> cc,
                              String subject,
                              String body,
                              List<Attachment> attachments) throws WebStateException {
        return MailSender.sendToGroup(to, cc, subject, body, attachments);
    }

    @Override
    public GroupResult sendBulk(Set<Addressee> to,
                                String subject,
                                String body,
                                List<Attachment> attachments) throws WebStateException {
        return MailServiceExecutor.sendBulk(to, subject, body, attachments);
    }
}