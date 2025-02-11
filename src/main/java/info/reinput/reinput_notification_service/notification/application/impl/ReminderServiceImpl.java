package info.reinput.reinput_notification_service.notification.application.impl;

import info.reinput.reinput_notification_service.notification.application.ReminderService;
import info.reinput.reinput_notification_service.notification.domain.Reminder;
import info.reinput.reinput_notification_service.notification.domain.ReminderSchedule;
import info.reinput.reinput_notification_service.notification.domain.ReminderType;
import info.reinput.reinput_notification_service.notification.infra.ReminderRepository;
import info.reinput.reinput_notification_service.notification.infra.ReminderScheduleRepository;
import info.reinput.reinput_notification_service.notification.presentation.dto.req.ReminderCreateReq;
import info.reinput.reinput_notification_service.notification.presentation.dto.res.ReminderCreateRes;
import info.reinput.reinput_notification_service.notification.presentation.dto.res.ReminderDetailRes;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import info.reinput.reinput_notification_service.notification.exception.InvalidReminderRequestException;
import java.util.Optional;
import java.util.List;
import java.util.stream.Collectors;
import info.reinput.reinput_notification_service.notification.exception.ReminderNotFoundException;
import info.reinput.reinput_notification_service.notification.exception.InvalidReminderTypeException;
import info.reinput.reinput_notification_service.notification.exception.ReminderScheduleDeletionException;

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
    
    @Override
    @Transactional(readOnly = true)  // 조회만 하므로 readOnly = true 추가
    public ReminderDetailRes getReminderDetail(Long insightId) {
        Reminder reminder = reminderRepository.findByInsightId(insightId)
            .orElseThrow(() -> new ReminderNotFoundException("해당 insightId에 대한 리마인더를 찾을 수 없습니다: " + insightId));
        
        if (!reminder.isActive()) {
            // 비활성화된 리마인더는 types 없이 반환
            return ReminderDetailRes.builder()
                    .reminderId(reminder.getId())
                    .insightId(reminder.getInsightId())
                    .isActive(false)
                    .build();
        } else {
            // 활성화된 리마인더인 경우, 관련 ReminderSchedule 조회
            List<ReminderSchedule> schedules = reminderScheduleRepository.findByReminder(reminder);
            
            if (schedules.isEmpty()) {
                throw new ReminderNotFoundException("활성화된 리마인더는 최소 하나 이상의 알림 스케줄이 존재해야 합니다.");
            }
            
            List<ReminderType> types = schedules.stream()
                    .map(ReminderSchedule::getReminderType)
                    .collect(Collectors.toList());
            
            return ReminderDetailRes.builder()
                    .reminderId(reminder.getId())
                    .insightId(reminder.getInsightId())
                    .isActive(true)
                    .types(types)
                    .build();
        }
    }
    
    private void validateReminderRequest(ReminderCreateReq request) {
        if (request.isActive()) {
            if (request.getTypes() == null || request.getTypes().isEmpty()) {
                throw new InvalidReminderRequestException("활성화된 리마인더는 반드시 알림 타입을 포함해야 합니다.");
            }
            // ReminderType 유효성 검증 추가
            request.getTypes().forEach(type -> {
                try {
                    ReminderType.valueOf(type.name());
                } catch (IllegalArgumentException e) {
                    throw new InvalidReminderTypeException("유효하지 않은 ReminderType입니다: " + type.name());
                }
            });
        } else {
            if (request.getTypes() != null && !request.getTypes().isEmpty()) {
                throw new InvalidReminderRequestException("비활성화된 리마인더는 알림 타입을 포함할 수 없습니다.");
            }
        }
    }

    @Override
    @Transactional
    public void deleteReminder(Long insightId) {
        // insightId로 reminder 조회
        Reminder reminder = reminderRepository.findByInsightId(insightId)
            .orElseThrow(() -> new ReminderNotFoundException("해당 insightId에 대한 리마인더를 찾을 수 없습니다: " + insightId));
        
        // reminder에 연결된 스케줄 삭제
        List<ReminderSchedule> associatedSchedules = reminderScheduleRepository.findByReminder(reminder);
        if (!associatedSchedules.isEmpty()) {
            reminderScheduleRepository.deleteAllByReminder(reminder);
            
            // 스케줄 삭제 후 삭제 여부 확인
            List<ReminderSchedule> remainingSchedules = reminderScheduleRepository.findByReminder(reminder);
            if (!remainingSchedules.isEmpty()) {
                throw new ReminderScheduleDeletionException("리마인더 스케줄 삭제에 실패했습니다.");
            }
        }
        
        // 스케줄 삭제가 정상적으로 완료되었으면 reminder 삭제
        reminderRepository.delete(reminder);
    }
} 