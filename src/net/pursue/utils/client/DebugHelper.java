package net.pursue.utils.client;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

public class DebugHelper {
    public static void sendMessage(Object message) {
        String chatPrefix = "\2477[" + TextFormatting.AQUA + TextFormatting.BOLD + "Nattalie" + TextFormatting.RESET + "\2477] " + TextFormatting.RESET;
        Minecraft.getMinecraft().player.addChatMessage(new TextComponentString(chatPrefix + message));
    }
}
