package com.uuhnaut69.demo.rest.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * @author uuhnaut
 * @project openvidu
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

  private UUID id;

  private String username;

  private String avatarUrl;
}
