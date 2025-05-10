package com.library.user_service.validation.annotation;

import com.library.user_service.validation.validator.UniqueNicknameValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueNicknameValidator.class)
public @interface UniqueNickname {

    String message() default "Username is already taken!";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
