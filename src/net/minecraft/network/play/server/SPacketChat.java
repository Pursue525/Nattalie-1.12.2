package net.minecraft.network.play.server;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;

import java.io.IOException;

@Getter
public class SPacketChat implements Packet<INetHandlerPlayClient>
{
    @Setter
    private ITextComponent chatComponent;
    private ChatType type;

    public SPacketChat()
    {
    }

    public SPacketChat(ITextComponent componentIn)
    {
        this(componentIn, ChatType.SYSTEM);
    }

    public SPacketChat(ITextComponent p_i47428_1_, ChatType p_i47428_2_)
    {
        this.chatComponent = p_i47428_1_;
        this.type = p_i47428_2_;
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer buf) throws IOException
    {
        this.chatComponent = buf.readTextComponent();
        this.type = ChatType.func_192582_a(buf.readByte());
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer buf) throws IOException
    {
        buf.writeTextComponent(this.chatComponent);
        buf.writeByte(this.type.func_192583_a());
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandlerPlayClient handler)
    {
        handler.handleChat(this);
    }

    public ITextComponent getChatComponent()
    {
        return this.chatComponent;
    }

    /**
     * This method returns true if the type is SYSTEM or ABOVE_HOTBAR, and false if CHAT
     */
    public boolean isSystem()
    {
        return this.type == ChatType.SYSTEM || this.type == ChatType.GAME_INFO;
    }

    public ChatType func_192590_c()
    {
        return this.type;
    }
}
