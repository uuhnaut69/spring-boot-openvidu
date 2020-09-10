package com.uuhnaut69.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * @author uuhnaut
 * @project openvidu
 */
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Conversation {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private UUID id;

  @Column(nullable = false, unique = true)
  private String title;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "owner_id", nullable = false)
  private User owner;

  @ManyToMany
  @JoinTable(
      name = "user_conversation",
      joinColumns = @JoinColumn(name = "user_id"),
      inverseJoinColumns = @JoinColumn(name = "conversation_id"))
  private Set<User> members = new HashSet<>();
}
