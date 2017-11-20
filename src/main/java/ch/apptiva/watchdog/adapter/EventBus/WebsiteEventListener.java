package ch.apptiva.watchdog.adapter.EventBus;

import com.github.messenger4j.exceptions.MessengerApiException;
import com.github.messenger4j.exceptions.MessengerIOException;
import com.github.messenger4j.send.MessengerSendClient;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.PayloadApplicationEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import ch.apptiva.watchdog.domain.core.model.Website;
import ch.apptiva.watchdog.domain.core.model.WebsiteOfflineEvent;
import ch.apptiva.watchdog.domain.core.model.WebsiteOnlineEvent;
import ch.apptiva.watchdog.domain.core.repository.WebsiteRepository;

@Component
public class WebsiteEventListener {

  private final WebsiteRepository websiteRepository;
  private final MessengerSendClient sendClient;

  @Autowired
  public WebsiteEventListener(WebsiteRepository websiteRepository, MessengerSendClient sendClient) {
    this.websiteRepository = websiteRepository;
    this.sendClient = sendClient;
  }

  @EventListener
  public void websiteIsOnline(PayloadApplicationEvent<WebsiteOnlineEvent> websiteOnlineEvent) {
    Website website = websiteRepository.findById(websiteOnlineEvent.getPayload().websiteId());
    try {
      sendClient.sendTextMessage(website.userId().getId(), "Alles OK. Die Website " + website.url() + " ist gerade online gegangen.");
    } catch (MessengerApiException | MessengerIOException e) {
      LoggerFactory.getLogger(getClass()).warn("Could not send website online message to user "+ website.userId() + " for website " + website.url());
    }
  }

  @EventListener
  public void websiteIsOffline(PayloadApplicationEvent<WebsiteOfflineEvent> websiteOfflineEvent) {
    Website website = websiteRepository.findById(websiteOfflineEvent.getPayload().websiteId());
    try {
      sendClient.sendTextMessage(website.userId().getId(), "Oh Schreck! Die Website " + website.url() + " ist gerade offline gegangen! Bitte kümmere dich um das Problem! Ich melde mich, wenn sie wieder online ist.");
    } catch (MessengerApiException | MessengerIOException e) {
      LoggerFactory.getLogger(getClass()).error("Could not send website offline message to user "+ website.userId() + " for website " + website.url());
    }
  }
}
