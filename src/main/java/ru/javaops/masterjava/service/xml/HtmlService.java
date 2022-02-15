package ru.javaops.masterjava.service.xml;

import ru.javaops.masterjava.xml.util.XsltProcessor;

import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * Works about generating html from xml
 */
public class HtmlService {
    private final XsltProcessor processor;

    public HtmlService(String pathToXsl) {
        this.processor = new XsltProcessor(this.getClass().getClassLoader().getResourceAsStream(pathToXsl));
    }

    public HtmlService(String pathToXsl, Map<String, String> params) {
        this.processor = new XsltProcessor(this.getClass().getClassLoader().getResourceAsStream(pathToXsl), params);
    }

    /**
     * Parse xml file and return html. Cans use params for xslt
     *
     * @param pathToXml path to parseable xml
     * @return html as string
     */
    public String getHtmlFromXml(String pathToXml) {
        try (InputStream xmlInputStream = this.getClass().getClassLoader().getResourceAsStream(pathToXml)) {
            return processor.transform(xmlInputStream);
        } catch (IOException e) {
            throw new RuntimeException("The '" + pathToXml + "' file loading was failed", e);
        } catch (TransformerException e) {
            throw new RuntimeException("The '" + pathToXml + "' file parsing was failed", e);
        }
    }
}
