package ch.apptiva.watchdog.domain.core.repository;

import java.net.URL;
import java.util.Collection;
import java.util.UUID;

import ch.apptiva.watchdog.domain.core.model.UserId;
import ch.apptiva.watchdog.domain.core.model.Website;

public interface WebsiteRepository {

    public void persist(Website website);

    public Website findById(UUID uuid);

    public Website findByUrl(URL url);

    public Collection<Website> findByUser(UserId userId);

    public Collection<Website> findAll();
}
