package net.pursue.utils.Germ.forge;

import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SPacketCustomPayload;

public class ForgeChannel {
    public FMLHandshakeClientState currentState;

    public void processForge(SPacketCustomPayload payload) {
        final PacketBuffer buffer = payload.getBufferData();
        this.currentState.accept(buffer.readByte(), buffer, s -> {
            this.currentState = s;
        });
    }
}
