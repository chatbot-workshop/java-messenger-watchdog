package ch.apptiva.watchdog.domain.core.model;

import java.net.URL;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalField;
import java.util.Deque;
import java.util.LinkedList;
import java.util.UUID;

import ch.apptiva.watchdog.domain.core.service.TestService;
import ch.apptiva.watchdog.domain.shared.Entity;

public class Website extends Entity {

  private final UUID uuid;
  private final UserId userId;
  private final URL url;
  private final Deque<TestResult> testResults;

  private Duration interval;

  public Website (URL url, UserId userId) {
    this(UUID.randomUUID(), userId, url, new LinkedList<TestResult>());
  }

  public Website (UUID uuid, UserId userId, URL url, Deque<TestResult> testResults) {
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

    if(testResults == null) {
      throw new IllegalArgumentException("TestResults must be set.");
    }
    this.testResults = testResults;

    changeIntervalTo(Duration.ofMinutes(10));
  }

  public UserId userId() {
    return userId;
  }

  public void changeIntervalTo(Duration interval) {
    this.interval = interval;
  }

  public Deque<TestResult> testResults() {
    return testResults;
  }

  public void test(TestService testService) {
    if (testService == null) {
      throw new IllegalArgumentException(("TestService must be set."));
    }
    testResults.add(testService.testWebsite(this));
  }

  public URL url() {
    return url;
  }

  public void testIfOverdue(TestService testService) {
    if (testService == null) {
      throw new IllegalArgumentException(("TestService must be set."));
    }
    TestResult lastTestResult = testResults.peekLast();
    if (lastTestResult == null) {
      this.test(testService);
    } else {
      LocalDateTime now = LocalDateTime.now();
      LocalDateTime targetTime = lastTestResult.dateTime().plus(interval.toMillis(), ChronoUnit.MILLIS);
      if (targetTime.isBefore(now)) {
        this.test(testService);
      }
    }
  }

  @Override
  public int hashCode() {
    return uuid.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null)
      return false;
    if (!(obj instanceof Website))
      return false;
    Website other = (Website)obj;
    return uuid.equals(other.uuid);
  }
}
