package net.pursue.utils.player;

import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CPacketCustomPayload;
import net.pursue.utils.client.UtilsManager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;


public class PacketUtils extends UtilsManager {

    public static void sendPacketNoEvent(Packet<?> packet) {
        mc.player.connection.sendPacketNoEvent(packet);
    }

    public static void send(Packet<?> packet) {
        mc.player.connection.sendPacket(packet);
    }

    public static boolean isCPacket(Packet<?> packet) {
        return EnumConnectionState.PLAY.getPacketDirection(packet) == EnumPacketDirection.SERVERBOUND;
    }

    public static void sendToServer(String channelName, PacketBuffer buffer) {
        Minecraft.getMinecraft().getConnection().getNetworkManager().sendPacket(new CPacketCustomPayload(channelName, buffer));
    }

    public static boolean isSPacket(Packet<?> packet) {
        return !isCPacket(packet);
    }


    public static PacketType getPacketType(Packet<?> packet) {
        String className = packet.getClass().getSimpleName();
        if (className.toUpperCase().startsWith("C")) {
            return PacketType.CLIENTSIDE;
        } else if (className.toUpperCase().startsWith("S")) {
            return PacketType.SERVERSIDE;
        }
        return PacketType.UNKNOWN;
    }

    public enum PacketType {
        SERVERSIDE,
        CLIENTSIDE,
        UNKNOWN
    }
}

