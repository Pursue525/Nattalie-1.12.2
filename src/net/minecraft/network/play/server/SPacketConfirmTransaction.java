package net.minecraft.network.play.server;

import java.io.IOException;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.pursue.Nattalie;
import net.pursue.mode.misc.Disabler;

public class SPacketConfirmTransaction implements Packet<INetHandlerPlayClient>
{
    private int windowId;
    private short actionNumber;
    private boolean accepted;

    public SPacketConfirmTransaction()
    {
    }

    public SPacketConfirmTransaction(int windowIdIn, short actionNumberIn, boolean acceptedIn)
    {
        this.windowId = windowIdIn;
        this.actionNumber = actionNumberIn;
        this.accepted = acceptedIn;
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandlerPlayClient handler)
    {
        handler.handleConfirmTransaction(this);
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer buf) throws IOException {

        if (ViaLoadingBase.getInstance().getTargetVersion().newerThanOrEqualTo(ProtocolVersion.v1_17)) {
            this.windowId = buf.readInt();
        } else {
            this.windowId = buf.readUnsignedByte();
            this.actionNumber = buf.readShort();
            this.accepted = buf.readBoolean();
        }

        if (Disabler.instance.getGrimPost() && this.actionNumber < 0) {
            Disabler.pingPackets.add((int) this.actionNumber);
        }
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer buf) throws IOException
    {
        buf.writeByte(this.windowId);
        buf.writeShort(this.actionNumber);
        buf.writeBoolean(this.accepted);
    }

    public int getWindowId()
    {
        return this.windowId;
    }

    public short getActionNumber()
    {
        return this.actionNumber;
    }

    public boolean wasAccepted()
    {
        return this.accepted;
    }
}
