package info.reinput.reinput_notification_service.notification.presentation.dto.res;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReminderCreateRes {
    private Long reminderId;
    private Long insightId;
    
    @JsonProperty("isActive")
    private boolean isActive;

    @JsonGetter("isActive")
    public boolean isActive() {
        return isActive;
    }
} 