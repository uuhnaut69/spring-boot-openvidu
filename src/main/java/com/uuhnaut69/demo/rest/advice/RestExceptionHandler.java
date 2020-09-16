package com.uuhnaut69.demo.rest.advice;

import com.uuhnaut69.demo.rest.exception.BadRequestException;
import com.uuhnaut69.demo.rest.exception.ForbiddenException;
import com.uuhnaut69.demo.rest.exception.InternalServerErrorException;
import com.uuhnaut69.demo.rest.exception.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * @author uuhnaut
 * @project openvidu
 */
@RestControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(BadRequestException.class)
  public ResponseEntity<ErrorMsg> badRequestHandler(Exception exception) {
    ErrorMsg errorMsg =
        ErrorMsg.builder()
            .errorMessage(exception.getMessage())
            .code(HttpStatus.BAD_REQUEST.value())
            .build();
    return new ResponseEntity<>(errorMsg, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(ForbiddenException.class)
  public ResponseEntity<ErrorMsg> forbiddenHandler(Exception exception) {
    ErrorMsg errorMsg =
        ErrorMsg.builder()
            .errorMessage(exception.getMessage())
            .code(HttpStatus.FORBIDDEN.value())
            .build();
    return new ResponseEntity<>(errorMsg, HttpStatus.FORBIDDEN);
  }

  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<ErrorMsg> notFoundHandler(Exception exception) {
    ErrorMsg errorMsg =
        ErrorMsg.builder()
            .errorMessage(exception.getMessage())
            .code(HttpStatus.NOT_FOUND.value())
            .build();
    return new ResponseEntity<>(errorMsg, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(InternalServerErrorException.class)
  public ResponseEntity<ErrorMsg> serverErrorHandler(Exception exception) {
    ErrorMsg errorMsg =
        ErrorMsg.builder()
            .errorMessage(exception.getMessage())
            .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .build();
    return new ResponseEntity<>(errorMsg, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorMsg> generalHandler(Exception exception) {
    ErrorMsg errorMsg =
        ErrorMsg.builder()
            .errorMessage(exception.getMessage())
            .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .build();
    return new ResponseEntity<>(errorMsg, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
