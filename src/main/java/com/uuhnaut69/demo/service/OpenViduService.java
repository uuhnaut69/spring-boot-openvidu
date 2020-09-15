package com.uuhnaut69.demo.service;

/**
 * @author uuhnaut
 * @project openvidu
 */
public interface OpenViduService {

  String createToken(String conversationId);

  void revokeToken(String conversationId);
}
