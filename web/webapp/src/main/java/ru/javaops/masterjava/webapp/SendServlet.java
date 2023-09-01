package ru.javaops.masterjava.webapp;

import lombok.extern.slf4j.Slf4j;
import ru.javaops.masterjava.service.mail.Attachment;
import ru.javaops.masterjava.service.mail.GroupResult;
import ru.javaops.masterjava.service.mail.InputStreamDataSource;
import ru.javaops.masterjava.service.mail.MailWSClient;

import javax.activation.DataHandler;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

@WebServlet("/send")
@Slf4j
@MultipartConfig
public class SendServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String result;
        try {
            log.info("Start sending");
            req.setCharacterEncoding("UTF-8");
            resp.setCharacterEncoding("UTF-8");
            String users = req.getParameter("users");
            String subject = req.getParameter("subject");
            String body = req.getParameter("body");

            Part attachmentPart = req.getPart("attachment");
            List<Attachment> attachments = null;
            if (attachmentPart != null) {
                attachments = Collections.singletonList(
                        new Attachment(attachmentPart.getSubmittedFileName(),
                                       new DataHandler(new InputStreamDataSource(req.getPart("attachment")
                                                                                    .getInputStream())))
                );
            }

            GroupResult groupResult = MailWSClient.sendBulk(MailWSClient.split(users), subject, body, attachments);
            result = groupResult.toString();
            log.info("Processing finished with result: {}", result);
        } catch (Exception e) {
            log.error("Processing failed", e);
            result = e.toString();
        }
        resp.getWriter().write(result);
    }
}
