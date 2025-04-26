package net.pursue.command.commands;

import net.minecraft.util.text.TextFormatting;
import net.pursue.Nattalie;
import net.pursue.command.Command;
import net.pursue.utils.client.DebugHelper;

public class AutoL extends Command {

    public AutoL() {
        super("AutoL");
    }

    @Override
    public void execute(String[] args) {
        if (args.length >= 1 && args.length < 3) {

            if ((args[0].contains("add") || args[0].contains("a")) && !args[1].isEmpty()) {
                Nattalie.instance.getAutoLManager().addString(args[1]);
            } else if (args[0].contains("remove") || args[0].contains("r")) {
                Nattalie.instance.getAutoLManager().removeString(Integer.parseInt(args[1]));
            } else if ((args[0].contains("list") || args[0].contains("l"))) {
                DebugHelper.sendMessage("AutoL","=======================");
                for (String string : net.pursue.mode.player.AutoL.list) {
                    DebugHelper.sendMessage("AutoL","参数值[" + TextFormatting.GREEN + string.hashCode() + TextFormatting.WHITE + "]" + "  内容[" + TextFormatting.BLUE + string + TextFormatting.WHITE + "]");
                }
                DebugHelper.sendMessage("AutoL","=======================");
            }

        } else {
            DebugHelper.sendMessage("AutoL","通过 add 或者 a 来添加内容");
            DebugHelper.sendMessage("AutoL","通过 remove 或者 r 来删除内容 (输入他的参数值)");
            DebugHelper.sendMessage("AutoL","通过 list 或者 l 可以显示所有内容包括内容参数值");
        }
    }
}
