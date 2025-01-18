package net.pursue.event.update;


import lombok.Getter;
import lombok.Setter;
import net.pursue.event.Event;

@Getter
@Setter
public class EventMotion extends Event {
    private final Type type;
    private float rotationYaw;
    private float rotationPitch;

    public EventMotion(Type type, float yaw, float pitch) {
        this.type = type;
        this.rotationYaw = yaw;
        this.rotationPitch = pitch;
    }

    public EventMotion(Type type) {
        this.type = type;
    }

    public enum Type {
        Pre,
        Post
    }
}
