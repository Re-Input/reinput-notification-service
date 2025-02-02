package info.reinput.reinput_notification_service.notification.batch;

import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.TimeZone;

@Configuration
public class QuartzConfig {
    
    @Bean
    public JobDetail reminderJobDetail() {
        return JobBuilder.newJob(ReminderBatchJob.class)
                .withIdentity("reminderJob")
                .storeDurably()
                .build();
    }
    
    @Bean
    public Trigger reminderJobTrigger(JobDetail reminderJobDetail) {
        // 매일 아침 6시 (Asia/Seoul 기준)
        return TriggerBuilder.newTrigger()
                .forJob(reminderJobDetail)
                .withIdentity("reminderTrigger")
                .withSchedule(CronScheduleBuilder.dailyAtHourAndMinute(6, 0)
                        .inTimeZone(TimeZone.getTimeZone("Asia/Seoul")))
                .build();
    }

    /*
* 문제상황: Timezone을 Asia/Seoul로 설정하였으나, 아래 로그에서는 2025-02-01T21:00:00.045Z 로 나오는 모습을 확인할 수 있음. 왜냐? 서버가 UTC를 쓰니까. 그럼 저 inTimeZone 설정이 무용지물이 되는거 아닌가?

     * 2025-02-01T21:00:00.045Z  INFO 1 --- [reinput-notification-service] [eduler_Worker-1] i.r.r.n.batch.ReminderBatchJob           : ReminderBatchJob 시작
2025-02-01T21:00:00.056Z  INFO 1 --- [reinput-notification-service] [eduler_Worker-1] i.r.r.n.batch.ReminderBatchJob           : 오늘의 날짜: 2025-02-01
2025-02-01T21:00:00.057Z  INFO 1 --- [reinput-notification-service] [eduler_Worker-1] i.r.r.n.batch.ReminderBatchJob           : 오늘의 Monthly 타입: Monthly_1
2025-02-01T21:00:00.057Z  INFO 1 --- [reinput-notification-service] [eduler_Worker-1] i.r.r.n.batch.ReminderBatchJob           : 오늘의 Weekly 타입: Weekly_Sat
Hibernate: 
    select
        rs1_0.id,
        rs1_0.created_at,
        rs1_0.reminder_id,
        rs1_0.reminder_type 
    from
        reminder_schedule rs1_0
2025-02-01T21:00:00.304Z  INFO 1 --- [reinput-notification-service] [eduler_Worker-1] i.r.r.n.batch.ReminderBatchJob           : ReminderSchedule 레코드 총 0개 조회됨.
2025-02-01T21:00:00.304Z  INFO 1 --- [reinput-notification-service] [eduler_Worker-1] i.r.r.n.batch.ReminderBatchJob           : 오늘 리마인드 대상 reminder id 개수: 0
Hibernate: 
    update
        reminder r1_0 
    set
        is_today=0
2025-02-01T21:00:00.533Z  INFO 1 --- [reinput-notification-service] [eduler_Worker-1] i.r.r.n.batch.ReminderBatchJob           : 모든 reminder의 isToday 플래그를 false로 초기화함.
2025-02-01T21:00:00.533Z  INFO 1 --- [reinput-notification-service] [eduler_Worker-1] i.r.r.n.batch.ReminderBatchJob           : 업데이트할 reminder가 없습니다.
2025-02-01T21:00:00.533Z  INFO 1 --- [reinput-notification-service] [eduler_Worker-1] i.r.r.n.batch.ReminderBatchJob           : ReminderBatchJob 작업 완료됨.
     */
}