package com.uuhnaut69.demo.service;

import com.uuhnaut69.demo.rest.payload.response.GenericResponse;

import java.util.UUID;

/**
 * @author uuhnaut
 * @project openvidu
 */
public interface OpenViduService {

  GenericResponse createSession(UUID conversationId);
}
