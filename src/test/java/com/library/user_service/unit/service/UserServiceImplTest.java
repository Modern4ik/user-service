package com.library.user_service.unit.service;

import com.library.common.events.UserDeletedEvent;
import com.library.user_service.dto.user.UserRequestCreateDto;
import com.library.user_service.dto.user.UserRequestFilterDto;
import com.library.user_service.dto.user.UserResponseDto;
import com.library.user_service.entity.User;
import com.library.user_service.mapper.UserMapperImpl;
import com.library.user_service.repository.UserRepository;
import com.library.user_service.service.cache.CacheVersionService;
import com.library.user_service.service.user.UserServiceImpl;
import com.library.user_service.utils.UserTestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private KafkaTemplate<String, UserDeletedEvent> kafkaTemplate;
    @Spy
    private CacheVersionService cacheVersionService;
    @Spy
    private UserMapperImpl userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    public void shouldSaveUser() {
        UserRequestCreateDto createDto =
                UserTestUtils.generateUserCreateDto(
                        "Test user", "Serg", "Zayts", "test@mail.ru");
        User expectedNewUser =
                UserTestUtils.generateUser(
                        1L, createDto.nickname(), createDto.firstName(), createDto.lastName(), createDto.email(), LocalDateTime.now());

        Mockito.when(userMapper.toEntity(createDto))
                .thenReturn(expectedNewUser);
        Mockito.when(userRepository.save(expectedNewUser))
                .thenReturn(expectedNewUser);

        UserResponseDto responseDto = userService.saveUser(createDto);
        Assertions.assertNotNull(responseDto);
        Assertions.assertEquals(expectedNewUser.getId(), responseDto.getId());
        Assertions.assertEquals(expectedNewUser.getNickname(), responseDto.getNickname());
        Assertions.assertEquals(expectedNewUser.getEmail(), responseDto.getEmail());
        Assertions.assertEquals(expectedNewUser.getCreatedAt(), responseDto.getCreatedAt());
    }

    @Test
    public void shouldReturnUserById() {
        User expectedUser =
                UserTestUtils.generateUser(
                        1L, "Test user", "Serg", "Zayts", "test@mail.ru", LocalDateTime.now());

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(expectedUser));

        UserResponseDto responseDto = userService.getUserById(1L);
        Assertions.assertNotNull(responseDto);
        Assertions.assertEquals(expectedUser.getId(), responseDto.getId());
        Assertions.assertEquals(expectedUser.getNickname(), responseDto.getNickname());
    }

    @Test
    public void shouldReturnUsersByFirstNameAndCreatedAt() {
        UserRequestFilterDto filterDto =
                UserTestUtils.generateUserFilterDto("Sergey", null, LocalDateTime.now());
        List<User> expectedUsers = UserTestUtils.generateUsersList(filterDto, 3);

        Mockito.when(userRepository.findByFilters(filterDto.firstName(), filterDto.lastName(), filterDto.createdAt()))
                .thenReturn(expectedUsers);

        List<UserResponseDto> responseDtoList = userService.getUsers(filterDto);
        Assertions.assertNotNull(responseDtoList);
        Assertions.assertEquals(3, responseDtoList.size());
        for (UserResponseDto responseDto : responseDtoList) {
            Assertions.assertEquals(filterDto.firstName(), responseDto.getFirstName());
            Assertions.assertEquals(filterDto.createdAt(), responseDto.getCreatedAt());
        }
    }

    @Test
    public void shouldReturnTrueExistsUserById() {
        Mockito.when(userRepository.existsById(1L))
                .thenReturn(true);

        boolean existsResult = userService.existsById(1L);
        Assertions.assertTrue(existsResult);
    }

    @Test
    public void shouldReturnFalseExistsUserById() {
        Mockito.when(userRepository.existsById(1L))
                .thenReturn(false);

        boolean existsResult = userService.existsById(1L);
        Assertions.assertFalse(existsResult);
    }

    @Test
    public void shouldDeleteUserById() {
        userService.deleteUserById(1L);

        Mockito.verify(userRepository).deleteById(1L);
        Mockito.verify(kafkaTemplate).send("user-deleted-topic", new UserDeletedEvent(1L));
    }
}
