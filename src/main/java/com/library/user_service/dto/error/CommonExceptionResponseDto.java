package com.library.user_service.dto.error;

public record CommonExceptionResponseDto(String exceptionMessage, int status, String timeStamp) {
}
