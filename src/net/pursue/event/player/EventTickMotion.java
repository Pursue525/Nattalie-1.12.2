package net.pursue.event.player;

import lombok.Getter;
import lombok.Setter;
import net.pursue.event.Event;

@Getter
@Setter
public class EventTickMotion extends Event {

    private int tick;

    public EventTickMotion(int tick) {
        this.tick = tick;
    }
}
