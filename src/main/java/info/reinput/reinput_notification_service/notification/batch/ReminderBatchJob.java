package info.reinput.reinput_notification_service.notification.batch;

import info.reinput.reinput_notification_service.notification.domain.ReminderSchedule;
import info.reinput.reinput_notification_service.notification.domain.ReminderType;
import info.reinput.reinput_notification_service.notification.infra.ReminderRepository;
import info.reinput.reinput_notification_service.notification.infra.ReminderScheduleRepository;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class ReminderBatchJob implements Job {

    private static final Logger log = LoggerFactory.getLogger(ReminderBatchJob.class);

    // Quartz Job은 스프링 빈이 아니므로, JobDataMap을 통해 주입 받거나, 스프링에서 지원하는 방식 사용
    // 여기서는 생성자 주입으로 가정 (설정에 따라 QuartzJobBean을 상속하거나 JobFactory를 설정할 수 있음)
    private final ReminderRepository reminderRepository;
    private final ReminderScheduleRepository reminderScheduleRepository;

    public ReminderBatchJob(ReminderRepository reminderRepository, ReminderScheduleRepository reminderScheduleRepository) {
        this.reminderRepository = reminderRepository;
        this.reminderScheduleRepository = reminderScheduleRepository;
    }
    
    @Override
    @Transactional
    public void execute(JobExecutionContext context) throws JobExecutionException {
        log.info("ReminderBatchJob 시작");

        // 오늘 날짜 계산 (시간은 제외)
        LocalDate today = LocalDate.now();
        
        // 오늘의 Monthly 타입: 예) "Monthly_30"
        String monthlyTypeName = "Monthly_" + today.getDayOfMonth();
        
        // 오늘의 Weekly 타입: 예) "Weekly_Mon"
        // java.time.DayOfWeek에서 MONDAY를 "Mon"으로 변환
        DayOfWeek dayOfWeek = today.getDayOfWeek();
        String dayShort = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
        // displayName "Mon", "Tue", ... 유지
        String weeklyTypeName = "Weekly_" + dayShort;
        
        log.info("오늘의 날짜: {}", today);
        log.info("오늘의 Monthly 타입: {}", monthlyTypeName);
        log.info("오늘의 Weekly 타입: {}", weeklyTypeName);
        
        // todayReminders를 담을 HashSet
        Set<Long> todayReminders = new HashSet<>();
        
        List<ReminderSchedule> schedules = reminderScheduleRepository.findAll();
        log.info("ReminderSchedule 레코드 총 {}개 조회됨.", schedules.size());
        
        for (ReminderSchedule rs : schedules) {
            ReminderType type = rs.getReminderType();
            // 조건 2.a: 오늘 타입과 일치하는 경우
            if(type.name().equals(monthlyTypeName) || type.name().equals(weeklyTypeName)) {
                todayReminders.add(rs.getReminder().getId());
            }
            // 조건 2.b: Recommended 타입인 경우
            if(type == ReminderType.Recommended) {
                LocalDate createdDate = rs.getCreatedAt().toLocalDate();
                long daysDiff = java.time.temporal.ChronoUnit.DAYS.between(createdDate, today);
                if(daysDiff == 1 || daysDiff == 7 || daysDiff == 30) {
                    todayReminders.add(rs.getReminder().getId());
                }
            }
        }
        log.info("오늘 리마인드 대상 reminder id 개수: {}", todayReminders.size());
        
        // 3단계: 모든 reminder의 isToday를 false로 초기화 후, 대상 reminder의 isToday를 true로 업데이트
        reminderRepository.resetTodayReminder();
        log.info("모든 reminder의 isToday 플래그를 false로 초기화함.");
        
        if (!todayReminders.isEmpty()){
            reminderRepository.updateTodayReminders(todayReminders);
            log.info("reminder id {} 에 대해 isToday 플래그를 true로 업데이트함.", todayReminders);
        } else {
            log.info("업데이트할 reminder가 없습니다.");
        }
        
        log.info("ReminderBatchJob 작업 완료됨.");
    }
} 