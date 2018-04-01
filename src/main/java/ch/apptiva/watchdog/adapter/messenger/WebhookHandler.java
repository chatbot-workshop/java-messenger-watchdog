package ch.apptiva.watchdog.adapter.messenger;

import static com.github.messenger4j.MessengerPlatform.CHALLENGE_REQUEST_PARAM_NAME;
import static com.github.messenger4j.MessengerPlatform.MODE_REQUEST_PARAM_NAME;
import static com.github.messenger4j.MessengerPlatform.SIGNATURE_HEADER_NAME;
import static com.github.messenger4j.MessengerPlatform.VERIFY_TOKEN_REQUEST_PARAM_NAME;

import ch.apptiva.watchdog.domain.conversation.GetStatistics;
import ch.apptiva.watchdog.domain.conversation.Intent;
import ch.apptiva.watchdog.domain.conversation.IntentAnalyzer;
import ch.apptiva.watchdog.domain.conversation.TextReplyIntent;
import ch.apptiva.watchdog.domain.conversation.UnwatchWebsite;
import ch.apptiva.watchdog.domain.conversation.WatchWebsite;
import ch.apptiva.watchdog.domain.core.model.UserId;
import ch.apptiva.watchdog.domain.core.model.Website;
import ch.apptiva.watchdog.domain.core.repository.WebsiteRepository;
import ch.apptiva.watchdog.domain.core.service.StatisticService;
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

/**
 * This Controller receives all Facebook Messenger hooks from your chat users. There are two entry points:
 * <p>
 * 1. GET Request: Used only for the registration of the hook.
 * 2. POST Request: Used for all Facebook hooks from text messages till uploads.
 * <p>
 * We use the library messenger4j to work with the messenger API. This frees us from parsing and composing
 * lots of json stuff.
 */
@RestController
@RequestMapping("/webhook")
public class WebhookHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebhookHandler.class);
    private final MessengerReceiveClient receiveClient;
    private final MessengerSendClient sendClient;
    private final WebsiteRepository websiteRepository;
    private final StatisticService statisticService;

    @Autowired
    public WebhookHandler(@Value("${messenger.appSecret}") final String appSecret, @Value("${messenger.verifyToken}") final String verifyToken,
            final MessengerSendClient sendClient, final WebsiteRepository websiteRepository, StatisticService statisticService) {
        this.statisticService = statisticService;
        this.receiveClient =
                MessengerPlatform.newReceiveClientBuilder(appSecret, verifyToken).onTextMessageEvent(newTextMessageEventHandler())
                        // You may register here many more handlers to make an even better bot.
                        .build();
        this.sendClient = sendClient;
        this.websiteRepository = websiteRepository;
    }

    /**
     * This will be called when you register the webhook. Use it to check the verifyToken.
     *
     * @param mode
     * @param verifyToken Verify Token which you have been entering at registering the webhook.
     * @param challenge
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<String> verifyWebhook(@RequestParam(MODE_REQUEST_PARAM_NAME) final String mode, @RequestParam(VERIFY_TOKEN_REQUEST_PARAM_NAME) final String verifyToken,
            @RequestParam(CHALLENGE_REQUEST_PARAM_NAME) final String challenge) {

        LOGGER.debug("Received Webhook verification request - mode: {} | verifyToken: {} | challenge: {}", mode, verifyToken, challenge);
        try {
            return ResponseEntity.ok(this.receiveClient.verifyWebhook(mode, verifyToken, challenge));
        } catch (MessengerVerificationException e) {
            LOGGER.warn("Webhook verification failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    /**
     * This is called on every event that is happening in your chat. This is the place where you answer
     * every request from your users.
     *
     * @param payload
     * @param signature
     * @return
     */
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Void> handleWebhook(@RequestBody final String payload, @RequestHeader(SIGNATURE_HEADER_NAME) final String signature) {
        LOGGER.info("Received Messenger Platform webhook - payload: {} | signature: {}", payload, signature);
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
                    websiteRepository.persist(website);
                    sendTextMessage(senderId, "OK, wird gemacht");
                } else if (intent instanceof UnwatchWebsite) {
                    UnwatchWebsite unwatchWebsite = (UnwatchWebsite)intent;
                    Website website = websiteRepository.findByUrl(unwatchWebsite.url());
                    websiteRepository.delete(website);
                    sendTextMessage(senderId, "OK. " + website.url() + " wurde gelöscht.");
                } else if (intent instanceof GetStatistics) {
                    // TODO
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
}
