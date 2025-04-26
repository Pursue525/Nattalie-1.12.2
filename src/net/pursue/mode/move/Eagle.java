package net.pursue.mode.move;

import net.minecraft.block.BlockAir;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.math.BlockPos;
import net.pursue.event.EventTarget;
import net.pursue.event.update.EventMotion;
import net.pursue.mode.Mode;
import net.pursue.utils.category.Category;

public class Eagle extends Mode {
    public Eagle() {
        super("Eagle", "蹲起", "自动在方块边缘蹲下", Category.MOVE);
    }

    @Override
    public void disable() {
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), false);
    }

    @EventTarget
    public void onMotion(final EventMotion event) {
        if (event.getType() == EventMotion.Type.Pre) {
            if (mc.world.getBlockState(new BlockPos(mc.player.posX, mc.player.posY - 1, mc.player.posZ)).getBlock() instanceof BlockAir) {
                if (mc.player.onGround) {
                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), true);
                }
            } else if (mc.player.onGround) {
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), false);
            }
        }
    }
}
