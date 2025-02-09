package info.reinput.reinput_notification_service.notification.infra;

import info.reinput.reinput_notification_service.notification.domain.ReminderSchedule;
import info.reinput.reinput_notification_service.notification.domain.Reminder;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReminderScheduleRepository extends JpaRepository<ReminderSchedule, Long> {
    void deleteAllByReminder(Reminder reminder);

    List<ReminderSchedule> findByReminder(Reminder reminder);
} 