package ch.apptiva.watchdog.adapter.persistence;

import ch.apptiva.watchdog.domain.core.model.UserId;
import ch.apptiva.watchdog.domain.core.model.Website;
import ch.apptiva.watchdog.domain.core.repository.WebsiteRepository;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class MemoryWebsiteRepository implements WebsiteRepository {

    private final Set<Website> websites = new HashSet<>();

    public MemoryWebsiteRepository() {
        // add some example data
        try {
            websites.add(new Website(new URL("http://www.apptiva.ch/"), new UserId("u-id")));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void persist(Website website) {
        websites.add(website);
    }

    @Override
    public Website findById(UUID uuid) {
        return websites.stream().filter(website -> website.id().equals(uuid)).findAny().get();
    }

    @Override
    public Website findByUrl(URL url) {
        return websites.stream().filter(website -> website.url().equals(url)).findAny().get();
    }

    @Override
    public Collection<Website> findByUser(UserId userId) {
        return websites.stream().filter(website -> website.userId().equals(userId)).collect(Collectors.toList());
    }

    @Override
    public Collection<Website> findAll() {
        return Collections.unmodifiableCollection(new ArrayList<>(websites));
    }
}
