package com.uuhnaut69.demo.rest;

import com.uuhnaut69.demo.mappers.ConversationMapper;
import com.uuhnaut69.demo.model.Conversation;
import com.uuhnaut69.demo.rest.payload.request.ConversationRequest;
import com.uuhnaut69.demo.rest.payload.response.ConversationEvent;
import com.uuhnaut69.demo.rest.payload.response.ConversationResponse;
import com.uuhnaut69.demo.rest.payload.response.GenericResponse;
import com.uuhnaut69.demo.rest.payload.response.OperationType;
import com.uuhnaut69.demo.service.ConversationService;
import com.uuhnaut69.demo.service.OpenViduService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author uuhnaut
 * @project openvidu
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/conversations")
public class ConversationResource {

  private final OpenViduService openViduService;

  private final ConversationService conversationService;

  private final SimpMessagingTemplate messagingTemplate;

  private final ConversationMapper conversationMapper;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public GenericResponse create(@RequestBody @Valid ConversationRequest conversationRequest) {
    Conversation conversation = conversationService.create(conversationRequest);
    ConversationResponse conversationResponse =
        conversationMapper.toConversationResponse(conversation);
    if (!conversation.getMembers().isEmpty()) {
      conversation
          .getMembers()
          .forEach(
              user ->
                  this.messagingTemplate.convertAndSend(
                      "/topic/" + user.getUsername() + ".conversations",
                      new ConversationEvent(conversationResponse, OperationType.CREATE)));
    }
    return new GenericResponse(conversationResponse);
  }

  @PostMapping(path = "/{conversationId}/generate")
  public GenericResponse generateToken(@PathVariable String conversationId) {
    String token = openViduService.createToken(conversationId);
    return new GenericResponse(token);
  }

  @PostMapping(path = "/{conversationId}/revoke")
  public ResponseEntity<Object> revokeToken(@PathVariable String conversationId) {
    openViduService.revokeToken(conversationId);
    return ResponseEntity.noContent().build();
  }
}
