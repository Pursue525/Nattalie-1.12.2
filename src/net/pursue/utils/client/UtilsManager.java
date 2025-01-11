package net.pursue.utils.client;

import net.minecraft.client.Minecraft;
import net.pursue.event.EventManager;

public class UtilsManager {
    public static final Minecraft mc = Minecraft.getMinecraft();

    public UtilsManager() {
        EventManager.instance.register(this);
    }
}
