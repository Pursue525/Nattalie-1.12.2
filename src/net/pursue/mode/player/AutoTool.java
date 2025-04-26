package net.pursue.mode.player;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.pursue.event.EventTarget;
import net.pursue.event.player.EventClickBlock;
import net.pursue.mode.Mode;
import net.pursue.utils.category.Category;

public class AutoTool extends Mode {
    public AutoTool() {
        super("AutoTool", "自动工具", "自动切换到合适的工具", Category.PLAYER);
    }

    @EventTarget
    private void onClickBlock(EventClickBlock eventClickBlock) {
        switchSlot(eventClickBlock.getClickedBlock());
    }

    private void switchSlot(BlockPos blockPos) {
        float bestSpeed = 1.0F;
        int bestSlot = -1;

        IBlockState blockState = mc.world.getBlockState(blockPos);

        for (int i = 0; i <= 8; i++) {
            ItemStack item = mc.player.inventory.getStackInSlot(i);
            if (item.func_190926_b()) {
                continue;
            }
            float speed = item.getStrVsBlock(blockState);

            if (speed > bestSpeed) {
                bestSpeed = speed;
                bestSlot = i;
            }
        }

        if (bestSlot != -1) {
            mc.player.inventory.currentItem = bestSlot;
            mc.playerController.updateController();
        }
    }
}
