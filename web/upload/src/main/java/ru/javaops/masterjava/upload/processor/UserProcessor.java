package ru.javaops.masterjava.upload.processor;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import ru.javaops.masterjava.persist.DBIProvider;
import ru.javaops.masterjava.persist.dao.GroupDao;
import ru.javaops.masterjava.persist.dao.UserDao;
import ru.javaops.masterjava.persist.dao.UserGroupDao;
import ru.javaops.masterjava.persist.model.City;
import ru.javaops.masterjava.persist.model.User;
import ru.javaops.masterjava.persist.model.UserGroup;
import ru.javaops.masterjava.persist.model.type.UserFlag;
import ru.javaops.masterjava.upload.processor.PayloadProcessor.FailedEmails;
import ru.javaops.masterjava.xml.schema.ObjectFactory;
import ru.javaops.masterjava.xml.util.JaxbParser;
import ru.javaops.masterjava.xml.util.StaxStreamProcessor;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@Slf4j
public class UserProcessor {
    private static final int NUMBER_THREADS = 4;

    private static final JaxbParser jaxbParser = new JaxbParser(ObjectFactory.class);

    private static final GroupDao groupDao = DBIProvider.getDao(GroupDao.class);
    private static final UserDao userDao = DBIProvider.getDao(UserDao.class);
    private static final UserGroupDao userGroupDao = DBIProvider.getDao(UserGroupDao.class);

    private final ExecutorService executorService = Executors.newFixedThreadPool(NUMBER_THREADS);

    /*
     * return failed users chunks
     */
    public List<FailedEmails> process(final StaxStreamProcessor processor,
                                      Map<String, City> cities,
                                      int chunkSize) throws XMLStreamException, JAXBException {
        log.info("Start processing with chunkSize=" + chunkSize);
        val unmarshaller = jaxbParser.createUnmarshaller();

        Map<String, Future<Map<String, Integer>>> chunkFutures = new LinkedHashMap<>();  // ordered map (emailRange -> chunk future)
        List<FailedEmails> failed = new ArrayList<>();

        Map<String, Integer> groupNamesWithIds = groupDao.getIdsAsMap();
        Map<Integer, List<Integer>> usersIdsWithTheirGroupsIds = new HashMap<>();

        List<User> chunk = new ArrayList<>(chunkSize);
        int id = userDao.getSeqAndSkip(chunkSize);
        while (processor.doUntil(XMLEvent.START_ELEMENT, "User")) {

            // JAXB unmarshalling doesn't get objects by refs
            val cityRef = processor.getAttribute("city");

            String groupRefsAttribute = processor.getAttribute("groupRefs");
            val groupRefs = groupRefsAttribute == null ? new String[0] : groupRefsAttribute.split(" ");

            ru.javaops.masterjava.xml.schema.User xmlUser =
                    unmarshaller.unmarshal(processor.getReader(), ru.javaops.masterjava.xml.schema.User.class);

            List<Integer> groupIds = getGroupIds(groupRefs, groupNamesWithIds);

            // checks reports generating
            List<String> reports = new ArrayList<>();
            val cityRefReport = getCityRefReport(cityRef, cities);
            if (Objects.nonNull(cityRefReport)) {
                reports.add(cityRefReport);
            }
            val groupRefsReport = getGroupRefsReport(groupRefs, groupIds);
            if (Objects.nonNull(groupRefsReport)) {
                reports.add(groupRefsReport);
            }
            val invalidCausesReport = String.join(", ", reports);

            val email = xmlUser.getEmail();
            if (!invalidCausesReport.isEmpty()) {
                failed.add(new FailedEmails(email, invalidCausesReport));
                continue;
            }

            usersIdsWithTheirGroupsIds.put(id, groupIds);
            final User user = new User(id++, xmlUser.getValue(), email, UserFlag.valueOf(xmlUser.getFlag().value()), cityRef);

            chunk.add(user);
            if (chunk.size() == chunkSize) {
                addChunkFutures(chunkFutures, chunk);
                chunk = new ArrayList<>(chunkSize);
                id = userDao.getSeqAndSkip(chunkSize);
            }
        }

        if (!chunk.isEmpty()) {
            addChunkFutures(chunkFutures, chunk);
        }

        Map<String, Integer> allAlreadyPresented = new HashMap<>();
        processChunkFutures(chunkFutures, allAlreadyPresented, failed);
        if (!allAlreadyPresented.isEmpty()) {
            failed.add(new FailedEmails(allAlreadyPresented.keySet().toString(), "already presented"));
        }

        for (Integer alreadyPresentedUserId : allAlreadyPresented.values()) {
            usersIdsWithTheirGroupsIds.remove(alreadyPresentedUserId);
        }

        insertUserGroups(usersIdsWithTheirGroupsIds);

        return failed;
    }

    private String getGroupRefsReport(String[] groupRefs, List<Integer> groupIds) {
        if (Objects.isNull(groupIds)) {
            return "Some of groups: '" + Arrays.toString(groupRefs) + "' are not presented in DB";
        }
        return null;
    }

    private String getCityRefReport(String cityRef, Map<String, City> cities) {
        if (cities.get(cityRef) == null) {
            return "City '" + cityRef + "' is not presented in DB";
        }
        return null;
    }

    private List<Integer> getGroupIds(String[] groupRefs, Map<String, Integer> groupRefsWithIds) {
        List<Integer> foundIds =
                Arrays.stream(groupRefs)
                        .map(groupRefsWithIds::get)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());

        if (foundIds.size() < groupRefs.length) {
            return null;
        }
        return foundIds;
    }

    private void addChunkFutures(Map<String, Future<Map<String, Integer>>> chunkFutures, List<User> chunk) {
        String emailRange = String.format("[%s-%s]", chunk.get(0).getEmail(), chunk.get(chunk.size() - 1).getEmail());
        Future<Map<String, Integer>> future = executorService.submit(() -> userDao.insertAndGetConflictEmails(chunk));
        chunkFutures.put(emailRange, future);
        log.info("Submit chunk: " + emailRange);
    }

    private void processChunkFutures(Map<String, Future<Map<String, Integer>>> chunkFutures,
                                     Map<String, Integer> allAlreadyPresents,
                                     List<FailedEmails> failed) {
        chunkFutures.forEach((emailRange, future) -> {
            try {
                Map<String, Integer> alreadyPresentsInChunk = future.get();
                log.info(
                        "{} successfully executed with already presents: {}",
                        emailRange,
                        alreadyPresentsInChunk.keySet()
                );
                allAlreadyPresents.putAll(alreadyPresentsInChunk);
            } catch (InterruptedException | ExecutionException e) {
                log.error(emailRange + " failed", e);
                failed.add(new FailedEmails(emailRange, e.toString()));
            }
        });
    }

    private void insertUserGroups(Map<Integer, List<Integer>> usersIdsWithTheirGroupsIds) {
        userGroupDao.insertBatch(
                usersIdsWithTheirGroupsIds.entrySet()
                        .stream()
                        .flatMap(entry ->
                                entry.getValue()
                                        .stream()
                                        .map(groupId -> new UserGroup(entry.getKey(), groupId)))
                        .collect(Collectors.toList())
        );
    }
}
