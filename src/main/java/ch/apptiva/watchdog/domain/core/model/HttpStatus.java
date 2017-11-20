package ch.apptiva.watchdog.domain.core.model;

import ch.apptiva.watchdog.domain.shared.ValueObject;

public class HttpStatus extends ValueObject {

  private final int status;

  public HttpStatus(int status) {
    this.status = status;
  }

  public boolean isGood() {
    return status >= 200 && status < 400;
  }
}
