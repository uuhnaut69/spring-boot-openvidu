package com.uuhnaut69.demo.service.impl;

import com.google.common.base.Strings;
import com.uuhnaut69.demo.model.Conversation;
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
    String token = "";

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
      try {
        token = this.mapSessions.get(conversationId).generateToken(tokenOptions);
        this.mapSessionNamesTokens.get(conversationId).put(currentUsernameLogin.get(), token);
      } catch (OpenViduJavaClientException | OpenViduHttpException e) {
        log.error(e.getMessage());
      }
    } else {
      log.info("Generate new session for conversation {}", conversationId);
      try {
        Session session = this.openVidu.createSession();
        token = session.generateToken(tokenOptions);
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
      } catch (OpenViduJavaClientException | OpenViduHttpException e) {
        log.error(e.getMessage());
      }
    }
    return token;
  }

  @Override
  public void revokeToken(String conversationId) {
    Optional<String> currentUsernameLogin = getCurrentUserLogin();
    if (currentUsernameLogin.isPresent()
        && Objects.nonNull(this.mapSessions.get(conversationId))
        && Objects.nonNull(this.mapSessionNamesTokens.get(conversationId))
        && !Strings.isNullOrEmpty(
            this.mapSessionNamesTokens.get(conversationId).remove(currentUsernameLogin.get()))
        && this.mapSessionNamesTokens.get(conversationId).isEmpty()) {

      // If session has no connection -> Remove it
      this.mapSessions.remove(conversationId);
    }
  }
}
