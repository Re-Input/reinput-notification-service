package info.reinput.reinput_notification_service.notification.presentation.dto.req;

import info.reinput.reinput_notification_service.notification.domain.ReminderType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonGetter;

import java.util.List;

@Getter
@NoArgsConstructor
public class ReminderCreateReq {
    @NotNull
    private Long insightId;
    @NotNull
    @JsonProperty("isActive")
    private boolean isActive;
    
    private List<ReminderType> types;

    @JsonGetter("isActive")
    public boolean isActive() {
        return isActive;
    }
} 