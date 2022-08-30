package ru.javaops.masterjava.upload;

import ru.javaops.masterjava.persist.DBIProvider;
import ru.javaops.masterjava.persist.dao.UserDao;
import ru.javaops.masterjava.persist.model.UserFlag;
import ru.javaops.masterjava.xml.schema.ObjectFactory;
import ru.javaops.masterjava.xml.util.JaxbParser;
import ru.javaops.masterjava.xml.util.JaxbUnmarshaller;
import ru.javaops.masterjava.xml.util.StaxStreamProcessor;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static ru.javaops.masterjava.persist.dao.UserDao.ALLOCATION_SIZE;

public class UserProcessor {
    private static final int THREAD_NUMBER = 10;

    private static final ExecutorService executor = Executors.newFixedThreadPool(THREAD_NUMBER);
    private static final JaxbParser jaxbParser = new JaxbParser(ObjectFactory.class);

    private final UserDao userDao = DBIProvider.getDao(UserDao.class);

    public UsersInsertingResult process(final InputStream is, int chunkSize) throws XMLStreamException, JAXBException {
        final StaxStreamProcessor processor = new StaxStreamProcessor(is);

        CompletionService<UsersBatchInsertingResult> completionService = new ExecutorCompletionService<>(executor);
        Map<Future<UsersBatchInsertingResult>, List<String>> futuresWithUserInsertingRange = new HashMap<>();

        JaxbUnmarshaller unmarshaller = jaxbParser.createUnmarshaller();

        List<Integer> ids = new ArrayList<>();
        List<String> fullNames = new ArrayList<>();
        List<String> emails = new ArrayList<>();
        List<UserFlag> userFlags = new ArrayList<>();

        int startVal = userDao.nextVal();
        int nextVal = startVal;
        userDao.prepareSequenceToBatchInsert();

        int currentChunkSize = 0;
        while (processor.doUntil(XMLEvent.START_ELEMENT, "User")) {
            ru.javaops.masterjava.xml.schema.User xmlUser =
                    unmarshaller.unmarshal(processor.getReader(), ru.javaops.masterjava.xml.schema.User.class);
            ids.add(nextVal);
            fullNames.add(xmlUser.getValue());
            emails.add(xmlUser.getEmail());
            userFlags.add(UserFlag.valueOf(xmlUser.getFlag().value()));
            if (startVal + ALLOCATION_SIZE == nextVal + 1) {
                startVal = userDao.nextVal();
                nextVal = startVal;
            } else {
                nextVal++;
            }
            if (currentChunkSize + 1 == chunkSize) {
                futuresWithUserInsertingRange.put(
                        insertUsers(completionService, ids, fullNames, emails, userFlags),
                        fullNames
                );
                ids = new ArrayList<>();
                fullNames = new ArrayList<>();
                emails = new ArrayList<>();
                userFlags = new ArrayList<>();
                currentChunkSize = 0;
            } else {
                currentChunkSize++;
            }
        }

        if (!fullNames.isEmpty()) {
            futuresWithUserInsertingRange.put(insertUsers(completionService, ids, fullNames, emails, userFlags), fullNames);
        }

        UsersInsertingResult insertingResult = new Callable<UsersInsertingResult>() {
            private final List<String> insertedUsers = new ArrayList<>();
            private final List<String> ignoredUsers = new ArrayList<>();
            private final List<FailedUsersInsertingRangeData> failedInsertsData = new ArrayList<>();

            @Override
            public UsersInsertingResult call() {
                List<Future<UsersBatchInsertingResult>> submittedFutures =
                        new ArrayList<>(futuresWithUserInsertingRange.keySet());

                submittedFutures.forEach(this::processFuture);

                return new UsersInsertingResult(insertedUsers, ignoredUsers, failedInsertsData);
            }

            private void processFuture(Future<UsersBatchInsertingResult> future) {
                try {
                    UsersBatchInsertingResult usersBatchInsertingResult = future.get();
                    insertedUsers.addAll(usersBatchInsertingResult.getInsertedUsers());
                    ignoredUsers.addAll(usersBatchInsertingResult.getIgnoredUsers());
                } catch (ExecutionException | InterruptedException e) {
                    List<String> failedUsers = futuresWithUserInsertingRange.get(future);
                    failedInsertsData.add(
                            new FailedUsersInsertingRangeData(failedUsers, e.getMessage()));
                }
            }
        }.call();

        userDao.returnSequenceToInitialCondition();
        return insertingResult;
    }

    private Future<UsersBatchInsertingResult> insertUsers(CompletionService<UsersBatchInsertingResult> completionService,
                                                          List<Integer> ids,
                                                          List<String> fullNames,
                                                          List<String> emailsToInsert,
                                                          List<UserFlag> userFlagsToInsert) {
        return completionService.submit(() -> {
            int[] insertResult = userDao.insert(ids, fullNames, emailsToInsert, userFlagsToInsert);
            List<String> insertedUsers = new ArrayList<>();
            List<String> ignoredUsers = new ArrayList<>();

            for (int i = 0; i < insertResult.length; i++) {
                String handledUser = emailsToInsert.get(i);
                if (insertResult[i] == 1) {
                    insertedUsers.add(handledUser);
                } else {
                    ignoredUsers.add(handledUser);
                }
            }

            return new UsersBatchInsertingResult(insertedUsers, ignoredUsers);
        });
    }

    public static class UsersInsertingResult {
        private final List<String> insertedUsers;
        private final List<String> ignoredUsers;
        private final List<FailedUsersInsertingRangeData> failedUsersInsertingRangeDataList;

        public UsersInsertingResult(List<String> insertedUsers,
                                    List<String> ignoredUsers,
                                    List<FailedUsersInsertingRangeData> failedUsersInsertingRangeDataList) {
            this.insertedUsers = insertedUsers;
            this.ignoredUsers = ignoredUsers;
            this.failedUsersInsertingRangeDataList = failedUsersInsertingRangeDataList;
        }

        public List<String> getInsertedUsers() {
            return insertedUsers;
        }

        public List<String> getIgnoredUsers() {
            return ignoredUsers;
        }

        public List<FailedUsersInsertingRangeData> getFailedUserInsertingRangeDataList() {
            return failedUsersInsertingRangeDataList;
        }
    }

    public static class FailedUsersInsertingRangeData {
        private final List<String> failedUsers;
        private final String message;

        public FailedUsersInsertingRangeData(List<String> failedUsers, String message) {
            this.failedUsers = failedUsers;
            this.message = message;
        }

        public List<String> getFailedUsers() {
            return failedUsers;
        }

        public String getMessage() {
            return message;
        }
    }

    public static class UsersBatchInsertingResult {
        private final List<String> insertedUsers;
        private final List<String> ignoredUsers;

        public UsersBatchInsertingResult(List<String> insertedUsers, List<String> alreadyExistingUsers) {
            this.insertedUsers = insertedUsers;
            this.ignoredUsers = alreadyExistingUsers;
        }

        public List<String> getInsertedUsers() {
            return insertedUsers;
        }

        public List<String> getIgnoredUsers() {
            return ignoredUsers;
        }
    }
}
