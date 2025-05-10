package com.library.user_service.integration.semi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.user_service.controller.UserController;
import com.library.user_service.dto.user.UserRequestCreateDto;
import com.library.user_service.dto.user.UserRequestFilterDto;
import com.library.user_service.dto.user.UserResponseDto;
import com.library.user_service.service.user.UserService;
import com.library.user_service.utils.UserTestUtils;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;

@WebMvcTest(UserController.class)
@ActiveProfiles("test")
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @Test
    public void shouldSaveNewUser() throws Exception {
        UserRequestCreateDto createDto =
                UserTestUtils.generateUserCreateDto(
                        "Test user", "Serg", "Zayts", "test@mail.ru");
        UserResponseDto expectedResponseDto =
                UserTestUtils.generateUserResponseDto(
                        1L, createDto.nickname(), createDto.firstName(), createDto.lastName(), createDto.email(), LocalDateTime.now());

        Mockito.when(userService.saveUser(createDto))
                .thenReturn(expectedResponseDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.nickname").value(createDto.nickname()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(createDto.email()));
    }

    @Test
    public void shouldReturnUserById() throws Exception {
        UserResponseDto expectedResponseDto =
                UserTestUtils.generateUserResponseDto(
                        1L, "nickname", "Serg", "Zayts", "test@mail.ru", LocalDateTime.now());

        Mockito.when(userService.getUserById(1L))
                .thenReturn(expectedResponseDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/users/{id}", 1L))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.nickname").value("nickname"));
    }

    @Test
    public void shouldReturnUsersByFirstNameAndLastName() throws Exception {
        UserRequestFilterDto filterDto =
                UserTestUtils.generateUserFilterDto("Serg", "Zayts", LocalDateTime.now());

        Mockito.when(userService.getUsers(filterDto))
                .thenReturn(UserTestUtils.generateUserDtosList(filterDto, 3));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filterDto)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(3)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].firstName").value(filterDto.firstName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].lastName").value(filterDto.lastName()));
    }

    @Test
    public void shouldReturnTrueExistsUserById() throws Exception {
        Mockito.when(userService.existsById(1L))
                .thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/users/exists/{id}", 1L))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("true"));
    }

    @Test
    public void shouldReturnFalseExistsUserById() throws Exception {
        Mockito.when(userService.existsById(1L))
                .thenReturn(false);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/users/exists/{id}", 1L))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("false"));
    }

    @Test
    public void shouldDeleteUserById() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/users/{id}", 1L))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

}