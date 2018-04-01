package ch.apptiva.watchdog.domain.core.model;

import ch.apptiva.watchdog.domain.core.service.EventPublisher;
import ch.apptiva.watchdog.domain.core.service.TestService;
import ch.apptiva.watchdog.domain.shared.Entity;
import java.io.Serializable;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.Queue;
import java.util.UUID;
import org.apache.commons.collections4.queue.CircularFifoQueue;

public class Website extends Entity implements Serializable {

    private final static int FAILED_TEST_FIRE_EVENT_TREASHHOLD = 3;

    private final UUID uuid;
    private final UserId userId;
    private final URL url;
    private final CircularFifoQueue<TestResult> testResults;
    private int failedTestResultConter = 0;

    private Duration interval;

    public Website(URL url, UserId userId) {
        this(UUID.randomUUID(), userId, url, new CircularFifoQueue<TestResult>(20));
    }

    public Website(UUID uuid, UserId userId, URL url, CircularFifoQueue<TestResult> testResults) {
        if (uuid == null) {
            throw new IllegalArgumentException("UUID must be set.");
        }
        this.uuid = uuid;

        if (userId == null) {
            throw new IllegalArgumentException("UserId must be set.");
        }
        this.userId = userId;

        if (url == null) {
            throw new IllegalArgumentException("URL must be set.");
        }
        this.url = url;

        if (testResults == null) {
            throw new IllegalArgumentException("TestResults must be set.");
        }
        this.testResults = testResults;
        for (TestResult testResult : testResults) {
            if (testResult.isOk()) {
                failedTestResultConter = 0;
            } else {
                failedTestResultConter++;
            }
        }

        changeIntervalTo(Duration.ofMinutes(10));
    }

    public UUID id() {
        return uuid;
    }

    public UserId userId() {
        return userId;
    }

    public Optional<TestResult> currentResult() {
        if (testResults.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(testResults.get(testResults.size() - 1));
        }
    }

    public void changeIntervalTo(Duration interval) {
        this.interval = interval;
    }

    public Queue<TestResult> testResults() {
        return testResults;
    }

    public void test(TestService testService, EventPublisher eventPublisher) {
        if (testService == null) {
            throw new IllegalArgumentException(("TestService must be set."));
        }
        TestResult currentResult = currentResult().orElse(null);
        TestResult newTestResult = testService.testWebsite(this);
        if (newTestResult.isOk()) {
            failedTestResultConter = 0;
            if (newTestResult.isDifferentFrom(currentResult)) {
                eventPublisher.publishWebsiteOnlineEvent(new WebsiteOnlineEvent(this.uuid));
            }
        } else {
            failedTestResultConter++;
            if (failedTestResultConter >= FAILED_TEST_FIRE_EVENT_TREASHHOLD) {
                System.out.println("offline");
                eventPublisher.publishWebsiteOfflineEvent(new WebsiteOfflineEvent(this.uuid));
            }
        }
        addTestResult(newTestResult);
    }

    private void addTestResult(TestResult newTestResult) {
        testResults.add(newTestResult);
    }

    public URL url() {
        return url;
    }

    public void testIfOverdue(TestService testService, EventPublisher eventPublisher) {
        if (testService == null) {
            throw new IllegalArgumentException(("TestService must be set."));
        }
        TestResult lastTestResult = testResults.peek();
        if (lastTestResult == null) {
            this.test(testService, eventPublisher);
        } else {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime targetTime = lastTestResult.dateTime().plus(interval.toMillis(), ChronoUnit.MILLIS);
            if (targetTime.isBefore(now)) {
                this.test(testService, eventPublisher);
            }
        }
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Website)) {
            return false;
        }
        Website other = (Website) obj;
        return uuid.equals(other.uuid);
    }
}
