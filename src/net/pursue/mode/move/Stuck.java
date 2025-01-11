package net.pursue.mode.move;

import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemEnderPearl;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.pursue.event.EventTarget;
import net.pursue.event.packet.EventPacket;
import net.pursue.event.player.EventMove;
import net.pursue.event.player.EventTickMotion;
import net.pursue.event.update.EventUpdate;
import net.pursue.utils.category.Category;
import net.pursue.mode.Mode;
import net.pursue.utils.player.PacketUtils;
import net.pursue.utils.rotation.SilentRotation;
import net.pursue.value.values.ModeValue;

public class Stuck extends Mode {

    private final ModeValue<mode> modeValue = new ModeValue<>(this, "Mode", mode.values(), mode.Legit);

    enum mode {
        Legit,
        Normal
    }

    public Stuck() {
        super("Stuck", "卡空", "将你挂在空中", Category.MOVE);
    }

    private int i = 0;

    private double x,y,z = 0;

    @Override
    public void enable() {

        if (mc.player == null) return;

        i = 0;

        x = mc.player.motionX;
        y = mc.player.motionY;
        z = mc.player.motionZ;
    }

    @Override
    public void disable() {

        if (mc.player == null) return;

        mc.player.motionX = x;
        mc.player.motionY = y;
        mc.player.motionZ = z;
    }

    @EventTarget
    public void onMove(EventTickMotion eventTickMotion) {
        if (modeValue.getValue().equals(mode.Legit)) {
            if (mc.player.positionUpdateTicks < 20) {
                eventTickMotion.cancelEvent();
            }
        }
    }

    @EventTarget
    public void onUpdate(EventUpdate eventUpdate) {
        x = mc.player.motionX;
        y = mc.player.motionY;
        z = mc.player.motionZ;

        if (modeValue.getValue().equals(mode.Legit)) {
            if (mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemEnderPearl && mc.player.isHandActive()) {
                setEnable(false);
            }
        }
    }

    @EventTarget
    public void onMove(EventMove eventMove) {
        if (modeValue.getValue().equals(mode.Normal)) {
            eventMove.setStopMove(true);
        }
    }

    @EventTarget
    public void onPacket(EventPacket eventPacket) {
        Packet<?> packet = eventPacket.getPacket();

        if (mc.player == null) return;

        ItemStack stack = mc.player.getHeldItemMainhand();

        if (modeValue.getValue().equals(mode.Normal)) {
            if (packet instanceof CPacketPlayer) {
                eventPacket.cancelEvent();
            }

            if (packet instanceof CPacketPlayerTryUseItem) {
                if (stack.getItem() instanceof ItemEnderPearl) {
                    eventPacket.cancelEvent();
                    PacketUtils.sendPacketNoEvent(new CPacketPlayer.Rotation(mc.player.rotationYaw, mc.player.rotationPitch, mc.player.onGround));
                    PacketUtils.sendPacketNoEvent(packet);
                }
            }

            if (packet instanceof CPacketPlayerDigging) {
                if (stack.getItem() instanceof ItemBow) {
                    eventPacket.cancelEvent();
                    PacketUtils.sendPacketNoEvent(new CPacketPlayer.Rotation(mc.player.rotationYaw, mc.player.rotationPitch, mc.player.onGround));
                    PacketUtils.sendPacketNoEvent(new CPacketPlayerDigging(CPacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                }
            }

            if (packet instanceof CPacketPlayerTryUseItemOnBlock) {
                eventPacket.cancelEvent();
                PacketUtils.sendPacketNoEvent(new CPacketPlayer.Rotation(SilentRotation.getRotations().x, SilentRotation.getRotations().y, mc.player.onGround));
                PacketUtils.sendPacketNoEvent(packet);
            }

            if (packet instanceof CPacketUseEntity && i < 4) {
                eventPacket.cancelEvent();
                PacketUtils.sendPacketNoEvent(new CPacketPlayer.Rotation(SilentRotation.getRotations().x, SilentRotation.getRotations().y, mc.player.onGround));
                PacketUtils.sendPacketNoEvent(packet);
                i++;
            }
        }

        if (modeValue.getValue().equals(mode.Normal)) {
            if (packet instanceof SPacketPlayerPosLook) {
                setEnable(false);
            }
        }
    }
}
