package net.minecraft.network.play.client;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

import java.io.IOException;

public class CPacketChatMessage implements Packet<INetHandlerPlayServer>
{
    public String message;

    public CPacketChatMessage()
    {
    }

    public CPacketChatMessage(String messageIn)
    {
        if (messageIn.length() > 256)
        {
            messageIn = messageIn.substring(0, 256);
        }

        this.message = messageIn;
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer buf) throws IOException
    {
        this.message = buf.readStringFromBuffer(256);
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer buf) throws IOException
    {
        buf.writeString(this.message);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandlerPlayServer handler)
    {
        handler.processChatMessage(this);
    }

    public String getMessage()
    {
        return this.message;
    }
}
