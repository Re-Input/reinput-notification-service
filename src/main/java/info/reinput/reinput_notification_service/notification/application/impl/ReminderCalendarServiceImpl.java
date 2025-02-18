package info.reinput.reinput_notification_service.notification.application.impl;

import info.reinput.reinput_notification_service.notification.application.ReminderCalendarService;
import info.reinput.reinput_notification_service.notification.client.ContentServiceClient;
import info.reinput.reinput_notification_service.notification.domain.Reminder;
import info.reinput.reinput_notification_service.notification.domain.ReminderSchedule;
import info.reinput.reinput_notification_service.notification.domain.ReminderType;
import info.reinput.reinput_notification_service.notification.infra.ReminderRepository;
import info.reinput.reinput_notification_service.notification.infra.ReminderScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ReminderCalendarServiceImpl implements ReminderCalendarService {

    private final ContentServiceClient contentServiceClient;
    private final ReminderRepository reminderRepository;
    private final ReminderScheduleRepository reminderScheduleRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Long> getReminderCalendar(Long memberId, LocalDate date) {
        // 1. content-service를 통해 해당 member의 모든 인사이트 ID 조회
        List<Long> allInsightIds = contentServiceClient.getInsightIdsByMemberId(memberId);
        if (allInsightIds == null || allInsightIds.isEmpty()) {
            return Collections.emptyList();
        }
        // 2. 인사이트 ID 리스트에 해당하며 active 상태인 Reminder 조회
        List<Reminder> activeReminders = reminderRepository.findByInsightIdInAndIsActiveTrue(allInsightIds);
        if (activeReminders.isEmpty()) {
            return Collections.emptyList();
        }
        // 3. active Reminder들에 대한 모든 ReminderSchedule 조회
        List<ReminderSchedule> schedules = reminderScheduleRepository.findByReminderIn(activeReminders);
        // 4. 입력된 날짜를 기반으로 Monthly 및 Weekly 타입 도출
        String monthlyType = "Monthly_" + date.getDayOfMonth();
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        String weeklyType = "Weekly_" + dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
        // 5. 해당 조건에 맞는 reminder의 insightId 추출
        Set<Long> matchingInsightIds = new HashSet<>();
        for (ReminderSchedule schedule : schedules) {
            String scheduleType = schedule.getReminderType().name();
            if (scheduleType.equals(monthlyType) || scheduleType.equals(weeklyType)) {
                matchingInsightIds.add(schedule.getReminder().getInsightId());
            }
            // Recommended 타입인 경우, 생성일과 입력 날짜와의 차이가 1, 7, 30인 경우 추가
            if (schedule.getReminderType() == ReminderType.Recommended) {
                LocalDate createdDate = schedule.getCreatedAt().toLocalDate();
                long daysDiff = ChronoUnit.DAYS.between(createdDate, date);
                if (daysDiff == 1 || daysDiff == 7 || daysDiff == 30) {
                    matchingInsightIds.add(schedule.getReminder().getInsightId());
                }
            }
        }
        return new ArrayList<>(matchingInsightIds);
    }
} 