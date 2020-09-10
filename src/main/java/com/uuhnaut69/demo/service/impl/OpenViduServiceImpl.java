package com.uuhnaut69.demo.service.impl;

import com.uuhnaut69.demo.model.Conversation;
import com.uuhnaut69.demo.service.ConversationService;
import com.uuhnaut69.demo.service.OpenViduService;
import io.openvidu.java.client.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
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

  private final ConversationService conversationService;

  private final SimpMessagingTemplate messagingTemplate;

  @Override
  public String createSession(UUID conversationId) {
    String token = null;
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

    if (token != null && !token.isEmpty()) {
      Conversation conversation = conversationService.findById(conversationId);
      if (!conversation.getMembers().isEmpty()) {
        String finalToken = token;
        conversation
            .getMembers()
            .forEach(
                user ->
                    this.messagingTemplate.convertAndSend(
                        "/topic/" + user.getUsername(),
                        new Notification(finalToken, NotificationType.INCOMING_VIDEO_CALL)));
      }
    }
    return token;
  }

  public static class Notification {
    private String token;

    private NotificationType type;

    public Notification(String token, NotificationType type) {
      this.token = token;
      this.type = type;
    }
  }

  public enum NotificationType {
    INCOMING_TEXT,
    INCOMING_VIDEO_CALL
  }
}
