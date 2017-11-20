package ch.apptiva.watchdog.domain.core.repository;

import java.util.Collection;

import ch.apptiva.watchdog.domain.core.model.UserId;
import ch.apptiva.watchdog.domain.core.model.Website;

public interface WebsiteRepository {

  public void persistWebsite(Website website);

  public Collection<Website> findWebsitesByUser(UserId userId);

  public Collection<Website> findAll();
}
