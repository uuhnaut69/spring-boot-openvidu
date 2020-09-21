package com.uuhnaut69.demo.rest.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author uuhnaut
 * @project openvidu
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CallEvent {

  private String conversationId;

  private String conversationTitle;
}
