package com.uuhnaut69.demo.mappers;

import com.uuhnaut69.demo.config.MapStructConfig;
import com.uuhnaut69.demo.model.Conversation;
import com.uuhnaut69.demo.model.User;
import com.uuhnaut69.demo.rest.payload.response.ConversationResponse;
import com.uuhnaut69.demo.rest.payload.response.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * @author uuhnaut
 * @project openvidu
 */
@Mapper(config = MapStructConfig.class)
public interface ConversationMapper {

  @Mapping(source = "conversation.owner", target = "owner")
  @Mapping(source = "conversation.members", target = "members")
  ConversationResponse toConversationResponse(Conversation conversation);

  List<ConversationResponse> toConversationResponses(List<Conversation> conversations);

  UserResponse toUserResponse(User user);
}
