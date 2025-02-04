package net.pursue.mode.misc;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.Packet;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.play.client.CPacketClickWindow;
import net.pursue.event.EventTarget;
import net.pursue.event.packet.EventPacket;
import net.pursue.utils.category.Category;
import net.pursue.mode.Mode;
import net.pursue.utils.client.DebugHelper;
import net.pursue.value.Value;
import net.pursue.value.exploit.PacketBooleanValue;
import net.pursue.value.values.BooleanValue;
import org.apache.logging.log4j.LogManager;

import java.util.ArrayList;
import java.util.List;

public class PacketManager extends Mode {

    public static final List<PacketBooleanValue<Boolean>> packetBooleanValues = new ArrayList<PacketBooleanValue<Boolean>>();

    public PacketManager() {
        super("PacketManager", "发包管理器", "管理所有的发包，以及服务器的", Category.MISC);
        for (Value<?> value : packetBooleanValues) {
            this.addValues(value);
        }
    }

    @EventTarget
    private void onPacket(EventPacket event) {
        for (PacketBooleanValue<Boolean> value : packetBooleanValues) {
            if (event.getPacket().getClass().getSimpleName().equals(value.getPacket().getSimpleName())) {
                if (value.getValue()) {
                    event.cancelEvent();
                }

                if (value.getValue2()) {
                    DebugHelper.sendPacketMessage(event.getPacket());
                }
            }
        }
    }

    public static void registerPacket(EnumPacketDirection direction, Class <? extends Packet<? >> packetClass)
    {
        packetBooleanValues.add(new PacketBooleanValue<>(packetClass, false, false));
    }
}
