package net.pursue.mode.player;

import net.minecraft.block.Block;
import net.minecraft.client.gui.inventory.GuiBrewingStand;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiFurnace;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.*;
import net.minecraft.network.play.client.CPacketClickWindow;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionUtils;
import net.pursue.Nattalie;
import net.pursue.event.EventTarget;
import net.pursue.event.update.EventTick;
import net.pursue.event.update.EventUpdate;
import net.pursue.event.world.EventWorldLoad;

import net.pursue.mode.Mode;
import net.pursue.utils.TimerUtils;
import net.pursue.utils.category.Category;
import net.pursue.utils.client.DebugHelper;
import net.pursue.utils.player.InvUtils;
import net.pursue.value.values.BooleanValue;
import net.pursue.value.values.NumberValue;

import java.util.Arrays;
import java.util.List;

import static net.minecraft.enchantment.EnchantmentHelper.getEnchantmentLevel;
import static net.minecraft.util.text.TextFormatting.*;
import static net.minecraft.util.text.TextFormatting.GOLD;

public class Manager extends Mode {

    public static Manager instance;

    public final NumberValue<Double> blocks = new NumberValue<>(this,"Blocks", 128.0, 16.0, 512.0, 16.0);
    public final NumberValue<Double> swordSlot = new NumberValue<>(this,"SwordSlot", 1.0, 1.0, 9.0, 1.0);
    public final NumberValue<Double> blockSlot = new NumberValue<>(this,"BlockSlot", 2.0, 1.0, 9.0, 1.0);
    public final NumberValue<Double> pickAxeSlot = new NumberValue<>(this,"PickaxeSlot", 4.0, 1.0, 9.0, 1.0);
    public final NumberValue<Double> axeSlot = new NumberValue<>(this,"AxeSlot", 9.0, 1.0, 9.0, 1.0);
    public final NumberValue<Double> projectilesSlot = new NumberValue<>(this,"ProjectilesSlot", 8.0, 1.0, 9.0, 1.0);
    public final NumberValue<Double> bowSlot = new NumberValue<>(this,"BowSlot", 3.0, 1.0, 9.0, 1.0);
    public final NumberValue<Double> arrows = new NumberValue<>(this," BowArrows", 128.0, 64.0, 512.0, 64.0);
    public final NumberValue<Double> enderPearlSlot = new NumberValue<>(this,"EnderPearlSlot",5.0,1.0,9.0,1.0);
    public final NumberValue<Double> enchantedGappleSlot = new NumberValue<>(this,"Enchanted AppleSlot", 6.0, 1.0, 9.0, 1.0);
    public final NumberValue<Double> gappleSlot = new NumberValue<>(this,"AppleSlot", 7.0, 1.0, 9.0, 1.0);
    public final NumberValue<Number> delay = new NumberValue<>(this,"ItemDelay", 0,0,100,10);
    public final BooleanValue<Boolean> open_inv = new BooleanValue<>(this,"OpenInv", false);
    private final BooleanValue<Boolean> offGApple = new BooleanValue<>(this,"(1.9+)OFFGApple", false);
    private final BooleanValue<Boolean> armor = new BooleanValue<>(this,"ArmorClick_Shift", false);

    public Manager() {
        super("Manager", "背包管理器", "整理你的背包", Category.PLAYER);
        instance = this;
    }


    private final TimerUtils timerUtils = new TimerUtils();


    @Override
    public void enable() {
        timerUtils.reset();
    }

    private boolean isBestSword(ItemStack stack) {
        final float damage = getDamage(stack);

        for (int i = 9; i < 45; i++) {
            if (mc.player.getSlotFromPlayerContainer(i).getHasStack()) {
                final ItemStack is = mc.player.getSlotFromPlayerContainer(i).getStack();
                if (getDamage(is) > damage && is.getItem() instanceof ItemSword) {
                    return false;
                }
            }
        }

        return stack.getItem() instanceof ItemSword;
    }

    private boolean isBlock(ItemStack stack) {


        Item item = stack.getItem();
        return item instanceof ItemBlock;
    }

    private boolean isHead(ItemStack stack) {
        return stack.getItem() instanceof ItemSkull && stack.getDisplayName().contains("Head") && !stack
                .getDisplayName().equalsIgnoreCase("Wither Skeleton Skull") && !stack.getDisplayName()
                .equalsIgnoreCase("Zombie Head") && !stack.getDisplayName().equalsIgnoreCase("Creeper Head") && !stack
                .getDisplayName().equalsIgnoreCase("Skeleton Skull");
    }

    private boolean isGoldenApple(ItemStack stack) {
        return stack.getItem() instanceof ItemAppleGold && !isEnchantedGoldenApple(stack);
    }
    private boolean isEnchantedGoldenApple(ItemStack stack) {
        if (stack.getItem() instanceof ItemAppleGold goldenApple) {
            return goldenApple.hasEffect(stack);
        }
        return false;
    }

    @EventTarget
    public void onTick(EventTick event) {
        final int
                swordSlot = (int) (this.swordSlot.getValue() - 1),
                enderPearlSlot = (int) (this.enderPearlSlot.getValue() - 1),
                blockSlot = (int) (this.blockSlot.getValue() - 1),
                pickAxeSlot = (int) (this.pickAxeSlot.getValue() - 1),
                bowSlot = (int) (this.bowSlot.getValue() - 1),
                shovelSlot = (int) (this.projectilesSlot.getValue() - 1),
                axeSlot = (int) (this.axeSlot.getValue() - 1),
                headSlot = (int) (this.enchantedGappleSlot.getValue() - 1),
                gappleSlot = (int) (this.gappleSlot.getValue() - 1);

        ItemStack stack;

        if ((mc.player.openContainer.windowId == 0 && mc.currentScreen == null) && !open_inv.getValue() || mc.currentScreen instanceof GuiInventory) {
            if ((!AutoHeal.instance.isEnable() || !AutoHeal.instance.modeValue.getValue().equals(AutoHeal.mode.Golden_Apple))) {

                for (int slotIndex = 9; slotIndex < 46; slotIndex++) {
                    stack = mc.player.getSlotFromPlayerContainer(slotIndex).getStack();

                    if (shouldDrop(stack) && timerUtils.hasTimePassed(delay.getValue().intValue())) {
                        InvUtils.drop(slotIndex);
                        timerUtils.reset();
                    }

                    if (isBestSword(stack) && timerUtils.hasTimePassed(delay.getValue().intValue()) && shouldSwap(swordSlot)[0]) {
                        InvUtils.swap(slotIndex, swordSlot);
                        timerUtils.reset();
                    }
                    if (isBlock(stack) && timerUtils.hasTimePassed(delay.getValue().intValue()) && shouldSwap(blockSlot)[1] && !(mc.player.inventoryContainer.getSlot(blockSlot + 36).getStack().getItem() instanceof ItemBlock) || isBlock(stack) && mc.player.inventoryContainer.getSlot(blockSlot + 36).getStack().getItem() instanceof ItemBlock && stack.stackSize > mc.player.inventoryContainer.getSlot(blockSlot + 36).getStack().stackSize && mc.player.inventoryContainer.getSlot(blockSlot + 36).getStack().stackSize < 3) {
                        InvUtils.swap(slotIndex, blockSlot);
                        timerUtils.reset();
                    }

                    if (isBestPickaxe(stack) && timerUtils.hasTimePassed(delay.getValue().intValue()) && shouldSwap(pickAxeSlot)[3]) {
                        InvUtils.swap(slotIndex, pickAxeSlot);
                        timerUtils.reset();
                    }

                    if (isBestAxe(stack) && timerUtils.hasTimePassed(delay.getValue().intValue()) && shouldSwap(axeSlot)[2]) {
                        InvUtils.swap(slotIndex, axeSlot);
                        timerUtils.reset();
                    }

                    if (isBestBow(stack) && timerUtils.hasTimePassed(delay.getValue().intValue()) && shouldSwap(bowSlot)[6] && !stack.getDisplayName().toLowerCase().contains("kit selector")) {
                        InvUtils.swap(slotIndex, bowSlot);
                        timerUtils.reset();
                    }

                    if (isEnchantedGoldenApple(stack) && timerUtils.hasTimePassed(delay.getValue().intValue()) && shouldSwap(headSlot)[7]) {
                        InvUtils.swap(slotIndex, headSlot);
                        timerUtils.reset();
                    }

                    if (isProjectiles(stack) && timerUtils.hasTimePassed(delay.getValue().intValue()) && shouldSwap(shovelSlot)[4] && !(mc.player.inventoryContainer.getSlot(headSlot + 36).getStack().getItem() instanceof ItemSnowball || mc.player.inventoryContainer.getSlot(headSlot + 36).getStack().getItem() instanceof ItemEgg)) {
                        InvUtils.swap(slotIndex, shovelSlot);
                        timerUtils.reset();
                    }

                    if (!offGApple.getValue()) {
                        if (isGoldenApple(stack) && timerUtils.hasTimePassed(delay.getValue().intValue()) && shouldSwap(gappleSlot)[8] && !(mc.player.inventoryContainer.getSlot(shovelSlot + 36).getStack().getItem() instanceof ItemAppleGold)) {
                            InvUtils.swap(slotIndex, gappleSlot);
                            timerUtils.reset();
                        }
                    } else {
                        if (slotIndex != 45 && isGoldenApple(stack) && timerUtils.hasTimePassed(delay.getValue().intValue()) && !mc.player.inventoryContainer.getSlot(45).getHasStack()) {
                            InvUtils.swapOFF(slotIndex);
                            timerUtils.reset();
                        }
                    }

                    if (isBestEnderPearl(stack) && timerUtils.hasTimePassed(delay.getValue().intValue()) && shouldSwap(enderPearlSlot)[9]) {
                        InvUtils.swap(slotIndex, enderPearlSlot);
                        timerUtils.reset();
                    }

                    if (stack.getItem() instanceof ItemArmor) {
                        for (int i = 5; i < 9; i++) {
                            ItemStack armor = mc.player.getSlotFromPlayerContainer(i).getStack();
                            if (mc.player.getSlotFromPlayerContainer(i).getHasStack() && !isBestArmor(armor, getArmorType(armor))) {
                                InvUtils.drop(i);
                                timerUtils.reset();
                            }
                        }

                        if (isBestArmor2(stack, getArmorType(stack))) {
                            AutoArmor.shiftClick(slotIndex, getArmorType(stack) + 4, armor.getValue());
                            timerUtils.reset();
                        }
                    }
                }
            }
        }
    }

    @EventTarget
    private void onWorld(EventWorldLoad eventWorldLoad) {
        timerUtils.reset();
    }

    private boolean[] shouldSwap(int slot) {
        return new boolean[]{
                !mc.player.inventoryContainer.getSlot(slot + 36).getHasStack() || !isBestSword(mc.player.inventoryContainer.getSlot(slot + 36).getStack()),
                !mc.player.inventoryContainer.getSlot(slot + 36).getHasStack() || !isBlock(mc.player.inventoryContainer.getSlot(slot + 36).getStack()),
                !mc.player.inventoryContainer.getSlot(slot + 36).getHasStack() || !isBestAxe(mc.player.inventoryContainer.getSlot(slot + 36).getStack()),
                !mc.player.inventoryContainer.getSlot(slot + 36).getHasStack() || !isBestPickaxe(mc.player.inventoryContainer.getSlot(slot + 36).getStack()),
                !mc.player.inventoryContainer.getSlot(slot + 36).getHasStack() || !isProjectiles(mc.player.inventoryContainer.getSlot(slot + 36).getStack()),
                !mc.player.inventoryContainer.getSlot(slot + 36).getHasStack() || !isHead(mc.player.inventoryContainer.getSlot(slot + 36).getStack()),
                !mc.player.inventoryContainer.getSlot(slot + 36).getHasStack() || !isBestBow(mc.player.inventoryContainer.getSlot(slot + 36).getStack()),
                !mc.player.inventoryContainer.getSlot(slot + 36).getHasStack() || !isEnchantedGoldenApple(mc.player.inventoryContainer.getSlot(slot + 36).getStack()),
                !mc.player.inventoryContainer.getSlot(slot + 36).getHasStack() || !isGoldenApple(mc.player.inventoryContainer.getSlot(slot + 36).getStack()),
                !mc.player.inventoryContainer.getSlot(slot + 36).getHasStack() || !isBestEnderPearl(mc.player.inventoryContainer.getSlot(slot + 36).getStack()),
                !mc.player.inventoryContainer.getSlot(slot + 36).getHasStack() || !isMushroomStew(mc.player.inventoryContainer.getSlot(slot + 36).getStack())};
    }

    private boolean isMushroomStew(ItemStack stack) {
        return stack.getItem() == Items.MUSHROOM_STEW;
    }

    private boolean isBestArmor(ItemStack armor, int id) {
        for (int i = 9; i < 45; i++) {
            ItemStack stack = mc.player.getSlotFromPlayerContainer(i).getStack();

            if (stack.getItem() instanceof ItemArmor && getArmorType(stack) == id && AutoArmor.getProtection(stack) > AutoArmor.getProtection(armor)) {
                return false;
            }
        }
        return true;
    }

    private boolean isBestArmor2(ItemStack armor, int id) {
        for (int i = 5; i < 9; i++) {
            ItemStack stack = mc.player.getSlotFromPlayerContainer(i).getStack();

            if (getArmorType(stack) == id && AutoArmor.getProtection(armor) > AutoArmor.getProtection(stack)) return false;

        }
        return true;
    }


    public static int getArmorType(ItemStack stack) {
        if (stack.getItem() instanceof ItemArmor armor) {
            return switch (armor.armorType) {
                case HEAD -> 1; // 头盔
                case CHEST -> 2; // 胸甲
                case LEGS -> 3; // 裤子
                case FEET -> 4; // 鞋子
                default -> 0; // 其他情况
            };
        }
        return 0; // 不是盔甲
    }

    private float getDamage(ItemStack stack) {
        float damage = 0;
        final Item item = stack.getItem();

        if (item instanceof ItemTool) {
            damage += ((ItemTool) item).getDamageVsEntity();
        } else if (item instanceof ItemSword) {
            damage += ((ItemSword) item).getDamageVsEntity();
        }

        damage += getEnchantmentLevel(Enchantment.getEnchantmentByID(16), stack) * 1.25F + getEnchantmentLevel(
                Enchantment.getEnchantmentByID(20), stack) * 0.01F;
        return damage;
    }

    private final List<Block> blacklistedBlocks2 = Arrays.asList(Blocks.AIR, Blocks.WATER, Blocks.FLOWING_WATER, Blocks.LAVA, Blocks.WOODEN_SLAB, Blocks.WOODEN_SLAB, Blocks.CHEST, Blocks.FLOWING_LAVA,
            Blocks.ENCHANTING_TABLE, Blocks.CARPET, Blocks.GLASS_PANE, Blocks.SKULL, Blocks.STAINED_GLASS_PANE, Blocks.IRON_BARS, Blocks.SNOW_LAYER, Blocks.ICE, Blocks.PACKED_ICE,
            Blocks.COAL_ORE, Blocks.DIAMOND_ORE, Blocks.EMERALD_ORE, Blocks.CHEST, Blocks.TRAPPED_CHEST, Blocks.TNT, Blocks.TORCH, Blocks.ANVIL, Blocks.TRAPPED_CHEST,
            Blocks.NOTEBLOCK, Blocks.JUKEBOX, Blocks.TNT, Blocks.GOLD_ORE, Blocks.IRON_ORE, Blocks.LAPIS_ORE, Blocks.LIT_REDSTONE_ORE, Blocks.QUARTZ_ORE, Blocks.REDSTONE_ORE,
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

    private int getBlocksCounter() {
        int blockCount = 0;

        for (int i = 0; i < 45; i++) {
            if (mc.player.getSlotFromPlayerContainer(i).getHasStack()) {
                final ItemStack stack = mc.player.getSlotFromPlayerContainer(i).getStack();
                final Item item = stack.getItem();

                if (stack.getItem() instanceof ItemBlock && !blacklistedBlocks2.contains(((ItemBlock) item).getBlock())) {
                    blockCount += stack.stackSize;
                }
            }
        }

        return blockCount;
    }

    private boolean isBestEnderPearl(ItemStack stack) {
        final Item item = stack.getItem();

        return item instanceof ItemEnderPearl;
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

    public static int getProjectiles() {
        int eggsCount = 0;
        int snowballsCount = 0;

        int eggSlot = 0;
        int snowballsSlot = 0;

        for (int i = 0; i < 45; ++i) {

            if (!mc.player.inventoryContainer.getSlot(i + 36).getHasStack()) continue;

            Item item = mc.player.inventoryContainer.getSlot(i + 36).getStack().getItem();

            if (item instanceof ItemEgg) {
                eggsCount += mc.player.inventoryContainer.getSlot(i + 36).getStack().func_190916_E();
                eggSlot = i;
            } else if (item instanceof ItemSnowball) {
                snowballsCount += mc.player.inventoryContainer.getSlot(i + 36).getStack().func_190916_E();
                snowballsSlot = i;
            }

            if (eggsCount > snowballsCount) {
                return eggSlot;
            } else if (snowballsCount > eggsCount) {
                return snowballsSlot;
            } else if (eggSlot != 0) {
                return eggSlot;
            } else if (snowballsSlot != 0) {
                return snowballsSlot;
            }

        }
        return -1;
    }

    private boolean isProjectiles(ItemStack stack) {
        int eggsCount = 0;
        int snowballsCount = 0;
        final Item item = stack.getItem();

        if (item instanceof ItemEgg) eggsCount = stack.func_190916_E();

        if (item instanceof ItemSnowball) snowballsCount = stack.func_190916_E();

        if (eggsCount > snowballsCount) {
            return item instanceof ItemEgg;
        } else if (snowballsCount > eggsCount) {
            return item instanceof ItemSnowball;
        } else return eggsCount != 0 ? item instanceof ItemEgg : snowballsCount != 0 ? item instanceof ItemSnowball : false;
    }



    private int getIronIngotsCounter() {
        int count = 0;

        for (int i = 0; i < 45; i++) {
            if (mc.player.getSlotFromPlayerContainer(i).getHasStack()) {
                final ItemStack stack = mc.player.getSlotFromPlayerContainer(i).getStack();
                if (stack.getItem() == Items.IRON_INGOT) count += stack.stackSize;
            }
        }

        return count;
    }

    private int getCoalCounter() {
        int count = 0;

        for (int i = 0; i < 45; i++) {
            if (mc.player.getSlotFromPlayerContainer(i).getHasStack()) {
                final ItemStack stack = mc.player.getSlotFromPlayerContainer(i).getStack();
                if (stack.getItem() == Items.COAL) count += stack.stackSize;
            }
        }

        return count;
    }

    private int getSwordsCounter() {
        int count = 0;

        for (int i = 0; i < 45; i++) {
            if (mc.player.getSlotFromPlayerContainer(i).getHasStack()) {
                final ItemStack stack = mc.player.getSlotFromPlayerContainer(i).getStack();
                if (stack.getItem() instanceof ItemSword && isBestSword(stack)) count += stack.stackSize;
            }
        }

        return count;
    }

    private int getBowsCounter() {
        int count = 0;

        for (int i = 0; i < 45; i++) {
            if (mc.player.getSlotFromPlayerContainer(i).getHasStack()) {
                final ItemStack stack = mc.player.getSlotFromPlayerContainer(i).getStack();
                if (stack.getItem() instanceof ItemBow && isBestBow(stack)) count += stack.stackSize;
            }
        }

        return count;
    }

    private int getPickaxexCounter() {
        int count = 0;

        for (int i = 0; i < 45; i++) {
            if (mc.player.getSlotFromPlayerContainer(i).getHasStack()) {
                final ItemStack stack = mc.player.getSlotFromPlayerContainer(i).getStack();
                if (stack.getItem() instanceof ItemPickaxe && isBestPickaxe(stack)) count += stack.stackSize;
            }
        }

        return count;
    }

    private int getAxesCounter() {
        int count = 0;

        for (int i = 0; i < 45; i++) {
            if (mc.player.getSlotFromPlayerContainer(i).getHasStack()) {
                final ItemStack stack = mc.player.getSlotFromPlayerContainer(i).getStack();
                if (stack.getItem() instanceof ItemAxe && isBestAxe(stack)) count += stack.stackSize;
            }
        }

        return count;
    }


    private boolean isBestPickaxe(ItemStack stack) {
        final Item item = stack.getItem();

        if (!(item instanceof ItemPickaxe)) {
            return false;
        }

        final float value = getToolEffect(stack);

        for (int i = 9; i < 45; i++) {
            if (mc.player.getSlotFromPlayerContainer(i).getHasStack()) {
                final ItemStack slotStack = mc.player.getSlotFromPlayerContainer(i).getStack();
                if (getToolEffect(slotStack) > value && slotStack.getItem() instanceof ItemPickaxe) return false;
            }
        }

        return true;
    }

    private boolean shouldDrop(ItemStack stack) {
        final Item item = stack.getItem();
        final String displayName = stack.getDisplayName();
        final int idFromItem = Item.getIdFromItem(item);

        if (item instanceof ItemShield || item instanceof ItemEnderPearl || item instanceof ItemAppleGold) return false;

        if (item instanceof ItemArmor) {
            for (int type = 1; type < 5; type++) {
                if (mc.player.getSlotFromPlayerContainer(4 + type).getHasStack()) {
                    final ItemStack slotStack = mc.player.getSlotFromPlayerContainer(4 + type).getStack();
                    if (AutoArmor.isBestArmor(slotStack, type)) continue;
                }

                if (AutoArmor.isBestArmor(stack, type)) return false;
            }
        }

        if (idFromItem == 269 || idFromItem == 30 || idFromItem == 46 || idFromItem == 281) {
            return true;
        }

        if (idFromItem == 58 || displayName.toLowerCase().contains(OBFUSCATED + "||") // @off
                || displayName.contains(GREEN + "Game Menu " + GRAY + "(Right Click)")
                || displayName.equalsIgnoreCase(AQUA + "" + BOLD + "Spectator Settings " + GRAY + "(Right Click)")
                || displayName.equalsIgnoreCase(AQUA + "" + BOLD + "Play Again " + GRAY + "(Right Click)")
                || displayName.equalsIgnoreCase(GREEN + "" + BOLD + "Teleporter " + GRAY + "(Right Click)")
                || displayName.equalsIgnoreCase(GREEN + "SkyWars Challenges " + GRAY + "(Right Click)")
                || displayName.equalsIgnoreCase(GREEN + "Collectibles " + GRAY + "(Right Click)")
                || displayName.equalsIgnoreCase(GREEN + "Kit Selector " + GRAY + "(Right Click)")
                || displayName.equalsIgnoreCase(GREEN + "Kill Effect Selector " + GRAY + "(Right Click)")
                || displayName.equalsIgnoreCase(WHITE + "Players: " + RED + "Hidden " + GRAY + "(Right Click)")
                || displayName.equalsIgnoreCase(GREEN + "Shop " + GRAY + "(Right Click)")
                || displayName.equalsIgnoreCase(WHITE + "Players: " + RED + "Visible " + GRAY + "(Right Click)")
                || displayName.equalsIgnoreCase(GOLD + "Excalibur") || displayName.equalsIgnoreCase("aDragon Sword")
                || displayName.equalsIgnoreCase(GREEN + "Cornucopia")
                || displayName.equalsIgnoreCase(RED + "Bloodlust") || displayName.equalsIgnoreCase(RED + "Artemis' Bow")
                || displayName.equalsIgnoreCase(GREEN + "Miner's Blessing") || displayName.equalsIgnoreCase(GOLD + "Axe of Perun")
                || displayName.equalsIgnoreCase(GOLD + "Cornucopia") || idFromItem == 145
                || displayName.equalsIgnoreCase("§aAndúril")
                || idFromItem == 259 || idFromItem == 332 || idFromItem == 344) {
            return false;
        }

        final int swordSlot = (int) (this.swordSlot.getValue() - 1),
                blockSlot = (int) (this.blockSlot.getValue() - 1),
                pickAxeSlot = (int) (this.pickAxeSlot.getValue() - 1),
                bowSlot = (int) (this.bowSlot.getValue() - 1),
                axeSlot = (int) (this.axeSlot.getValue() - 1);

        if (stack.getItem() instanceof ItemSpade ||
                (isBestBow(stack) && getBowsCounter() < 2 || stack.getItem() instanceof ItemBow && stack == mc.player.inventory.getStackInSlot(bowSlot)) ||
                (isBestAxe(stack) && getAxesCounter() < 2 || stack.getItem() instanceof ItemAxe && stack == mc.player.inventory.getStackInSlot(axeSlot)) ||
                (isBestPickaxe(stack) && getPickaxexCounter() < 2 || stack.getItem() instanceof ItemPickaxe && stack == mc.player.inventory.getStackInSlot(pickAxeSlot)) ||
                (isBlock(stack) && stack == mc.player.inventory.getStackInSlot(blockSlot)) ||
                (isBestSword(stack) && getSwordsCounter() < 2 || stack.getItem() instanceof ItemSword && stack == mc.player.inventory.getStackInSlot(swordSlot))) {
            return false;
        }

        if (item instanceof ItemBlock && (getBlocksCounter() > blocks.getValue()
                || blacklistedBlocks2.contains(((ItemBlock) item).getBlock()))
                || item instanceof ItemPotion && isBadPotion(stack) || item instanceof ItemFood
                && !(item instanceof ItemAppleGold) && item != Items.BREAD && item
                != Items.PUMPKIN_PIE && item != Items.BAKED_POTATO && item != Items.COOKED_CHICKEN
                && item != Items.CARROT && item != Items.APPLE && item != Items.BEEF
                && item != Items.COOKED_BEEF && item != Items.PORKCHOP && item != Items.COOKED_PORKCHOP
                && item != Items.MUSHROOM_STEW  && item != Items.COOKED_FISH && item != Items.MELON
                || item instanceof ItemHoe || item instanceof ItemTool || item instanceof ItemSword || item instanceof ItemArmor) {
            return true;
        }

        final String unlocalizedName = item.getUnlocalizedName();

        // @off
        return unlocalizedName.contains("stick") || getIronIngotsCounter() > 64 && item == Items.IRON_INGOT || getCoalCounter() > 64 && item == Items.COAL || unlocalizedName.contains("string") || unlocalizedName.contains("flint") || unlocalizedName.contains("compass") || unlocalizedName.contains("dyePowder") || unlocalizedName.contains("feather") || unlocalizedName.contains("chest") && !displayName.toLowerCase().contains("collect") || unlocalizedName.contains("snow") || unlocalizedName.contains("torch") || unlocalizedName.contains("seeds") || unlocalizedName.contains("leather") || unlocalizedName.contains("reeds") || unlocalizedName.contains("record") || item instanceof ItemGlassBottle || item instanceof ItemSlab || idFromItem == 113 || idFromItem == 106 || idFromItem == 325 || idFromItem == 327 || idFromItem == 111 || idFromItem == 85 || idFromItem == 188 || idFromItem == 189 || idFromItem == 190 || idFromItem == 191 || idFromItem == 401 || idFromItem == 192 || idFromItem == 81 || idFromItem == 32 || unlocalizedName.contains("gravel") || unlocalizedName.contains("flower") || unlocalizedName.contains("tallgrass") || item instanceof ItemBow || item == Items.ARROW && getArrowsCounter() > arrows.getValue() || idFromItem == 175 || idFromItem == 340 || idFromItem == 339 || idFromItem == 160 || idFromItem == 101 || idFromItem == 102 || idFromItem == 321 || idFromItem == 323 || idFromItem == 389 || idFromItem == 416 || idFromItem == 171 || idFromItem == 139 || idFromItem == 23 || idFromItem == 25 || idFromItem == 69 || idFromItem == 70 || idFromItem == 72 || idFromItem == 77 || idFromItem == 96 || idFromItem == 107 || idFromItem == 123 || idFromItem == 131 || idFromItem == 143 || idFromItem == 147 || idFromItem == 148 || idFromItem == 151 || idFromItem == 152 || idFromItem == 154 || idFromItem == 158 || idFromItem == 167 || idFromItem == 403 || idFromItem == 183 || idFromItem == 184 || idFromItem == 185 || idFromItem == 186 || idFromItem == 187 || idFromItem == 331 || idFromItem == 356 || idFromItem == 404 || idFromItem == 27 || idFromItem == 28 || idFromItem == 66 || idFromItem == 76 || idFromItem == 157 || idFromItem == 328 || idFromItem == 384 || idFromItem == 342 || idFromItem == 343 || idFromItem == 398 || idFromItem == 407 || idFromItem == 408 || idFromItem == 138 || idFromItem == 352 || idFromItem == 385 || idFromItem == 386 || idFromItem == 395 || idFromItem == 402 || idFromItem == 418 || idFromItem == 419 || idFromItem == 256 || idFromItem == 273 || idFromItem == 277 || idFromItem == 284 || idFromItem == 281 || idFromItem == 289 || idFromItem == 337 || idFromItem == 336 || idFromItem == 348 || idFromItem == 353 || idFromItem == 369 || idFromItem == 372 || idFromItem == 405 || idFromItem == 406 || idFromItem == 409 || idFromItem == 410 || idFromItem == 415 || idFromItem == 370 || idFromItem == 376 || idFromItem == 377 || idFromItem == 378 || idFromItem == 379 || idFromItem == 380 || idFromItem == 382 || idFromItem == 414 || idFromItem == 346 || idFromItem == 347 || idFromItem == 420 || idFromItem == 397 || idFromItem == 421 || idFromItem == 341 || idFromItem == 264 || idFromItem == 265 || idFromItem == 364 || idFromItem == 56 || idFromItem == 393 || idFromItem == 30 || unlocalizedName.contains("sapling") || unlocalizedName.contains("stairs") || unlocalizedName.contains("door") || unlocalizedName.contains("monster_egg") || unlocalizedName.contains("sand") || unlocalizedName.contains("piston");
    }

    private boolean isBestAxe(ItemStack stack) {
        final Item item = stack.getItem();

        if (!(item instanceof ItemAxe)) {
            return false;
        }

        float sharpnessLevel = getSharpnessLevel(stack);

        return sharpnessLevel > 50.0;
    }


    private float getToolEffect(ItemStack stack) {
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

    private float getBowEffect(ItemStack stack) {
        return (1 + getEnchantmentLevel(Enchantment.getEnchantmentByID(48), stack) + getEnchantmentLevel(
                Enchantment.getEnchantmentByID(50), stack) + getEnchantmentLevel(Enchantment.getEnchantmentByID(51), stack));
    }

    private boolean isBadPotion(ItemStack stack) {
        if (stack != null && stack.getItem() instanceof ItemPotion) {
            return isBadPotionEffect(stack);
        }

        return false;
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

    private float getSharpnessLevel(ItemStack stack) {
        float damage = 0;
        final Item item = stack.getItem();

        if (item instanceof ItemTool) {
            damage += ((ItemTool) item).getDamageVsEntity();
        }

        damage += getEnchantmentLevel(Enchantment.getEnchantmentByID(16), stack) * 1.25F + getEnchantmentLevel(
                Enchantment.getEnchantmentByID(20), stack) * 0.01F;
        return damage;
    }


    private boolean isBestBow(ItemStack stack) {
        final Item item = stack.getItem();
        if (!(item instanceof ItemBow)) return false;
        final float value = getBowEffect(stack);

        for (int i = 9; i < 45; i++) {
            if (mc.player.getSlotFromPlayerContainer(i).getHasStack()) {
                final ItemStack slotStack = mc.player.getSlotFromPlayerContainer(i).getStack();
                if (getBowEffect(slotStack) > value && slotStack.getItem() instanceof ItemBow) return false;
            }
        }

        return true;
    }
}
