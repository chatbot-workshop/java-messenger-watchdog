package ch.apptiva.watchdog.domain.core.service;

import ch.apptiva.watchdog.domain.core.model.SiteStatistic;
import ch.apptiva.watchdog.domain.core.model.UserId;
import ch.apptiva.watchdog.domain.core.model.Website;
import ch.apptiva.watchdog.domain.core.repository.WebsiteRepository;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StatisticService {
    private final WebsiteRepository websiteRepository;

    @Autowired
    public StatisticService(WebsiteRepository websiteRepository) {
        this.websiteRepository = websiteRepository;
    }

    public List<SiteStatistic> getStatisticsFor(UserId userId) {
        Collection<Website> websites = websiteRepository.findByUser(userId);
        return websites.stream().map(SiteStatistic::new).collect(Collectors.toList());
    }
}
