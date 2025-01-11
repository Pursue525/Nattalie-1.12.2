package net.pursue.event.player;

import lombok.Getter;
import net.pursue.event.Event;


@Getter
public class EventPlace extends Event {
    private int slot;

    public EventPlace(int slot) {
        this.slot = slot;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }
}
