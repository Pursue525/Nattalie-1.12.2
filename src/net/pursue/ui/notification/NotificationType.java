package net.pursue.ui.notification;

import lombok.Getter;

@Getter
public enum NotificationType {
    SUCCESS("a"),
    INFO("C"),
    WARNING("D"),
    ERROR("B");

    private final String icon;

    NotificationType(String icon) {
        this.icon = icon;
    }

}