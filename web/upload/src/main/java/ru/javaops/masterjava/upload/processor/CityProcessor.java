package ru.javaops.masterjava.upload.processor;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import ru.javaops.masterjava.persist.DBIProvider;
import ru.javaops.masterjava.persist.dao.CityDao;
import ru.javaops.masterjava.persist.model.City;
import ru.javaops.masterjava.xml.schema.CityType;
import ru.javaops.masterjava.xml.util.JaxbUnmarshaller;
import ru.javaops.masterjava.xml.util.StaxStreamProcessor;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class CityProcessor implements XmlProcessor<List<CityProcessor.FailedCities>> {

    private static final CityDao cityDao = DBIProvider.getDao(CityDao.class);

    private final ExecutorService executorService;

    @Override
    public List<CityProcessor.FailedCities> process(
            final StaxStreamProcessor processor,
            final JaxbUnmarshaller unmarshaller,
            int chunkSize
    ) throws XMLStreamException, JAXBException {
        Map<List<String>, Future<List<String>>> chunkFutures = new LinkedHashMap<>();

        Integer id = null;
        List<City> chunk = new ArrayList<>(chunkSize);

        while (processor.doUntil(
                XMLEvent.START_ELEMENT,
                "City",
                XMLEvent.END_ELEMENT,
                "Cities"
        )) {
            if (chunk.isEmpty()) {
                id = cityDao.getSeqAndSkip(chunkSize);
            }
            CityType xmlCity = unmarshaller.unmarshal(processor.getReader(), CityType.class);
            chunk.add(new City(id++, xmlCity.getId(), xmlCity.getValue()));
            if (chunk.size() == chunkSize) {
                processChunk(chunkFutures, chunk, chunkSize);
                chunk = new ArrayList<>(chunkSize);
            }
        }

        if (!chunk.isEmpty()) {
            processChunk(chunkFutures, chunk, chunkSize);
        }

        List<FailedCities> failedCities = new ArrayList<>();
        chunkFutures.forEach((range, future) -> {
            try {
                List<String> ignoredCities = future.get();
                if (!ignoredCities.isEmpty()) {
                    log.warn("Ignored cities: {}", ignoredCities);
                    failedCities.add(new FailedCities(ignoredCities, "already exist"));
                }
            } catch (ExecutionException | InterruptedException e) {
                log.warn("Failed cities chunk: {}, thrown exception: {}", range, e);
                failedCities.add(new FailedCities(range, e.toString()));
            }
        });

        return failedCities;
    }

    private void processChunk(Map<List<String>, Future<List<String>>> chunkFutures, List<City> chunk, int chunkSize) {
        List<String> cities = chunk.stream().map(City::getName).collect(Collectors.toList());
        chunkFutures.put(cities, executorService.submit(() -> cityDao.insertAndGetIgnored(chunk, chunkSize)));
        log.info("Submit cities chunk: {}", cities);
    }

    @Value
    public static class FailedCities {
        List<String> cities;
        String reason;
    }
}
