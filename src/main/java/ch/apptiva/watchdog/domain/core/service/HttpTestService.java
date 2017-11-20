package ch.apptiva.watchdog.domain.core.service;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.time.Duration;
import java.time.LocalDateTime;

import ch.apptiva.watchdog.domain.core.model.HttpStatus;
import ch.apptiva.watchdog.domain.core.model.TestResult;
import ch.apptiva.watchdog.domain.core.model.Website;

@Component
public class HttpTestService implements TestService {

  @Override
  public TestResult testWebsite(Website website) {
    TestResult testResult;
    try {
      HttpURLConnection con = (HttpURLConnection)website.url().openConnection();
      con.setConnectTimeout(5000);
      con.setReadTimeout(5000);
      LocalDateTime startTime = LocalDateTime.now();
      con.setRequestMethod("GET");
      int responseCode = con.getResponseCode();
      LocalDateTime endTime = LocalDateTime.now();
      Duration responseTime = Duration.between(startTime, endTime);
      testResult = new TestResult(LocalDateTime.now(), new HttpStatus(responseCode), responseTime);
    } catch (IOException e) {
      testResult = new TestResult(LocalDateTime.now(), new HttpStatus(0), Duration.ZERO);
    }
    return testResult;
  }
}
