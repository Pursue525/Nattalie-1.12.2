package net.pursue.ui.notification;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.pursue.mode.hud.Notification;

import java.util.ArrayList;
import java.util.Iterator;


public class NotificationManager {

    private final ArrayList<NotificationRender> notifications = new ArrayList<>();

    public void post(String title, String message, long time, NotificationType type) {
        if (Notification.INSTANCE.isEnable()) {
            if (title != null && message != null) {
                this.notifications.add(new NotificationRender(title, message, time, type));
            }
        }
    }

    public void stopNoti() {
        if (Notification.INSTANCE.isEnable()) {
            this.notifications.clear();
        }
    }


    public void drawNotifications() {
        if (Notification.INSTANCE.isEnable()) {
            ScaledResolution res = new ScaledResolution(Minecraft.getMinecraft());
            double startY = res.getScaledHeight() - 24;
            Iterator<NotificationRender> iterator = notifications.iterator();

            while (iterator.hasNext()) {
                NotificationRender not = iterator.next();
                if (not.shouldDelete()) {
                    iterator.remove();
                } else {
                    not.draw(startY);
                    startY -= not.getHeight() + 8;
                }
            }
        }
    }
}
