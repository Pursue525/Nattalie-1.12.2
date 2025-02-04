package net.pursue.mode.combat;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.pursue.event.EventTarget;
import net.pursue.event.packet.EventPacket;
import net.pursue.event.player.EventStrafe;
import net.pursue.event.update.EventMotion;
import net.pursue.event.update.EventTick;
import net.pursue.event.update.EventUpdate;
import net.pursue.utils.category.Category;
import net.pursue.mode.Mode;
import net.pursue.mode.player.Scaffold;
import net.pursue.utils.client.DebugHelper;
import net.pursue.utils.player.PacketUtils;
import net.pursue.utils.rotation.RotationUtils;
import net.pursue.value.values.BooleanValue;
import net.pursue.value.values.ModeValue;
import net.pursue.value.values.NumberValue;

public class AntiKnockBack extends Mode {

    private final ModeValue<mode> modeValue = new ModeValue<>(this, "Mode", mode.values(), mode.Normal);

    enum mode {
        GrimReduce,
        Normal,
        Intave,
        Jump
    }

    private final NumberValue<Number> packets = new NumberValue<>(this, "Packets",8,1,10,1, () -> modeValue.getValue().equals(mode.GrimReduce));

    private final BooleanValue<Boolean> bjdFix = new BooleanValue<>(this, "Heyixel", false, () -> modeValue.getValue().equals(mode.GrimReduce));

    private final ModeValue<jump> jumpModeValue = new ModeValue<>(this, "JumpMode", jump.values(), jump.Jump, () -> modeValue.getValue().equals(mode.Jump));

    enum jump {
        Motion,
        Jump,
        Both
    }

    private final BooleanValue<Boolean> jumpReductionValue = new BooleanValue<>(this, "ExtraReduction", false, () -> modeValue.getValue().equals(mode.Jump));

    private final NumberValue<Number> jumpReductionAmountValue = new NumberValue<>(this, "ExtraReductionAmount",1.0,0.1,1,0.1, () -> modeValue.getValue().equals(mode.Jump) && jumpReductionValue.getValue());

    private final NumberValue<Number> motionValue = new NumberValue<>(this, "Motion",0.42,0.4,0.5,0.01, () -> modeValue.getValue().equals(mode.Jump) && !jumpModeValue.getValue().equals(jump.Jump));

    private final BooleanValue<Boolean> failValue = new BooleanValue<>(this, "SmartFail", false, () -> modeValue.getValue().equals(mode.Jump));

    private final NumberValue<Number> failRateValue = new NumberValue<>(this, "FailRate",0.3,0.0,1.0,0.1, () -> modeValue.getValue().equals(mode.Jump) && failValue.getValue());

    private final NumberValue<Number> failJumpValue = new NumberValue<>(this, "FailJumpRate",0.25,0.0,1.0,0.01, () -> modeValue.getValue().equals(mode.Jump) && failValue.getValue());


    public AntiKnockBack() {
        super("Velocity", "反击退", "抵消你受到的击退", Category.COMBAT);
    }

    private boolean doJump = true;
    private boolean failJump = false;
    private boolean skipVeloc = false;

    private double motion;
    private boolean reduce;

    private int jumped = 0;

    @EventTarget
    private void onUpdate(EventUpdate event) {

        switch (modeValue.getValue()) {
            case Jump -> {
                if ((failJump || mc.player.hurtTime > 7) && mc.player.onGround) {
                    if (failJump) {
                        failJump = false;
                    }
                    if (!doJump) {
                        skipVeloc = true;
                    }
                    if (Math.random() <= failRateValue.getValue().doubleValue() && failValue.getValue()) {
                        if (Math.random() <= failJumpValue.getValue().doubleValue()) {
                            doJump = true;
                            failJump = true;
                        } else {
                            doJump = false;
                            failJump = false;
                        }
                    } else {
                        doJump = true;
                        failJump = false;
                    }
                    if (skipVeloc) {
                        skipVeloc = false;
                        return;
                    }

                    switch (jumpModeValue.getValue()) {
                        case Jump -> mc.player.jump();
                        case Motion -> mc.player.motionY = motionValue.getValue().doubleValue();
                        case Both -> {
                            mc.player.jump();
                            mc.player.motionY = motionValue.getValue().doubleValue();
                        }
                    }
                }
            }
            case GrimReduce -> {
                if (mc.player.hurtTime == 0 && reduce) {
                    reduce = false;
                }
            }
            case Intave -> {
            }
        }
    }

    @EventTarget
    private void onTick(EventTick tick) {
        if (modeValue.getValue().equals(mode.GrimReduce)) {
            RayTraceResult rayTraceResult = mc.objectMouseOver;

            EntityLivingBase entityLivingBase = KillAura.INSTANCE.target != null && KillAura.INSTANCE.isEnable() && !Scaffold.INSTANCE.isEnable() ? KillAura.INSTANCE.target : rayTraceResult != null ? (EntityLivingBase) rayTraceResult.entityHit : null;

            if (reduce && entityLivingBase != null) {

                boolean noSprint = false;

                if (!mc.player.serverSprintState) {
                    mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SPRINTING));
                    noSprint = true;
                }

                for (int i = 1; i < packets.getValue().intValue(); i++) {
                    mc.playerController.attackEntity(mc.player, entityLivingBase);
                    mc.player.connection.sendPacket(new CPacketAnimation(EnumHand.MAIN_HAND));
                }

                mc.player.motionX *= motion;
                mc.player.motionZ *= motion;

                if (noSprint) {
                    mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SPRINTING));
                }

                reduce = false;
            }
        }
    }

    @EventTarget
    public void onPacket(EventPacket event) {
        Packet<?> packet = event.getPacket();

        if (mc.player == null) return;

        if (packet instanceof SPacketEntityVelocity velocity && velocity.getEntityID() == mc.player.getEntityId()) {

            switch ((mode) modeValue.getValue()) {
                case GrimReduce: {
                    double strength = new Vec3d(
                            velocity.getMotionX(),
                            velocity.getMotionY(),
                            velocity.getMotionZ()
                    ).lengthVector();

                    if (strength >= 20000.0) {
                        if (mc.player.onGround) {
                            motion = 0.05425;
                        } else {
                            motion = 0.065;
                        }
                    } else if (strength >= 5000.0) {
                        if (mc.player.onGround) {
                            motion = 0.01625;
                        } else {
                            motion = 0.0452;
                        }
                    } else {
                        motion = 0.0075;
                    }
                    reduce = true;
                    break;
                }
                case Normal: {
                    event.cancelEvent();
                    mc.player.setVelocity(mc.player.motionX,mc.player.motionY,mc.player.motionZ);
                    break;
                }

                case Jump: {
                    if (jumpReductionValue.getValue()) {
                        velocity.setMotionX((int) (velocity.getMotionX() * jumpReductionAmountValue.getValue().doubleValue()));
                        velocity.setMotionZ((int) (velocity.getMotionX() * jumpReductionAmountValue.getValue().doubleValue()));
                    }
                    break;
                }
                case Intave: {
                    if (mc.player.hurtTime == 9) {
                        if (++jumped % 2 == 0 && mc.player.onGround && mc.player.isSprinting() && mc.currentScreen == null) {
                            KeyBinding.setKeyBindState(mc.gameSettings.keyBindJump.getKeyCode(), true);
                            jumped = 0;
                        }
                    } else {
                        KeyBinding.setKeyBindState(mc.gameSettings.keyBindJump.getKeyCode(), GameSettings.isKeyDown(mc.gameSettings.keyBindJump));
                    }
                }
            }
        }
    }
}