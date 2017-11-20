package ch.apptiva.watchdog.domain.conversation;

public class HelpReply extends TextReplyIntent {

  public HelpReply() {
    super("Ich verstehe leider nicht, was du möchtest.",
        "Du kannst mich dazu bringen, regelmässig die Verfügbarkeit von Webseiten zu prüfen. Zum Beispiel mit folgender Nachricht:",
        "Halte ein Auge auf https://www.apptiva.ch/",
        "Sobald eine Website nicht mehr online ist, melde ich mich bei dir wieder.",
        "Wenn du den aktuellen Status wissen möchtest, frage mich etwas wie:",
        "Sind meine Seiten noch online? / Wie läuft es?",
        "Wenn du schlussendlich eine Seite nicht mehr überwachen möchtest, gib mir so Bescheid:",
        "Stoppe eine Überwachung.");
  }
}
