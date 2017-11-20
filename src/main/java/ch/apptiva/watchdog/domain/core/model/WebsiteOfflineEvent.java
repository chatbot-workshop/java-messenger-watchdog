package ch.apptiva.watchdog.domain.core.model;

import java.util.UUID;

import ch.apptiva.watchdog.domain.shared.ValueObject;

public class WebsiteOfflineEvent extends ValueObject {

  private final UUID websiteId;

  WebsiteOfflineEvent(UUID websiteId) {
    this.websiteId = websiteId;
  }

  public UUID websiteId() {
    return websiteId;
  }
}
