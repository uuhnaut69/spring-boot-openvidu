package com.uuhnaut69.demo.service;

import com.uuhnaut69.demo.domain.model.User;
import com.uuhnaut69.demo.rest.payload.request.UserRequest;

import java.util.List;
import java.util.Set;

/**
 * @author uuhnaut
 * @project openvidu
 */
public interface UserService {

  User create(UserRequest userRequest);

  List<User> findAll();

  User findByUsername(String username);

  Set<User> findUsersByUsernameIn(Set<String> usernameSet);
}
