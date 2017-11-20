package ch.apptiva.watchdog.adapter.persistence;

import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;

import ch.apptiva.watchdog.domain.core.model.UserId;
import ch.apptiva.watchdog.domain.core.model.Website;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class MemoryWebsiteRepositoryTest {

  @Test
  public void testPersisting() throws MalformedURLException {
    MemoryWebsiteRepository repository = new MemoryWebsiteRepository();
    assertThat(repository.findAll().size(), is(0));
    Website website = new Website(new URL("http://botfabrik.ch/"), new UserId("Id"));
    repository.persist(website);
    assertThat(repository.findAll().size(), is(1));
    repository.persist(website);
    assertThat(repository.findAll().size(), is(1));
  }

}