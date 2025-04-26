package net.pursue.mode.combat;

import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.potion.Potion;
import net.minecraft.util.math.RayTraceResult;
import net.pursue.event.EventTarget;
import net.pursue.event.packet.EventPacket;
import net.pursue.event.player.EventAttack;
import net.pursue.event.player.EventTickMotion;
import net.pursue.event.update.EventMotion;
import net.pursue.mode.Mode;
import net.pursue.mode.player.AutoHeal;
import net.pursue.utils.category.Category;
import net.pursue.utils.player.PacketUtils;
import net.pursue.value.values.ModeValue;
import net.pursue.value.values.NumberValue;

public class Criticals extends Mode {

    private final ModeValue<mode> modeValue = new ModeValue<>(this, "Mode", mode.values(), mode.Jump);

    enum mode {
        Packet,
        SlowStuck,
        Jump,
    }

    private final NumberValue<Number> tick = new NumberValue<>(this, "StuckTick", 3,1,20,1);

    public Criticals() {
        super("Criticals", "刀刀暴击", "让你打出完美暴击伤害", Category.COMBAT);
    }

    private final double[] offsets = new double[]{0.0625, 0};
    private Entity entity;

    private boolean stuck;

    @EventTarget
    private void onAttack(EventAttack attack) {
        if (attack.getType() == EventAttack.Type.Pre) {
            entity = attack.getTarget();

            if (modeValue.getValue().equals(mode.Packet)) {
                for (final double offset : offsets) {
                    PacketUtils.send(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + offset, mc.player.posZ, false));
                }

                mc.player.onCriticalHit(attack.getTarget());
            }
        }
    }

    @EventTarget
    private void onMotion(EventMotion motion) {
        if (modeValue.getValue().equals(mode.SlowStuck)) {
            if (mc.player.fallDistance > 0 && mc.player.offGroundTicks > 2 && entity != null) {
                stuck = true;
            }

            if (motion.getType() == EventMotion.Type.Pre) {
                if (KillAura.INSTANCE.isEnable() && KillAura.INSTANCE.target == null && stuck) {
                    stuck = false;
                }

                if (!KillAura.INSTANCE.isEnable() || KillAura.INSTANCE.target == null) {
                    if (mc.objectMouseOver == null || mc.objectMouseOver.typeOfHit != RayTraceResult.Type.ENTITY || mc.objectMouseOver.entityHit.isDead || mc.objectMouseOver.entityHit.getDistance(mc.player) > 2.9) {
                        stuck = false;
                    }
                }

                if (stuck && !(mc.player.fallDistance > 0 && !mc.player.onGround && !mc.player.isOnLadder() && !mc.player.isInWater() && !mc.player.isPotionActive(Potion.getPotionById(15)) && mc.player.getRidingEntity() == null)) {
                    stuck = false;
                }
            }
        }
    }

    @EventTarget
    private void onTickMotion(EventTickMotion tickMotion) {
        if (AutoHeal.instance.isEnable() && AutoHeal.instance.modeValue.getValue().equals(AutoHeal.mode.Golden_Apple)) return;


        if (modeValue.getValue().equals(mode.SlowStuck)) {
            if (stuck) {
                tickMotion.setTick(tick.getValue().intValue());
                tickMotion.cancelEvent();
            }
        }
    }

    @EventTarget
    private void onPacket(EventPacket packet) {
        if (mc.player == null) return;

        if (modeValue.getValue().equals(mode.Jump)) {
            if (packet.getPacket() instanceof CPacketUseEntity useEntity) {
                if (mc.player.fallDistance > 0 && !mc.player.onGround && !mc.player.isOnLadder() && !mc.player.isInWater() && !mc.player.isPotionActive(Potion.getPotionById(15)) && mc.player.getRidingEntity() == null) {
                    mc.player.onCriticalHit(useEntity.getEntityFromWorld(mc.world));
                } else {
                    packet.cancelEvent();

                    if (mc.player.onGround && !mc.gameSettings.keyBindJump.isKeyDown()) mc.player.jump();
                }
            }
        }
    }
}
