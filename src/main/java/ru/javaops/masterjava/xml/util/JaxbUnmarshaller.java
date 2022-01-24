package ru.javaops.masterjava.xml.util;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;

public class JaxbUnmarshaller {
    private final Unmarshaller unmarshaller;

    public JaxbUnmarshaller(JAXBContext ctx) throws JAXBException {
        unmarshaller = ctx.createUnmarshaller();
    }

    public synchronized void setSchema(Schema schema) {
        unmarshaller.setSchema(schema);
    }

    @SuppressWarnings("unchecked")
    public synchronized <T> T unmarshal(InputStream is) throws JAXBException {
        return ((JAXBElement<T>) unmarshaller.unmarshal(is)).getValue();
    }

    @SuppressWarnings("unchecked")
    public synchronized <T> T unmarshal(Reader reader) throws JAXBException {
        return ((JAXBElement<T>) unmarshaller.unmarshal(reader)).getValue();
    }

    public Object unmarshal(String str) throws JAXBException {
        return unmarshal(new StringReader(str));
    }
}
