package com.uuhnaut69.demo.service.impl;

import com.uuhnaut69.demo.model.Conversation;
import com.uuhnaut69.demo.rest.exception.BadRequestException;
import com.uuhnaut69.demo.rest.exception.InternalServerErrorException;
import com.uuhnaut69.demo.rest.payload.response.CallEvent;
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

  private Map<String, Session> mapSessions = new ConcurrentHashMap<>();

  private Map<String, Map<String, String>> mapSessionNamesTokens = new ConcurrentHashMap<>();

  @Override
  public String createToken(String conversationId) {
    Optional<String> currentUsernameLogin = getCurrentUserLogin();
    if (!currentUsernameLogin.isPresent()) {
      throw new InternalServerErrorException("Can't get token from server at this time");
    }
    TokenOptions tokenOptions =
        new TokenOptions.Builder()
            .data(currentUsernameLogin.get())
            .role(OpenViduRole.PUBLISHER)
            .kurentoOptions(
                new KurentoOptions.Builder()
                    .allowedFilters(new String[] {"GStreamerFilter", "FaceOverlayFilter"})
                    .build())
            .build();

    if (Objects.nonNull(mapSessions.get(conversationId))) {
      log.info("Session of conversation {} already exists", conversationId);
      // Generate a new token with the recently created tokenOptions
      try {
        String token = this.mapSessions.get(conversationId).generateToken(tokenOptions);
        // Update our collection storing the new token
        this.mapSessionNamesTokens.get(conversationId).put(currentUsernameLogin.get(), token);
        return token;
      } catch (OpenViduJavaClientException e1) {
        throw new BadRequestException(e1.getMessage());
      } catch (OpenViduHttpException e2) {
        log.error(e2.getMessage());
        if (404 == e2.getStatus()) {
          // Invalid sessionId (user left unexpectedly). Session object is not valid
          // anymore. Clean collections and continue as new session
          this.mapSessions.remove(conversationId);
          this.mapSessionNamesTokens.remove(conversationId);
        }
      }
    }

    // New session
    try {
      Session session = this.openVidu.createSession();
      String token = session.generateToken(tokenOptions);
      this.mapSessions.put(conversationId, session);
      Map<String, String> tokenInfo = new ConcurrentHashMap<>();
      tokenInfo.put(currentUsernameLogin.get(), token);
      this.mapSessionNamesTokens.put(conversationId, tokenInfo);

      // Push notification for members in conversation
      Conversation conversation = conversationService.findById(UUID.fromString(conversationId));
      if (!conversation.getMembers().isEmpty()) {
        conversation.getMembers().stream()
            .filter(user -> !user.getUsername().equalsIgnoreCase(currentUsernameLogin.get()))
            .forEach(
                user ->
                    this.messagingTemplate.convertAndSend(
                        "/topic/" + user.getUsername() + ".call-event",
                        new CallEvent(conversationId, conversation.getTitle())));
      }
      return token;
    } catch (OpenViduJavaClientException | OpenViduHttpException e) {
      throw new BadRequestException(e.getMessage());
    }
  }

  @Override
  public void revokeToken(String conversationId) {
    Optional<String> currentUsernameLogin = getCurrentUserLogin();
    if (currentUsernameLogin.isPresent()
        && Objects.nonNull(this.mapSessions.get(conversationId))
        && Objects.nonNull(this.mapSessionNamesTokens.get(conversationId))) {
      if (Objects.nonNull(
          this.mapSessionNamesTokens.get(conversationId).remove(currentUsernameLogin.get()))) {
        if (this.mapSessionNamesTokens.get(conversationId).isEmpty()) {
          this.mapSessions.remove(conversationId);
        }
      } else {
        throw new BadRequestException("Request failed");
      }
    }
  }
}
