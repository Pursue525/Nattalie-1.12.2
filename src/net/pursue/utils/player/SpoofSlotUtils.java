package net.pursue.utils.player;

import net.minecraft.item.ItemStack;
import net.pursue.utils.client.UtilsManager;

public class SpoofSlotUtils extends UtilsManager {
    public static SpoofSlotUtils instance = new SpoofSlotUtils();
    private static int slot;
    private static boolean isSpoof;

    public static void setSlot(int slot) {
        SpoofSlotUtils.slot = slot;
        SpoofSlotUtils.isSpoof = true;
    }

    public static void stopSpoofSlot() {
        SpoofSlotUtils.slot = -1;
        SpoofSlotUtils.isSpoof = false;
    }

    public static int getSlot() {
        return SpoofSlotUtils.isSpoof ? slot : mc.player.inventory.currentItem;
    }

    public static ItemStack getStack() {
        return SpoofSlotUtils.isSpoof ? mc.player.inventoryContainer.getSlot(slot + 36).getStack() : mc.player.getHeldItemMainhand();
    }
}
