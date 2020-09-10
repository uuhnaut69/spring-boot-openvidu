package com.uuhnaut69.demo.repository;

import com.uuhnaut69.demo.model.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * @author uuhnaut
 * @project openvidu
 */
@Repository
public interface ConversationRepository extends JpaRepository<Conversation, UUID> {}
