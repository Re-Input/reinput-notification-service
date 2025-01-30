package info.reinput.reinput_notification_service.global.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ErrorResponse {
    private String message;
    private String code;
} 