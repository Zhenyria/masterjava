package ru.javaops.masterjava.upload.web;

import ru.javaops.masterjava.upload.service.xml.schema.ObjectFactory;
import ru.javaops.masterjava.upload.service.xml.schema.Payload;
import ru.javaops.masterjava.upload.service.xml.util.jaxb.JaxbParser;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.io.InputStream;

@WebServlet("/xml")
@MultipartConfig
public class XmlParsingServlet extends HttpServlet {
    private static final String pathToXmlParsingJsp = "/WEB-INF/jsp/xmlParsing.jsp";
    private static final String pathToParsedXmlJsp = "/WEB-INF/jsp/parsedXml.jsp";
    private static final String currentRelativeUrl = "/upload/xml";
    private static final String parsedXmlFileName = "payload.xml";
    private JaxbParser jaxbParser;

    @Override
    public void init() {
        this.jaxbParser = new JaxbParser(ObjectFactory.class);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        getServletContext().getRequestDispatcher(pathToXmlParsingJsp).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Part payloadXmlPart = req.getParts()
                .stream()
                .filter(part -> part.getSubmittedFileName().equals(parsedXmlFileName))
                .findFirst()
                .orElse(null);

        if (payloadXmlPart == null || payloadXmlPart.getSize() == 0) {
            resp.sendRedirect(currentRelativeUrl);
            return;
        }

        Payload payload;
        try (InputStream payloadXmlInputStream = payloadXmlPart.getInputStream()) {
            payload = jaxbParser.unmarshal(payloadXmlInputStream);
        } catch (JAXBException e) {
            throw new IllegalArgumentException(e);
        }

        if (payload == null) {
            resp.sendRedirect(currentRelativeUrl);
            return;
        }

        req.getServletContext().setAttribute("projects", payload.getProjects().getProject());
        getServletContext().getRequestDispatcher(pathToParsedXmlJsp).forward(req, resp);
    }
}
