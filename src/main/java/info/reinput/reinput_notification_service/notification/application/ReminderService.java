package info.reinput.reinput_notification_service.notification.application;

import info.reinput.reinput_notification_service.notification.presentation.dto.req.ReminderCreateReq;
import info.reinput.reinput_notification_service.notification.presentation.dto.res.ReminderCreateRes;

public interface ReminderService {
    ReminderCreateRes createReminder(ReminderCreateReq request);
} 