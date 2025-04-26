package net.pursue.mode.combat;

import io.netty.buffer.Unpooled;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CPacketCustomPayload;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.util.EnumHand;
import net.pursue.Nattalie;
import net.pursue.event.EventTarget;
import net.pursue.event.player.EventAttack;
import net.pursue.event.update.EventUpdate;
import net.pursue.mode.Mode;
import net.pursue.mode.player.AutoHeal;
import net.pursue.mode.player.Scaffold;
import net.pursue.utils.category.Category;
import net.pursue.value.values.ModeValue;
import net.pursue.value.values.NumberValue;

import static net.minecraft.enchantment.EnchantmentHelper.getEnchantmentLevel;

public class ArmorBreak extends Mode {

    private final ModeValue<mode> modeValue = new ModeValue<>(this, "Mode", mode.values(), mode.Normal);

    enum mode {
        Normal,
        SharpBug
    }

    private final NumberValue<Double> Health = new NumberValue<>(this,"Health", 5.0, 0.1, 20.0, 0.1);
    private final ModeValue<sendMode> sendModeModeValue = new ModeValue<>(this, "SendMode", sendMode.values(), sendMode.C08UseItem);

    enum sendMode {
        C08UseItem,
        C17,
        None
    }

    public ArmorBreak() {
        super("ArmorBreak", "自动武器", "自动选择伤害最高的武器", Category.COMBAT);
    }

    @EventTarget
    private void onUpdate(EventUpdate update) {
        setSuffix(this.modeValue.getValue().toString());
    }

    @EventTarget
    public void onAttack(EventAttack eventAttack) {
        if (Nattalie.instance.getModeManager().getByClass(AutoHeal.class).isEnable() || Nattalie.instance.getModeManager().getByClass(Scaffold.class).isEnable())
            return;

        switch (modeValue.getValue()) {
            case Normal -> {
                if (eventAttack.getType() == EventAttack.Type.Pre) {
                    if (getCounter() >= 0 && getCounter() != mc.player.inventory.currentItem) {
                        mc.player.inventory.currentItem = getCounter();
                        mc.playerController.updateController();
                    }
                }
            }

            case SharpBug -> {
                if (mc.player.getHealth() > Health.getValue().floatValue() && (mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemSword || mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemAxe)) {
                    if (eventAttack.getType() == EventAttack.Type.Pre) {
                        if (getGoodWeapon() >= 0) {
                            mc.player.connection.sendPacket(new CPacketHeldItemChange(getGoodWeapon()));
                            switch (sendModeModeValue.getValue()) {
                                case C17 -> mc.player.connection.sendPacket(new CPacketCustomPayload("bypass_hyt", new PacketBuffer(Unpooled.buffer())));
                                case C08UseItem -> mc.player.connection.sendPacket(new CPacketPlayerTryUseItem(EnumHand.OFF_HAND));
                            }
                        }
                    } else {
                        mc.player.connection.sendPacket(new CPacketHeldItemChange(mc.player.inventory.currentItem));
                    }
                } else {
                    if (getCounter() >= 0 && getCounter() != mc.player.inventory.currentItem) {
                        mc.player.inventory.currentItem = getCounter();
                        mc.playerController.updateController();
                    }
                }
            }
        }
    }

    private int getCounter() {
        ItemStack itemAxe = null;
        int swordSlot = -1;
        int axeSlot = -1;

        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.getSlotFromPlayerContainer(i + 36).getStack();

            if (!mc.player.inventoryContainer.getSlot(i + 36).getHasStack() || !(stack.getItem() instanceof ItemAxe) || !(getDamage(stack) > 100))
                continue;

            itemAxe = stack;
            axeSlot = i;
        }

        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.getSlotFromPlayerContainer(i + 36).getStack();

            if (!mc.player.inventoryContainer.getSlot(i + 36).getHasStack() || !(stack.getItem() instanceof ItemSword) || !isBestSword(stack))
                continue;

            swordSlot = i;
        }

        if (mc.player.getHealth() <= Health.getValue().floatValue() || getPlayerSize() < 3 && getPlayerSize() != 0) {
            if (itemAxe != null) {

                return axeSlot;
            } else return swordSlot;
        } else return swordSlot;
    }

    private boolean isBestSword(ItemStack stack) {
        final float damage = getDamage(stack);

        for (int i = 9; i < mc.player.inventoryContainer.inventorySlots.size(); i++) {
            if (mc.player.getSlotFromPlayerContainer(i).getHasStack()) {
                final ItemStack is = mc.player.getSlotFromPlayerContainer(i).getStack();
                if (getDamage(is) > damage && is.getItem() instanceof ItemSword) {
                    return false;
                }
            }
        }

        return stack.getItem() instanceof ItemSword;
    }

    private int getGoodWeapon() {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.getSlotFromPlayerContainer(i + 36).getStack();

            if (!mc.player.inventoryContainer.getSlot(i + 36).getHasStack() || !(stack.getItem() instanceof ItemSword || stack.getItem() instanceof ItemAxe) || i == mc.player.inventory.currentItem)
                continue;

            if (stack.getItem() instanceof ItemAxe) {
                if (getEnchantmentLevel(Enchantment.getEnchantmentByID(16), stack) > getEnchantmentLevel(Enchantment.getEnchantmentByID(16), mc.player.inventoryContainer.getSlot(mc.player.inventory.currentItem + 36).getStack()) && getDamage(stack) < 100) {
                    return i;
                }
            }

            if (stack.getItem() instanceof ItemSword) {
                if (getEnchantmentLevel(Enchantment.getEnchantmentByID(16), stack) > getEnchantmentLevel(Enchantment.getEnchantmentByID(16), mc.player.inventoryContainer.getSlot(mc.player.inventory.currentItem + 36).getStack())) {
                    return i;
                }
            }
        }

        return -1;
    }

    public static float getDamage(ItemStack stack) {
        float damage = 0;
        final Item item = stack.getItem();

        if (item instanceof ItemAxe) {
            damage += ((ItemAxe) item).getDamageVsEntity();
        } else if (item instanceof ItemSword) {
            damage += ((ItemSword) item).getDamageVsEntity();
        }

        damage += getEnchantmentLevel(Enchantment.getEnchantmentByID(16), stack) * 1.25F + getEnchantmentLevel(
                Enchantment.getEnchantmentByID(20), stack) * 0.01F;
        return damage;
    }

    private int getPlayerSize() {
        if (mc.getConnection() != null) {
            return mc.getConnection().getPlayerInfoMap().size() -1;
        }
        return 0;
    }
}
