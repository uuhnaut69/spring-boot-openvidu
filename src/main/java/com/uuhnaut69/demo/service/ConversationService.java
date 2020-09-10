package com.uuhnaut69.demo.service;

import com.uuhnaut69.demo.model.Conversation;
import com.uuhnaut69.demo.rest.payload.request.ConversationRequest;

import java.util.UUID;

/**
 * @author uuhnaut
 * @project openvidu
 */
public interface ConversationService {

  Conversation create(ConversationRequest conversationRequest);

  Conversation findById(UUID conversationId);
}
