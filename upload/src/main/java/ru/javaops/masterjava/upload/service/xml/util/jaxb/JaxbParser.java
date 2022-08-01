package ru.javaops.masterjava.upload.service.xml.util.jaxb;

import org.xml.sax.SAXException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Marshalling/Unmarshalling JAXB helper
 * XML Facade
 */
public class JaxbParser {

    private final Map<String, Object> marshallerProperties = new ConcurrentHashMap<>();
    protected Schema schema;
    protected JAXBContext context;

    public JaxbParser(Class<?>... classesToBeBound) {
        try {
            init(JAXBContext.newInstance(classesToBeBound));
        } catch (JAXBException e) {
            throw new IllegalArgumentException(e);
        }
    }

    //    http://stackoverflow.com/questions/30643802/what-is-jaxbcontext-newinstancestring-contextpath
    public JaxbParser(String context) {
        try {
            init(JAXBContext.newInstance(context));
        } catch (JAXBException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private void init(JAXBContext ctx) throws JAXBException {
        this.context = ctx;
    }

    // Unmarshaller

    @SuppressWarnings(value = "unchecked")
    public <T> T unmarshal(InputStream is) throws JAXBException {
        return (T) createUnmarshaller().unmarshal(is);
    }

    @SuppressWarnings(value = "unchecked")
    public <T> T unmarshal(Reader reader) throws JAXBException {
        return (T) createUnmarshaller().unmarshal(reader);
    }

    @SuppressWarnings(value = "unchecked")
    public <T> T unmarshal(String str) throws JAXBException {
        return (T) createUnmarshaller().unmarshal(str);
    }

    public <T> T unmarshal(XMLStreamReader reader, Class<T> elementClass) throws JAXBException {
        return createUnmarshaller().unmarshal(reader, elementClass);
    }

    // Marshaller

    public void setMarshallerProperty(String prop, Object value) {
        marshallerProperties.put(prop, value);
    }

    public String marshal(Object instance) throws JAXBException {
        return createMarshaller().marshal(instance);
    }

    public void marshal(Object instance, Writer writer) throws JAXBException {
        createMarshaller().marshal(instance, writer);
    }

    public void setSchema(Schema schema) {
        this.schema = schema;
    }

    public void validate(String str) throws IOException, SAXException {
        validate(new StringReader(str));
    }

    public void validate(Reader reader) throws IOException, SAXException {
        schema.newValidator().validate(new StreamSource(reader));
    }

    protected JaxbMarshaller createMarshaller() throws JAXBException {
        JaxbMarshaller marshaller = new JaxbMarshaller(context);
        marshaller.setProperties(marshallerProperties);
        return marshaller;
    }

    protected JaxbUnmarshaller createUnmarshaller() throws JAXBException {
        return new JaxbUnmarshaller(context);
    }
}
