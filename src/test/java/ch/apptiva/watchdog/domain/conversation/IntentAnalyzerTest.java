package ch.apptiva.watchdog.domain.conversation;

import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class IntentAnalyzerTest {

  @Test
  public void helpIntent() {
    Intent intent = IntentAnalyzer.analyzeIntent("Was kannst du?");
    assertTrue(intent instanceof HelpReply);
  }

  @Test
  public void WatchWebsiteIntent() throws MalformedURLException {
    Intent intent = IntentAnalyzer.analyzeIntent("Halte ein Auge auf https://www.apptiva.ch/");
    assertTrue(intent instanceof WatchWebsite);
    WatchWebsite watchWebsite = (WatchWebsite) intent;
    assertThat(watchWebsite.url(), is(new URL("https://www.apptiva.ch/")));
  }

  @Test
  public void WatchWebsiteIntentCaseInsensitive() throws MalformedURLException {
    Intent intent = IntentAnalyzer.analyzeIntent("Schau mir bitte auf HTTP://WWW.APPTIVA.CH/. Danke!");
    assertTrue(intent instanceof WatchWebsite);
    WatchWebsite watchWebsite = (WatchWebsite) intent;
    assertThat(watchWebsite.url(), is(new URL("http://www.apptiva.ch/")));
  }

}