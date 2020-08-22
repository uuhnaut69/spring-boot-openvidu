package com.uuhnaut69.demo.rest.payload.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author uuhnaut
 * @project openvidu
 */
@Data
public class UserRequest {

  @NotBlank private String username;

  @NotBlank private String password;
}
