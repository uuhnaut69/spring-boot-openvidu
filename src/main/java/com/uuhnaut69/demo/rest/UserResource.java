package com.uuhnaut69.demo.rest;

import com.uuhnaut69.demo.rest.payload.request.UserRequest;
import com.uuhnaut69.demo.rest.payload.response.GenericResponse;
import com.uuhnaut69.demo.security.JwtFilter;
import com.uuhnaut69.demo.security.TokenProvider;
import com.uuhnaut69.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author uuhnaut
 * @project openvidu
 */
@RestController
@RequiredArgsConstructor
public class UserResource {

  private final UserService userService;

  private final TokenProvider tokenProvider;

  private final AuthenticationManagerBuilder authenticationManagerBuilder;

  @PostMapping(path = "/register")
  @ResponseStatus(HttpStatus.CREATED)
  public GenericResponse register(@RequestBody UserRequest userRequest) {
    return new GenericResponse(userService.create(userRequest));
  }

  @PostMapping("/authenticate")
  public GenericResponse authenticate(@RequestBody UserRequest userRequest) {
    UsernamePasswordAuthenticationToken authenticationToken =
        new UsernamePasswordAuthenticationToken(
            userRequest.getUsername(), userRequest.getPassword());

    Authentication authentication =
        authenticationManagerBuilder.getObject().authenticate(authenticationToken);
    SecurityContextHolder.getContext().setAuthentication(authentication);
    String jwt = tokenProvider.createToken(authentication);
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER, "Bearer " + jwt);
    return new GenericResponse(jwt);
  }
}
