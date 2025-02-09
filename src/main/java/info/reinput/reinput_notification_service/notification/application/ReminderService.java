package info.reinput.reinput_notification_service.notification.application;

import info.reinput.reinput_notification_service.notification.presentation.dto.req.ReminderCreateReq;
import info.reinput.reinput_notification_service.notification.presentation.dto.res.ReminderCreateRes;
import info.reinput.reinput_notification_service.notification.presentation.dto.res.ReminderDetailRes;

public interface ReminderService {
    ReminderCreateRes createReminder(ReminderCreateReq request);
    
    ReminderDetailRes getReminderDetail(Long insightId);
} 