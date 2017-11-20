package ch.apptiva.watchdog.adapter.persistence;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import ch.apptiva.watchdog.domain.core.model.UserId;
import ch.apptiva.watchdog.domain.core.model.Website;
import ch.apptiva.watchdog.domain.core.repository.WebsiteRepository;

@Component
public class MemoryWebsiteRepository implements WebsiteRepository {

  private final List<Website> websites = new ArrayList<>();

  @Override
  public void persistWebsite(Website website) {
    websites.add(website);
  }

  @Override
  public Collection<Website> findWebsitesByUser(UserId userId) {
    return websites.stream().filter(website -> website.userId().equals(userId)).collect(Collectors.toList());
  }

  @Override
  public Collection<Website> findAll() {
    return websites;
  }
}
