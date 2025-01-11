package net.pursue.event.player;


import lombok.Getter;
import lombok.Setter;
import net.pursue.event.Event;


@Setter
@Getter
public class EventSlow extends Event {
    private float moveStrafe;
    private float moveForward;

    private int sprintToggleTimer;

    public EventSlow(float MoveStrafe, float MoveForward, int SprintToggleTimer) {
        this.moveForward = MoveForward;
        this.moveStrafe = MoveStrafe;
        this.sprintToggleTimer = SprintToggleTimer;
    }

}
