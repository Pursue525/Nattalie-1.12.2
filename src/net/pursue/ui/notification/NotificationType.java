package net.pursue.ui.notification;

import lombok.Getter;

@Getter
public enum NotificationType {
    SUCCESS("Success", "a", 0),
    INFO("Info", "C", 1),
    WARNING("Warning", "D", 2),
    ERROR("Error", "B", 3);

    private final String name;
    private final String icon;
    private final int type;

    NotificationType(String name, String icon, int type) {
        this.name = name;
        this.icon = icon;
        this.type = type;
    }

}