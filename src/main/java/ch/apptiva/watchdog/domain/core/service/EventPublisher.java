package ch.apptiva.watchdog.domain.core.service;

import ch.apptiva.watchdog.domain.core.model.WebsiteOfflineEvent;
import ch.apptiva.watchdog.domain.core.model.WebsiteOnlineEvent;

public interface EventPublisher {

  public void publishWebsiteOnlineEvent(WebsiteOnlineEvent event);

  public void publishWebsiteOfflineEvent(WebsiteOfflineEvent event);
}
