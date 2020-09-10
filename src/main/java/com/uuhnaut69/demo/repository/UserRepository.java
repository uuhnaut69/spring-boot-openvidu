package com.uuhnaut69.demo.repository;

import com.uuhnaut69.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * @author uuhnaut
 * @project openvidu
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

  Optional<User> findByUsernameIgnoreCase(String username);

  boolean existsByUsernameIgnoreCase(String username);

  Set<User> findAllByUsernameIn(Set<String> usernameSet);
}
