package info.reinput.reinput_notification_service.notification.application.impl;

import info.reinput.reinput_notification_service.notification.application.ReminderService;
import info.reinput.reinput_notification_service.notification.domain.Reminder;
import info.reinput.reinput_notification_service.notification.domain.ReminderSchedule;
import info.reinput.reinput_notification_service.notification.infra.ReminderRepository;
import info.reinput.reinput_notification_service.notification.infra.ReminderScheduleRepository;
import info.reinput.reinput_notification_service.notification.presentation.dto.req.ReminderCreateReq;
import info.reinput.reinput_notification_service.notification.presentation.dto.res.ReminderCreateRes;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import info.reinput.reinput_notification_service.notification.exception.InvalidReminderRequestException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReminderServiceImpl implements ReminderService {
    private final ReminderRepository reminderRepository;
    private final ReminderScheduleRepository reminderScheduleRepository;

    @Override
    @Transactional
    public ReminderCreateRes createReminder(ReminderCreateReq request) {
        // 수정 또는 생성: 동일 insightId가 존재하는 경우 기존 리마인더 및 관련 schedule을 삭제
        Optional<Reminder> existingReminder = reminderRepository.findByInsightId(request.getInsightId());
        if (existingReminder.isPresent()) {
            Reminder reminderToDelete = existingReminder.get();
            // 관련 ReminderSchedule 모두 삭제
            reminderScheduleRepository.deleteAllByReminder(reminderToDelete);
            // 기존 Reminder 삭제
            reminderRepository.delete(reminderToDelete);
        }

        // isActive와 types 유효성 검증
        validateReminderRequest(request);
        
        Reminder reminder = Reminder.builder()
                .insightId(request.getInsightId())
                .isActive(request.isActive())
                .build();
        
        reminderRepository.save(reminder);

        if (request.isActive()) {
            request.getTypes().forEach(type -> {
                ReminderSchedule schedule = ReminderSchedule.builder()
                        .reminder(reminder)
                        .reminderType(type)
                        .build();
                reminderScheduleRepository.save(schedule);
            });
        }

        return ReminderCreateRes.builder()
                .reminderId(reminder.getId())
                .insightId(reminder.getInsightId())
                .isActive(reminder.isActive())
                .build();
    }

    private void validateReminderRequest(ReminderCreateReq request) {
        if (request.isActive()) {
            if (request.getTypes() == null || request.getTypes().isEmpty()) {
                throw new InvalidReminderRequestException("활성화된 리마인더는 반드시 알림 타입을 포함해야 합니다.");
            }
        } else {
            if (request.getTypes() != null && !request.getTypes().isEmpty()) {
                throw new InvalidReminderRequestException("비활성화된 리마인더는 알림 타입을 포함할 수 없습니다.");
            }
        }
    }
} 