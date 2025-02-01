package info.reinput.reinput_notification_service.notification.domain;

import lombok.Getter;

@Getter
public enum ReminderType {
    // Monthly types (매월 1일 ~ 31일)
    Monthly_1("0 0 0 1 * ?"),
    Monthly_2("0 0 0 2 * ?"),
    Monthly_3("0 0 0 3 * ?"),
    Monthly_4("0 0 0 4 * ?"),
    Monthly_5("0 0 0 5 * ?"),
    Monthly_6("0 0 0 6 * ?"),
    Monthly_7("0 0 0 7 * ?"),
    Monthly_8("0 0 0 8 * ?"),
    Monthly_9("0 0 0 9 * ?"),
    Monthly_10("0 0 0 10 * ?"),
    Monthly_11("0 0 0 11 * ?"),
    Monthly_12("0 0 0 12 * ?"),
    Monthly_13("0 0 0 13 * ?"),
    Monthly_14("0 0 0 14 * ?"),
    Monthly_15("0 0 0 15 * ?"),
    Monthly_16("0 0 0 16 * ?"),
    Monthly_17("0 0 0 17 * ?"),
    Monthly_18("0 0 0 18 * ?"),
    Monthly_19("0 0 0 19 * ?"),
    Monthly_20("0 0 0 20 * ?"),
    Monthly_21("0 0 0 21 * ?"),
    Monthly_22("0 0 0 22 * ?"),
    Monthly_23("0 0 0 23 * ?"),
    Monthly_24("0 0 0 24 * ?"),
    Monthly_25("0 0 0 25 * ?"),
    Monthly_26("0 0 0 26 * ?"),
    Monthly_27("0 0 0 27 * ?"),
    Monthly_28("0 0 0 28 * ?"),
    Monthly_29("0 0 0 29 * ?"),
    Monthly_30("0 0 0 30 * ?"),
    Monthly_31("0 0 0 31 * ?"),

    // Weekly types (매주 월~일)
    Weekly_Mon("0 0 0 ? * Mon"),
    Weekly_Tue("0 0 0 ? * Tue"),
    Weekly_Wed("0 0 0 ? * Wed"),
    Weekly_Thu("0 0 0 ? * Thu"),
    Weekly_Fri("0 0 0 ? * Fri"),
    Weekly_Sat("0 0 0 ? * Sat"),
    Weekly_Sun("0 0 0 ? * Sun"),

    // Recommended type (망각곡선)
    Recommended("0 0 0 * * ?");  // 매일 실행되며, 로직에서 날짜 차이 계산

    private final String cronExpression;

    ReminderType(String cronExpression) {
        this.cronExpression = cronExpression;
    }
} 