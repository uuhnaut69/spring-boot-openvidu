package com.uuhnaut69.demo.mappers;

import com.uuhnaut69.demo.config.MapStructConfig;
import com.uuhnaut69.demo.model.User;
import com.uuhnaut69.demo.rest.payload.response.UserResponse;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * @author uuhnaut
 * @project openvidu
 */
@Mapper(config = MapStructConfig.class)
public interface UserMapper {

  UserResponse toUserResponse(User user);

  List<UserResponse> toUserResponses(List<User> users);
}
