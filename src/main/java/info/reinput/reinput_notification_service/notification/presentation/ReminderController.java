package info.reinput.reinput_notification_service.notification.presentation;

import info.reinput.reinput_notification_service.notification.application.ReminderService;
import info.reinput.reinput_notification_service.notification.presentation.dto.req.ReminderCreateReq;
import info.reinput.reinput_notification_service.notification.presentation.dto.res.ReminderCreateRes;
import info.reinput.reinput_notification_service.notification.presentation.dto.res.ReminderDetailRes;
import info.reinput.reinput_notification_service.global.dto.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PatchMapping;

@Tag(name = "Reminder", description = "리마인더 API")
@RestController
@RequestMapping("/reminder")
@RequiredArgsConstructor
public class ReminderController {
    private final ReminderService reminderService;

    @Operation(
            summary = "리마인더 생성 및 수정",
            description = """
                    인사이트에 대한 리마인더를 생성하거나 수정합니다.
                    기존에 동일한 Insight ID의 리마인더가 존재하는 경우, 해당 리마인더와 관련 알림 스케줄을 삭제한 후
                    새로운 리마인더로 대체합니다.
                    
                    제약사항:
                    1. isActive가 true인 경우 반드시 types가 필요하며, 비어있을 수 없습니다.
                    2. isActive가 false인 경우 types가 포함되어서는 안됩니다.
                    3. types에는 다음과 같은 ReminderType 값만 사용 가능합니다:
                       - Monthly_12: 매월 12일 알림
                       - Monthly_31: 매월 31일 알림
                       - Weekly_Mon: 매주 월요일 알림
                       - Weekly_Fri: 매주 금요일 알림
                       - Recommended: 망각곡선 기반 추천 알림
                    """,
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "매월 12일, 31일 리마인더 생성 또는 수정",
                                            value = """
                                            {
                                              "insightId": 1,
                                              "isActive": true,
                                              "types": ["Monthly_12", "Monthly_31"]
                                            }
                                            """),
                                    @ExampleObject(
                                            name = "매주 월요일, 금요일 리마인더 생성 또는 수정",
                                            value = """
                                            {
                                              "insightId": 2,
                                              "isActive": true,
                                              "types": ["Weekly_Mon", "Weekly_Fri"]
                                            }
                                            """),
                                    @ExampleObject(
                                            name = "망각곡선 리마인더 생성 또는 수정",
                                            value = """
                                            {
                                                "insightId": 3,
                                                "isActive": true,
                                                "types": ["Recommended"]
                                            }
                                            """),
                                    @ExampleObject(
                                            name = "리마인더 비활성화",
                                            value = """
                                            {
                                              "insightId": 4,
                                              "isActive": false
                                            }
                                            """),
                                    @ExampleObject(
                                            name = "400 오류: 활성화 상태에서 types 누락",
                                            value = """
                                            {
                                              "insightId": 5,
                                              "isActive": true
                                            }
                                            """),
                                    @ExampleObject(
                                            name = "400 오류: 비활성화 상태에서 types 포함",
                                            value = """
                                            {
                                              "insightId": 6,
                                              "isActive": false,
                                              "types": ["Monthly_12"]
                                            }
                                            """),
                                    @ExampleObject(
                                            name = "400 오류: 유효하지 않은 ReminderType",
                                            value = """
                                            {
                                              "insightId": 7,
                                              "isActive": true,
                                              "types": ["Invalid_Type"]
                                            }
                                            """)
                            }
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "리마인더 생성/수정 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ReminderCreateRes.class),
                                    examples = @ExampleObject(
                                            value = """
                                            {
                                              "reminderId": 1,
                                              "insightId": 1,
                                              "isActive": true
                                            }
                                            """)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = """
                                    요청이 유효하지 않은 경우:
                                    - isActive가 true인데 types가 없거나 비어있는 경우
                                    - isActive가 false인데 types가 포함된 경우
                                    - 유효하지 않은 ReminderType이 포함된 경우 (예: "Invalid_Type")
                                    """,
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    examples = {
                                        @ExampleObject(
                                            name = "활성화 상태에서 types 누락",
                                            value = """
                                            {
                                              "message": "활성화된 리마인더는 반드시 알림 타입을 포함해야 합니다.",
                                              "code": "INVALID_REMINDER_REQUEST"
                                            }
                                            """),
                                        @ExampleObject(
                                            name = "유효하지 않은 ReminderType",
                                            value = """
                                            {
                                              "message": "유효하지 않은 ReminderType입니다: Invalid_Type",
                                              "code": "INVALID_REMINDER_REQUEST"
                                            }
                                            """)
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "서버 내부 오류 (데이터베이스 오류 등 예외적인 경우)",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    examples = @ExampleObject(
                                            value = """
                                            {
                                              "message": "내부 서버 오류가 발생했습니다.",
                                              "code": "INTERNAL_SERVER_ERROR"
                                            }
                                            """)
                            )
                    )
            }
    )
    @PatchMapping("/v2")
    public ResponseEntity<ReminderCreateRes> createReminder(@RequestBody ReminderCreateReq request) {
        ReminderCreateRes response = reminderService.createReminder(request);
        return ResponseEntity.ok(response);
    }
    
    @Operation(
            summary = "리마인더 조회",
            description = "insightId를 이용하여 리마인더를 조회합니다. 활성화된 리마인더인 경우 알림 스케줄의 타입도 포함됩니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "리마인더 조회 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ReminderDetailRes.class),
                                    examples = @ExampleObject(
                                            value = """
                                            {
                                              "reminderId": 1,
                                              "insightId": 1,
                                              "isActive": true,
                                              "types": ["Monthly_12", "Monthly_31"]
                                            }
                                            """)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "리마인더를 찾을 수 없는 경우",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    examples = @ExampleObject(
                                            value = """
                                            {
                                              "message": "해당 insightId에 대한 리마인더를 찾을 수 없습니다: 1",
                                              "code": "REMINDER_NOT_FOUND"
                                            }
                                            """)
                            )
                    )
            }
    )
    @GetMapping("/v1")
    public ResponseEntity<ReminderDetailRes> getReminder(@RequestParam("insightId") Long insightId) {
        ReminderDetailRes response = reminderService.getReminderDetail(insightId);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "리마인더 삭제",
            description = "해당 insightId에 해당하는 리마인더와 연결된 리마인더 스케줄을 삭제합니다. 스케줄 삭제가 성공적인 경우에만 리마인더를 삭제합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "리마인더 삭제 성공 (No Content)"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "해당 insightId에 대한 리마인더를 찾을 수 없는 경우",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "리마인더 스케줄 삭제 실패 또는 서버 내부 오류",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    )
            }
    )
    @DeleteMapping("/v1")
    public ResponseEntity<Void> deleteReminder(@RequestParam("insightId") Long insightId) {
        reminderService.deleteReminder(insightId);
        return ResponseEntity.noContent().build();
    }
} 