package ch.apptiva.watchdog.adapter.timer;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Collection;

import ch.apptiva.watchdog.domain.core.model.Website;
import ch.apptiva.watchdog.domain.core.repository.WebsiteRepository;
import ch.apptiva.watchdog.domain.core.service.TestService;

@Component
public class SpringTimer {

  private final WebsiteRepository websiteRepository;
  private final TestService testService;

  public SpringTimer(WebsiteRepository websiteRepository, TestService testService) {
    this.websiteRepository = websiteRepository;
    this.testService = testService;
  }

  @Scheduled(fixedRate = 5000)
  public void runTests() {
    Collection<Website> allWebsites = websiteRepository.findAll();
    allWebsites.forEach(website -> {
      website.testIfOverdue(testService);
    });
  }
}
