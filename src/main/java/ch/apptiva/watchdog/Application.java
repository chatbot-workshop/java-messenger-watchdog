package ch.apptiva.watchdog;

import com.github.messenger4j.MessengerPlatform;
import com.github.messenger4j.send.MessengerSendClient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import ch.apptiva.watchdog.adapter.persistence.MemoryWebsiteRepository;
import ch.apptiva.watchdog.domain.core.repository.WebsiteRepository;

@SpringBootApplication
@EnableScheduling
public class Application {

  public static void main(String[] args)  {
    SpringApplication.run(Application.class, args);
  }

  @Bean
  public MessengerSendClient messengerSendClient(@Value("${messenger.pageAccessToken}") String pageAccessToken) {
    MessengerSendClient sendClient = MessengerPlatform.newSendClientBuilder(pageAccessToken).build();
    return sendClient;
  }

}
