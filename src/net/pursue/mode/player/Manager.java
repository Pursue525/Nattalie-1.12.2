package net.pursue.mode.player;

import net.minecraft.block.Block;
import net.minecraft.client.gui.inventory.GuiBrewingStand;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiFurnace;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.*;
import net.minecraft.network.play.client.CPacketClickWindow;
import net.minecraft.network.play.client.CPacketCloseWindow;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.EnumHand;
import net.pursue.Nattalie;
import net.pursue.event.EventTarget;
import net.pursue.event.update.EventMotion;
import net.pursue.event.update.EventTick;
import net.pursue.event.update.EventUpdate;
import net.pursue.event.world.EventWorldLoad;

import net.pursue.mode.Mode;
import net.pursue.mode.move.NoSlow;
import net.pursue.utils.TimerUtils;
import net.pursue.utils.category.Category;
import net.pursue.utils.client.DebugHelper;
import net.pursue.utils.player.InvUtils;
import net.pursue.value.values.BooleanValue;
import net.pursue.value.values.NumberValue;
import org.apache.http.Header;

import java.util.Arrays;
import java.util.List;

import static net.minecraft.enchantment.EnchantmentHelper.getEnchantmentLevel;
import static net.minecraft.util.text.TextFormatting.*;
import static net.minecraft.util.text.TextFormatting.GOLD;

public class Manager extends Mode {

    public static Manager instance;

    public final NumberValue<Double> blocks = new NumberValue<>(this, "Blocks", 128.0, 16.0, 512.0, 32.0);
    public final NumberValue<Double> arrows = new NumberValue<>(this, " BowArrows", 128.0, 64.0, 512.0, 64.0);
    public final NumberValue<Double> swordSlot = new NumberValue<>(this, "SwordSlot", 1.0, 1.0, 9.0, 1.0);
    public final NumberValue<Double> blockSlot = new NumberValue<>(this, "BlockSlot", 2.0, 1.0, 9.0, 1.0);
    public final NumberValue<Double> pickAxeSlot = new NumberValue<>(this, "PickaxeSlot", 4.0, 1.0, 9.0, 1.0);
    public final NumberValue<Double> axeSlot = new NumberValue<>(this, "AxeSlot", 9.0, 1.0, 9.0, 1.0);
    public final NumberValue<Double> projectilesSlot = new NumberValue<>(this, "ProjectilesSlot", 8.0, 1.0, 9.0, 1.0);
    public final NumberValue<Double> bowSlot = new NumberValue<>(this, "BowSlot", 3.0, 1.0, 9.0, 1.0);
    public final NumberValue<Double> enderPearlSlot = new NumberValue<>(this, "EnderPearlSlot", 5.0, 1.0, 9.0, 1.0);
    public final NumberValue<Double> enchantedGappleSlot = new NumberValue<>(this, "Enchanted AppleSlot", 6.0, 1.0, 9.0, 1.0);
    public final NumberValue<Double> gappleSlot = new NumberValue<>(this, "AppleSlot", 7.0, 1.0, 9.0, 1.0);
    public final NumberValue<Number> delay = new NumberValue<>(this, "Delay", 0, 0, 50, 5);
    public final BooleanValue<Boolean> open_inv = new BooleanValue<>(this, "OpenInv", false);
    public final BooleanValue<Boolean> offGApple = new BooleanValue<>(this, "(1.9+)OFFGApple", false);


    public Manager() {
        super("Manager", "背包管理器", "整理你的背包", Category.PLAYER);
        instance = this;
    }

    private final TimerUtils itemDelay = new TimerUtils();
    public static boolean isManager;

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

        if (event.getType() == EventMotion.Type.Pre) {
            Item item;
            ItemStack stack;
            ItemStack stack1;
            int itemID;
            boolean slotNoItem; //你存在的意义是什么？

            for (int i = 9; i < 45; i++) {
                stack = mc.player.getSlotFromPlayerContainer(i).getStack();
                item = mc.player.getSlotFromPlayerContainer(i).getStack().getItem();
                itemID = Item.getIdFromItem(item);
                slotNoItem = !mc.player.getSlotFromPlayerContainer(i).getHasStack();

                isManager = true;
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
                        if (!offGoodBlock.contains(block.getBlock())) {
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
                        if (i != enchantedGappleSlot.getValue().intValue() + 35)
                            InvUtils.swap(i, enchantedGappleSlot.getValue().intValue() - 1);
                    } else {
                        if (offGApple.getValue()) {

                            stack1 = mc.player.getSlotFromPlayerContainer(45).getStack();

                            InvUtils.swap(i, gappleSlot.getValue().intValue() - 1);
                            if (mc.player.getSlotFromPlayerContainer(45).getHasStack() && stack1.getItem() instanceof ItemAppleGold) {
                                InvUtils.shiftClick(45);
                            }
                            InvUtils.swap(45, gappleSlot.getValue().intValue() - 1);
                        } else {
                            if (i != gappleSlot.getValue().intValue() + 35)
                                InvUtils.swap(i, gappleSlot.getValue().intValue() - 1);
                        }
                    }
                    itemDelay.reset();
                } else if (item instanceof ItemEnderPearl && itemDelay.hasTimePassed(delay.getValue().intValue())) {

                    if (i != enderPearlSlot.getValue().intValue() + 35)
                        InvUtils.swap(i, enderPearlSlot.getValue().intValue() - 1);
                    itemDelay.reset();
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
                } else if ((itemID == 449 || itemID == 426 || itemID == 385) && itemDelay.hasTimePassed(delay.getValue().intValue())) {
                    itemDelay.reset();
                } else if (((item == Items.ARROW && getArrowsCounter() > arrows.getValue()) || (!slotNoItem && item != Items.ARROW)) && itemDelay.hasTimePassed(delay.getValue().intValue())) {
                    InvUtils.drop(i);
                    itemDelay.reset();
                } else {
                    isManager = false;
                    itemDelay.reset();
                }
            }

            isManager = false;
        }
    }

    @EventTarget
    private void onWorld(EventWorldLoad event) {
        isManager = false;
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

            if (getDamage(stack1) == getDamage(stack)) return false;

            return getDamage(stack1) < getDamage(stack);
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

                if (stack.getItem() instanceof ItemBlock && !offGoodBlock.contains(((ItemBlock) item).getBlock())) {
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

    public static final List<Block> offGoodBlock = Arrays.asList(Blocks.AIR, Blocks.WATER, Blocks.FLOWING_WATER, Blocks.LAVA, Blocks.WOODEN_SLAB, Blocks.WOODEN_SLAB, Blocks.CHEST, Blocks.FLOWING_LAVA,
            Blocks.ENCHANTING_TABLE, Blocks.CARPET, Blocks.GLASS_PANE, Blocks.SKULL, Blocks.STAINED_GLASS_PANE, Blocks.IRON_BARS, Blocks.SNOW_LAYER, Blocks.ICE, Blocks.PACKED_ICE,
            Blocks.COAL_ORE, Blocks.DIAMOND_ORE, Blocks.EMERALD_ORE, Blocks.CHEST, Blocks.TRAPPED_CHEST, Blocks.TORCH, Blocks.ANVIL, Blocks.TRAPPED_CHEST,
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
}
