package info.reinput.reinput_notification_service.notification.application;

import java.time.LocalDate;
import java.util.List;

public interface ReminderCalendarService {
    List<Long> getReminderCalendar(Long memberId, LocalDate date);
} 