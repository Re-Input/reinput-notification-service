package info.reinput.reinput_notification_service.notification.presentation;

import info.reinput.reinput_notification_service.notification.application.ReminderCalendarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@Tag(name = "CalendarReminder", description = "캘린더 기반 리마인더 API")
@RestController
@RequestMapping("/reminder/calendar")
@RequiredArgsConstructor
public class CalendarReminderController {

    private final ReminderCalendarService reminderCalendarService;

    @Operation(
        summary = "캘린더 날짜별 리마인더 대상 인사이트 ID 조회",
        description = """
                입력된 날짜와 memberId에 대해 리마인드해야 하는 인사이트의 ID 목록을 반환합니다.
                
                날짜 형식: yyyy.MM.dd (예: 2025.02.17)
                """
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "리마인더 대상 인사이트 ID 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "매월 17일, 매주 월요일 리마인더 조회 성공",
                                            value = """
                                            [1, 2, 3]
                                            """),
                                    @ExampleObject(
                                            name = "리마인더가 없는 날짜 조회",
                                            value = """
                                            []
                                            """)
                            }
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "204",
                    description = """
                            다음의 경우 No Content를 반환합니다:
                            - 사용자의 인사이트가 없는 경우
                            - 활성화된 리마인더가 없는 경우
                            - 해당 날짜에 예정된 리마인더가 없는 경우
                            """
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "잘못된 날짜 형식",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "message": "잘못된 날짜 형식입니다. yyyy.MM.dd 형식을 사용해주세요.",
                                      "code": "INVALID_DATE_FORMAT"
                                    }
                                    """)
                    )
            )
    })
    @GetMapping("/v1")
    public ResponseEntity<List<Long>> getCalendarReminders(
            @Parameter(
                    name = "date",
                    description = "조회할 날짜",
                    required = true,
                    example = "2025.02.17",
                    in = ParameterIn.QUERY
            )
            @RequestParam("date") @DateTimeFormat(pattern = "yyyy.MM.dd") LocalDate date,
            
            @Parameter(
                    name = "X-User-Id",
                    description = "사용자 ID",
                    required = true,
                    example = "1",
                    in = ParameterIn.HEADER
            )
            @RequestHeader("X-User-Id") final Long memberId) {

        List<Long> matchingList = reminderCalendarService.getReminderCalendar(memberId, date);
        if (matchingList == null || matchingList.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(matchingList);
    }
} 