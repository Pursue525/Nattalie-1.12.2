package net.pursue.mode.combat;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.pursue.Nattalie;
import net.pursue.event.EventTarget;
import net.pursue.event.packet.EventPacket;
import net.pursue.event.player.EventAttack;
import net.pursue.utils.category.Category;
import net.pursue.mode.Mode;
import net.pursue.mode.player.AutoHeal;
import net.pursue.mode.player.Scaffold;
import net.pursue.value.values.NumberValue;

import static net.minecraft.enchantment.EnchantmentHelper.getEnchantmentLevel;

public class ArmorBreak extends Mode {

    private final NumberValue<Double> Health = new NumberValue<>(this,"Health", 10.0, 0.1, 20.0, 0.1);

    public ArmorBreak() {
        super("ArmorBreak", "破甲", "自动选择伤害最高的武器", Category.COMBAT);
    }

    private boolean attackEnemy;

    @EventTarget
    public void onAttack(EventAttack eventAttack) {
        attackEnemy = true;
    }

    @EventTarget
    public void onPacket(EventPacket eventPacketSend) {
        Packet<?> packet = eventPacketSend.getPacket();

        if (Nattalie.instance.getModeManager().getByClass(AutoHeal.class).isEnable() || Nattalie.instance.getModeManager().getByClass(Scaffold.class).isEnable()) return;

        if (packet instanceof CPacketUseEntity cPacketUseEntity) {

            if (cPacketUseEntity.getAction() == CPacketUseEntity.Action.ATTACK && attackEnemy) {
                attackEnemy = false;

                if (getCounter() == mc.player.inventory.currentItem || getCounter() < 0 || getCounter() > 9) return;

                mc.player.inventory.currentItem = getCounter();
                mc.playerController.updateController();
            }

            mc.player.connection.sendPacketNoEvent(packet);
            eventPacketSend.cancelEvent();
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
        int i = 0;
        for (EntityPlayer entity : mc.world.playerEntities) {
            if (!entity.isDead && entity != mc.player) {
                i++;
            }
        }
        return i;
    }
}
