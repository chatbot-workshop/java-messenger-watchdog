package ch.apptiva.watchdog.adapter.web;

import ch.apptiva.watchdog.domain.core.model.SiteStatistic;
import ch.apptiva.watchdog.domain.core.model.SiteStatisticsChart;
import ch.apptiva.watchdog.domain.core.model.Website;
import ch.apptiva.watchdog.domain.core.repository.WebsiteRepository;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/statistics")
public class StatisticsImageRestController {

    private final WebsiteRepository websiteRepository;

    @Autowired
    public StatisticsImageRestController(WebsiteRepository websiteRepository) {
        this.websiteRepository = websiteRepository;
    }

    @RequestMapping(method = RequestMethod.GET, produces = "image/jpeg")
    public ResponseEntity<byte[]> getImage(@RequestParam("url") String websiteUrlEncoded)
        throws IOException {
        String websiteUrl = URLDecoder.decode(websiteUrlEncoded, "utf-8");
        Website website = websiteRepository.findByUrl(new URL(websiteUrl));
        SiteStatistic statistic = new SiteStatistic(website);
        return ResponseEntity.ok(SiteStatisticsChart.generateStatImage(statistic));
    }

}
