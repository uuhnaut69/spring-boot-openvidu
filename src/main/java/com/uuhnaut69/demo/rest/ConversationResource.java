package com.uuhnaut69.demo.rest;

import com.uuhnaut69.demo.domain.model.Conversation;
import com.uuhnaut69.demo.rest.payload.request.ConversationRequest;
import com.uuhnaut69.demo.rest.payload.response.GenericResponse;
import com.uuhnaut69.demo.service.ConversationService;
import com.uuhnaut69.demo.service.OpenViduService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

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

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public GenericResponse create(@RequestBody @Valid ConversationRequest conversationRequest) {
    Conversation conversation = conversationService.create(conversationRequest);
    return new GenericResponse(conversation);
  }

  @PostMapping(path = "/{conversationId}/generate")
  public GenericResponse generateToken(@PathVariable UUID conversationId) {
    String token = openViduService.createToken(conversationId);
    return new GenericResponse(token);
  }

  @PostMapping(path = "/{conversationId}/revoke")
  public ResponseEntity<Object> revokeToken(
      @PathVariable UUID conversationId, @RequestBody String token) {
    openViduService.revokeToken(conversationId, token);
    return ResponseEntity.noContent().build();
  }
}
