package com.uuhnaut69.demo.rest.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * @author uuhnaut
 * @project openvidu
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConversationResponse {

  private UUID id;

  private String title;

  private UserResponse owner;

  private Set<UserResponse> members = new HashSet<>();
}
