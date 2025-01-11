package net.pursue.event.render;

import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.pursue.event.Event;


@Getter
public class EventRender2D extends Event {
    private final float partialTicks;
    public EventRender2D(float partialTicks) {
        this.partialTicks = partialTicks;
    }

    public ScaledResolution getScaledResolution() {
        return new ScaledResolution(Minecraft.getMinecraft());
    }
}

