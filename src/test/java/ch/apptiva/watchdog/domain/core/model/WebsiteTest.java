package ch.apptiva.watchdog.domain.core.model;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import ch.apptiva.watchdog.domain.core.service.EventPublisher;
import ch.apptiva.watchdog.domain.core.service.TestService;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;
import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class WebsiteTest {

  private UUID uuid = UUID.randomUUID();
  private UserId userId = new UserId("userid");
  private URL url = new URL("https://www.apptiva.ch/");
  private CircularFifoQueue<TestResult> testResults = new CircularFifoQueue<>();
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
    when(testServiceMock.testWebsite(website))
        .thenReturn(new TestResult(LocalDateTime.now(), new HttpStatus(200), Duration.ofMillis(500)));
    website.test(testServiceMock, eventPublisherMock);
    assertThat(website.testResults().size(), is(1));
    TestResult testResult = website.testResults().peek();
    assertTrue(testResult.httpStatus().isGood());
    assertThat(testResult.responseTime(), is(Duration.ofMillis(500)));
  }

  @Test
  public void failTestResult() throws MalformedURLException {
    Website website = new Website(uuid, userId, url, testResults);
    when(testServiceMock.testWebsite(website)).thenReturn(
        new TestResult(LocalDateTime.now(), new HttpStatus(404), Duration.ofMillis(200)));
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
    Website firstWebsite = new Website(firstUUID, firstUserId, new URL("http://www.apptiva.ch/"),
        new CircularFifoQueue<>(5));
    UUID secondUUID = UUID.randomUUID();
    UserId secondUserId = new UserId("otherID");
    Website secondWebsite = new Website(secondUUID, secondUserId,
        new URL("http://www.botfabrik.ch/"), new CircularFifoQueue<>(5));
    Website equalSecondWebsite = new Website(secondUUID, secondUserId,
        new URL("http://www.botfabrik.ch/"), new CircularFifoQueue<>(5));

    assertFalse(firstWebsite.equals(secondWebsite));
    assertTrue(secondWebsite.equals(equalSecondWebsite));
  }

  @Test
  public void testCurrentResult() throws MalformedURLException {
    Website website = new Website(new URL("http://www.apptiva.ch/"), new UserId("user-id"));
    when(testServiceMock.testWebsite(website)).thenReturn(
        new TestResult(LocalDateTime.now().minusMinutes(2), new HttpStatus(200),
            Duration.ofMillis(200)),
        new TestResult(LocalDateTime.now().minusMinutes(1), new HttpStatus(200),
            Duration.ofMillis(300)),
        new TestResult(LocalDateTime.now(), new HttpStatus(404), Duration.ofMillis(400))
    );
    website.test(testServiceMock, eventPublisherMock);
    assertTrue(website.currentResult().get().httpStatus().isGood());
    website.test(testServiceMock, eventPublisherMock);
    assertTrue(website.currentResult().get().httpStatus().isGood());
    website.test(testServiceMock, eventPublisherMock);
    assertFalse(website.currentResult().get().httpStatus().isGood());

  }

  @Test
  public void testTestResultSize() {
    Website website = new Website(url, userId);
    assertThat(website.testResults().size(), is(0));
    // add 20 test results
    when(testServiceMock.testWebsite(website)).thenAnswer(m -> {
      TestResult result = new TestResult(LocalDateTime.now(), new HttpStatus(200),
          Duration.ofMillis((long) (1000 * Math.random())));
      return result;
    });
    for (int i = 0; i < 20; i++) {
      website.test(testServiceMock, eventPublisherMock);
    }
    assertThat(website.testResults().size(), is(20));

    TestResult firstResult = website.testResults().peek();

    assertThat(website.testResults().size(), is(20));
    website.test(testServiceMock, eventPublisherMock);
    assertThat(website.testResults().size(), is(20));
    assertFalse(website.testResults().contains(firstResult));
  }

  @Test
  public void expectEventToBeFiredOnlyAfter3FailedTests() {
    Website website = new Website(url, userId);
      LocalDateTime startTime = LocalDateTime.now();

    // first execution is successful, fire event
    when(testServiceMock.testWebsite(website)).thenReturn(
        new TestResult(startTime.minusSeconds(10), new HttpStatus(200), Duration.ofMillis(300)));
    website.test(testServiceMock, eventPublisherMock);
    verify(eventPublisherMock).publishWebsiteOnlineEvent(any(WebsiteOnlineEvent.class));

    // record 3 test failed executions, fire event
    when(testServiceMock.testWebsite(website)).thenReturn(
        new TestResult(startTime.minusSeconds(5), new HttpStatus(404), Duration.ofMillis(300)),
        new TestResult(startTime.minusSeconds(4), new HttpStatus(404), Duration.ofMillis(300)),
        new TestResult(startTime.minusSeconds(3), new HttpStatus(404), Duration.ofMillis(300)));
    website.test(testServiceMock, eventPublisherMock);
    website.test(testServiceMock, eventPublisherMock);
    website.test(testServiceMock, eventPublisherMock);
    verify(eventPublisherMock).publishWebsiteOfflineEvent(any(WebsiteOfflineEvent.class));

    // now we go online again, fire event
    when(testServiceMock.testWebsite(website)).thenReturn(
        new TestResult(LocalDateTime.now(), new HttpStatus(200), Duration.ofMillis(300)));
    website.test(testServiceMock, eventPublisherMock);
    verify(eventPublisherMock, times(2)).publishWebsiteOnlineEvent(any(WebsiteOnlineEvent.class));

    // there should be no more other interactions with the event publisher
    verifyNoMoreInteractions(eventPublisherMock);
  }
}
