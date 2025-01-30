package info.reinput.reinput_notification_service.notification.infra;

import info.reinput.reinput_notification_service.notification.domain.ReminderSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReminderScheduleRepository extends JpaRepository<ReminderSchedule, Long> {
} 