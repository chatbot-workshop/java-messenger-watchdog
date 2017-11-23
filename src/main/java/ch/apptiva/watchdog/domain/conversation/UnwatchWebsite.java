package ch.apptiva.watchdog.domain.conversation;

import java.net.URL;

public class UnwatchWebsite implements Intent {

  private final URL url;

  public UnwatchWebsite(URL url) {
    this.url = url;
  }

  public URL url() {
    return url;
  }
}
