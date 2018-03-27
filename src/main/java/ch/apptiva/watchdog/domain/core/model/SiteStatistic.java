package ch.apptiva.watchdog.domain.core.model;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.Queue;

public class SiteStatistic {

    private final URL url;
    private final int upPercentage;
    private final int responseTimeMillis1Hour;
    private final int responseTimeMillis30Min;
    private final int responseTimeMillis15Min;
    private final int responseTimeMillis5Min;
    private final int responseTimeMillis1Min;
    private final HttpStatus currentStatus;

    public SiteStatistic(Website website) {
        url = website.url();
        Queue<TestResult> results = website.testResults();
        upPercentage = (int) results.stream().mapToInt(res -> res.httpStatus().isGood() ? 100 : 0).average().orElse(0);

        LocalDateTime lastHour = website.currentResult().map(TestResult::dateTime).map(dt -> dt.minusHours(1))
            .orElse(LocalDateTime.now());
        responseTimeMillis1Hour = average(lastHour, results);
        responseTimeMillis30Min = average(lastHour.plusMinutes(30), results);
        responseTimeMillis15Min = average(lastHour.plusMinutes(45), results);
        responseTimeMillis5Min = average(lastHour.plusMinutes(55), results);;
        responseTimeMillis1Min = average(lastHour.plusMinutes(59), results);;
        currentStatus = website.currentResult().map(TestResult::httpStatus).orElse(null);
    }

    private static int average(LocalDateTime resultsAfter, Queue<TestResult> results) {
        return (int) results.stream()
            .filter(tr -> tr.dateTime().isAfter(resultsAfter))
            .mapToLong(tr -> tr.responseTime().toMillis()).average()
            .orElse(0.0);
    }

    public URL url() {
        return url;
    }

    public int upPercentage() {
        return upPercentage;
    }

    public HttpStatus currentStatus() {
        return currentStatus;
    }

    public int responseTimeMillis1Hour() {
        return responseTimeMillis1Hour;
    }

    public int responseTimeMillis30Min() {
        return responseTimeMillis30Min;
    }

    public int responseTimeMillis15Min() {
        return responseTimeMillis15Min;
    }

    public int responseTimeMillis5Min() {
        return responseTimeMillis5Min;
    }

    public int responseTimeMillis1Min() {
        return responseTimeMillis1Min;
    }
}
