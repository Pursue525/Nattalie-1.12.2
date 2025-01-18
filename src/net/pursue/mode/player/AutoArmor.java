package net.pursue.mode.player;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.pursue.utils.category.Category;
import net.pursue.mode.Mode;

public class AutoArmor extends Mode {

    public AutoArmor() {
        super("AutoArmor", "自动穿装备", "自动把最好的装备穿上", Category.PLAYER);
    }


    public static void getBestArmor() {
        for (int type = 1; type < 5; ++type) {
            if (mc.player.inventoryContainer.getSlot(4 + type).getHasStack()) {
                ItemStack is = mc.player.inventoryContainer.getSlot(4 + type).getStack();
                if (isBestArmor(is, type)) continue;

                drop(4 + type);
            }
            for (int i = 9; i < 45; ++i) {
                ItemStack is;
                if (!mc.player.inventoryContainer.getSlot(i).getHasStack() || !isBestArmor(is = mc.player.inventoryContainer.getSlot(i).getStack(), type) || !(getProtection(is) > 0.0f)) continue;

                shiftClick(i, type + 4, false);
                if (true) continue;
                return;
            }
        }
    }

    public static boolean isBestArmor(ItemStack stack, int type) {
        String strType = switch (type) {
            case 1 -> "helmet";
            case 2 -> "chestplate";
            case 3 -> "leggings";
            case 4 -> "boots";
            default -> "";
        };

        if (!stack.getUnlocalizedName().contains(strType)) {
            return false;
        }
        float protection = getProtection(stack);
        for (int i = 5; i < 45; ++i) {
            ItemStack is;
            if (!mc.player.inventoryContainer.getSlot(i).getHasStack() || !(getProtection(is = mc.player.inventoryContainer.getSlot(i).getStack()) > protection) || !is.getUnlocalizedName().contains(strType)) continue;
            return false;
        }
        return true;
    }

    public static float getProtection(ItemStack stack){
        float prot = 0;
        if ((stack.getItem() instanceof ItemArmor armor)) {
            switch (armor.getArmorMaterial().name()) {
                case "LEATHER" -> // 皮革甲
                        prot += 10;
                case "IRON" -> // 铁甲
                        prot += 30;
                case "DIAMOND" -> // 钻石甲
                        prot += 50;
                case "GOLD" -> // 黄金甲
                        prot += 15;
                case "CHAINMAIL" -> // 锁链甲
                        prot += 20;
                default -> prot += 20;
            }
            prot += armor.damageReduceAmount + (100 - armor.damageReduceAmount) * EnchantmentHelper.getEnchantmentLevel(Enchantment.getEnchantmentByID(0), stack) / 100f;
        }
        return prot;
    }

    public static void shiftClick(int slot, int slotI, boolean move) {
        if (mc.player != null && mc.playerController != null) {
            if (!move) {
                mc.playerController.windowClick(mc.player.inventoryContainer.windowId, slot, 0, ClickType.PICKUP, mc.player);
                mc.playerController.windowClick(mc.player.inventoryContainer.windowId, slotI, 0, ClickType.PICKUP, mc.player);
            } else {
                mc.playerController.windowClick(mc.player.inventoryContainer.windowId, slot, 0, ClickType.QUICK_MOVE, mc.player);
            }
        }
    }

    public static void drop(int slot) {
        if (mc.player != null && mc.playerController != null) mc.playerController.windowClick(mc.player.inventoryContainer.windowId, slot, 1, ClickType.THROW, mc.player);
    }
}
