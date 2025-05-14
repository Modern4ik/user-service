package com.library.user_service.integration.full;

import com.library.user_service.controller.UserController;
import com.library.user_service.dto.user.UserRequestCreateDto;
import com.library.user_service.dto.user.UserRequestFilterDto;
import com.library.user_service.dto.user.UserResponseDto;
import com.library.user_service.repository.UserRepository;
import com.library.user_service.utils.UserTestUtils;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Map;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Sql(scripts = "/test-users-data.sql")
@EmbeddedKafka(partitions = 1, topics = {"user-deleted-topic"}, brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"})
public class UserIntegrationTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private UserController userController;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @AfterEach
    public void resetSequence() {
        entityManager.createNativeQuery(
                "ALTER TABLE users ALTER COLUMN id RESTART WITH 1"
        ).executeUpdate();
    }

    @Test
    public void shouldSaveUser() {
        UserRequestCreateDto createDto =
                UserTestUtils.generateUserCreateDto(
                        "Nickname", null, null, "test@mail.ru");

        UserResponseDto responseDto = userController.saveUser(createDto);

        Assertions.assertNotNull(responseDto);
        Assertions.assertEquals(4L, responseDto.getId());
        Assertions.assertEquals(createDto.nickname(), responseDto.getNickname());
        Assertions.assertEquals(createDto.email(), responseDto.getEmail());
        Assertions.assertNotNull(responseDto.getCreatedAt());

        Assertions.assertEquals(4, userRepository.count());
    }

    @Test
    public void shouldReturnUserById() {
        UserResponseDto responseDto = userController.getUserById(2L);

        Assertions.assertNotNull(responseDto);
        Assertions.assertEquals(2L, responseDto.getId());
        Assertions.assertEquals("TWO", responseDto.getNickname());
    }

    @Test
    public void shouldReturnUsersByFirstNameAndLastName() {
        UserRequestFilterDto filterDto =
                UserTestUtils.generateUserFilterDto("Serg", "Zayts", null);

        List<UserResponseDto> responseDtoList = userController.getUsers(filterDto);

        Assertions.assertNotNull(responseDtoList);
        Assertions.assertEquals(2, responseDtoList.size());
        for (UserResponseDto responseDto : responseDtoList) {
            Assertions.assertEquals(filterDto.firstName(), responseDto.getFirstName());
            Assertions.assertEquals(filterDto.lastName(), responseDto.getLastName());
        }
    }

    @Test
    public void shouldReturnTrueExistsUserById() {
        boolean existsResult = userController.existsById(1L);

        Assertions.assertTrue(existsResult);
    }

    @Test
    public void shouldReturnFalseExistsUserById() {
        boolean existsResult = userController.existsById(4L);

        Assertions.assertFalse(existsResult);
    }

    @Test
    public void shouldDeleteUserById() {
        userController.deleteUserById(1L);

        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps("test-group", "true", embeddedKafkaBroker);
        Consumer<String, String> consumer = new DefaultKafkaConsumerFactory<String, String>(consumerProps).createConsumer();
        try (consumer) {
            embeddedKafkaBroker.consumeFromAnEmbeddedTopic(consumer, "user-deleted-topic");
            ConsumerRecord<String, String> receivedRecord = KafkaTestUtils.getSingleRecord(consumer, "user-deleted-topic");

            Assertions.assertEquals("user-deleted-topic", receivedRecord.topic());
            Assertions.assertEquals("{\"userId\":1}", receivedRecord.value());

            Assertions.assertFalse(userRepository.existsById(1L));
            Assertions.assertEquals(2, userRepository.count());
        }
    }

}
