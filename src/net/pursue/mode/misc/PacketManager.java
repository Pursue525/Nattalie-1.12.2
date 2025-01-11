package net.pursue.mode.misc;

import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketClickWindow;
import net.pursue.event.EventTarget;
import net.pursue.event.packet.EventPacket;
import net.pursue.utils.category.Category;
import net.pursue.mode.Mode;
import net.pursue.utils.client.DebugHelper;
import net.pursue.value.values.BooleanValue;

public class PacketManager extends Mode {

    private final BooleanValue<Boolean> c0e = new BooleanValue<>(this, "C0E", false);

    public PacketManager() {
        super("PacketManager", "发包管理器", "管理所有的发包，以及服务器的", Category.MISC);
    }

    @EventTarget
    private void onPacket(EventPacket event) {
        Packet<?> packet = event.getPacket();

        if (c0e.getValue()) {
            if (packet instanceof CPacketClickWindow window) {
                DebugHelper.sendMessage("监听到发包C0E，参值： " + window.getWindowId() + ", " + window.getSlotId() + ", " + window.getUsedButton() + ", " + window.getClickType());
            }
        }
    }
}
