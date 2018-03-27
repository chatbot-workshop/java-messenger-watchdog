package ch.apptiva.watchdog.domain.conversation;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IntentAnalyzer {

  private static String URL_REGEX = "(http|https)://([\\w_-]+(?:(?:\\.[\\w_-]+)+))([\\w.,@?^=%&:/~+#-]*[\\w@?^=%&/~+#-])?";
  private static Pattern URL_PATTERN = Pattern.compile(URL_REGEX, Pattern.CASE_INSENSITIVE);

  public static Intent analyzeIntent(String message) {
    Intent intent = new HelpReply();
    try {
      if (message.toLowerCase().matches(".*(vergis|unwatch|stop).*")) {
        Optional<URL> url = extractURL(message);
        if (url.isPresent()) {
          intent = new UnwatchWebsite(url.get());
        }
      } else if (message.toLowerCase().matches(".*(auge|schau|watch|wach).*")) {
        Optional<URL> url = extractURL(message);
        if (url.isPresent()) {
          intent = new WatchWebsite(url.get());
        }
      } else if (message.toLowerCase().matches(".*(seiten|läuft).*")) {
        intent = new GetStatistics();
      }
    } catch (MalformedURLException e) {
      intent = new HelpReply();
    }
    return intent;
  }

  private static Optional<URL> extractURL(String message) throws MalformedURLException {
    Matcher matcher = URL_PATTERN.matcher(message);
    final URL url;
    if (matcher.find()) {
      url = new URL(matcher.group(0));
    } else {
      url = null;
    }
    return Optional.ofNullable(url);
  }
}
