package net.pursue.command.commands;

import net.minecraft.util.text.TextFormatting;
import net.pursue.Nattalie;
import net.pursue.command.Command;
import net.pursue.mode.Mode;
import net.pursue.utils.client.DebugHelper;
import org.lwjgl.input.Keyboard;

public class Bind extends Command {
    public Bind() {
        super("Bind");
    }

    @Override
    public void execute(String[] args) {
        if (args.length >= 2) {
            Mode module = Nattalie.instance.getModeManager().getByName(args[0].replaceAll(" ",""));

            if (module != null) {
                int k = Keyboard.getKeyIndex(args[1].toUpperCase());
                module.setKey(k);
                Object[] arrobject = new Object[2];
                arrobject[0] = module.getModeName();
                arrobject[1] = k == 0 ? "none" : args[1].toUpperCase();
                DebugHelper.sendMessage(String.format("绑定 " + TextFormatting.YELLOW + "%s" + TextFormatting.RESET + " 到 " + TextFormatting.GREEN + "%s", arrobject));
            } else {
                DebugHelper.sendMessage("模块：" + TextFormatting.RED + args[0] + TextFormatting.GRAY + " 无效！");
            }
        }
    }
}
