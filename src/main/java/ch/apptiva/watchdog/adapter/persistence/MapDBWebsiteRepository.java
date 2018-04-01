package ch.apptiva.watchdog.adapter.persistence;

import ch.apptiva.watchdog.domain.core.model.UserId;
import ch.apptiva.watchdog.domain.core.model.Website;
import ch.apptiva.watchdog.domain.core.repository.WebsiteRepository;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;
import org.mapdb.Serializer;
import org.springframework.stereotype.Component;

@Component
public class MapDBWebsiteRepository implements WebsiteRepository {
    private static final String SITES_MAP_NAME = "sites";

    private final DB mapDB;
    private final Set<Website> websites = new HashSet<>();

    public MapDBWebsiteRepository() {
        mapDB = DBMaker.fileDB("database.db").make();
    }

    @Override
    public void persist(Website website) {
        getWebsiteMap().put(website.id(), website);
        mapDB.commit();
    }

    @Override
    public Website findById(UUID uuid) {
        return getWebsiteMap().get(uuid);
    }

    @Override
    public Website findByUrl(URL url) {
        return getWebsiteMap().getValues().stream().filter(website -> website.url().equals(url)).findAny().get();
    }

    @Override
    public Collection<Website> findByUser(UserId userId) {
        return getWebsiteMap().getValues().stream().filter(website -> website.userId().equals(userId)).collect(Collectors.toList());
    }

    @Override
    public Collection<Website> findAll() {
        return Collections.unmodifiableCollection(new ArrayList<>(websites));
    }

    public HTreeMap<UUID, Website> getWebsiteMap() {
        return mapDB.<UUID, Website>hashMap(SITES_MAP_NAME, Serializer.UUID, Serializer.JAVA).createOrOpen();
    }
}
