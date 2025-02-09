package info.reinput.reinput_notification_service.notification.presentation.dto.res;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import info.reinput.reinput_notification_service.notification.domain.ReminderType;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ReminderDetailRes {
    private Long reminderId;
    private Long insightId;
    
    @JsonProperty("isActive")
    private boolean isActive;
    
    // isActive가 true인 경우에만 포함, 그렇지 않으면 null/빈 리스트
    private List<ReminderType> types;
    
    @JsonGetter("isActive")
    public boolean isActive() {
        return isActive;
    }
} 