package by.toukach.currencies.config;

import feign.Logger;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
@Getter
public class FeignClientConfig {

  @Value("${currency.api.url}")
  private String apiUrl;

  @Value("${server.port}")
  private String serverPort;

  @Bean
  Logger.Level feignLoggerLevel() {
    return Logger.Level.FULL;
  }
}
