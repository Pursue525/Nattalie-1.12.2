package net.pursue.utils.Germ.forge;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketBuffer;
import net.pursue.utils.Germ.GermManager;
import net.pursue.utils.player.PacketUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.function.Consumer;

public enum FMLHandshakeClientState implements IHandshakeState<FMLHandshakeClientState> {
    START {
        public void accept(int id, ByteBuf payload, Consumer<? super FMLHandshakeClientState> cons) {
            cons.accept(HELLO);
        }
    },
    HELLO {
        public void accept(int id, ByteBuf payload, Consumer<? super FMLHandshakeClientState> cons) {
            cons.accept(WAITINGSERVERDATA);
            PacketUtils.sendToServer("REGISTER", new PacketBuffer(Unpooled.buffer().writeBytes(GermManager.REGISTER_CHANNEL)));
            PacketBuffer helloBuffer = new PacketBuffer(Unpooled.buffer());
            helloBuffer.writeByte(1);
            helloBuffer.writeByte(2);
            PacketUtils.sendToServer("FML|HS", helloBuffer);
            PacketUtils.sendToServer("FML|HS", new PacketBuffer(Unpooled.buffer().writeBytes(GermManager.MOD_LIST)));
        }
    },
    WAITINGSERVERDATA {
        public void accept(int id, ByteBuf payload, Consumer<? super FMLHandshakeClientState> cons) {
            cons.accept(WAITINGSERVERCOMPLETE);
            PacketUtils.sendToServer("FML|HS", new PacketBuffer(Unpooled.buffer().writeByte(-1).writeByte(2)));
        }
    },
    WAITINGSERVERCOMPLETE {
        public void accept(int id, ByteBuf payload, Consumer<? super FMLHandshakeClientState> cons) {
            new HashMap();
            new HashSet();
            boolean hasMore = payload.readBoolean();
            if (hasMore) {
                cons.accept(WAITINGSERVERCOMPLETE);
            } else {
                cons.accept(PENDINGCOMPLETE);
                PacketUtils.sendToServer("FML|HS", new PacketBuffer(Unpooled.buffer().writeByte(-1).writeByte(3)));
            }
        }
    },
    PENDINGCOMPLETE {
        public void accept(int id, ByteBuf payload, Consumer<? super FMLHandshakeClientState> cons) {
            cons.accept(COMPLETE);
            PacketUtils.sendToServer("FML|HS", new PacketBuffer(Unpooled.buffer().writeByte(-1).writeByte(4)));
        }
    },
    COMPLETE {
        public void accept(int id, ByteBuf payload, Consumer<? super FMLHandshakeClientState> cons) {
            cons.accept(DONE);
            PacketUtils.sendToServer("FML|HS", new PacketBuffer(Unpooled.buffer().writeByte(-1).writeByte(5)));
        }
    },
    DONE {
        public void accept(int id, ByteBuf payload, Consumer<? super FMLHandshakeClientState> cons) {
            if (id == -2) {
                cons.accept(HELLO);
            }

        }
    },
    ERROR {
        public void accept(int id, ByteBuf payload, Consumer<? super FMLHandshakeClientState> cons) {
        }
    };

    FMLHandshakeClientState() {
    }

}
