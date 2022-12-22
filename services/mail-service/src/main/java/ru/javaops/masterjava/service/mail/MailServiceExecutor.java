package ru.javaops.masterjava.service.mail;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import ru.javaops.masterjava.service.mail.model.MailSendingResult;
import ru.javaops.masterjava.service.mail.model.MailSendingStatus;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
public class MailServiceExecutor {
    private static final int BATCH_SIZE = 10;

    private static final String INTERRUPTED_BY_FAULTS = "Interrupted by faults";
    private static final String INTERRUPTED_BY_TIMEOUT = "Interrupted by timeout";
    private static final String INTERRUPTED_EXCEPTION = "InterruptedException";

    private final ExecutorService mailExecutor = Executors.newFixedThreadPool(8);

    public List<MailSendingResult> sendToList(final String title,
                                              final String body,
                                              final List<UserAddress> userAddresses) {
        final CompletionService<MailsBatchSendingResult> completionService = new ExecutorCompletionService<>(mailExecutor);

        AtomicInteger counter = new AtomicInteger();
        List<Future<MailsBatchSendingResult>> futures =
                userAddresses
                        .stream()
                        .collect(Collectors.groupingBy(userAddress -> counter.getAndIncrement() / BATCH_SIZE))
                        .values()
                        .stream()
                        .map(userAddressesBatch ->
                                completionService.submit(() -> sendToUsers(userAddressesBatch, title, body)))
                        .collect(Collectors.toList());

        Set<UserAddress> unprocessedUserAddresses = new HashSet<>(userAddresses);

        return new Callable<List<MailSendingResult>>() {
            private final List<MailSendingResult> mailSendingResults = new ArrayList<>();

            @Override
            public List<MailSendingResult> call() {
                while (!futures.isEmpty()) {
                    try {
                        Future<MailsBatchSendingResult> future = completionService.poll(10, TimeUnit.SECONDS);
                        if (future == null) {
                            return cancelUnprocessed(INTERRUPTED_BY_TIMEOUT);
                        }
                        futures.remove(future);
                        MailsBatchSendingResult mailsBatchSendingResult = future.get();
                        val status = mailsBatchSendingResult.getStatus();
                        val description = mailsBatchSendingResult.getDescription();
                        mailsBatchSendingResult.getUserAddresses().forEach(userAddress -> {
                            mailSendingResults.add(
                                    createMailSendingResult(
                                            userAddress, status.getSingleMailSendingStatus(), description
                                    )
                            );
                            unprocessedUserAddresses.remove(userAddress);
                        });

                        if (MailsBatchSendingResultStatus.FAILED == mailsBatchSendingResult.getStatus()) {
                            return cancelUnprocessed(INTERRUPTED_BY_FAULTS);
                        }
                    } catch (ExecutionException e) {
                        return cancelUnprocessed(e.getCause().toString());
                    } catch (InterruptedException e) {
                        return cancelUnprocessed(INTERRUPTED_EXCEPTION);
                    }
                }
                return mailSendingResults;
            }

            private List<MailSendingResult> cancelUnprocessed(String cause) {
                futures.forEach(f -> f.cancel(true));
                unprocessedUserAddresses.forEach(userAddress ->
                        mailSendingResults.add(
                                createMailSendingResult(userAddress, MailSendingStatus.INTERRUPTED, cause)
                        )
                );
                return mailSendingResults;
            }

            private MailSendingResult createMailSendingResult(UserAddress userAddress,
                                                              MailSendingStatus mailSendingStatus,
                                                              String description) {
                return new MailSendingResult(
                        userAddress.getEmail(),
                        userAddress.getName(),
                        title,
                        body,
                        mailSendingStatus,
                        description,
                        null
                );
            }
        }.call();
    }

    private MailsBatchSendingResult sendToUsers(List<UserAddress> to, String title, String body) {
        try {
            MailSender.sendMail(to, Collections.emptyList(), title, body);
        } catch (MessagingException | UnsupportedEncodingException e) {
            val errorMessage = e.getMessage();
            log.error(errorMessage);
            return new MailsBatchSendingResult(to, MailsBatchSendingResultStatus.FAILED, errorMessage);
        }
        return new MailsBatchSendingResult(to, MailsBatchSendingResultStatus.SUCCESS, null);
    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @Getter
    @ToString
    private static class MailsBatchSendingResult {
        private final List<UserAddress> userAddresses;
        private final MailsBatchSendingResultStatus status;
        private final String description;
    }

    @RequiredArgsConstructor
    @Getter
    private enum MailsBatchSendingResultStatus {
        SUCCESS(MailSendingStatus.SUCCESS),
        FAILED(MailSendingStatus.FAILED);

        private final MailSendingStatus singleMailSendingStatus;
    }
}