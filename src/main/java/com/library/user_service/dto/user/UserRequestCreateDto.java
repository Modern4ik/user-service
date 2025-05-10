package com.library.user_service.dto.user;

import com.library.user_service.validation.annotation.UniqueEmail;
import com.library.user_service.validation.annotation.UniqueNickname;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserRequestCreateDto(@NotBlank @UniqueNickname String nickname,
                                   String firstName,
                                   String lastName,
                                   @NotNull @Email @UniqueEmail String email) {
}

