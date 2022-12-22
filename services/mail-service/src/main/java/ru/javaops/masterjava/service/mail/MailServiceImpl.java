package ru.javaops.masterjava.service.mail;

import lombok.extern.slf4j.Slf4j;
import ru.javaops.masterjava.persist.DBIProvider;
import ru.javaops.masterjava.service.mail.dao.MailSendingResultDao;

import javax.jws.WebService;
import java.util.List;

@Slf4j
@WebService(endpointInterface = "ru.javaops.masterjava.service.mail.MailService")
public class MailServiceImpl implements MailService {
    private final MailSendingResultDao mailSendingResultDao = DBIProvider.getDao(MailSendingResultDao.class);
    private final MailServiceExecutor mailServiceExecutor = new MailServiceExecutor();

    public void sendMail(List<UserAddress> to, List<UserAddress> cc, String subject, String body) {
        mailSendingResultDao.insert(mailServiceExecutor.sendToList(subject, body, to));
    }
}