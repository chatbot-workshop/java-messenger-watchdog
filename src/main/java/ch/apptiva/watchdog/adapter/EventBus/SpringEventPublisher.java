package ch.apptiva.watchdog.adapter.EventBus;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import ch.apptiva.watchdog.domain.core.model.WebsiteOfflineEvent;
import ch.apptiva.watchdog.domain.core.model.WebsiteOnlineEvent;
import ch.apptiva.watchdog.domain.core.service.EventPublisher;

@Component
public class SpringEventPublisher implements EventPublisher {

  private final ApplicationEventPublisher publisher;

  @Autowired
  public SpringEventPublisher(ApplicationEventPublisher publisher) {
    this.publisher = publisher;
  }

  @Override
  public void publishWebsiteOnlineEvent(WebsiteOnlineEvent event) {
    publisher.publishEvent(event);
  }

  @Override
  public void publishWebsiteOfflineEvent(WebsiteOfflineEvent event) {
    publisher.publishEvent(event);
  }
}
