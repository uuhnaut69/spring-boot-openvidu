package com.uuhnaut69.demo.domain.openvidu;

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
public class Notification {

  private UUID conversationId;

  private NotificationType type;
}
