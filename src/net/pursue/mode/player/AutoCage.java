package net.pursue.mode.player;

import net.minecraft.block.BlockGlass;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.pursue.event.EventTarget;
import net.pursue.event.packet.EventPacket;
import net.pursue.event.update.EventTick;
import net.pursue.event.update.EventUpdate;
import net.pursue.mode.Mode;
import net.pursue.utils.category.Category;
import net.pursue.value.values.ModeValue;

public class AutoCage extends Mode {

    private final ModeValue<mode> modeValue = new ModeValue<>(this, "Mode", mode.values(), mode.Heyixel);

    enum mode {
        HuaYuTing,
        Heyixel
    }

    public AutoCage() {
        super("AutoCage", "自动出笼子", "提前从笼子当中提前出来", Category.PLAYER);
    }

    private int i;
    private boolean timer;

    @Override
    public void enable() {
        i = 0;
        timer = false;
    }

    @Override
    public void disable() {
        Blink.instance.setEnable(false);
    }

    @EventTarget
    private void onUpdate(EventUpdate e) {
        if (mc.world.getBlockState(mc.player.getPos(1)).getBlock() instanceof BlockGlass) {
            switch (modeValue.getValue()) {
                case HuaYuTing -> {
                    if (!Blink.instance.isEnable()) Blink.instance.setEnable(true);

                    setBlockAir(mc.player.getPos(1));
                }
                case Heyixel -> {
                    if (!Blink.instance.isEnable()) Blink.instance.setEnable(true);

                    KeyBinding[] keyBindings = {mc.gameSettings.keyBindForward, mc.gameSettings.keyBindBack, mc.gameSettings.keyBindLeft, mc.gameSettings.keyBindRight, mc.gameSettings.keyBindJump, mc.gameSettings.keyBindSprint};

                    for (KeyBinding keyBinding : keyBindings) {
                        KeyBinding.setKeyBindState(keyBinding.getKeyCode(), false);
                    }

                    mc.player.motionX = 0;
                    mc.player.motionZ = 0;

                    setBlockAir(mc.player.getPos(1));
                }
            }
        }
    }

    @EventTarget
    private void onTick(EventTick eventTick) {
        if (timer) {
            i++;
        }

        if (i >= 23) {
            setEnable(false);
        }
    }

    @EventTarget
    private void onPacket(EventPacket eventPacket) {
        if (eventPacket.getPacket() instanceof SPacketChat s02PacketChat) {

            switch (modeValue.getValue()) {
                case HuaYuTing -> {
                    if (s02PacketChat.getChatComponent().getUnformattedText().contains("开始倒计时: 1 秒")) {
                        timer = true;
                    }
                }

                case Heyixel -> {
                    if (s02PacketChat.getChatComponent().getUnformattedText().contains("开始战斗！")) setEnable(false);
                }
            }
        }
    }

    private void setBlockAir(BlockPos pos) {
        mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, pos, EnumFacing.DOWN));
        mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, pos, EnumFacing.DOWN));
        mc.world.setBlockToAir(pos);
    }
}
