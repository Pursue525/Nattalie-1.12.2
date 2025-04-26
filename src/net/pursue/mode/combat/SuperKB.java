package net.pursue.mode.combat;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.pursue.event.EventTarget;
import net.pursue.event.player.EventAttack;
import net.pursue.mode.Mode;
import net.pursue.mode.player.Scaffold;
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
    public void onTick(EventAttack eventAttack) {
        setSuffix(modeValue.getValue().name());


        Entity entity = eventAttack.getTarget();

        if (!(entity instanceof EntityLivingBase livingBase) || Scaffold.INSTANCE.isEnable()) return;

        switch (modeValue.getValue()) {
            case Normal -> {
                if (livingBase.hurtTime == 10) {
                    mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SPRINTING));
                    mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SPRINTING));
                    mc.player.serverSprintState = true;
                }
            }
            case Legit -> {
                if (livingBase.hurtTime == 10 && mc.player.hurtTime <= 0) {
                    if (mc.player.serverSprintState) {
                        mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SPRINTING));
                    }

                    mc.player.setSprinting(true);

                    if (mc.player.serverSprintState) {
                        mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SPRINTING));
                    }
                }
            }
        }
    }
}
