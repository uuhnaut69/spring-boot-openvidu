package com.uuhnaut69.demo.rest.exception;

/**
 * @author uuhnaut
 * @project openvidu
 */
public class BadRequestException extends RuntimeException {
  public BadRequestException(String message) {
    super(message);
  }
}
