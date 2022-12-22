package ru.javaops.masterjava.service.mail.dao;

import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlBatch;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import ru.javaops.masterjava.persist.dao.AbstractDao;
import ru.javaops.masterjava.service.mail.model.MailSendingResult;

import java.util.Collection;

public abstract class MailSendingResultDao implements AbstractDao {

    @SqlUpdate("TRUNCATE mail_sending_result CASCADE")
    @Override
    public abstract void clean();

    @SqlBatch("INSERT INTO mail_sending_result (email, name_val, message_title, message_body, status, description) " +
              "VALUES (" +
              ":email, :name, :messageTitle, :messageBody, CAST(:status AS mail_sending_status), :description" +
              ") " +
              "ON CONFLICT DO NOTHING")
    public abstract void insert(@BindBean Collection<? extends MailSendingResult> mailSendingResults);
}
