package ch.apptiva.watchdog.domain.conversation;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IntentAnalyzer {

  private static String URL_REGEX = "(http|https)://([\\w_-]+(?:(?:\\.[\\w_-]+)+))([\\w.,@?^=%&:/~+#-]*[\\w@?^=%&/~+#-])?";
  private static Pattern URL_PATTERN = Pattern.compile(URL_REGEX, Pattern.CASE_INSENSITIVE);

  public static Intent analyzeIntent(String message) {
    Intent intent = new HelpReply();
    try {
      if (message.toLowerCase().matches(".*http.*")) {
        Matcher matcher = URL_PATTERN.matcher(message);
        if (matcher.find()) {
          String url = matcher.group(0);
          intent = new WatchWebsite(new URL(url));
        }
      }
    } catch (MalformedURLException e) {
      intent = new HelpReply();
    }
    return intent;
  }
}
