package com.library.user_service.validation.validator;

import com.library.user_service.service.user.UserService;
import com.library.user_service.validation.annotation.UniqueNickname;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UniqueNicknameValidator implements ConstraintValidator<UniqueNickname, String> {

    private final UserService userService;

    public boolean isValid(String nickname, ConstraintValidatorContext constraintValidatorContext) {
        return !userService.nicknameExists(nickname);
    }

}
