package com.uuhnaut69.demo.config;

import io.openvidu.java.client.OpenVidu;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author uuhnaut
 * @project openvidu
 */
@Configuration
public class OpenViduConfig {

  @Value(value = "${openvidu.hostname}")
  private String hostName;

  @Value(value = "${openvidu.secret}")
  private String secret;

  @Bean
  public OpenVidu openVidu() {
    return new OpenVidu(hostName, secret);
  }
}
