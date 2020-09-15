package com.uuhnaut69.demo.domain.openvidu;

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
public class Notification {

  private String conversationId;

  private String conversationTitle;

  private NotificationType type;
}
