package ch.apptiva.watchdog.domain.core.model;

import java.time.Duration;
import java.time.LocalDateTime;

import ch.apptiva.watchdog.domain.shared.ValueObject;

public class TestResult extends ValueObject {

  private final LocalDateTime dateTime;
  private final HttpStatus httpStatus;
  private final Duration responseTime;

  public TestResult(LocalDateTime dateTime, HttpStatus httpStatus, Duration responseTime) {
    this.dateTime = dateTime;
    this.httpStatus = httpStatus;
    this.responseTime = responseTime;
  }

  public LocalDateTime dateTime() {
    return dateTime;
  }

  public HttpStatus httpStatus() {
    return httpStatus;
  }

  public Duration responseTime() {
    return responseTime;
  }

  public boolean isOk() {
    return httpStatus().isGood();
  }

  public boolean isDifferentFrom(TestResult lastTestResult) {
    if (lastTestResult == null) {
      return true;
    } else {
      return isOk() != lastTestResult.isOk();
    }
  }

  @Override
  public int hashCode() {
    return 13 * dateTime.hashCode() * httpStatus.hashCode() * responseTime.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    } else if (obj instanceof TestResult) {
      TestResult other = (TestResult) obj;
      return dateTime.equals(other.dateTime)
          && httpStatus.equals(other.httpStatus)
          && responseTime.equals(other.responseTime);
    } else {
      return false;
    }
  }
}
