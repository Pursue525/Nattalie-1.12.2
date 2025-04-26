package net.pursue.command.commands;

import net.pursue.Nattalie;
import net.pursue.command.Command;
import net.pursue.mode.Mode;
import net.pursue.utils.client.DebugHelper;
import org.lwjgl.input.Keyboard;

import java.util.Objects;

public class Binds extends Command {
    public Binds() {
        super("Binds");
    }

    @Override
    public void execute(String[] var1) {
        for (Mode module : Nattalie.instance.getModeManager().getModes()) {
            String key = Keyboard.getKeyName(module.getKey());
            if (!Objects.equals(key, "NONE")) {
                DebugHelper.sendMessage( module.getName() + ": " + Keyboard.getKeyName(module.getKey()));
            }
        }
    }
}
