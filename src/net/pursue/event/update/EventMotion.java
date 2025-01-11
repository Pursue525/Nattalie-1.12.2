package net.pursue.event.update;


import lombok.Getter;
import lombok.Setter;
import net.pursue.event.Event;

@Getter
@Setter
public class EventMotion extends Event {
    private final Type type;

    private boolean ground;
    private double x;
    private double z;
    private double y;
    private double lastReportedPosX;
    private double lastReportedPosY;
    private double lastReportedPosZ;

    private boolean isSprinting;
    private boolean isSneaking;

    private float yaw;
    private float pitch;

    public EventMotion(boolean Ground, double X, double y, double Z,double LastReportedPosX, double LastReportedPosY, double LastReportedPosZ, boolean isSprinting, boolean isSneaking, float yaw, float pitch) {
        this.type = Type.Pre;
        this.ground = Ground;
        this.x = X;
        this.y = y;
        this.z = Z;
        this.lastReportedPosX = LastReportedPosX;
        this.lastReportedPosY = LastReportedPosY;
        this.lastReportedPosZ = LastReportedPosZ;
        this.isSprinting = isSprinting;
        this.isSneaking = isSneaking;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public EventMotion() {
        this.type = Type.Post;
    }

    public enum Type {
        Pre,
        Post
    }
}
