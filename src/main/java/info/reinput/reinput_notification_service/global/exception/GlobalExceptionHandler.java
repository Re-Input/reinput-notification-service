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
import info.reinput.reinput_notification_service.notification.exception.ReminderNotFoundException;
import info.reinput.reinput_notification_service.notification.exception.InvalidReminderTypeException;

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

    @Hidden
    @ExceptionHandler(ReminderNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleReminderNotFoundException(ReminderNotFoundException e) {
        ErrorResponse response = ErrorResponse.builder()
                .message(e.getMessage())
                .code("REMINDER_NOT_FOUND")
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @Hidden
    @ExceptionHandler(InvalidReminderTypeException.class)
    public ResponseEntity<ErrorResponse> handleInvalidReminderTypeException(InvalidReminderTypeException e) {
        ErrorResponse response = ErrorResponse.builder()
                .message(e.getMessage())
                .code("INVALID_REMINDER_REQUEST")
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
} 