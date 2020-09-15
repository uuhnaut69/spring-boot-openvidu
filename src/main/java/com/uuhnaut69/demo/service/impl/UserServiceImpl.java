package com.uuhnaut69.demo.service.impl;

import com.uuhnaut69.demo.domain.model.User;
import com.uuhnaut69.demo.repository.UserRepository;
import com.uuhnaut69.demo.rest.exception.BadRequestException;
import com.uuhnaut69.demo.rest.exception.InternalServerErrorException;
import com.uuhnaut69.demo.rest.payload.request.UserRequest;
import com.uuhnaut69.demo.security.AuthoritiesConstants;
import com.uuhnaut69.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.uuhnaut69.demo.security.SecurityUtils.getCurrentUserLogin;

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
    user.setUsername(userRequest.getUsername().toLowerCase());
    user.setRole(AuthoritiesConstants.USER);
    user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
    user.setAvatarUrl(userRequest.getAvatarUrl());
    return userRepository.save(user);
  }

  @Override
  @Transactional(readOnly = true)
  public List<User> findAll() {
    Optional<String> currentUsernameLogin = getCurrentUserLogin();
    if (currentUsernameLogin.isPresent()) {
      return userRepository.findAllByUsernameNotContains(currentUsernameLogin.get());
    } else {
      throw new InternalServerErrorException("Server error !!!");
    }
  }

  @Override
  @Transactional(readOnly = true)
  public User findByUsername(String username) {
    return userRepository
        .findByUsernameIgnoreCase(username)
        .orElseThrow(() -> new UsernameNotFoundException("Not found !!!"));
  }

  @Override
  @Transactional(readOnly = true)
  public Set<User> findUsersByUsernameIn(Set<String> usernameSet) {
    return userRepository.findAllByUsernameIn(usernameSet);
  }
}
