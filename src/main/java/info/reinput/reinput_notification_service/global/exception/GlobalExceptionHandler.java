package info.reinput.reinput_notification_service.global.exception;

import info.reinput.reinput_notification_service.global.dto.ErrorResponse;
import info.reinput.reinput_notification_service.notification.exception.InvalidReminderRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.RestController;
import info.reinput.reinput_notification_service.notification.presentation.ReminderController;
import io.swagger.v3.oas.annotations.Hidden;

@Hidden
@RestControllerAdvice(
    annotations = {RestController.class},
    basePackageClasses = {ReminderController.class}
)
public class GlobalExceptionHandler {

    @Hidden
    @ExceptionHandler(InvalidReminderRequestException.class)
    public ResponseEntity<ErrorResponse> handleInvalidReminderRequestException(InvalidReminderRequestException e) {
        ErrorResponse response = ErrorResponse.builder()
                .message(e.getMessage())
                .code("INVALID_REMINDER_REQUEST")
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @Hidden
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        ErrorResponse response = ErrorResponse.builder()
                .message("내부 서버 오류가 발생했습니다.")
                .code("INTERNAL_SERVER_ERROR")
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
} 