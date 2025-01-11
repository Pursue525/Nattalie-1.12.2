package net.pursue.utils.player;

import net.minecraft.init.Blocks;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.*;
import net.pursue.utils.client.UtilsManager;

public class InvUtils extends UtilsManager {
    public static boolean isGoldenApple(ItemStack stack) {
        return stack.getItem() instanceof ItemAppleGold && !isEnchantedGoldenApple(stack);
    }

    public static boolean isEnchantedGoldenApple(ItemStack stack) {
        if (stack.getItem() instanceof ItemAppleGold goldenApple) {
            return goldenApple.hasEffect(stack);
        }
        return false;
    }

    public static void drop(int slot){
        mc.playerController.windowClick(mc.player.inventoryContainer.windowId, slot, 1, ClickType.THROW, mc.player);
    }

    public static void shiftClick(int slot) {
        mc.playerController.windowClick(mc.player.inventoryContainer.windowId, slot, 0, ClickType.QUICK_MOVE, mc.player);
    }

    public static void swap(int inventorySlot, int hotbarSlot) {
        mc.playerController.windowClick(mc.player.inventoryContainer.windowId, inventorySlot, hotbarSlot, ClickType.SWAP, mc.player);
    }

    public static int getBlockSlot() {
        for (int i = 0; i < 9; ++i) {
            if (!mc.player.inventoryContainer.getSlot(i + 36).getHasStack() || !(mc.player.inventoryContainer.getSlot(i + 36).getStack().getItem() instanceof ItemBlock) || !(mc.player.inventoryContainer.getSlot(i + 36).getStack().func_190916_E() > 0) || mc.player.inventoryContainer.getSlot(i + 36).getStack().getItem().getUnlocalizedName().equals(Blocks.LADDER.getUnlocalizedName())) continue;
            return i;
        }
        return -1;
    }

    public static int count(ContainerChest chest) {
        IInventory inv = chest.getLowerChestInventory();
        int count = 0;
        for (int i = 0; i < inv.getSizeInventory(); i++) {
            if (!inv.getStackInSlot(i).func_190926_b()) {
                count++;
            }
        }
        return count;
    }
}
