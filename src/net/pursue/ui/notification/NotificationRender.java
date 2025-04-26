package net.pursue.ui.notification;

import net.minecraft.client.Minecraft;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.pursue.mode.hud.Notification;
import net.pursue.ui.font.FontManager;
import net.pursue.ui.font.FontUtils;
import net.pursue.ui.gui.Click;
import net.pursue.ui.gui.OldClick;
import net.pursue.utils.TimerUtils;
import net.pursue.utils.render.AnimationUtils;
import net.pursue.utils.render.RoundedUtils;


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
    private final FontUtils font = FontManager.font16;

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
        float animationx = (float) -this.width;
        this.width = font.getStringWidth(this.message) + 38;
        this.height = 26;

        this.animationX = AnimationUtils.smooth(this.isFinished() ? this.width + 100 : 0.0D, animationX, 8f / Minecraft.getDebugFPS());

        if (this.posY == -1.0D) {
            this.posY = (float) getY;
        } else {
            this.posY = (float) AnimationUtils.smooth(getY, posY, 8f / Minecraft.getDebugFPS());
        }
        int x1 = 830;
        int y1 = (int) (this.posY - height);

        double maxhealthnonnull = this.stayTime;
        double healthnonnull = this.timer.getTimePassed();

        float w = Math.max(font.getStringWidth(this.title) + 42, font.getStringWidth(this.message) + 42);

        double healthPercentage = MathHelper.clamp((healthnonnull) / (maxhealthnonnull), 0, 1);

        float endWidth = (float) Math.max(0, w * healthPercentage);

        animationx = (float) AnimationUtils.smooth(this.isFinished() ? 0.0D : -this.width, animationX, 8f / Minecraft.getDebugFPS());

        if (Notification.INSTANCE.render.getValue()) {
            if (maxhealthnonnull == -1) {
                RoundedUtils.drawRound((float) x1 + animationx, (float) y1, (float) this.width / 2, (float) this.height, 2, Notification.INSTANCE.renderValue.getColor());
            }
            RoundedUtils.drawRound((float) x1 + animationx, (float) y1, (float) endWidth, (float) this.height, 2, Notification.INSTANCE.renderValue.getColor());
        }

        if (Notification.INSTANCE.blur.getValue()) {
            RoundedUtils.drawRoundBlur((float) x1 + animationx, (float) y1, w, (float) this.height, 2, Notification.INSTANCE.backValue.getColor(), Notification.INSTANCE.blurInt.getValue().intValue());
        } else {
            RoundedUtils.drawRound((float) x1 + animationx, (float) y1, w, (float) this.height, 2, Notification.INSTANCE.backValue.getColor());
        }

        FontManager.Noti42.drawString(type.getIcon(), (int) ((double) x1 + animationx + (this.height - (double) 16) / 2.0D) - 2, y1 + (int) ((this.height - (double) 16) / 2.0D) + 1, Notification.INSTANCE.colorValue.getColorRGB());

        ++y1;
        FontManager.font20.drawStringWithShadow(TextFormatting.BOLD + this.title, x1 + 26 + animationx, y1 + this.height / 4.0D - 4, Notification.INSTANCE.colorValue.getColorRGB());
        font.drawStringWithShadow(this.message, x1 + animationx + 25 + 1, y1 + this.height / 4.0D + 10, Notification.INSTANCE.colorValue.getColorRGB());

    }

    public boolean shouldDelete() {
        return this.isFinished() && this.animationX >= (this.width + 100);
    }

    private boolean isFinished() {
        if (this.stayTime == -1) {
            return !(Minecraft.getMinecraft().currentScreen instanceof Click || Minecraft.getMinecraft().currentScreen instanceof OldClick);
        }

        return this.timer.hasTimePassed(this.stayTime);
    }

    public double getHeight() {
        return this.height;
    }
}
