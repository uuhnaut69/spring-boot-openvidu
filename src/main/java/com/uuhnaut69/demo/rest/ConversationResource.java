package com.uuhnaut69.demo.rest;

import com.uuhnaut69.demo.model.Conversation;
import com.uuhnaut69.demo.rest.payload.request.ConversationRequest;
import com.uuhnaut69.demo.rest.payload.response.GenericResponse;
import com.uuhnaut69.demo.service.ConversationService;
import com.uuhnaut69.demo.service.OpenViduService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

  @PostMapping(path = "/{conversationId}")
  public GenericResponse makeACall(@PathVariable UUID conversationId) {
    String token = openViduService.createSession(conversationId);
    return new GenericResponse(token);
  }
}
