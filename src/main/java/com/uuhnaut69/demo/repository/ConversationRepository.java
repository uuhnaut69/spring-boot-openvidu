package com.uuhnaut69.demo.repository;

import com.uuhnaut69.demo.model.Conversation;
import com.uuhnaut69.demo.model.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @author uuhnaut
 * @project openvidu
 */
@Repository
public interface ConversationRepository extends JpaRepository<Conversation, UUID> {

  @EntityGraph(attributePaths = {"members", "owner"})
  Optional<Conversation> findById(UUID conversationId);

  @EntityGraph(attributePaths = {"members"})
  List<Conversation> findAllByMembersContains(User currentUser);
}
