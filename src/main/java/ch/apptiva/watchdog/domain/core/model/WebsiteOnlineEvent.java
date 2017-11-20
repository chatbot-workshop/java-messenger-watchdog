package ch.apptiva.watchdog.domain.core.model;

import java.util.UUID;

import ch.apptiva.watchdog.domain.shared.ValueObject;

public class WebsiteOnlineEvent extends ValueObject {

  private final UUID websiteId;

  WebsiteOnlineEvent(UUID websiteId) {
    this.websiteId = websiteId;
  }

  public UUID websiteId() {
    return websiteId;
  }
}
