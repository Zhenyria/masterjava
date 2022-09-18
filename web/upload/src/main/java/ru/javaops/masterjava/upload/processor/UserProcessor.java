package ru.javaops.masterjava.upload.processor;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import ru.javaops.masterjava.persist.DBIProvider;
import ru.javaops.masterjava.persist.dao.CityDao;
import ru.javaops.masterjava.persist.dao.UserDao;
import ru.javaops.masterjava.persist.model.City;
import ru.javaops.masterjava.persist.model.User;
import ru.javaops.masterjava.persist.model.UserFlag;
import ru.javaops.masterjava.xml.util.JaxbUnmarshaller;
import ru.javaops.masterjava.xml.util.StaxStreamProcessor;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

@Slf4j
@RequiredArgsConstructor
public class UserProcessor implements XmlProcessor<List<UserProcessor.FailedEmails>> {

    private static final UserDao userDao = DBIProvider.getDao(UserDao.class);
    private static final CityDao cityDao = DBIProvider.getDao(CityDao.class);

    private final ExecutorService executorService;

    @Override
    public List<UserProcessor.FailedEmails> process(final StaxStreamProcessor processor,
                                                    final JaxbUnmarshaller unmarshaller,
                                                    int chunkSize) throws XMLStreamException, JAXBException {
        Map<String, Future<List<String>>> chunkFutures = new LinkedHashMap<>();  // ordered map (emailRange -> chunk future)
        List<String> usersFromNotExistingCities = new ArrayList<>();

        Map<String, Integer> usedCities = new HashMap<>();

        int id = userDao.getSeqAndSkip(chunkSize);
        List<User> chunk = new ArrayList<>(chunkSize);

        while (processor.doUntil(XMLEvent.START_ELEMENT, "User")) {
            val cityCode = processor.getAttribute("city");
            ru.javaops.masterjava.xml.schema.User xmlUser = unmarshaller.unmarshal(processor.getReader(), ru.javaops.masterjava.xml.schema.User.class);
            val usersEmail = xmlUser.getEmail();

            Integer usersCityId = getCityId(cityCode, usedCities);
            if (usersCityId == null) {
                usersFromNotExistingCities.add(usersEmail);
                continue;
            } else {
                usedCities.put(cityCode, usersCityId);
            }

            chunk.add(new User(id++, xmlUser.getValue(), usersEmail, UserFlag.valueOf(xmlUser.getFlag().value()), usersCityId));
            if (chunk.size() == chunkSize) {
                addChunkFutures(chunkFutures, chunk);
                chunk = new ArrayList<>(chunkSize);
                id = userDao.getSeqAndSkip(chunkSize);
            }
        }

        if (!chunk.isEmpty()) {
            addChunkFutures(chunkFutures, chunk);
        }

        List<FailedEmails> failed = new ArrayList<>();
        List<String> allAlreadyPresents = new ArrayList<>();

        chunkFutures.forEach((emailRange, future) -> {
            try {
                List<String> alreadyPresentsInChunk = future.get();
                log.info("{} successfully executed with already presents: {}", emailRange, alreadyPresentsInChunk);
                allAlreadyPresents.addAll(alreadyPresentsInChunk);
            } catch (InterruptedException | ExecutionException e) {
                log.error(emailRange + " failed", e);
                failed.add(new FailedEmails(emailRange, e.toString()));
            }
        });
        if (!allAlreadyPresents.isEmpty()) {
            failed.add(new FailedEmails(allAlreadyPresents.toString(), "already presents"));
        }
        if (!usersFromNotExistingCities.isEmpty()) {
            failed.add(new FailedEmails(usersFromNotExistingCities.toString(), "from not existing cities"));
        }
        return failed;
    }

    private void addChunkFutures(Map<String, Future<List<String>>> chunkFutures, List<User> chunk) {
        String emailRange = String.format("[%s-%s]", chunk.get(0).getEmail(), chunk.get(chunk.size() - 1).getEmail());
        Future<List<String>> future = executorService.submit(() -> userDao.insertAndGetConflictEmails(chunk));
        chunkFutures.put(emailRange, future);
        log.info("Submit chunk: " + emailRange);
    }

    private Integer getCityId(String cityCode, Map<String, Integer> usedCities) {
        Integer cityId = usedCities.get(cityCode);
        return cityId == null
                ? Optional.ofNullable(cityDao.getByCode(cityCode)).map(City::getId).orElse(null)
                : cityId;
    }

    @AllArgsConstructor
    public static class FailedEmails {
        public String emailsOrRange;
        public String reason;

        @Override
        public String toString() {
            return emailsOrRange + " : " + reason;
        }
    }
}
