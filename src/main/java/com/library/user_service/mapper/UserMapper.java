package com.library.user_service.mapper;

import com.library.user_service.dto.user.UserRequestCreateDto;
import com.library.user_service.dto.user.UserResponseDto;
import com.library.user_service.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    User toEntity(UserRequestCreateDto userRequestCreateDto);

    UserResponseDto toDto(User user);

    List<UserResponseDto> mapToDto(List<User> users);

}
