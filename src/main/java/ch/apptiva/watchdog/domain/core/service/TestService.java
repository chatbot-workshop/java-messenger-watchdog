package ch.apptiva.watchdog.domain.core.service;

import ch.apptiva.watchdog.domain.core.model.TestResult;
import ch.apptiva.watchdog.domain.core.model.Website;

public interface TestService {

  public TestResult testWebsite(Website website);
}
