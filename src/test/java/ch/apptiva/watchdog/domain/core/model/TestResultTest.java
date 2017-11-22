package ch.apptiva.watchdog.domain.core.model;

import org.junit.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.Assert.*;

public class TestResultTest {

  @Test
  public void testEquals() {
    LocalDateTime now = LocalDateTime.now();
    TestResult testResult = new TestResult(now, new HttpStatus(200), Duration.ofMillis(1453));
    assertTrue(testResult.equals(testResult));
    TestResult sameTestResult = new TestResult(now, new HttpStatus(200), Duration.ofMillis(1453));
    assertTrue(testResult.equals(sameTestResult));
  }
}