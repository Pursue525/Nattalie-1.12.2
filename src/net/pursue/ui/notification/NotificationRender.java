package net.pursue.ui.notification;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextFormatting;
import net.pursue.Nattalie;
import net.pursue.mode.hud.Notification;
import net.pursue.ui.font.FontManager;
import net.pursue.ui.font.RapeMasterFontManager;
import net.pursue.utils.TimerUtils;
import net.pursue.utils.render.AnimationUtils;
import net.pursue.utils.render.RoundedUtils;


import java.awt.*;

import static org.lwjgl.opengl.GL11.*;


public class NotificationRender {
    private String title;
    private String message;
    private TimerUtils timer;
    private float posY;
    private double width;
    private double height;
    private double animationX;

    private long stayTime;
    private NotificationType type;
    private final RapeMasterFontManager font = FontManager.font16;

    public NotificationRender(final String title, final String message, long time, final NotificationType type) {
        this.title = title;
        this.message = message;
        (this.timer = new TimerUtils()).reset();

        this.width = font.getStringWidth(message) + 20;
        this.height = 24.0D;
        this.animationX = this.width;
        this.stayTime = time;
        this.posY = -1.0f;
        this.type = type;
    }

    public void draw(final double getY) {

        float w = 0;
        float animationx = (float) -this.width;
        this.width = font.getStringWidth(this.message) + 38;
        this.height = 26;
        this.animationX = AnimationUtils.smooth(this.isFinished() ? this.width : 0.0D, animationX, 8f / Minecraft.getDebugFPS());
        if (this.posY == -1.0D) {
            this.posY = (float) getY;
        } else {
            this.posY = (float) AnimationUtils.smooth(getY, posY, 8f / Minecraft.getDebugFPS());
        }
        int x1 = 830;
        int y1 = (int) (this.posY - height);


        animationx = (float) AnimationUtils.smooth(this.isFinished() ? 0.0D : -this.width, animationX, 8f / Minecraft.getDebugFPS());

        RoundedUtils.drawRound((float) x1 + animationx, (float) y1, font.getStringWidth(this.message) + 100, (float) this.height, 2, new Color(0, 0, 0, 130));

        FontManager.Noti42.drawString(type.getIcon(), (int) ((double) x1 + animationx + (this.height - (double) 16) / 2.0D) - 2, y1 + (int) ((this.height - (double) 16) / 2.0D) + 1, Notification.INSTANCE.colorValue.getColorRGB());

        ++y1;
        long time = stayTime / 10 - timer.getTimePassed() / 10;
        FontManager.font20.drawStringWithShadow(TextFormatting.BOLD + this.title, x1 + 26 + animationx, y1 + this.height / 4.0D - 4, Notification.INSTANCE.colorValue.getColorRGB());
        font.drawStringWithShadow(this.message, x1 + animationx + 25 + 1, y1 + this.height / 4.0D + 10, Notification.INSTANCE.colorValue.getColorRGB());

        if (stayTime < 4000) {
            w = (10f * time) / 40f;
        } else if (stayTime > 5000) {
            w = time / 40f;
        }

        RoundedUtils.drawRound((float) (x1 + 26) + font.getStringWidth(this.message) + 5 + animationx, (float) (y1 + this.height / 4.0D + 10), w, font.getHeight() - 5, 0, Notification.INSTANCE.colorValue.getColor());
    }

    public boolean shouldDelete() {
        return this.isFinished() && this.animationX >= this.width;
    }

    private boolean isFinished() {
        return this.timer.hasTimePassed(this.stayTime);
    }

    public double getHeight() {
        return this.height;
    }
}
