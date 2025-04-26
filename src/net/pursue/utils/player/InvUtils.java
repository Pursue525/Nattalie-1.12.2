package net.pursue.utils.player;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAppleGold;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.pursue.utils.client.UtilsManager;

import java.util.Arrays;
import java.util.List;

public class InvUtils extends UtilsManager {

    public static final List<Block> offGoodBlock = Arrays.asList(Blocks.AIR, Blocks.WATER, Blocks.FLOWING_WATER, Blocks.LAVA, Blocks.WOODEN_SLAB, Blocks.WOODEN_SLAB, Blocks.CHEST, Blocks.FLOWING_LAVA,
            Blocks.ENCHANTING_TABLE, Blocks.CARPET, Blocks.GLASS_PANE, Blocks.SKULL, Blocks.STAINED_GLASS_PANE, Blocks.IRON_BARS, Blocks.SNOW_LAYER, Blocks.ICE, Blocks.PACKED_ICE,
            Blocks.COAL_ORE, Blocks.TNT, Blocks.DIAMOND_ORE, Blocks.EMERALD_ORE, Blocks.CHEST, Blocks.TRAPPED_CHEST, Blocks.TORCH, Blocks.ANVIL, Blocks.TRAPPED_CHEST,
            Blocks.NOTEBLOCK, Blocks.JUKEBOX, Blocks.GOLD_ORE, Blocks.IRON_ORE, Blocks.LAPIS_ORE, Blocks.LIT_REDSTONE_ORE, Blocks.QUARTZ_ORE, Blocks.REDSTONE_ORE,
            Blocks.WOODEN_PRESSURE_PLATE, Blocks.STONE_PRESSURE_PLATE, Blocks.TRAPPED_CHEST, Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE, Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE,
            Blocks.STONE_BUTTON, Blocks.WOODEN_BUTTON, Blocks.LEVER, Blocks.TALLGRASS, Blocks.TRIPWIRE, Blocks.TRIPWIRE_HOOK, Blocks.RAIL, Blocks.WATERLILY, Blocks.RED_FLOWER,
            Blocks.RED_MUSHROOM, Blocks.BROWN_MUSHROOM, Blocks.VINE, Blocks.TRAPDOOR, Blocks.YELLOW_FLOWER, Blocks.LADDER, Blocks.FURNACE, Blocks.SAND, Blocks.CACTUS,
            Blocks.DISPENSER, Blocks.NOTEBLOCK, Blocks.DROPPER, Blocks.CRAFTING_TABLE, Blocks.PUMPKIN, Blocks.SAPLING, Blocks.COBBLESTONE_WALL,
            Blocks.OAK_FENCE, Blocks.ACTIVATOR_RAIL, Blocks.DETECTOR_RAIL, Blocks.GOLDEN_RAIL, Blocks.REDSTONE_TORCH, Blocks.ACACIA_STAIRS,
            Blocks.BIRCH_STAIRS, Blocks.BRICK_STAIRS, Blocks.DARK_OAK_STAIRS, Blocks.JUNGLE_STAIRS, Blocks.NETHER_BRICK_STAIRS, Blocks.OAK_STAIRS,
            Blocks.QUARTZ_STAIRS, Blocks.RED_SANDSTONE_STAIRS, Blocks.SANDSTONE_STAIRS, Blocks.SPRUCE_STAIRS, Blocks.STONE_BRICK_STAIRS, Blocks.STONE_STAIRS,
            Blocks.WOODEN_SLAB, Blocks.DOUBLE_WOODEN_SLAB, Blocks.STONE_SLAB, Blocks.DOUBLE_STONE_SLAB, Blocks.STONE_SLAB2, Blocks.DOUBLE_STONE_SLAB2,
            Blocks.WEB, Blocks.GRAVEL, Blocks.DAYLIGHT_DETECTOR_INVERTED, Blocks.DAYLIGHT_DETECTOR, Blocks.SOUL_SAND, Blocks.PISTON, Blocks.PISTON_EXTENSION,
            Blocks.PISTON_HEAD, Blocks.STICKY_PISTON, Blocks.IRON_TRAPDOOR, Blocks.ENDER_CHEST, Blocks.END_PORTAL, Blocks.END_PORTAL_FRAME, Blocks.STANDING_BANNER,
            Blocks.WALL_BANNER, Blocks.DEADBUSH, Blocks.SLIME_BLOCK, Blocks.ACACIA_FENCE_GATE, Blocks.BIRCH_FENCE_GATE, Blocks.DARK_OAK_FENCE_GATE,
            Blocks.JUNGLE_FENCE_GATE, Blocks.SPRUCE_FENCE_GATE, Blocks.OAK_FENCE_GATE);

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

    public static void shiftClick(int inventorySlot) {
        mc.playerController.windowClick(mc.player.inventoryContainer.windowId, inventorySlot, 0, ClickType.QUICK_MOVE, mc.player);
    }

    public static void swap(int inventorySlot, int hotbarSlot) {
        mc.playerController.windowClick(mc.player.inventoryContainer.windowId, inventorySlot, hotbarSlot, ClickType.SWAP, mc.player);
    }

    public static int getBlockSlot() {
        for (int i = 0; i < 9; ++i) {
            ItemStack stack = mc.player.inventoryContainer.getSlot(i + 36).getStack();

            if (!(stack.getItem() instanceof ItemBlock) || offGoodBlock.contains(((ItemBlock) stack.getItem()).getBlock())) continue;
            return i;
        }
        return -1;
    }

    public static boolean isBlock(ItemStack stack) {
        Item item = stack.getItem();
        return item instanceof ItemBlock;
    }

    public static int getBlockIndex() {
        int blockCount = 0;
        for (int slotIndex = 9; slotIndex < mc.player.inventoryContainer.inventorySlots.size(); slotIndex++) {
            ItemStack stack = mc.player.getSlotFromPlayerContainer(slotIndex).getStack();

            if (isBlock(stack)) {
                blockCount += stack.stackSize;
            }
        }
        return blockCount;
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

    public static void swapArmor(int invSlot, int slot, boolean shift) {
        if (shift) {
            shiftClick(invSlot);
        } else {
            swapOFF(invSlot, slot);
        }
    }

    public static void swapOFF(int slot, int shift) {
        mc.playerController.windowClick(mc.player.inventoryContainer.windowId, slot, 0, ClickType.PICKUP, mc.player);
        mc.playerController.windowClick(mc.player.inventoryContainer.windowId, shift, 0, ClickType.PICKUP, mc.player);
    }
}
