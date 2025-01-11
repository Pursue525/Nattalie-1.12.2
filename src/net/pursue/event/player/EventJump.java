package net.pursue.event.player;

import lombok.Getter;
import net.pursue.event.Event;


@Getter
public class EventJump extends Event {

    private final double motionY;
    private float yaw;
    private final boolean boosting;

    public EventJump(double motionY, float rotationYaw, boolean sprinting) {
        this.motionY = motionY;
        this.yaw = rotationYaw;
        this.boosting = sprinting;
    }

    public void setYaw(float rotationYaw) {
        this.yaw = rotationYaw;
    }
}