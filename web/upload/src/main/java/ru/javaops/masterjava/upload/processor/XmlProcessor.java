package ru.javaops.masterjava.upload.processor;

import ru.javaops.masterjava.xml.util.JaxbUnmarshaller;
import ru.javaops.masterjava.xml.util.StaxStreamProcessor;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

public interface XmlProcessor<T> {

    T process(final StaxStreamProcessor processor,
              final JaxbUnmarshaller unmarshaller,
              int chunkSize) throws XMLStreamException, JAXBException;
}
