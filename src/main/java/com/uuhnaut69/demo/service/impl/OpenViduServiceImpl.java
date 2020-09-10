package com.uuhnaut69.demo.service.impl;

import com.uuhnaut69.demo.rest.payload.response.GenericResponse;
import com.uuhnaut69.demo.service.OpenViduService;
import io.openvidu.java.client.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * @author uuhnaut
 * @project openvidu
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OpenViduServiceImpl implements OpenViduService {

  private final OpenVidu openVidu;

  @Override
  public GenericResponse createSession(UUID conversationId) {
    String token = "";
    try {
      Session session = this.openVidu.createSession(new SessionProperties.Builder().build());
      TokenOptions tokenOptions =
          new TokenOptions.Builder()
              .data(conversationId.toString())
              .role(OpenViduRole.PUBLISHER)
              .build();
      token = session.generateToken(tokenOptions);
    } catch (OpenViduJavaClientException | OpenViduHttpException e) {
      log.error(e.getMessage());
    }
    return new GenericResponse(token);
  }
}
