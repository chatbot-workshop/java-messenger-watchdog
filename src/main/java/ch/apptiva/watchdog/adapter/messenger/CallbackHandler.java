package ch.apptiva.watchdog.adapter.messenger;

import com.github.messenger4j.MessengerPlatform;
import com.github.messenger4j.exceptions.MessengerApiException;
import com.github.messenger4j.exceptions.MessengerIOException;
import com.github.messenger4j.exceptions.MessengerVerificationException;
import com.github.messenger4j.receive.MessengerReceiveClient;
import com.github.messenger4j.receive.handlers.EchoMessageEventHandler;
import com.github.messenger4j.receive.handlers.TextMessageEventHandler;
import com.github.messenger4j.send.MessengerSendClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import ch.apptiva.watchdog.domain.conversation.Intent;
import ch.apptiva.watchdog.domain.conversation.IntentAnalyzer;
import ch.apptiva.watchdog.domain.conversation.TextReplyIntent;
import ch.apptiva.watchdog.domain.conversation.WatchWebsite;
import ch.apptiva.watchdog.domain.core.model.UserId;
import ch.apptiva.watchdog.domain.core.model.Website;
import ch.apptiva.watchdog.domain.core.repository.WebsiteRepository;

import static com.github.messenger4j.MessengerPlatform.CHALLENGE_REQUEST_PARAM_NAME;
import static com.github.messenger4j.MessengerPlatform.MODE_REQUEST_PARAM_NAME;
import static com.github.messenger4j.MessengerPlatform.SIGNATURE_HEADER_NAME;
import static com.github.messenger4j.MessengerPlatform.VERIFY_TOKEN_REQUEST_PARAM_NAME;

@RestController
@RequestMapping("/callback")
public class CallbackHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(CallbackHandler.class);


  private final MessengerReceiveClient receiveClient;
  private final MessengerSendClient sendClient;
  private final WebsiteRepository websiteRepository;

  @Autowired
  public CallbackHandler(@Value("${messenger.appSecret}") final String appSecret,
                         @Value("${messenger.verifyToken}") final String verifyToken,
                         final MessengerSendClient sendClient,
                         final WebsiteRepository websiteRepository) {
    this.receiveClient = MessengerPlatform.newReceiveClientBuilder(appSecret, verifyToken)
        .onEchoMessageEvent(newEchoMessageEventHandler())
        .onTextMessageEvent(newTextMessageEventHandler())
        .build();
    this.sendClient = sendClient;
    this.websiteRepository = websiteRepository;
  }

  private EchoMessageEventHandler newEchoMessageEventHandler() {
    return event -> {
      LOGGER.info("Received EchoMessage from App '{}'.", event.getAppId());
    };
  }

  private TextMessageEventHandler newTextMessageEventHandler() {
    return event -> {
      LOGGER.info("Received TextMessage with text '{}'.", event.getText());
      String senderId = event.getSender().getId();
      Intent intent = IntentAnalyzer.analyzeIntent(event.getText());
      try {
        if (intent instanceof TextReplyIntent) {
          sendTextReply(senderId, ((TextReplyIntent) intent));
        } else if (intent instanceof WatchWebsite) {
          WatchWebsite watchWebsite = (WatchWebsite) intent;
          Website website = new Website(watchWebsite.url(), new UserId(senderId));
          websiteRepository.persistWebsite(website);
        }
      } catch (MessengerApiException | MessengerIOException e) {
        handleSendException(e);
      }
    };
  }

  private void handleSendException(Exception e) {
    LOGGER.warn("Cannot send message.", e);
  }

  private void sendTextReply(String senderId, TextReplyIntent textReplyIntent) throws MessengerApiException, MessengerIOException {
    for (String line : textReplyIntent.replies()) {
      sendTextMessage(senderId, line);
    }
  }

  private void sendTextMessage(String senderId, String text) throws MessengerApiException, MessengerIOException {
    this.sendClient.sendTextMessage(senderId, text);
  }

  @RequestMapping(method = RequestMethod.GET)
  public ResponseEntity<String> verifyWebhook(@RequestParam(MODE_REQUEST_PARAM_NAME) final String mode,
                                              @RequestParam(VERIFY_TOKEN_REQUEST_PARAM_NAME) final String verifyToken,
                                              @RequestParam(CHALLENGE_REQUEST_PARAM_NAME) final String challenge) {

    LOGGER.debug("Received Webhook verification request - mode: {} | verifyToken: {} | challenge: {}", mode,
        verifyToken, challenge);
    try {
      return ResponseEntity.ok(this.receiveClient.verifyWebhook(mode, verifyToken, challenge));
    } catch (MessengerVerificationException e) {
      LOGGER.warn("Webhook verification failed: {}", e.getMessage());
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
    }
  }

  @RequestMapping(method = RequestMethod.POST)
  public ResponseEntity<Void> handleCallback(@RequestBody final String payload,
                                             @RequestHeader(SIGNATURE_HEADER_NAME) final String signature) {
    LOGGER.info("Received Messenger Platform callback - payload: {} | signature: {}", payload, signature);
    try {
      this.receiveClient.processCallbackPayload(payload, signature);
      LOGGER.debug("Processed callback payload successfully");
      return ResponseEntity.status(HttpStatus.OK).build();
    } catch (MessengerVerificationException e) {
      LOGGER.warn("Processing of callback payload failed: {}", e.getMessage());
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    } catch (Exception e) {
      LOGGER.error("Error processing payload: " + payload, e);
      return ResponseEntity.status(HttpStatus.OK).build();
    }
  }
}
