package com.library.user_service.repository;

import com.library.user_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u WHERE " +
            "(:firstName IS NULL OR LOWER(u.firstName) = LOWER(CAST(:firstName AS STRING))) AND " +
            "(:lastName IS NULL OR LOWER(u.lastName) = LOWER(CAST(:lastName AS STRING))) AND " +
            "(CAST(:createdAt AS TIMESTAMP) IS NULL OR u.createdAt = :createdAt)")
    List<User> findByFilters(String firstName, String lastName, LocalDateTime createdAt);

    boolean existsByNickname(String nickname);

    boolean existsByEmail(String email);
    
}
