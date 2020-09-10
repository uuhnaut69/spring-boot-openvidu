package com.uuhnaut69.demo.rest.payload.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Set;

/**
 * @author uuhnaut
 * @project openvidu
 */
@Data
public class ConversationRequest {

  @NotBlank private String title;

  @NotNull private Set<String> members;
}
