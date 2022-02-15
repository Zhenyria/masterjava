package ru.javaops.masterjava.xml.util;

import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;

public class XsltProcessor {
    private static TransformerFactory FACTORY = TransformerFactory.newInstance();
    private final Transformer xformer;

    public XsltProcessor(InputStream xslInputStream) {
        this(new BufferedReader(new InputStreamReader(xslInputStream, StandardCharsets.UTF_8)), Collections.emptyMap());
    }

    public XsltProcessor(InputStream xslInputStream, Map<String, String> params) {
        this(new BufferedReader(new InputStreamReader(xslInputStream, StandardCharsets.UTF_8)), params);
    }

    public XsltProcessor(Reader xslReader, Map<String, String> params) {
        try {
            Templates template = FACTORY.newTemplates(new StreamSource(xslReader));
            xformer = template.newTransformer();
            params.forEach(xformer::setParameter);
        } catch (TransformerConfigurationException e) {
            throw new IllegalStateException("XSLT transformer creation failed: " + e.toString(), e);
        }
    }

    public String transform(InputStream xmlInputStream) throws TransformerException {
        StringWriter out = new StringWriter();
        transform(xmlInputStream, out);
        return out.getBuffer().toString();
    }

    public void transform(InputStream xmlInputStream, Writer result) throws TransformerException {
        transform(new BufferedReader(new InputStreamReader(xmlInputStream, StandardCharsets.UTF_8)), result);
    }

    public void transform(Reader sourceReader, Writer result) throws TransformerException {
        xformer.transform(new StreamSource(sourceReader), new StreamResult(result));
    }

    public static String getXsltHeader(String xslt) {
        return "<?xml-stylesheet type=\"text/xsl\" href=\"" + xslt + "\"?>\n";
    }
}
