package com.uuhnaut69.demo.service;

import com.uuhnaut69.demo.model.User;
import com.uuhnaut69.demo.rest.payload.request.UserRequest;

/**
 * @author uuhnaut
 * @project openvidu
 */
public interface UserService {

  User create(UserRequest userRequest);

  User findByUsername(String username);
}
