package ch.apptiva.watchdog.domain.core.model;

import static java.time.Duration.ofMillis;
import static java.time.LocalDateTime.of;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ch.apptiva.watchdog.domain.core.service.EventPublisher;
import ch.apptiva.watchdog.domain.core.service.TestService;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.stream.IntStream;
import org.junit.Test;

public class SiteStatisticTest {

    private LocalDate date = LocalDate.of(2018, 3, 25);
    private HttpStatus OK = new HttpStatus(200);
    private HttpStatus NotFound = new HttpStatus(404);

    @Test
    public void statistics() throws MalformedURLException {
        Website website = new Website(new URL("https://www.apptiva.ch/"), new UserId("u-id"));
        TestService testService = mock(TestService.class);
        TestResult[] testResults = new TestResult[] { //
                new TestResult(of(date, LocalTime.of(18, 0)), OK, ofMillis(200)),//
                new TestResult(of(date, LocalTime.of(18, 5)), OK, ofMillis(220)),//
                new TestResult(of(date, LocalTime.of(18, 10)), OK, ofMillis(240)),//
                new TestResult(of(date, LocalTime.of(18, 15)), OK, ofMillis(260)),//
                new TestResult(of(date, LocalTime.of(18, 20)), NotFound, ofMillis(280)),//
                new TestResult(of(date, LocalTime.of(18, 25)), OK, ofMillis(300)),//
                new TestResult(of(date, LocalTime.of(18, 30)), OK, ofMillis(320)),//
                new TestResult(of(date, LocalTime.of(18, 35)), OK, ofMillis(340)),//
                new TestResult(of(date, LocalTime.of(18, 40)), NotFound, ofMillis(360)),//
                new TestResult(of(date, LocalTime.of(18, 45)), OK, ofMillis(380)),//
                new TestResult(of(date, LocalTime.of(18, 50)), OK, ofMillis(400)),//
                new TestResult(of(date, LocalTime.of(18, 55)), OK, ofMillis(420)),//
                new TestResult(of(date, LocalTime.of(19, 0)), NotFound, ofMillis(440))//
        };
        when(testService.testWebsite(website)).thenReturn(testResults[0], Arrays.copyOfRange(testResults, 1, testResults.length));
        EventPublisher eventPublisher = mock(EventPublisher.class);
        IntStream.range(0, testResults.length).forEach((i) -> {
            website.test(testService, eventPublisher);
        });
        SiteStatistic statistic = new SiteStatistic(website);

        assertThat(statistic.currentStatus(), is(NotFound));
        assertThat(statistic.upPercentage(), is(76));

        try {
            byte[] imageBytes = SiteStatisticsChart.generateStatImage(statistic);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
