package com.stayhub.mapper;

import com.stayhub.dto.UserResponse;
import com.stayhub.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponse toResponse(User user);
}
