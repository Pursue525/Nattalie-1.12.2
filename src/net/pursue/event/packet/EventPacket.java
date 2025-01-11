package net.pursue.event.packet;

import lombok.Getter;
import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.pursue.event.Event;



@Getter
public class EventPacket extends Event {
    private final Packet<?> packet;
    private EnumPacketDirection direction;
    private INetHandler netHandler;

    public EventPacket(Packet<?> packet, EnumPacketDirection direction, INetHandler netHandler) {
        this.packet = packet;
        this.direction = direction;
        this.netHandler = netHandler;
    }

    public EventPacket(Packet<?> packet) {
        this.packet = packet;
    }

}
