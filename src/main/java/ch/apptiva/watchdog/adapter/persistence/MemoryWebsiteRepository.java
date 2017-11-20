package ch.apptiva.watchdog.adapter.persistence;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import ch.apptiva.watchdog.domain.core.model.UserId;
import ch.apptiva.watchdog.domain.core.model.Website;
import ch.apptiva.watchdog.domain.core.repository.WebsiteRepository;

@Component
public class MemoryWebsiteRepository implements WebsiteRepository {

  private final Set<Website> websites = new HashSet<>();

  @Override
  public void persist(Website website) {
    websites.add(website);
  }

  @Override
  public Website findById(UUID uuid) {
    return websites.stream().filter(website -> website.id().equals(uuid)).findAny().get();
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
