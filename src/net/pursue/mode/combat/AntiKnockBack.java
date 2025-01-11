package net.pursue.mode.combat;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.pursue.event.EventTarget;
import net.pursue.event.packet.EventPacket;
import net.pursue.utils.category.Category;
import net.pursue.mode.Mode;
import net.pursue.mode.player.Scaffold;
import net.pursue.value.values.BooleanValue;
import net.pursue.value.values.ModeValue;
import net.pursue.value.values.NumberValue;


public class AntiKnockBack extends Mode {

    private final ModeValue<mode> modeValue = new ModeValue<>(this, "Mode", mode.values(), mode.Normal);

    enum mode {
        GrimReduce,
        Normal
    }

    private final NumberValue<Number> packets = new NumberValue<>(this, "Packets",8,1,10,1);
    public final BooleanValue<Boolean> sendC03 = new BooleanValue<>(this, "SendC03",false, () -> modeValue.getValue().equals(mode.GrimReduce));

    public AntiKnockBack() {
        super("AntiKnockBack", "反击退", "抵消你受到的击退", Category.COMBAT);
    }

    @EventTarget
    public void onPacket(EventPacket event) {
        Packet<?> packet = event.getPacket();

        if (mc.player == null) return;

        if (packet instanceof SPacketEntityVelocity velocity && velocity.getEntityID() == mc.player.getEntityId()) {

            RayTraceResult rayTraceResult = mc.objectMouseOver;

            EntityLivingBase entityLivingBase = KillAura.INSTANCE.target != null && KillAura.INSTANCE.isEnable() && !Scaffold.INSTANCE.isEnable() ? KillAura.INSTANCE.target : rayTraceResult != null ? (EntityLivingBase) rayTraceResult.entityHit : null;

            switch ((mode) modeValue.getValue()) {
                case GrimReduce: {
                    if (entityLivingBase != null) {

                        boolean noSprint = false;
                        if (!mc.player.serverSprintState) {
                            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SPRINTING));
                            noSprint = true;
                        }

                        event.cancelEvent();

                        for (int i = 0; i < packets.getValue().intValue(); i++) {
                            mc.player.connection.sendPacket(new CPacketAnimation(EnumHand.MAIN_HAND));
                            mc.player.connection.sendPacket(new CPacketUseEntity(entityLivingBase));
                        }

                        double motion = 0.07776d;

                        mc.player.setVelocity((velocity.getMotionX() / 8000.0D) * motion, velocity.getMotionY() / 8000.0D, (velocity.getMotionZ() / 8000.0D) * motion);

                        if (noSprint) {
                            if (sendC03.getValue()) mc.player.connection.sendPacket(new CPacketPlayer(mc.player.onGround));
                            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SPRINTING));
                        }
                    }
                    break;
                }
                case Normal: {
                    event.cancelEvent();
                    mc.player.setVelocity(mc.player.motionX,mc.player.motionY,mc.player.motionZ);
                    break;
                }
            }
        }
    }
}
