package net.pursue.mode.player;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityEgg;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.util.math.Vec3d;
import net.pursue.Nattalie;
import net.pursue.event.EventTarget;
import net.pursue.event.packet.EventPacket;
import net.pursue.event.update.EventUpdate;
import net.pursue.event.world.EventWorldLoad;
import net.pursue.mode.Mode;
import net.pursue.mode.combat.KillAura;
import net.pursue.ui.notification.NotificationType;
import net.pursue.utils.TimerUtils;
import net.pursue.utils.category.Category;
import net.pursue.utils.friend.FriendManager;
import net.pursue.utils.player.FakePlayer;
import net.pursue.utils.player.PacketUtils;
import net.pursue.value.values.ColorValue;
import net.pursue.value.values.ModeValue;

import java.awt.*;
import java.util.LinkedList;

public class Blink extends Mode {

    public static Blink instance;

    private final ModeValue<mode> modeValue = new ModeValue<>(this, "Mode", mode.values(), mode.Normal);

    enum mode {
        Spartan,
        Normal
    }

    public final ColorValue<Color> colorValue = new ColorValue<>(this, "color", Color.WHITE);

    public Blink() {
        super("Blink", "瞬移", "暂停所有移动发包后本体留原地，关闭后回到当前位置", Category.PLAYER);
        instance = this;
    }

    private final LinkedList<Packet<?>> packets = new LinkedList<Packet<?>>();
    private final TimerUtils timerUtils = new TimerUtils();
    public static FakePlayer fakePlayer;
    public static boolean pollPacketIng; // render
    private boolean c0f;

    public int c0fs;
    private Vec3d c03Pos;


    @Override
    public void enable() {
        packets.clear();
        c0fs = 0;
        pollPacketIng = false;
        if (Blink.mc.world != null && Blink.mc.player != null) {
            timerUtils.reset();
            fakePlayer = new FakePlayer(mc.player);
            fakePlayer.setSneaking(mc.player.isSneaking());
            fakePlayer.setSprinting(mc.player.isSprinting());
            c03Pos = new Vec3d(mc.player.posX, mc.player.posY, mc.player.posZ);
        }
    }

    @Override
    public void disable() {
        if (mc.player != null && mc.world != null) {

            pollPacketIng = true;

            while (!packets.isEmpty()) sendPacket(packets.poll());

            try {
                if (fakePlayer != null)
                    mc.world.removeEntity(fakePlayer);
            } catch (Exception ignored) {
            }
        }
    }

    @EventTarget
    public void onWorld(EventWorldLoad event) {
        this.setEnable(false);
    }

    @EventTarget
    private void onPacket(EventPacket event) {
        if (mc.player == null) {
            this.setEnable(false);
            return;
        }

        if (PacketUtils.isCPacket(event.getPacket())) {

            if (event.getPacket() instanceof CPacketConfirmTransaction) {
                c0fs++;
            }

            this.packets.add(event.getPacket());
            event.cancelEvent();
        }

        if (event.getPacket() instanceof SPacketEntityVelocity velocity && velocity.getEntityID() == mc.player.getEntityId()) {
            if (modeValue.getValue().equals(mode.Spartan)) {
                setEnable(false);
            }
        }
    }

    @EventTarget
    private void onUpdate(EventUpdate update) {
        setSuffix(String.valueOf(packets.size()));

        if (fakePlayer != null) {
            for (Entity player : mc.world.loadedEntityList) {
                if (player != null && player != mc.player && player != fakePlayer && !player.isDead) {

                    if (player instanceof EntityPlayer || player instanceof EntityEgg || player instanceof EntitySnowball || player instanceof EntityArrow) {

                        if (player instanceof EntityPlayer && FriendManager.isFriend(player) || FriendManager.isBot(player)) continue;

                        if (fakePlayer.getDistance(player) < 6.0) {
                            while (!packets.isEmpty()) {
                                Packet<?> packet = packets.poll();

                                sendPacket(packet);

                                if (fakePlayer.getDistance(player) > 6.0) break;
                            }

                            timerUtils.reset();
                        }
                    }
                }
            }
        }

        if (modeValue.getValue().equals(mode.Spartan)) {

            if (c0fs > 100) c0f = true;

            if (c0f) {
                while (!packets.isEmpty()) {
                    Packet<?> packet = packets.poll();

                    sendPacket(packet);

                    if (c0fs < 50) {
                        c0f = false;
                        break;
                    }
                }
            }
        }

        if (KillAura.INSTANCE.target != null) {
            while (!packets.isEmpty()) {
                Packet<?> packet = packets.poll();

                if (KillAura.INSTANCE.target.getDistance(c03Pos) > 6.0) {
                    if (!(packet instanceof CPacketUseEntity || packet instanceof CPacketAnimation)) {
                        sendPacket(packet);
                    }
                } else {
                    sendPacket(packet);
                }
            }

            timerUtils.reset();
        }

        if (timerUtils.hasTimePassed(9500)) {
            Nattalie.instance.getNotificationManager().post(this.getName(), "你差点被Kick", 2000, NotificationType.INFO);

            while (!packets.isEmpty()) sendPacket(packets.poll());

            timerUtils.reset();
        }

        if (modeValue.getValue().equals(mode.Spartan)) {

            if (fakePlayer != null && !packets.isEmpty()) {
                if (!fakePlayer.onGround) {
                    while (!packets.isEmpty()) {
                        sendPacket(packets.poll());

                        if (fakePlayer.onGround) break;
                    }

                    timerUtils.reset();
                }
            }
        }
    }

    public void sendPacket(Packet<?> packet) {
        if (packet instanceof CPacketConfirmTransaction) c0fs--;

        mc.player.connection.sendPacketNoEvent(packet);
        handleFakePlayerPacket(packet);
    }

    private void handleFakePlayerPacket(Packet<?> packet) {
        if (packet instanceof CPacketPlayer.Position position) {
            fakePlayer.setPositionAndRotationDirect(
                    position.getX(0D),
                    position.getY(0D),
                    position.getZ(0D),
                    fakePlayer.rotationYaw,
                    fakePlayer.rotationPitch,
                    3, true
            );
            c03Pos = new Vec3d(position.getX(0D), position.getY(0D), position.getZ(0D));
            fakePlayer.onGround = position.isOnGround();
        } else if (packet instanceof CPacketPlayer.Rotation rotation) {
            fakePlayer.setPositionAndRotationDirect(
                    fakePlayer.posX,
                    fakePlayer.posY,
                    fakePlayer.posZ,
                    rotation.getYaw(0F),
                    rotation.getPitch(0F),
                    3,
                    true
            );
            fakePlayer.onGround = rotation.isOnGround();

            fakePlayer.rotationYawHead = rotation.getYaw(0F);
            fakePlayer.rotationYaw = rotation.getYaw(0F);
            fakePlayer.rotationPitch = rotation.getPitch(0F);
        } else if (packet instanceof CPacketPlayer.PositionRotation positionRotation) {
            fakePlayer.setPositionAndRotationDirect(
                    positionRotation.getX(0D),
                    positionRotation.getY(0D),
                    positionRotation.getZ(0D),
                    positionRotation.getYaw(0F),
                    positionRotation.getPitch(0F),
                    3,
                    true
            );
            fakePlayer.onGround = positionRotation.isOnGround();

            c03Pos = new Vec3d(positionRotation.getX(0D), positionRotation.getY(0D), positionRotation.getZ(0D));
            fakePlayer.rotationYawHead = positionRotation.getYaw(0F);
            fakePlayer.rotationYaw = positionRotation.getYaw(0F);
            fakePlayer.rotationPitch = positionRotation.getPitch(0F);
        } else if (packet instanceof CPacketEntityAction action) {
            if (action.getAction() == CPacketEntityAction.Action.START_SPRINTING) {
                fakePlayer.setSprinting(true);
            } else if (action.getAction() == CPacketEntityAction.Action.STOP_SPRINTING) {
                fakePlayer.setSprinting(false);
            } else if (action.getAction() == CPacketEntityAction.Action.START_SNEAKING) {
                fakePlayer.setSneaking(true);
            } else if (action.getAction() == CPacketEntityAction.Action.STOP_SNEAKING) {
                fakePlayer.setSneaking(false);
            }
        } else if (packet instanceof CPacketAnimation animation) {
            fakePlayer.swingArm(animation.getHand());
        }
    }

    public long getTime() {
        return this.timerUtils.getTimePassed();
    }

    public int getPackets() {
        return packets.isEmpty() ? 0 : packets.size();
    }
}
