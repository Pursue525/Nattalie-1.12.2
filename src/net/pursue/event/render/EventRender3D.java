package net.pursue.event.render;

import lombok.Getter;
import net.pursue.event.Event;

@Getter
public class EventRender3D extends Event {
    private final float partialTicks;

    public EventRender3D(float partialTicks) {
        this.partialTicks = partialTicks;
    }
}

