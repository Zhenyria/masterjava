package ru.javaops.masterjava.upload.processor;

import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import ru.javaops.masterjava.xml.schema.ObjectFactory;
import ru.javaops.masterjava.xml.util.JaxbParser;
import ru.javaops.masterjava.xml.util.StaxStreamProcessor;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class PayloadProcessor {
    private static final int NUMBER_THREADS = 4;

    private static final JaxbParser jaxbParser = new JaxbParser(ObjectFactory.class);

    private final ExecutorService executorService = Executors.newFixedThreadPool(NUMBER_THREADS);

    private final CityProcessor cityProcessor = new CityProcessor(executorService);
    private final UserProcessor userProcessor = new UserProcessor(executorService);

    /*
     * return failed users chunks
     */
    public PayloadProcessorResult process(final InputStream is, int chunkSize) throws XMLStreamException, JAXBException {
        log.info("Start processing with chunkSize=" + chunkSize);

        val processor = new StaxStreamProcessor(is);
        val unmarshaller = jaxbParser.createUnmarshaller();

        List<CityProcessor.FailedCities> failedCities = cityProcessor.process(processor, unmarshaller, chunkSize);
        List<UserProcessor.FailedEmails> failedEmails = userProcessor.process(processor, unmarshaller, chunkSize);

        return new PayloadProcessorResult(failedCities, failedEmails);
    }

    @Value
    public static class PayloadProcessorResult {
        List<CityProcessor.FailedCities> failedCities;
        List<UserProcessor.FailedEmails> failedEmails;
    }
}
