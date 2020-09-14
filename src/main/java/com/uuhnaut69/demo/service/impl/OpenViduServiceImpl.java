package com.uuhnaut69.demo.service.impl;

import com.uuhnaut69.demo.domain.model.Conversation;
import com.uuhnaut69.demo.domain.openvidu.Notification;
import com.uuhnaut69.demo.domain.openvidu.NotificationType;
import com.uuhnaut69.demo.rest.exception.InternalServerErrorException;
import com.uuhnaut69.demo.service.ConversationService;
import com.uuhnaut69.demo.service.OpenViduService;
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

  private Map<UUID, Map<String, String>> mapSessionNamesTokens = new ConcurrentHashMap<>();

  @Override
  public String createToken(UUID conversationId) {
    Optional<String> currentUsernameLogin = getCurrentUserLogin();
    if (!currentUsernameLogin.isPresent()) {
      throw new InternalServerErrorException("Can't get token from server at this time");
    }
    String token = "";

    TokenOptions tokenOptions = new TokenOptions.Builder().role(OpenViduRole.PUBLISHER).build();

    if (Objects.nonNull(conversationId) && Objects.nonNull(mapSessions.get(conversationId))) {
      log.info("Session of conversation {} already exists", conversationId);
      try {
        token = this.mapSessions.get(conversationId).generateToken(tokenOptions);
        this.mapSessionNamesTokens.get(conversationId).put(token, currentUsernameLogin.get());
      } catch (OpenViduJavaClientException | OpenViduHttpException e) {
        log.error(e.getMessage());
      }
    } else {
      log.info("Generate new session for conversation {}", conversationId);
      try {
        Session session = this.openVidu.createSession();
        token = session.generateToken(tokenOptions);
        this.mapSessions.put(conversationId, session);
        this.mapSessionNamesTokens.put(conversationId, new ConcurrentHashMap<>());
        this.mapSessionNamesTokens.get(conversationId).put(token, currentUsernameLogin.get());

        // Push notification for members in conversation
        Conversation conversation = conversationService.findById(conversationId);
        if (!conversation.getMembers().isEmpty()) {
          conversation.getMembers().stream()
              .filter(user -> !user.getUsername().equalsIgnoreCase(currentUsernameLogin.get()))
              .forEach(
                  user ->
                      this.messagingTemplate.convertAndSend(
                          "/topic/" + user.getUsername(),
                          new Notification(
                              conversationId,
                              conversation.getTitle(),
                              NotificationType.INCOMING_VIDEO_CALL)));
        }
      } catch (OpenViduJavaClientException | OpenViduHttpException e) {
        log.error(e.getMessage());
      }
    }
    return token;
  }

  @Override
  public void revokeToken(UUID conversationId, String token) {
    Optional<String> currentUsernameLogin = getCurrentUserLogin();

    // If the session exists
    if (currentUsernameLogin.isPresent()
        && Objects.nonNull(this.mapSessions.get(conversationId))
        && Objects.nonNull(this.mapSessionNamesTokens.get(conversationId))) {

      // Validate owner permission token before remove
      if (Objects.nonNull(this.mapSessionNamesTokens.get(conversationId).get(token))
          && this.mapSessionNamesTokens
              .get(conversationId)
              .get(token)
              .equalsIgnoreCase(currentUsernameLogin.get())) {
        this.mapSessionNamesTokens.get(conversationId).remove(token);
      }

      // Remove session if all members left
      if (this.mapSessionNamesTokens.get(conversationId).isEmpty()) {
        // Last user left: session must be removed
        this.mapSessions.remove(conversationId);
      }
    }
  }
}
