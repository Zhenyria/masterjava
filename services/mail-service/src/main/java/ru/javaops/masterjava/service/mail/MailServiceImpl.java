package ru.javaops.masterjava.service.mail;

import ru.javaops.web.WebStateException;

import javax.activation.DataHandler;
import javax.jws.WebService;
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
                              DataHandler attachment) throws WebStateException {
        return MailSender.sendToGroup(to, cc, subject, body);
    }

    @Override
    public GroupResult sendBulk(Set<Addressee> to,
                                String subject,
                                String body,
                                DataHandler attachment) throws WebStateException {
        return MailServiceExecutor.sendBulk(to, subject, body);
    }
}