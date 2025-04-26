package net.pursue.command.commands;

import net.minecraft.util.text.TextFormatting;
import net.pursue.command.Command;
import net.pursue.config.ConfigManager;
import net.pursue.utils.client.DebugHelper;

import java.util.ArrayList;
import java.util.List;

public class Config extends Command {

    private final List<String> string = new ArrayList<String>();

    public Config() {
        super("Config");


        string.add("reload");
        string.add("save");
        string.add("load");
    }

    @Override
    public void execute(String[] var1) {
        if (var1.length == 2) {
            for (String s : string) {
                if (var1[0].equals(s)) {
                    switch (var1[0]) {
                        case "load" -> {
                            if (ConfigManager.load(var1[1])) {
                                DebugHelper.sendMessage(TextFormatting.WHITE +"登录配置 " + TextFormatting.GREEN + var1[1] + TextFormatting.WHITE + " 成功！");
                            } else {
                                DebugHelper.sendMessage(TextFormatting.WHITE +"登录配置 " + TextFormatting.GREEN + var1[1] + TextFormatting.WHITE + " 失败！");
                            }
                        }
                        case "save" -> {
                            if (ConfigManager.save(var1[1])) {
                                DebugHelper.sendMessage(TextFormatting.WHITE +"保存配置 " + TextFormatting.GREEN + var1[1] + TextFormatting.WHITE + " 成功！");
                            } else {
                                DebugHelper.sendMessage(TextFormatting.WHITE +"保存配置 " + TextFormatting.GREEN + var1[1] + TextFormatting.WHITE + " 失败！");
                            }
                        }
                        case "reload" -> DebugHelper.sendMessage("删个jb删，自己打开文件夹删去");
                    }
                }
            }
        } else {
            DebugHelper.sendMessage("Config参数错误，有效参数仅有: [" + TextFormatting.GREEN + String.join(" | ", string) + TextFormatting.WHITE + "] + 配置名称");
        }
    }
}
