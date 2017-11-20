package ch.apptiva.watchdog.domain.conversation;

public class TextReplyIntent implements Intent {

  private final String[] replies;

  public TextReplyIntent(String... replyLine) {
    replies = replyLine;
  }

  public String[] replies() {
    return replies;
  }
}
