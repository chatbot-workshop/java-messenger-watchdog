package ch.apptiva.watchdog.domain.core.model;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Map;
import java.util.Queue;
import java.util.stream.Collectors;

public class SiteStatistic {

    private final URL url;
    private final int upPercentage;
    private final Map<Long, Long> data;
    private final HttpStatus currentStatus;

    public SiteStatistic(Website website) {
        url = website.url();
        Queue<TestResult> results = website.testResults();
        upPercentage = (int) results.stream().mapToInt(res -> res.httpStatus().isGood() ? 100 : 0).average().orElse(0);

        LocalDateTime lastHour = website.currentResult().map(TestResult::dateTime).map(dt -> dt.minusHours(1))
            .orElse(LocalDateTime.now());
        currentStatus = website.currentResult().map(TestResult::httpStatus).orElse(null);

        data = results.stream().filter(tr -> tr.dateTime().isAfter((lastHour)))
            .collect(Collectors.toMap(
                (tr) -> lastHour.plusHours(1).until(tr.dateTime(), ChronoUnit.SECONDS),
                (tr) -> tr.responseTime().toMillis()
            ));
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

    public Map<Long, Long> data() {
        return Collections.unmodifiableMap(data);
    }
}
