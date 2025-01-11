package net.pursue.event.world;


import lombok.Getter;
import net.pursue.event.Event;


@Getter
public class EventChat extends Event {
    private final String message;

    public EventChat(String message) {
        this.message = message;
    }

}

