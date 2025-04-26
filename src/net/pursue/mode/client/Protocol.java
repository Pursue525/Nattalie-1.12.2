package net.pursue.mode.client;

import net.pursue.mode.Mode;
import net.pursue.utils.Germ.GermManager;
import net.pursue.utils.Germ.forge.ForgeChannel;
import net.pursue.utils.Germ.vexview.VexViewWrapper;
import net.pursue.utils.category.Category;


public class Protocol extends Mode {

    public Protocol() {
        super("HYTProtocol", "花雨庭协议", "曹寺花雨庭！！！", Category.CLIENT);
    }

    public static final GermManager germModPacket = new GermManager();
    public static final VexViewWrapper vexViewWrapper = new VexViewWrapper();
    public static final ForgeChannel forgeChannel = new ForgeChannel();
}
