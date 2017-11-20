package ch.apptiva.watchdog.domain.conversation;

import java.net.URL;

public class WatchWebsite implements Intent {

  private final URL url;

  public WatchWebsite(URL url) {
    this.url = url;
  }

  public URL url() {
    return url;
  }
}
