package com.library.user_service.service.user;

import com.library.user_service.constants.MessageTemplates;
import com.library.common.events.UserDeletedEvent;
import com.library.user_service.dto.user.UserRequestCreateDto;
import com.library.user_service.dto.user.UserRequestFilterDto;
import com.library.user_service.dto.user.UserResponseDto;
import com.library.user_service.mapper.UserMapper;
import com.library.user_service.repository.UserRepository;
import com.library.user_service.service.cache.CacheVersionService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final CacheVersionService cacheVersionService;
    private final KafkaTemplate<String, UserDeletedEvent> kafkaTemplate;

    @Override
    @Transactional
    public UserResponseDto saveUser(UserRequestCreateDto userRequestCreateDto) {
        UserResponseDto newUserDto = userMapper.toDto(
                userRepository.save(userMapper.toEntity(userRequestCreateDto)));

        cacheVersionService.incrementVersion();
        return newUserDto;
    }

    @Override
    @Cacheable(value = "userById", key = "#id")
    public UserResponseDto getUserById(Long id) {
        return userMapper.toDto(userRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException(MessageTemplates.USER_NOT_FOUND_MESSAGE.formatted(id))));
    }

    @Override
    @Cacheable(value = "usersByFilter", key = "{#userRequestFilterDto, @cacheVersionService.getCurrentVersion()}")
    public List<UserResponseDto> getUsers(UserRequestFilterDto userRequestFilterDto) {
        return userMapper.mapToDto(
                userRepository.findByFilters(
                        userRequestFilterDto.firstName(), userRequestFilterDto.lastName(), userRequestFilterDto.createdAt())
        );
    }

    @Override
    public boolean existsById(Long id) {
        return userRepository.existsById(id);
    }

    @Override
    @CacheEvict(value = "userById", key = "#id")
    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
        kafkaTemplate.send("user-deleted-topic", new UserDeletedEvent(id));

        cacheVersionService.incrementVersion();
    }

    @Override
    public boolean nicknameExists(String nickname) {
        return userRepository.existsByNickname(nickname);
    }

    @Override
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

}