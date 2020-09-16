package com.uuhnaut69.demo.rest.advice;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author uuhnaut
 * @project openvidu
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorMsg {

  private String errorMessage;

  private int code;
}
