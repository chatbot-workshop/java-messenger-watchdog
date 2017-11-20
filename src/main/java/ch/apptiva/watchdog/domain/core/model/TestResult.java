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
}
