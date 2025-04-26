package net.pursue.command.commands;

import net.minecraft.util.text.TextFormatting;
import net.pursue.Nattalie;
import net.pursue.command.Command;
import net.pursue.mode.Mode;
import net.pursue.utils.client.DebugHelper;


public class Toggle
extends Command {

    public Toggle() {
        super("Toggle");
    }

    @Override
    public void execute(String[] args) {
        boolean found = false;

        Mode m = Nattalie.instance.getModeManager().getByName(args[0].replaceAll(" ",""));

        if (m != null) {
            m.setEnable(!m.isEnable());
            found = true;
            if (m.isEnable()) {
                DebugHelper.sendMessage(m.getModeName() + TextFormatting.GRAY + " 是" + TextFormatting.GREEN + " 打开的");
            } else {
                DebugHelper.sendMessage(m.getModeName() + TextFormatting.GRAY + " 是" + TextFormatting.RED + " 关闭的");
            }
        }
        if (!found) {
            DebugHelper.sendMessage("模块：" + TextFormatting.RED + args[0] + TextFormatting.GRAY + " 无效！");
        }
    }
}

