package ru.javaops.masterjava.xml.util;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class StaxStreamProcessor implements AutoCloseable {
    private static final XMLInputFactory FACTORY = XMLInputFactory.newInstance();

    private final XMLStreamReader reader;

    public StaxStreamProcessor(InputStream is) throws XMLStreamException {
        this.reader = FACTORY.createXMLStreamReader(is);
    }

    public XMLStreamReader getReader() {
        return reader;
    }

    public boolean doUntil(int stopEvent, String... values) throws XMLStreamException {
        Set<String> valuesSet = Arrays.stream(values).collect(Collectors.toSet());
        while (reader.hasNext()) {
            int event = reader.next();
            if (event == stopEvent) {
                if (valuesSet.contains(getValue(event))) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean parseElementUntil(String borderElementName, String... values) throws XMLStreamException {
        Set<String> valuesSet = Arrays.stream(values).collect(Collectors.toSet());
        while (reader.hasNext()) {
            int event = reader.next();
            String value = getValue(event);
            if (event == XMLEvent.END_ELEMENT) {
                if (borderElementName.equals(value)) {
                    return false;
                }
            } else {
                if (valuesSet.contains(value)) {
                    return true;
                }
            }
        }
        return false;
    }

    public String getAttributeValue(String attributeName) {
        return reader.getAttributeValue(null, attributeName);
    }

    public String getValue(int event) throws XMLStreamException {
        return (event == XMLEvent.CHARACTERS) ? reader.getText() : reader.getLocalName();
    }

    public String getCurrentElementValue() throws XMLStreamException {
        return reader.getElementText();
    }

    public String getElementValue(String element) throws XMLStreamException {
        return doUntil(XMLEvent.START_ELEMENT, element) ? reader.getElementText() : null;
    }

    public String getText() throws XMLStreamException {
        return reader.getElementText();
    }

    @Override
    public void close() {
        if (reader != null) {
            try {
                reader.close();
            } catch (XMLStreamException e) {
                // empty
            }
        }
    }
}
