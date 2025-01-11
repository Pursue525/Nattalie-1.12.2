package net.pursue.command;

import lombok.Getter;

@Getter
public abstract class Command {
    private final String name;

    protected Command(String name) {
        this.name = name;
    }

    public abstract void execute(String[] var1);

}
