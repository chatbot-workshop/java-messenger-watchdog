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
  public void watchWebsiteIntent() throws MalformedURLException {
    Intent intent = IntentAnalyzer.analyzeIntent("Halte ein Auge auf https://www.apptiva.ch/");
    assertTrue(intent instanceof WatchWebsite);
    WatchWebsite watchWebsite = (WatchWebsite) intent;
    assertThat(watchWebsite.url(), is(new URL("https://www.apptiva.ch/")));
  }

  @Test
  public void watchWebsiteIntentCaseInsensitive() throws MalformedURLException {
    Intent intent = IntentAnalyzer.analyzeIntent("Schau mir bitte auf HTTP://WWW.APPTIVA.CH/. Danke!");
    assertTrue(intent instanceof WatchWebsite);
    WatchWebsite watchWebsite = (WatchWebsite) intent;
    assertThat(watchWebsite.url(), is(new URL("http://www.apptiva.ch/")));
  }

  @Test
  public void watchWebsiteIntentSchau() throws MalformedURLException {
    Intent intent = IntentAnalyzer.analyzeIntent("Schau bitte auf https://www.apptiva.ch.");
    assertTrue(intent instanceof WatchWebsite);
    WatchWebsite watchWebsite = (WatchWebsite) intent;
    assertThat(watchWebsite.url(), is(new URL("https://www.apptiva.ch")));
  }

  @Test
  public void watchWebsiteIntentWatch() throws MalformedURLException {
    Intent intent = IntentAnalyzer.analyzeIntent("Please watch https://www.apptiva.ch.");
    assertTrue(intent instanceof WatchWebsite);
    WatchWebsite watchWebsite = (WatchWebsite) intent;
    assertThat(watchWebsite.url(), is(new URL("https://www.apptiva.ch")));
  }

  @Test
  public void watchWebsiteIntentWache() throws MalformedURLException {
    Intent intent = IntentAnalyzer.analyzeIntent("Bitte bewache https://www.apptiva.ch.");
    assertTrue(intent instanceof WatchWebsite);
    WatchWebsite watchWebsite = (WatchWebsite) intent;
    assertThat(watchWebsite.url(), is(new URL("https://www.apptiva.ch")));
  }

  @Test
  public void notWatchWebsiteIntent() {
    Intent intent = IntentAnalyzer.analyzeIntent("Das ist eine lustige website: https://www.apptiva.ch.");
    assertFalse(intent instanceof WatchWebsite);
    intent = IntentAnalyzer.analyzeIntent("Bitte vergiss https://www.apptiva.ch");
    assertFalse(intent instanceof WatchWebsite);
  }
}