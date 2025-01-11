package net.pursue.mode.world;

import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketConfirmTransaction;
import net.minecraft.network.play.server.SPacketConfirmTransaction;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.pursue.event.EventTarget;
import net.pursue.event.packet.EventPacket;
import net.pursue.event.update.EventUpdate;
import net.pursue.utils.category.Category;
import net.pursue.mode.Mode;
import net.pursue.utils.player.MovementUtils;
import net.pursue.utils.player.PacketUtils;
import net.pursue.utils.client.DebugHelper;
import net.pursue.value.values.BooleanValue;
import net.pursue.value.values.NumberValue;

import java.util.LinkedList;

public class Timer extends Mode {

    public static Timer instance;

    public final NumberValue<Number> speed = new NumberValue<>(this, "Speed", 2.0f,0.0f,10.0f,0.1f);
    private final BooleanValue<Boolean> grim = new BooleanValue<>(this, "GrimBypass", false);

    public Timer() {
        super("Timer", "时间管理器", "设置游戏时间", Category.WORLD);
        instance = this;
    }

    private final LinkedList<Packet<NetHandlerPlayClient>> inBus = new LinkedList<>();
    private int tick = 0;

    @Override
    public void disable() {
        mc.timer.timerSpeed = 1.0f;

        while (!inBus.isEmpty()) {
            inBus.poll().processPacket(mc.getConnection());
        }
    }

    @EventTarget
    private void onUpdate(EventUpdate eventUpdate) {
        if (!grim.getValue()) {
            mc.timer.timerSpeed = speed.getValue().floatValue();
        } else {
            if (tick % 10 == 0) {
                DebugHelper.sendMessage(tick);
            }

            if (MovementUtils.isMoving()) {
                if (tick >= 1) {
                    mc.timer.timerSpeed = speed.getValue().floatValue();
                    tick -= (int) speed.getValue().floatValue();
                } else {
                    if (mc.timer.timerSpeed != 1.0f) {
                        setEnable(false);
                    }
                }
            }
        }
    }

    @EventTarget
    private void onPacket(EventPacket eventPacket) {
        Packet<?> packet = eventPacket.getPacket();

        if (mc.player == null) return;

        if (packet instanceof SPacketConfirmTransaction) {
            inBus.add((Packet<NetHandlerPlayClient>) packet);
            eventPacket.cancelEvent();
            PacketUtils.send(new CPacketConfirmTransaction(0, (short) 0,true));
            tick++;
        }

        if (packet instanceof SPacketEntityVelocity velocity && velocity.getEntityID() == mc.player.getEntityId()) {
            inBus.add((Packet<NetHandlerPlayClient>) packet);
            eventPacket.cancelEvent();
        }
    }
}
