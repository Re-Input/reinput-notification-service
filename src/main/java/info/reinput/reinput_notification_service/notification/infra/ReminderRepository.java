package info.reinput.reinput_notification_service.notification.infra;

import info.reinput.reinput_notification_service.notification.domain.Reminder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
import java.util.List;

public interface ReminderRepository extends JpaRepository<Reminder, Long> {
    boolean existsByInsightId(Long insightId);
    
    Optional<Reminder> findByInsightId(Long insightId);
    
    // 모든 reminder의 isToday 플래그를 false로 변경
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("update Reminder r set r.isToday = false")
    void resetTodayReminder();
    
    // 특정 reminder id set에 해당하는 reminder의 isToday 플래그를 true로 변경
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("update Reminder r set r.isToday = true where r.id in :ids")
    void updateTodayReminders(@Param("ids") java.util.Set<Long> ids);
    
    // 입력된 insightId 리스트 중 활성화된(== true) reminder를 조회
    List<Reminder> findByInsightIdInAndIsActiveTrue(List<Long> insightIds);
} 