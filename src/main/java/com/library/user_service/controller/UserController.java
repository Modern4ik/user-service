package com.library.user_service.controller;

import com.library.user_service.dto.user.UserRequestCreateDto;
import com.library.user_service.dto.user.UserRequestFilterDto;
import com.library.user_service.dto.user.UserResponseDto;
import com.library.user_service.service.user.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDto saveUser(@RequestBody @Valid UserRequestCreateDto userRequestCreateDto) {
        return userService.saveUser(userRequestCreateDto);
    }

    @GetMapping(value = "/{id}")
    public UserResponseDto getUserById(@PathVariable @NotNull Long id) {
        return userService.getUserById(id);
    }

    @GetMapping
    public List<UserResponseDto> getUsers(@RequestBody UserRequestFilterDto userRequestFilterDto) {
        return userService.getUsers(userRequestFilterDto);
    }

    @GetMapping(value = "exists/{id}")
    public boolean existsById(@PathVariable @NotNull Long id) {
        return userService.existsById(id);
    }

    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUserById(@PathVariable @NotNull Long id) {
        userService.deleteUserById(id);
    }

}
