package com.uuhnaut69.demo.service.impl;

import com.uuhnaut69.demo.model.User;
import com.uuhnaut69.demo.repository.UserRepository;
import com.uuhnaut69.demo.rest.exception.BadRequestException;
import com.uuhnaut69.demo.rest.payload.request.UserRequest;
import com.uuhnaut69.demo.security.AuthoritiesConstants;
import com.uuhnaut69.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author uuhnaut
 * @project openvidu
 */
@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final PasswordEncoder passwordEncoder;
  private final UserRepository userRepository;

  @Override
  public User create(UserRequest userRequest) {
    if (userRepository.existsByUsernameIgnoreCase(userRequest.getUsername())) {
      throw new BadRequestException("Username already exist !!!");
    }
    User user = new User();
    user.setUsername(userRequest.getUsername());
    user.setRole(AuthoritiesConstants.USER);
    user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
    return userRepository.save(user);
  }

  @Override
  @Transactional(readOnly = true)
  public User findByUsername(String username) {
    return userRepository
        .findByUsernameIgnoreCase(username)
        .orElseThrow(() -> new UsernameNotFoundException("Not found !!!"));
  }
}
