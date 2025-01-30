package info.reinput.reinput_notification_service.notification.infra;

import info.reinput.reinput_notification_service.notification.domain.Reminder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReminderRepository extends JpaRepository<Reminder, Long> {
    boolean existsByInsightId(Long insightId);
} 