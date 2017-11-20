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

  @Override
  public String toString() {
    return userId;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    } else if (obj instanceof UserId) {
      UserId other = (UserId) obj;
      return getId().equals(other.getId());
    } else {
      return false;
    }
  }

  @Override
  public int hashCode() {
    return userId.hashCode();
  }
}
