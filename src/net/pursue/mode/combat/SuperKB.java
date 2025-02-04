package net.pursue.mode.combat;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.util.math.RayTraceResult;
import net.pursue.event.EventTarget;
import net.pursue.event.update.EventTick;
import net.pursue.mode.Mode;
import net.pursue.utils.category.Category;
import net.pursue.value.values.ModeValue;

public class SuperKB extends Mode {

    private final ModeValue<mode> modeValue = new ModeValue<>(this, "Mode", mode.values(), mode.Normal);

    enum mode {
        Normal,
        Legit
    }

    public SuperKB() {
        super("SuperKB", "超级击退", "自动让你打出最大击退", Category.COMBAT);
    }

    @EventTarget
    public void onTick(EventTick eventTick) {
        EntityLivingBase entity = getTarget();

        if (entity == null) return;

        switch (modeValue.getValue()) {
            case Normal -> {
                if (entity.hurtTime == 10) {
                    mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SPRINTING));
                    mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SPRINTING));
                    mc.player.serverSprintState = true;
                }
            }
            case Legit -> {
                if (entity.hurtTime == 10) {
                    if (mc.player.isSprinting()) {
                        mc.player.setSprinting(false);
                    }
                    mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SPRINTING));
                    mc.player.serverSprintState = true;
                }
            }
        }
    }

    private EntityLivingBase getTarget() {
        RayTraceResult rayTraceResult = mc.objectMouseOver;

        if (rayTraceResult != null && rayTraceResult.typeOfHit == RayTraceResult.Type.ENTITY) {
            return (EntityLivingBase) rayTraceResult.entityHit;
        }
        return KillAura.INSTANCE.isEnable() ? KillAura.INSTANCE.target : null;
    }
}
