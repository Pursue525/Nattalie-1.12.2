package net.pursue.event;

import lombok.Getter;

@Getter
public abstract class Event {
    private boolean cancelled;

    public void cancelEvent() {
        this.cancelled = true;
    }

    public void setCancelled(boolean state) {
        this.cancelled = state;
    }
}



