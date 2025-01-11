package net.pursue.event.player;

import lombok.Getter;
import net.pursue.event.Event;

@Getter
public class EventSlot extends Event {
    private final int slot;

    public EventSlot(int slot) {
        this.slot = slot;
    }
}
