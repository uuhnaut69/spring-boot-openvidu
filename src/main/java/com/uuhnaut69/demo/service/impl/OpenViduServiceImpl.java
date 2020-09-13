package com.uuhnaut69.demo.service.impl;

import com.uuhnaut69.demo.domain.model.Conversation;
import com.uuhnaut69.demo.domain.openvidu.Notification;
import com.uuhnaut69.demo.domain.openvidu.NotificationType;
import com.uuhnaut69.demo.rest.exception.BadRequestException;
import com.uuhnaut69.demo.service.ConversationService;
import com.uuhnaut69.demo.service.OpenViduService;
import com.uuhnaut69.demo.service.UserService;
import io.openvidu.java.client.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static com.uuhnaut69.demo.security.SecurityUtils.getCurrentUserLogin;

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

  private Map<UUID, Session> mapSessions = new ConcurrentHashMap<>();

  private Map<UUID, Map<String, OpenViduRole>> mapSessionNamesTokens = new ConcurrentHashMap<>();

  @Override
  public String createToken(UUID conversationId) {
    Optional<String> currentUsernameLogin = getCurrentUserLogin();
    String token = "";
    if (!currentUsernameLogin.isPresent()) {
      throw new BadRequestException("Can't get token from server at this time");
    }

    TokenOptions tokenOptions =
        new TokenOptions.Builder()
            .data(currentUsernameLogin.get())
            .role(OpenViduRole.SUBSCRIBER)
            .build();

    if (Objects.nonNull(conversationId) && mapSessions.containsKey(conversationId)) {
      try {
        token = this.mapSessions.get(conversationId).generateToken(tokenOptions);
        this.mapSessionNamesTokens.get(conversationId).put(token, OpenViduRole.SUBSCRIBER);
      } catch (OpenViduJavaClientException | OpenViduHttpException e) {
        log.error(e.getMessage());
      }
    } else {
      try {
        Session session = this.openVidu.createSession();
        token = session.generateToken(tokenOptions);
        this.mapSessions.put(conversationId, session);
        this.mapSessionNamesTokens.put(conversationId, new ConcurrentHashMap<>());
        this.mapSessionNamesTokens.get(conversationId).put(token, OpenViduRole.SUBSCRIBER);

        // Push notification for members in conversation
        Conversation conversation = conversationService.findById(conversationId);
        if (!conversation.getMembers().isEmpty()) {
          conversation.getMembers().stream()
              .filter(user -> user.getUsername().equalsIgnoreCase(currentUsernameLogin.get()))
              .forEach(
                  user ->
                      this.messagingTemplate.convertAndSend(
                          "/topic/" + user.getUsername(),
                          new Notification(conversationId, NotificationType.INCOMING_VIDEO_CALL)));
        }

      } catch (OpenViduJavaClientException | OpenViduHttpException e) {
        log.error(e.getMessage());
      }
    }
    return token;
  }
}
