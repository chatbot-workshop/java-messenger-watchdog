package ch.apptiva.watchdog.domain.core.model;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Deque;
import java.util.LinkedList;
import java.util.UUID;

import ch.apptiva.watchdog.domain.core.service.EventPublisher;
import ch.apptiva.watchdog.domain.core.service.TestService;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WebsiteTest {

  private UUID uuid = UUID.randomUUID();
  private UserId userId = new UserId("userid");
  private URL url = new URL("https://www.apptiva.ch/");
  private Deque<TestResult> testResults = new LinkedList<>();
  @Mock
  private TestService testServiceMock;
  @Mock
  private EventPublisher eventPublisherMock;

  public WebsiteTest() throws MalformedURLException {
  }

  @Test
  public void successfulCreationNewSite() {
    Website website = new Website(url, userId);
  }

  @Test
  public void successfulCreationWithExistingId() {
    Website website = new Website(UUID.randomUUID(), userId, url, testResults);
  }

  @Test(expected = IllegalArgumentException.class)
  public void unsuccessfulCreation() throws MalformedURLException {
    Website website = new Website(null, null, null, null);
  }

  @Test
  public void successfulTestResult() throws MalformedURLException {
    Website website = new Website(uuid, userId, url, testResults);
    when(testServiceMock.testWebsite(website)).thenReturn(new TestResult(LocalDateTime.now(), new HttpStatus(200), Duration.ofMillis(500)));
    website.test(testServiceMock, eventPublisherMock);
    assertThat(website.testResults().size(), is(1));
    TestResult testResult = website.testResults().peek();
    assertTrue(testResult.httpStatus().isGood());
    assertThat(testResult.responseTime(), is(Duration.ofMillis(500)));
  }

  @Test
  public void failTestResult() throws MalformedURLException {
    Website website = new Website(uuid, userId, url, testResults);
    when(testServiceMock.testWebsite(website)).thenReturn(new TestResult(LocalDateTime.now(), new HttpStatus(404), Duration.ofMillis(200)));
    website.test(testServiceMock, eventPublisherMock);
    assertThat(website.testResults().size(), is(1));
    TestResult testResult = website.testResults().peek();
    assertFalse(testResult.httpStatus().isGood());
    assertThat(testResult.responseTime(), is(Duration.ofMillis(200)));
  }

  @Test
  public void testEquals() throws MalformedURLException {
    UUID firstUUID = UUID.randomUUID();
    UserId firstUserId = new UserId("UserId");
    Website firstWebsite = new Website(firstUUID, firstUserId, new URL("http://www.apptiva.ch/"), new LinkedList<>());
    UUID secondUUID = UUID.randomUUID();
    UserId secondUserId = new UserId("otherID");
    Website secondWebsite = new Website(secondUUID, secondUserId, new URL("http://www.botfabrik.ch/"), new LinkedList<>());
    Website equalSecondWebsite = new Website(secondUUID, secondUserId, new URL("http://www.botfabrik.ch/"), new LinkedList<>());

    assertFalse(firstWebsite.equals(secondWebsite));
    assertTrue(secondWebsite.equals(equalSecondWebsite));
  }

  @Test
  public void testTestResultSize() {
    Website website = new Website(url, userId);
    assertThat(website.testResults().size(), is(0));
    // add 20 test results
    when(testServiceMock.testWebsite(website)).thenAnswer(m -> {
      TestResult result = new TestResult(LocalDateTime.now(), new HttpStatus(200), Duration.ofMillis((long) (1000 * Math.random())));
      return result;
    });
    for (int i = 0; i < 20; i++) {
      website.test(testServiceMock, eventPublisherMock);
    }
    assertThat(website.testResults().size(), is(20));

    TestResult firstResult = website.testResults().peek();

    assertThat(website.testResults().size(), is(20));
    website.test(testServiceMock,eventPublisherMock);
    assertThat(website.testResults().size(), is(20));
    assertFalse(website.testResults().contains(firstResult));
  }
}