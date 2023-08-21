package ru.javaops.masterjava.webapp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.util.StringUtils;
import ru.javaops.masterjava.service.mail.Addressee;
import ru.javaops.masterjava.service.mail.MailWSClient;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import static ru.javaops.masterjava.common.web.ThymeleafListener.engine;

@WebServlet("/send-email")
public class SendEmailServlet extends HttpServlet {
    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        SendEmailsDto sendEmailsDto;
        try (BufferedReader reader = req.getReader()) {
            sendEmailsDto = mapper.readValue(reader, SendEmailsDto.class);
        }

        String result;
        if (StringUtils.isEmpty(sendEmailsDto.getSubject())
            || StringUtils.isEmpty(sendEmailsDto.getBody())
            || sendEmailsDto.getUsers() == null
            || sendEmailsDto.getUsers().isEmpty()) {
            result = "No data for emails sending";
        } else {
            Set<Addressee> addresses = sendEmailsDto.getUsers()
                                                    .stream()
                                                    .map(userEmailDto -> new Addressee(userEmailDto.getEmail(),
                                                                                       userEmailDto.getFullName()))
                                                    .collect(Collectors.toSet());

            result = MailWSClient.sendToGroup(addresses,
                                              Collections.emptySet(),
                                              sendEmailsDto.getSubject(),
                                              sendEmailsDto.getBody());
        }

        out(req, resp, result);
    }

    private void out(HttpServletRequest req, HttpServletResponse resp, String result) throws IOException {
        resp.setCharacterEncoding("UTF-8");
        final WebContext webContext = new WebContext(req,
                                                     resp,
                                                     req.getServletContext(),
                                                     req.getLocale(),
                                                     ImmutableMap.of("result", result));
        engine.process("mailsSendingResult", webContext, resp.getWriter());
    }
}
