package net.pursue.event.player;

import lombok.Getter;
import net.minecraft.client.gui.GuiScreen;
import net.pursue.event.Event;

@Getter
public class EventScreen extends Event {
    private final GuiScreen guiScreen;

    public EventScreen(GuiScreen gui) {
        this.guiScreen = gui;
    }

}
