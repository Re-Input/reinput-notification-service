package info.reinput.reinput_notification_service.notification.presentation;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import info.reinput.reinput_notification_service.notification.client.ContentServiceClient;
import info.reinput.reinput_notification_service.notification.domain.Reminder;
import info.reinput.reinput_notification_service.notification.domain.ReminderSchedule;
import info.reinput.reinput_notification_service.notification.domain.ReminderType;
import info.reinput.reinput_notification_service.notification.infra.ReminderRepository;
import info.reinput.reinput_notification_service.notification.infra.ReminderScheduleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.IntStream;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class CalendarReminderIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ReminderRepository reminderRepository;

    @Autowired
    private ReminderScheduleRepository reminderScheduleRepository;

    @MockitoBean
    private ContentServiceClient contentServiceClient;

    // 테스트용 회원 ID
    private final Long memberId = 1000L;

    @BeforeEach
    public void setup() {
        // 기존 데이터 삭제 (테스트 간 간섭 최소화)
        reminderScheduleRepository.deleteAll();
        reminderRepository.deleteAll();

        // ContentServiceClient 모킹: 테스트 회원의 인사이트 리스트: 1~10
        List<Long> insightIds = IntStream.rangeClosed(1, 10)
                .mapToObj(i -> Long.valueOf(i))
                .collect(Collectors.toList());
        when(contentServiceClient.getInsightIdsByMemberId(memberId)).thenReturn(insightIds);

        // -------------------------------
        // 테스트용 리마인더 및 스케줄 생성
        // -------------------------------
        // Reminder1 (insightId = 1): 월별 리마인더 -> [Monthly_12, Monthly_31]
        Reminder reminder1 = reminderRepository.save(Reminder.builder()
                .insightId(1L)
                .isActive(true)
                .build());
        reminderScheduleRepository.save(ReminderSchedule.builder()
                .reminder(reminder1)
                .reminderType(ReminderType.Monthly_12)
                .build());
        reminderScheduleRepository.save(ReminderSchedule.builder()
                .reminder(reminder1)
                .reminderType(ReminderType.Monthly_31)
                .build());

        // Reminder2 (insightId = 2): 주별 리마인더 -> [Weekly_Mon, Weekly_Fri]
        Reminder reminder2 = reminderRepository.save(Reminder.builder()
                .insightId(2L)
                .isActive(true)
                .build());
        reminderScheduleRepository.save(ReminderSchedule.builder()
                .reminder(reminder2)
                .reminderType(ReminderType.Weekly_Mon)
                .build());
        reminderScheduleRepository.save(ReminderSchedule.builder()
                .reminder(reminder2)
                .reminderType(ReminderType.Weekly_Fri)
                .build());

        // Reminder3 (insightId = 3): Recommended -> 단일 Recommended, 생성일: 2025-01-01
        Reminder reminder3 = reminderRepository.save(Reminder.builder()
                .insightId(3L)
                .isActive(true)
                .build());
        ReminderSchedule schedule3 = reminderScheduleRepository.save(ReminderSchedule.builder()
                .reminder(reminder3)
                .reminderType(ReminderType.Recommended)
                .build());
        setCreatedAt(schedule3, LocalDate.of(2025, 1, 1).atStartOfDay());

        // Reminder4 (insightId = 4): 월별 리마인더 -> [Monthly_5]
        Reminder reminder4 = reminderRepository.save(Reminder.builder()
                .insightId(4L)
                .isActive(true)
                .build());
        reminderScheduleRepository.save(ReminderSchedule.builder()
                .reminder(reminder4)
                .reminderType(ReminderType.Monthly_5)
                .build());

        // Reminder5 (insightId = 5): 주별 리마인더 -> [Weekly_Wed, Weekly_Sat]
        Reminder reminder5 = reminderRepository.save(Reminder.builder()
                .insightId(5L)
                .isActive(true)
                .build());
        reminderScheduleRepository.save(ReminderSchedule.builder()
                .reminder(reminder5)
                .reminderType(ReminderType.Weekly_Wed)
                .build());
        reminderScheduleRepository.save(ReminderSchedule.builder()
                .reminder(reminder5)
                .reminderType(ReminderType.Weekly_Sat)
                .build());

        // Reminder6 (insightId = 6): Recommended -> 생성일: 2025-01-10
        Reminder reminder6 = reminderRepository.save(Reminder.builder()
                .insightId(6L)
                .isActive(true)
                .build());
        ReminderSchedule schedule6 = reminderScheduleRepository.save(ReminderSchedule.builder()
                .reminder(reminder6)
                .reminderType(ReminderType.Recommended)
                .build());
        setCreatedAt(schedule6, LocalDate.of(2025, 1, 10).atStartOfDay());

        // Reminder7 (insightId = 7): 월별 리마인더 -> [Monthly_1]
        Reminder reminder7 = reminderRepository.save(Reminder.builder()
                .insightId(7L)
                .isActive(true)
                .build());
        reminderScheduleRepository.save(ReminderSchedule.builder()
                .reminder(reminder7)
                .reminderType(ReminderType.Monthly_1)
                .build());

        // Reminder8 (insightId = 8): 주별 리마인더 -> [Weekly_Tue, Weekly_Thu]
        Reminder reminder8 = reminderRepository.save(Reminder.builder()
                .insightId(8L)
                .isActive(true)
                .build());
        reminderScheduleRepository.save(ReminderSchedule.builder()
                .reminder(reminder8)
                .reminderType(ReminderType.Weekly_Tue)
                .build());
        reminderScheduleRepository.save(ReminderSchedule.builder()
                .reminder(reminder8)
                .reminderType(ReminderType.Weekly_Thu)
                .build());

        // Reminder9 (insightId = 9): Recommended -> 생성일: 2025-01-20
        Reminder reminder9 = reminderRepository.save(Reminder.builder()
                .insightId(9L)
                .isActive(true)
                .build());
        ReminderSchedule schedule9 = reminderScheduleRepository.save(ReminderSchedule.builder()
                .reminder(reminder9)
                .reminderType(ReminderType.Recommended)
                .build());
        setCreatedAt(schedule9, LocalDate.of(2025, 1, 20).atStartOfDay());

        // Reminder10 (insightId = 10): 월별 리마인더 -> [Monthly_15, Monthly_20]
        Reminder reminder10 = reminderRepository.save(Reminder.builder()
                .insightId(10L)
                .isActive(true)
                .build());
        reminderScheduleRepository.save(ReminderSchedule.builder()
                .reminder(reminder10)
                .reminderType(ReminderType.Monthly_15)
                .build());
        reminderScheduleRepository.save(ReminderSchedule.builder()
                .reminder(reminder10)
                .reminderType(ReminderType.Monthly_20)
                .build());
    }

    // Recommended 타입의 ReminderSchedule에 대해 생성일(createdAt)을 강제로 설정하는 헬퍼 메서드
    private void setCreatedAt(ReminderSchedule schedule, LocalDateTime dateTime) {
        ReflectionTestUtils.setField(schedule, "createdAt", dateTime);
        reminderScheduleRepository.saveAndFlush(schedule);
    }

    // 테스트 시 주어진 날짜에 대해 예상되는 인사이트 ID 셋 계산 (비즈니스 로직에 기반)
    private Set<Long> computeExpectedReminderIds(LocalDate date) {
        Set<Long> expected = new HashSet<>();
        int day = date.getDayOfMonth();
        // "Weekly_" 타입은 java.time.DayOfWeek의 단축 표기 (Mon, Tue, Wed, Thu, Fri, Sat, Sun)
        String weekShort = date.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);

        // Reminder1 (insightId 1): [Monthly_12, Monthly_31]
        if (day == 12 || day == 31) {
            expected.add(1L);
        }
        // Reminder2 (insightId 2): [Weekly_Mon, Weekly_Fri]
        if (weekShort.equals("Mon") || weekShort.equals("Fri")) {
            expected.add(2L);
        }
        // Reminder3 (insightId 3): Recommended, 생성일 2025-01-01 => diff가 1, 7, 30일이면 호출
        long diff3 = java.time.temporal.ChronoUnit.DAYS.between(LocalDate.of(2025, 1, 1), date);
        if (diff3 == 1 || diff3 == 7 || diff3 == 30) {
            expected.add(3L);
        }
        // Reminder4 (insightId 4): [Monthly_5]
        if (day == 5) {
            expected.add(4L);
        }
        // Reminder5 (insightId 5): [Weekly_Wed, Weekly_Sat]
        if (weekShort.equals("Wed") || weekShort.equals("Sat")) {
            expected.add(5L);
        }
        // Reminder6 (insightId 6): Recommended, 생성일 2025-01-10 => diff가 1, 7, 30일
        long diff6 = java.time.temporal.ChronoUnit.DAYS.between(LocalDate.of(2025, 1, 10), date);
        if (diff6 == 1 || diff6 == 7 || diff6 == 30) {
            expected.add(6L);
        }
        // Reminder7 (insightId 7): [Monthly_1]
        if (day == 1) {
            expected.add(7L);
        }
        // Reminder8 (insightId 8): [Weekly_Tue, Weekly_Thu]
        if (weekShort.equals("Tue") || weekShort.equals("Thu")) {
            expected.add(8L);
        }
        // Reminder9 (insightId 9): Recommended, 생성일 2025-01-20 => diff가 1, 7, 30일
        long diff9 = java.time.temporal.ChronoUnit.DAYS.between(LocalDate.of(2025, 1, 20), date);
        if (diff9 == 1 || diff9 == 7 || diff9 == 30) {
            expected.add(9L);
        }
        // Reminder10 (insightId 10): [Monthly_15, Monthly_20]
        if (day == 15 || day == 20) {
            expected.add(10L);
        }
        return expected;
    }

    @Test
    public void testCalendarRemindersForJanuary2025() throws Exception {
        System.out.println("==== 캘린더 날짜별 리마인더 대상 인사이트 ID 조회 테스트 시작 (2025-01-01 ~ 2025-01-31) ====");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        LocalDate startDate = LocalDate.of(2025, 1, 1);
        LocalDate endDate = LocalDate.of(2025, 1, 31);
        ObjectMapper objectMapper = new ObjectMapper();

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            String formattedDate = formatter.format(date);
            String url = "/reminder/calendar/v1?date=" + formattedDate;

            // API 호출 (헤더 "X-User-Id" 전달)
            var mvcResult = mockMvc.perform(get(url)
                            .header("X-User-Id", memberId))
                    .andReturn();
            int status = mvcResult.getResponse().getStatus();
            Set<Long> responseSet;
            if (status == 204) {
                responseSet = new HashSet<>();
            } else {
                String responseContent = mvcResult.getResponse().getContentAsString();
                List<Long> responseList = objectMapper.readValue(responseContent, new TypeReference<List<Long>>() {});
                responseSet = new HashSet<>(responseList);
            }

            // 예상 결과 산출
            Set<Long> expectedSet = computeExpectedReminderIds(date);

            // 상세 결과 로그 출력 (사람이 읽기 쉽도록)
            System.out.println("------------------------------------------------");
            System.out.println("날짜: " + formattedDate + " (" + date.getDayOfWeek() + ")");
            System.out.println("  예상 리마인더 인사이트 IDs: " + expectedSet);
            System.out.println("  실제 리마인더 인사이트 IDs: " + responseSet);
            if (!expectedSet.equals(responseSet)) {
                System.out.println("  [오류] 결과 불일치 발생!");
            }
            assertEquals(expectedSet, responseSet, "날짜 " + formattedDate + " 에 대해 예상과 실제 결과가 일치하지 않습니다.");
        }
        System.out.println("==== 캘린더 날짜별 리마인더 테스트 완료 ====");
    }
} 