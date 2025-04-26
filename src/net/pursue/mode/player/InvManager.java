package net.pursue.mode.player;

import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.*;
import net.minecraft.network.play.client.CPacketClickWindow;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.EnumHand;
import net.pursue.event.EventTarget;
import net.pursue.event.packet.EventPacket;
import net.pursue.event.update.EventMotion;
import net.pursue.event.world.EventWorldLoad;
import net.pursue.mode.Mode;
import net.pursue.mode.combat.ArmorBreak;
import net.pursue.mode.move.NoSlow;
import net.pursue.shield.IsShield;
import net.pursue.utils.TimerUtils;
import net.pursue.utils.category.Category;
import net.pursue.utils.client.DebugHelper;
import net.pursue.utils.player.InvUtils;
import net.pursue.value.values.BooleanValue;
import net.pursue.value.values.NumberValue;

import static net.minecraft.enchantment.EnchantmentHelper.getEnchantmentLevel;

@IsShield
public class InvManager extends Mode {

    public static InvManager instance;

    public final NumberValue<Double> blocks = new NumberValue<>(this, "Blocks", 128.0, 16.0, 512.0, 32.0);
    public final NumberValue<Double> arrows = new NumberValue<>(this, " BowArrows", 128.0, 64.0, 512.0, 64.0);
    public final NumberValue<Double> swordSlot = new NumberValue<>(this, "SwordSlot", 1.0, 1.0, 9.0, 1.0);
    public final NumberValue<Double> blockSlot = new NumberValue<>(this, "BlockSlot", 2.0, 1.0, 9.0, 1.0);
    public final NumberValue<Double> pickAxeSlot = new NumberValue<>(this, "PickaxeSlot", 4.0, 1.0, 9.0, 1.0);
    public final NumberValue<Double> axeSlot = new NumberValue<>(this, "AxeSlot", 9.0, 1.0, 9.0, 1.0);
    public final NumberValue<Double> projectilesSlot = new NumberValue<>(this, "ProjectilesSlot", 8.0, 1.0, 9.0, 1.0);
    public final NumberValue<Double> bowSlot = new NumberValue<>(this, "BowSlot", 3.0, 1.0, 9.0, 1.0);
    public final BooleanValue<Boolean> doubleSword = new BooleanValue<>(this, "DoubleSword", false);
    public final NumberValue<Double> enderPearlSlot = new NumberValue<>(this, "EnderPearlSlot", 5.0, 1.0, 9.0, 1.0, () -> !doubleSword.getValue());
    public final NumberValue<Double> sword2Slot = new NumberValue<>(this, "DoubleSwordSlot", 5.0, 1.0, 9.0, 1.0, () -> doubleSword.getValue());
    public final NumberValue<Double> enchantedGappleSlot = new NumberValue<>(this, "Enchanted AppleSlot", 6.0, 1.0, 9.0, 1.0);
    public final NumberValue<Double> gappleSlot = new NumberValue<>(this, "AppleSlot", 7.0, 1.0, 9.0, 1.0);
    public final NumberValue<Number> delay = new NumberValue<>(this, "Delay", 0, 0, 50, 5);
    public final BooleanValue<Boolean> open_inv = new BooleanValue<>(this, "OpenInv", false);
    public final BooleanValue<Boolean> offGApple = new BooleanValue<>(this, "(1.9+)OFFGApple", false);


    public InvManager() {
        super("InvManager", "背包整理", "整理你的背包", Category.PLAYER);
        instance = this;
    }

    private final TimerUtils itemDelay = new TimerUtils();

    @Override
    public void enable() {
        itemDelay.reset();
    }

    @EventTarget
    public void onMotion(EventMotion event) {

        if (open_inv.getValue() && !(mc.currentScreen instanceof GuiInventory)) return;

        if (AutoHeal.instance.isEnable() && AutoHeal.instance.modeValue.getValue().equals(AutoHeal.mode.Golden_Apple))
            return;

        if (NoSlow.INSTANCE.isEnable() && NoSlow.INSTANCE.modeValue.getValue().equals(NoSlow.mode.Grim) && NoSlow.INSTANCE.foodModeValue.getValue().equals(NoSlow.foodMd.DropC07)) {
            if (mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemAppleGold) return;
        }

        if (Stealer.isScreen) return;

        if (event.getType() == EventMotion.Type.Pre) {
            Item item;
            ItemStack stack;
            ItemStack stack1;
            int itemID;
            boolean slotNoItem;

            for (int i = 9; i < 45; i++) {
                stack = mc.player.getSlotFromPlayerContainer(i).getStack();
                item = mc.player.getSlotFromPlayerContainer(i).getStack().getItem();
                itemID = Item.getIdFromItem(item);
                slotNoItem = !mc.player.getSlotFromPlayerContainer(i).getHasStack();

                if (item instanceof ItemArmor armor && itemDelay.hasTimePassed(delay.getValue().intValue())) {

                    int slot = switch (armor.armorType) {
                        case HEAD -> 5;
                        case CHEST -> 6;
                        case LEGS -> 7;
                        case FEET -> 8;
                        default -> 0;
                    };

                    collationArmor(stack, mc.player.getSlotFromPlayerContainer(slot).getStack(), armor, i, slot, open_inv.getValue());
                    itemDelay.reset();
                } else if (item instanceof ItemSword && itemDelay.hasTimePassed(delay.getValue().intValue())) {
                    if (isGoodSword(stack, i)) {
                        if (i != swordSlot.getValue().intValue() + 35)
                            InvUtils.swap(i, swordSlot.getValue().intValue() - 1);
                    } else if (isGoodDoubleSword(stack, i) && doubleSword.getValue()) {
                        if (i != sword2Slot.getValue().intValue() + 35)
                            InvUtils.swap(i, sword2Slot.getValue().intValue() - 1);
                    } else {
                        InvUtils.drop(i);
                    }
                    itemDelay.reset();
                } else if (item instanceof ItemAxe && itemDelay.hasTimePassed(delay.getValue().intValue())) {

                    if (isGoodAxe(stack, i)) {
                        if (i != axeSlot.getValue().intValue() + 35)
                            InvUtils.swap(i, axeSlot.getValue().intValue() - 1);
                    } else {
                        InvUtils.drop(i);
                    }
                    itemDelay.reset();
                } else if (item instanceof ItemBlock block && itemDelay.hasTimePassed(delay.getValue().intValue())) {

                    if (getBlocksCounter() < blocks.getValue()) {
                        if (!InvUtils.offGoodBlock.contains(block.getBlock())) {
                            if (isGoodBlock(stack, i)) {

                                stack1 = mc.player.getSlotFromPlayerContainer(blockSlot.getValue().intValue() + 35).getStack();

                                if (mc.player.getSlotFromPlayerContainer(blockSlot.getValue().intValue() + 35).getHasStack() && stack1.getItem() instanceof ItemBlock block1) {
                                    if (stack.stackSize > stack1.stackSize) {
                                        if (i != blockSlot.getValue().intValue() + 35)
                                            InvUtils.swap(i, blockSlot.getValue().intValue() - 1);
                                    }
                                } else {
                                    if (i != blockSlot.getValue().intValue() + 35)
                                        InvUtils.swap(i, blockSlot.getValue().intValue() - 1);
                                }
                            }
                        } else {
                            InvUtils.drop(i);
                        }
                    } else {
                        InvUtils.drop(i);
                    }
                    itemDelay.reset();
                } else if (item instanceof ItemAppleGold && itemDelay.hasTimePassed(delay.getValue().intValue())) {

                    if (InvUtils.isEnchantedGoldenApple(stack)) {
                        if (i != enchantedGappleSlot.getValue().intValue() + 35 && mc.player.getSlotFromPlayerContainer(enchantedGappleSlot.getValue().intValue() + 35).getStack().stackSize < 64) InvUtils.swap(i, enchantedGappleSlot.getValue().intValue() - 1);

                    } else {
                        if (offGApple.getValue()) {

                            stack1 = mc.player.getSlotFromPlayerContainer(45).getStack();

                            InvUtils.swap(i, gappleSlot.getValue().intValue() - 1);
                            if (mc.player.getSlotFromPlayerContainer(45).getHasStack() && stack1.getItem() instanceof ItemAppleGold) {
                                InvUtils.shiftClick(45);
                            }
                            InvUtils.swap(45, gappleSlot.getValue().intValue() - 1);
                        } else {
                            if (i != gappleSlot.getValue().intValue() + 35 && mc.player.getSlotFromPlayerContainer(gappleSlot.getValue().intValue() + 35).getStack().stackSize < 64) InvUtils.swap(i, gappleSlot.getValue().intValue() - 1);
                        }
                    }
                    itemDelay.reset();
                } else if (item instanceof ItemEnderPearl && itemDelay.hasTimePassed(delay.getValue().intValue())) {
                    if (!doubleSword.getValue()) {
                        if (i != enderPearlSlot.getValue().intValue() + 35)
                            InvUtils.swap(i, enderPearlSlot.getValue().intValue() - 1);
                        itemDelay.reset();
                    }
                } else if ((item instanceof ItemEgg || item instanceof ItemSnowball) && itemDelay.hasTimePassed(delay.getValue().intValue())) {

                    stack1 = mc.player.getSlotFromPlayerContainer(projectilesSlot.getValue().intValue() + 35).getStack();
                    if (mc.player.getSlotFromPlayerContainer(projectilesSlot.getValue().intValue() + 35).getHasStack()) {
                        if ((stack1.getItem() instanceof ItemEgg || stack1.getItem() instanceof ItemSnowball) && stack.stackSize > stack1.stackSize) {
                            if (i != projectilesSlot.getValue().intValue() + 35)
                                InvUtils.swap(i, projectilesSlot.getValue().intValue() - 1);
                        }
                    } else {
                        if (i != projectilesSlot.getValue().intValue() + 35)
                            InvUtils.swap(i, projectilesSlot.getValue().intValue() - 1);
                    }
                    itemDelay.reset();
                } else if (item instanceof ItemPickaxe && itemDelay.hasTimePassed(delay.getValue().intValue())) {

                    if (isGoodPickaxe(stack, i)) {
                        if (i != pickAxeSlot.getValue().intValue() + 35)
                            InvUtils.swap(i, pickAxeSlot.getValue().intValue() - 1);
                    } else {
                        InvUtils.drop(i);
                    }
                    itemDelay.reset();
                } else if (item instanceof ItemBow && itemDelay.hasTimePassed(delay.getValue().intValue())) {

                    if (isGoodBow(stack, i)) {
                        if (i != bowSlot.getValue().intValue() + 35)
                            InvUtils.swap(i, bowSlot.getValue().intValue() - 1);
                    } else {
                        InvUtils.drop(i);
                    }
                    itemDelay.reset();
                } else if (item instanceof ItemPotion) {
                    itemDelay.reset();
                } else if ((itemID == 449 || itemID == 426 || itemID == 385 || itemID == 326) && itemDelay.hasTimePassed(delay.getValue().intValue())) {
                    itemDelay.reset();
                } else if (((item == Items.ARROW && getArrowsCounter() > arrows.getValue()) || (!slotNoItem && item != Items.ARROW)) && itemDelay.hasTimePassed(delay.getValue().intValue())) {
                    InvUtils.drop(i);
                    itemDelay.reset();
                }
            }
        }
    }

    @EventTarget
    public void onPacket(EventPacket eventPacket) {
        if (mc.player == null) return;

        if (open_inv.getValue() && !(mc.currentScreen instanceof GuiInventory)) return;

        if (AutoHeal.instance.isEnable() && AutoHeal.instance.modeValue.getValue().equals(AutoHeal.mode.Golden_Apple))
            return;

        if (NoSlow.INSTANCE.isEnable() && NoSlow.INSTANCE.modeValue.getValue().equals(NoSlow.mode.Grim) && NoSlow.INSTANCE.foodModeValue.getValue().equals(NoSlow.foodMd.DropC07)) {
            if (mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemAppleGold) return;
        }

        if (Stealer.isScreen) return;

        if (mc.currentScreen == null) {
            if (eventPacket.getPacket() instanceof CPacketClickWindow window && window.getClickType() == ClickType.THROW) {
                ItemStack stack = window.getClickedItem();


                if (stack.getItem() instanceof ItemArmor armor) {
                    if (isGoodArmor(stack, armor, window.getSlotId())) {
                        eventPacket.cancelEvent();
                        DebugHelper.sendMessage("InvManager", "物品乱丢已拦截，物品为：" + stack.getDisplayName());
                    }
                } else if (stack.getItem() instanceof ItemSword) {
                    if (isGoodSword(stack, window.getSlotId())) {
                        eventPacket.cancelEvent();
                        DebugHelper.sendMessage("InvManager", "物品乱丢已拦截，物品为：" + stack.getDisplayName());
                    }
                }
            }
        }
    }

    @EventTarget
    private void onWorld(EventWorldLoad event) {
        itemDelay.reset();
    }

    private void collationArmor(ItemStack stack, ItemStack stack1, ItemArmor armor, int i, int slot, boolean shift) {

        if (isGoodArmor(stack, armor, i)) {
            if (mc.player.getSlotFromPlayerContainer(slot).getHasStack() && stack1.getItem() instanceof ItemArmor armor1) {
                if (getArmorProtection(armor,stack) > getArmorProtection(armor1,stack1)) {
                    InvUtils.drop(slot);
                    InvUtils.swapArmor(i, slot, shift);
                } else {
                    InvUtils.drop(i);
                }
            } else {
                InvUtils.swapArmor(i, slot, shift);
            }
        } else {
            InvUtils.drop(i);
        }
    }

    private static float getDamage(ItemStack stack) {
        float damage = 0;
        final Item item = stack.getItem();

        if (item instanceof ItemTool tool) {
            damage += tool.getDamageVsEntity();
        } else if (item instanceof ItemSword sword) {
            damage += sword.getDamageVsEntity();
        }

        int sharpnessLevel = EnchantmentHelper.getEnchantmentLevel(Enchantments.SHARPNESS, stack);
        int knockbackLevel = EnchantmentHelper.getEnchantmentLevel(Enchantments.KNOCKBACK, stack);

        damage += sharpnessLevel * 1.25F + knockbackLevel * 0.01F;

        return damage;
    }

    public static boolean isGoodSword(ItemStack stack, int slot) {
        ItemStack stack1;

        for (int i = 0; i < 45; i++) {
            stack1 = mc.player.getSlotFromPlayerContainer(i).getStack();

            if (i == slot || !(stack1.getItem() instanceof ItemSword)) continue;

            if (getDamage(stack1) == getDamage(stack)) return false;

            return getDamage(stack1) < getDamage(stack);
        }
        return true;
    }

    public static boolean isGoodDoubleSword(ItemStack stack, int slot) {
        ItemStack stack1;

        for (int i = 0; i < 45; i++) {
            stack1 = mc.player.getSlotFromPlayerContainer(i).getStack();

            if (i == slot || !(stack1.getItem() instanceof ItemSword)) continue;

            if (getDamage16(stack1) == getDamage16(stack)) return false;

            return getDamage16(stack1) < getDamage16(stack);
        }
        return true;
    }

    public static boolean isGoodDoubleAxe(ItemStack stack, int slot) {
        ItemStack stack1;

        for (int i = 0; i < 45; i++) {
            stack1 = mc.player.getSlotFromPlayerContainer(i).getStack();

            if (i == slot || !(stack1.getItem() instanceof ItemAxe)) continue;

            if (getDamage16(stack1) == getDamage16(stack)) return false;

            return getDamage16(stack1) < getDamage16(stack);
        }
        return true;
    }

    public static boolean isGoodPickaxe(ItemStack stack, int slot) {
        ItemStack stack1;

        for (int i = 0; i < 45; i++) {
            stack1 = mc.player.getSlotFromPlayerContainer(i).getStack();

            if (i == slot || !(stack1.getItem() instanceof ItemPickaxe)) continue;

            if (getToolEffect(stack1) == getToolEffect(stack)) return false;

            return getToolEffect(stack1) < getToolEffect(stack);
        }
        return true;
    }

    public static boolean isGoodAxe(ItemStack stack, int slot) {
        ItemStack stack1;

        for (int i = 0; i < 45; i++) {
            stack1 = mc.player.getSlotFromPlayerContainer(i).getStack();

            if (i == slot || !(stack1.getItem() instanceof ItemAxe)) continue;

            if (ArmorBreak.getDamage(stack1) == ArmorBreak.getDamage(stack)) return false;

            return ArmorBreak.getDamage(stack1) < ArmorBreak.getDamage(stack);
        }
        return true;
    }

    public static boolean isGoodBow(ItemStack stack, int slot) {
        ItemStack stack1;

        for (int i = 0; i < 45; i++) {
            stack1 = mc.player.getSlotFromPlayerContainer(i).getStack();

            if (i == slot || !(stack1.getItem() instanceof ItemBow)) continue;

            if (getBowEffect(stack1) == getBowEffect(stack)) return false;

            return getBowEffect(stack1) < getBowEffect(stack);
        }
        return true;
    }

    public static boolean isGoodBlock(ItemStack stack, int slot) {
        return stack.getItem() instanceof ItemBlock;
    }

    public static boolean isGoodArmor(ItemStack stack, ItemArmor armor, int slot) {
        ItemStack stack1;
        switch (armor.armorType) {
            case HEAD -> {
                // 处理头盔装备逻辑

                for (int i = 0; i < 45; i++) {
                    stack1 = mc.player.getSlotFromPlayerContainer(i).getStack();

                    if (slot == i || !(stack1.getItem() instanceof ItemArmor armor1) || armor1.armorType != EntityEquipmentSlot.HEAD) continue;

                    if (getArmorProtection(armor1,stack1) == getArmorProtection(armor,stack)) {
                        return false;
                    }

                    return getArmorProtection(armor1,stack1) < getArmorProtection(armor,stack);
                }
                return true;
            }
            case CHEST -> {
                // 处理胸甲装备逻辑
                for (int i = 0; i < 45; i++) {
                    stack1 = mc.player.getSlotFromPlayerContainer(i).getStack();

                    if (slot == i || !(stack1.getItem() instanceof ItemArmor armor1) || armor1.armorType != EntityEquipmentSlot.CHEST) continue;

                    if (getArmorProtection(armor1,stack1) == getArmorProtection(armor,stack)) {
                        return false;
                    }

                    return getArmorProtection(armor1,stack1) < getArmorProtection(armor,stack);
                }
                return true;
            }
            case LEGS -> {
                // 处理护腿装备逻辑
                for (int i = 0; i < 45; i++) {
                    stack1 = mc.player.getSlotFromPlayerContainer(i).getStack();

                    if (slot == i || !(stack1.getItem() instanceof ItemArmor armor1) || armor1.armorType != EntityEquipmentSlot.LEGS) continue;

                    if (getArmorProtection(armor1,stack1) == getArmorProtection(armor,stack)) {
                        return false;
                    }

                    return getArmorProtection(armor1,stack1) < getArmorProtection(armor,stack);
                }
                return true;
            }
            case FEET -> {
                // 处理靴子装备逻辑
                for (int i = 0; i < 45; i++) {
                    stack1 = mc.player.getSlotFromPlayerContainer(i).getStack();

                    if (slot == i || !(stack1.getItem() instanceof ItemArmor armor1) || armor1.armorType != EntityEquipmentSlot.FEET) continue;

                    if (getArmorProtection(armor1,stack1) == getArmorProtection(armor,stack)) {
                        return false;
                    }

                    return getArmorProtection(armor1,stack1) < getArmorProtection(armor,stack);
                }
                return true;
            }
        }
        return true;
    }

    private static float getBowEffect(ItemStack stack) {
        return (1 + getEnchantmentLevel(Enchantment.getEnchantmentByID(48), stack) + getEnchantmentLevel(
                Enchantment.getEnchantmentByID(50), stack) + getEnchantmentLevel(Enchantment.getEnchantmentByID(51), stack));
    }

    private static float getToolEffect(ItemStack stack) {
        final Item item = stack.getItem();

        if (!(item instanceof ItemTool)) {
            return 0;
        }

        final String name = item.getUnlocalizedName();
        float value = 1;

        if (item instanceof ItemPickaxe) {
            if (name.toLowerCase().contains("wood")) value = 2;
            if (name.toLowerCase().contains("stone")) value = 3;
            if (name.toLowerCase().contains("iron")) value = 4;
            if (name.toLowerCase().contains("diamond")) value = 5;
        } else if (item instanceof ItemSpade) {
            if (name.toLowerCase().contains("wood")) value = 2;
            if (name.toLowerCase().contains("stone")) value = 3;
            if (name.toLowerCase().contains("iron")) value = 4;
            if (name.toLowerCase().contains("diamond")) value = 5;
        } else if (item instanceof ItemAxe) {
            if (name.toLowerCase().contains("wood")) value = 2;
            if (name.toLowerCase().contains("stone")) value = 3;
            if (name.toLowerCase().contains("iron")) value = 4;
            if (name.toLowerCase().contains("diamond")) value = 5;
        }

        value += getEnchantmentLevel(Enchantment.getEnchantmentByID(32), stack) * 0.0075F;
        value += getEnchantmentLevel(Enchantment.getEnchantmentByID(34), stack) / 100.0F;

        return value;
    }

    private int getBlocksCounter() {
        int blockCount = 0;

        for (int i = 0; i < 45; i++) {
            if (mc.player.getSlotFromPlayerContainer(i).getHasStack()) {
                final ItemStack stack = mc.player.getSlotFromPlayerContainer(i).getStack();
                final Item item = stack.getItem();

                if (stack.getItem() instanceof ItemBlock && !InvUtils.offGoodBlock.contains(((ItemBlock) item).getBlock())) {
                    blockCount += stack.stackSize;
                }
            }
        }

        return blockCount;
    }

    private int getArrowsCounter() {
        int arrowCount = 0;

        for (int i = 0; i < 45; i++) {
            if (mc.player.getSlotFromPlayerContainer(i).getHasStack()) {
                final ItemStack is = mc.player.getSlotFromPlayerContainer(i).getStack();
                if (is.getItem() == Items.ARROW) arrowCount += is.stackSize;
            }
        }

        return arrowCount;
    }

    public boolean isBadPotionEffect(ItemStack stack) {
        for (final PotionEffect effect : PotionUtils.getEffectsFromStack(stack)) {
            final Potion potion = effect.getPotion();

            if (potion.isBadEffect()) {
                return true;
            }
        }

        return false;
    }

    public static int getDamage16(ItemStack stack) {
        return getEnchantmentLevel(Enchantment.getEnchantmentByID(16), stack);
    }

    private static int getArmorProtection(ItemArmor armor, ItemStack stack) {
        int prot = 0;
        int protectionLevel = EnchantmentHelper.getEnchantmentLevel(Enchantments.PROTECTION, stack);
        switch (armor.getArmorMaterial().name()) {
            case "LEATHER" -> prot += 1; // 皮革甲
            case "IRON" -> prot += 3; // 铁甲
            case "DIAMOND" -> prot += 8; // 钻石甲
            case "GOLD" -> prot += 2; // 黄金甲
            case "CHAINMAIL" -> prot += 3; // 锁链甲
            default -> prot += 1;
        }

        return protectionLevel + prot;
    }
}
