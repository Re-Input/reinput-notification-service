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
}