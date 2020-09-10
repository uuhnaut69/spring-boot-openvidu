package com.uuhnaut69.demo.service.impl;

import com.uuhnaut69.demo.model.Conversation;
import com.uuhnaut69.demo.model.User;
import com.uuhnaut69.demo.repository.ConversationRepository;
import com.uuhnaut69.demo.rest.payload.request.ConversationRequest;
import com.uuhnaut69.demo.service.ConversationService;
import com.uuhnaut69.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

import static com.uuhnaut69.demo.security.SecurityUtils.getCurrentUserLogin;

/**
 * @author uuhnaut
 * @project openvidu
 */
@Service
@Transactional
@RequiredArgsConstructor
public class ConversationServiceImpl implements ConversationService {

  private final UserService userService;

  private final ConversationRepository conversationRepository;

  @Override
  public Conversation create(ConversationRequest conversationRequest) {
    Conversation conversation = new Conversation();
    Set<User> users = userService.findUsersByUsernameIn(conversationRequest.getMembers());
    conversation.setMembers(users);
    Optional<String> currentUsernameLogin = getCurrentUserLogin();
    if (currentUsernameLogin.isPresent()) {
      User currentUser = userService.findByUsername(currentUsernameLogin.get());
      conversation.setOwner(currentUser);
    }
    conversation.setTitle(conversationRequest.getTitle());
    return conversationRepository.save(conversation);
  }
}
