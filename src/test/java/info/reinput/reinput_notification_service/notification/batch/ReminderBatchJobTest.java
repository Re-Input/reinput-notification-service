package info.reinput.reinput_notification_service.notification.batch;

import info.reinput.reinput_notification_service.notification.domain.Reminder;
import info.reinput.reinput_notification_service.notification.domain.ReminderSchedule;
import info.reinput.reinput_notification_service.notification.domain.ReminderType;
import info.reinput.reinput_notification_service.notification.infra.ReminderRepository;
import info.reinput.reinput_notification_service.notification.infra.ReminderScheduleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReminderBatchJobTest {

    @Mock
    private ReminderRepository reminderRepository;

    @Mock
    private ReminderScheduleRepository reminderScheduleRepository;

    @Mock
    private JobExecutionContext jobExecutionContext;

    private ReminderBatchJob reminderBatchJob;

    @BeforeEach
    public void setUp() {
        reminderBatchJob = new ReminderBatchJob(reminderRepository, reminderScheduleRepository);
    }

    @Test
    public void testReminderBatchJob_AllCases() throws JobExecutionException {
        System.out.println("테스트 시작: ReminderBatchJob 모든 케이스 테스트");
        // 고정된 날짜: 2025-02-01 (토요일)
        LocalDate fixedDate = LocalDate.of(2025, 2, 1);
        System.out.println("테스트 날짜 설정: " + fixedDate);
        LocalDateTime fixedDateTime = fixedDate.atStartOfDay(); // 비교에 사용

        // 예상되는 타입
        String expectedMonthlyType = "Monthly_" + fixedDate.getDayOfMonth();  // Monthly_1
        DayOfWeek day = fixedDate.getDayOfWeek();
        String dayShort = day.getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
        String expectedWeeklyType = "Weekly_" + dayShort;                    // Weekly_Sat

        // 테스트용 Reminder와 ReminderSchedule 객체 생성 (id값은 임의로 설정)
        Reminder reminder1 = Reminder.builder().id(1L).insightId(1L).isActive(true).build(); // Monthly_1
        Reminder reminder2 = Reminder.builder().id(2L).insightId(2L).isActive(true).build(); // Weekly_Sat
        Reminder reminder3 = Reminder.builder().id(3L).insightId(3L).isActive(true).build(); // Recommended, createdAt = 어제 (차이 1일)
        Reminder reminder4 = Reminder.builder().id(4L).insightId(4L).isActive(true).build(); // Recommended, createdAt 차이 2일 (미해당)
        Reminder reminder5 = Reminder.builder().id(5L).insightId(5L).isActive(true).build(); // Monthly_5 (미일치)

        // ReminderSchedule 목록 준비
        List<ReminderSchedule> scheduleList = new ArrayList<>();
        // 1. 타입이 Monthly_10 -> 조건 2.a
        scheduleList.add(ReminderSchedule.builder()
                .id(101L)
                .reminder(reminder1)
                .reminderType(ReminderType.valueOf(expectedMonthlyType))
                .createdAt(fixedDateTime)
                .build());
        // 2. 타입이 Weekly_Tue -> 조건 2.a
        scheduleList.add(ReminderSchedule.builder()
                .id(102L)
                .reminder(reminder2)
                .reminderType(ReminderType.valueOf(expectedWeeklyType))
                .createdAt(fixedDateTime)
                .build());
        // 3. Recommended 타입, createdAt = 어제 -> 조건 2.b (차이 1일)
        scheduleList.add(ReminderSchedule.builder()
                .id(103L)
                .reminder(reminder3)
                .reminderType(ReminderType.Recommended)
                .createdAt(fixedDateTime.minusDays(1))
                .build());
        // 4. Recommended 타입, createdAt = fixedDate.minusDays(2) -> 차이가 2일, 업데이트 대상이 아님
        scheduleList.add(ReminderSchedule.builder()
                .id(104L)
                .reminder(reminder4)
                .reminderType(ReminderType.Recommended)
                .createdAt(fixedDateTime.minusDays(2))
                .build());
        // 5. Monthly_5 -> 타입 불일치 (fixedDate의 일자가 1이므로)
        scheduleList.add(ReminderSchedule.builder()
                .id(105L)
                .reminder(reminder5)
                .reminderType(ReminderType.Monthly_5)
                .createdAt(fixedDateTime)
                .build());

        // ReminderScheduleRepository에서 findAll() 호출시 위 스케줄 목록을 반환
        when(reminderScheduleRepository.findAll()).thenReturn(scheduleList);

        // static mocking을 통해 LocalDate.now() 반환값을 고정
        try (MockedStatic<LocalDate> mockedLocalDate = Mockito.mockStatic(LocalDate.class, CALLS_REAL_METHODS)) {
            mockedLocalDate.when(LocalDate::now).thenReturn(fixedDate);

            // execute() 메서드 실행
            reminderBatchJob.execute(jobExecutionContext);
        }

        // 예상 대상 reminder id: reminder1 (id=1), reminder2 (id=2), reminder3 (id=3)
        Set<Long> expectedIds = new HashSet<>();
        expectedIds.add(1L);
        expectedIds.add(2L);
        expectedIds.add(3L);

        // resetTodayReminder() 호출 Verifiy
        verify(reminderRepository, times(1)).resetTodayReminder();
        // updateTodayReminders()가 위의 expectedIds와 함께 호출되는지 확인
        verify(reminderRepository, times(1)).updateTodayReminders(expectedIds);

        System.out.println("ReminderBatchJob 실행 완료");
        System.out.println("예상되는 업데이트 대상 ID: " + expectedIds);
        System.out.println("테스트 종료");
    }
} 