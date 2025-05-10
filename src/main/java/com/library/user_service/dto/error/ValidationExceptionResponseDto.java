package com.library.user_service.dto.error;

import java.util.Map;

public record ValidationExceptionResponseDto(String message, int status, String timeStamp, Map<String, String> errors) {
}
