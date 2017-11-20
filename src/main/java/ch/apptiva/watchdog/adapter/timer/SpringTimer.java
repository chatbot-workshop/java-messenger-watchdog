package ch.apptiva.watchdog.adapter.timer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Collection;

import ch.apptiva.watchdog.domain.core.model.Website;
import ch.apptiva.watchdog.domain.core.repository.WebsiteRepository;
import ch.apptiva.watchdog.domain.core.service.EventPublisher;
import ch.apptiva.watchdog.domain.core.service.TestService;

@Component
public class SpringTimer {

  private final WebsiteRepository websiteRepository;
  private final TestService testService;
  private final EventPublisher eventPublisher;

  @Autowired
  public SpringTimer(WebsiteRepository websiteRepository, TestService testService, EventPublisher eventPublisher) {
    this.websiteRepository = websiteRepository;
    this.testService = testService;
    this.eventPublisher = eventPublisher;
  }

  @Scheduled(fixedDelay = 10000)
  public void runTests() {
    Collection<Website> allWebsites = websiteRepository.findAll();
    allWebsites.forEach(website -> {
      website.testIfOverdue(testService, eventPublisher);
      websiteRepository.persist(website);
    });
  }
}
