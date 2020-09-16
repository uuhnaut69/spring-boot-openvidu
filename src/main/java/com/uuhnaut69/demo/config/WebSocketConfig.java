package com.uuhnaut69.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * @author uuhnaut
 * @project openvidu
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

  @Value("${rabbitmq.host}")
  private String rabbitHost;

  @Value("${rabbitmq.port}")
  private int rabbitPort;

  @Value("${rabbitmq.username}")
  private String rabbitUsername;

  @Value("${rabbitmq.password}")
  private String rabbitPass;

  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    registry.addEndpoint("/ws").setAllowedOrigins("*").withSockJS();
  }

  @Override
  public void configureMessageBroker(MessageBrokerRegistry config) {
    config.setApplicationDestinationPrefixes("/app");
    config
        .enableStompBrokerRelay("/topic")
        .setRelayHost(rabbitHost)
        .setRelayPort(rabbitPort)
        .setClientLogin(rabbitUsername)
        .setClientPasscode(rabbitPass);
  }
}
