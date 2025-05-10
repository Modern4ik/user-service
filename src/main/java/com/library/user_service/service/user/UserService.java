package com.library.user_service.service.user;

import com.library.user_service.dto.user.UserRequestCreateDto;
import com.library.user_service.dto.user.UserRequestFilterDto;
import com.library.user_service.dto.user.UserResponseDto;

import java.util.List;

public interface UserService {

    UserResponseDto saveUser(UserRequestCreateDto userRequestCreateDto);

    UserResponseDto getUserById(Long id);

    List<UserResponseDto> getUsers(UserRequestFilterDto userRequestFilterDto);

    boolean existsById(Long id);

    void deleteUserById(Long id);
    
    boolean nicknameExists(String nickname);

    boolean emailExists(String email);

}
