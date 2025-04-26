package net.pursue.mode.move;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.AxisAlignedBB;
import net.pursue.event.EventTarget;
import net.pursue.event.player.EventMove;
import net.pursue.event.player.EventStrafe;
import net.pursue.event.update.EventUpdate;
import net.pursue.mode.Mode;
import net.pursue.mode.player.Blink;
import net.pursue.mode.player.Scaffold;
import net.pursue.mode.world.Timer;
import net.pursue.utils.category.Category;
import net.pursue.utils.player.MovementUtils;
import net.pursue.utils.rotation.SilentRotation;
import net.pursue.value.values.ModeValue;
import net.pursue.value.values.NumberValue;

public class Speed extends Mode {

    private final ModeValue<mode> modeValue = new ModeValue<>(this, "Mode", mode.values(), mode.Entity);

    enum mode {
        Entity,
        Cote
    }

    private final NumberValue<Double> speed = new NumberValue<>(this,"Speed", 10.0, 1.0, 15.0, 1.0);

    public Speed() {
        super("Speed", "加速", "将你的移动速度增加", Category.MOVE);
    }

    @EventTarget
    private void onMove(EventMove eventMove) {
        if (Scaffold.INSTANCE.isEnable() || Timer.instance.isEnable()) return;

        if (modeValue.getValue().equals(mode.Entity)) {

        } else {
            eventMove.setSpeed(speed.getValue());
        }
    }

    @EventTarget
    private void onStrafe(EventStrafe strafe) {
        if (modeValue.getValue().equals(mode.Entity) && mc.player != null) {
            AxisAlignedBB playerBox = mc.player.boundingBox.expand(1.0, 1.0, 1.0);
            int c = 0;
            for (Entity entity : mc.world.loadedEntityList) {
                if ((Blink.fakePlayer != null && entity == Blink.fakePlayer) || !(entity instanceof EntityLivingBase) || entity.getEntityId() == mc.player.getEntityId() || !playerBox.intersectsWith(entity.boundingBox) || entity.getEntityId() == -8 || entity.getEntityId() == -1337) continue;
                ++c;
            }
            if (c > 0 && MovementUtils.isMoving()) {
                double strafeOffset = (double) Math.min(c, 3) * (speed.getValue() / 100);
                float yaw = this.getMoveYaw();
                double mx = -Math.sin(Math.toRadians(yaw));
                double mz = Math.cos(Math.toRadians(yaw));
                mc.player.addVelocity(mx * strafeOffset, 0.0, mz * strafeOffset);
            }
        }
    }

    @EventTarget
    private void onUpdate(EventUpdate eventUpdate) {
        setSuffix(modeValue.getValue().name());
    }

    private float getMoveYaw() {
        float moveYaw = SilentRotation.getRotations().x;
        if (mc.player.moveForward != 0.0f && mc.player.moveStrafing == 0.0f) moveYaw += mc.player.moveForward > 0.0f ? 0.0f : 180.0f;
        else if (mc.player.moveForward != 0.0f && mc.player.moveStrafing != 0.0f) {
            moveYaw = mc.player.moveForward > 0.0f ? (moveYaw += mc.player.moveStrafing > 0.0f ? -45.0f : 45.0f) : (moveYaw -= mc.player.moveStrafing > 0.0f ? -45.0f : 45.0f);
            moveYaw += mc.player.moveForward > 0.0f ? 0.0f : 180.0f;
        } else if (mc.player.moveStrafing != 0.0f && mc.player.moveForward == 0.0f) moveYaw += mc.player.moveStrafing > 0.0f ? -70.0f : 70.0f;
        if (mc.gameSettings.keyBindJump.isKeyDown()) moveYaw = SilentRotation.getRotations().x;
        return moveYaw;
    }
}
