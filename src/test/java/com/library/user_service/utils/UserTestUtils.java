package com.library.user_service.utils;

import com.library.user_service.dto.user.UserRequestCreateDto;
import com.library.user_service.dto.user.UserRequestFilterDto;
import com.library.user_service.dto.user.UserResponseDto;
import com.library.user_service.entity.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UserTestUtils {

    public static UserRequestCreateDto generateUserCreateDto(String nickname, String firstName, String lastName,
                                                             String email) {
        return new UserRequestCreateDto(nickname, firstName, lastName, email);
    }

    public static UserRequestFilterDto generateUserFilterDto(String firstName, String lastName, LocalDateTime createdAt) {
        return new UserRequestFilterDto(firstName, lastName, createdAt);
    }

    public static UserResponseDto generateUserResponseDto(Long id, String nickname, String firstName, String lastName,
                                                          String email, LocalDateTime createdAt) {
        return new UserResponseDto(id, nickname, firstName, lastName, email, createdAt);
    }

    public static User generateUser(Long id, String nickname, String firstName,
                                    String lastName, String email, LocalDateTime createdAt) {
        return new User(id, nickname, firstName, lastName, email, createdAt);
    }

    public static List<User> generateUsersList(UserRequestFilterDto filterDto, int count) {
        List<User> users = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            users.add(generateUser(
                    (long) i + 1,
                    null,
                    filterDto.firstName(),
                    filterDto.lastName(),
                    null,
                    filterDto.createdAt()
            ));
        }

        return users;
    }

    public static List<UserResponseDto> generateUserDtosList(UserRequestFilterDto filterDto, int count) {
        List<UserResponseDto> userResponseDtos = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            userResponseDtos.add(generateUserResponseDto(
                    (long) i + 1,
                    "nickname",
                    filterDto.firstName(),
                    filterDto.lastName(),
                    "test@mail.ru",
                    filterDto.createdAt()
            ));
        }

        return userResponseDtos;
    }
}
