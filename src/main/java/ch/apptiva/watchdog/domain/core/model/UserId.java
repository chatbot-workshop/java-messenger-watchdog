package ch.apptiva.watchdog.domain.core.model;

import ch.apptiva.watchdog.domain.shared.ValueObject;

public class UserId extends ValueObject {

  private final String userId;

  public UserId(String userId) {
    if (userId == null || "".equals(userId)) {
      throw new IllegalArgumentException("UserId must be set.");
    }
    this.userId = userId;
  }

  public String getId() {
    return userId;
  }
}
