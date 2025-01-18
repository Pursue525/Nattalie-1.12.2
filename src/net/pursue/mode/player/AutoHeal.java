package net.pursue.mode.player;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAppleGold;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;
import net.minecraft.util.EnumHand;
import net.pursue.event.EventTarget;
import net.pursue.event.packet.EventPacket;
import net.pursue.event.player.EventTickMotion;
import net.pursue.event.update.EventMotion;
import net.pursue.event.update.EventUpdate;
import net.pursue.event.world.EventWorldLoad;
import net.pursue.utils.category.Category;
import net.pursue.mode.Mode;
import net.pursue.mode.combat.KillAura;
import net.pursue.shield.IsShield;
import net.pursue.utils.player.InvUtils;
import net.pursue.utils.player.PacketUtils;
import net.pursue.utils.TimerUtils;
import net.pursue.utils.client.DebugHelper;
import net.pursue.value.values.ModeValue;
import net.pursue.value.values.NumberValue;

import java.util.LinkedList;

@IsShield
public class AutoHeal extends Mode {

    public static AutoHeal instance;

    public final ModeValue<mode> modeValue = new ModeValue<>(this,"Mode", mode.values(), mode.Golden_Apple);

    public enum mode {
        Golden_Apple,
        Player_skull,
        Mushroom_Stew
    }

    private final ModeValue<appleMode> appleModeValue = new ModeValue<>(this,"AppleMode", appleMode.values(), appleMode.Stuck);

    enum appleMode {
        Move,
        Stuck
    }

    private final NumberValue<Number> health = new NumberValue<>(this, "Health", 8.0,1.0,20.0,0.5, () -> modeValue.getValue() != mode.Golden_Apple);
    private final NumberValue<Number> delay = new NumberValue<>(this, "Delay", 100,100,1000,10, () -> modeValue.getValue() != mode.Golden_Apple);
    private final NumberValue<Number> ticks = new NumberValue<>(this, "Tick", 3,2,10,1, () -> modeValue.getValue() == mode.Golden_Apple);
    private final NumberValue<Number> health2 = new NumberValue<>(this, "LowHealth", 8.0,1.0,20.0,0.5, () -> modeValue.getValue() == mode.Golden_Apple);

    public AutoHeal() {
        super("AutoHeal", "自动回血", "自动使用可以回血的物品", Category.PLAYER);
        instance = this;
    }

    private final TimerUtils timerUtils = new TimerUtils();
    private final LinkedList<Packet<?>> packets = new LinkedList<Packet<?>>();

    private int tick;
    private int sb = 0;


    @Override
    public void enable() {
        packets.clear();
        timerUtils.reset();
        tick = 0;
        sb = 0;
    }

    @Override
    public void disable() {
        release();
        sb =0;
    }

    @EventTarget
    private void onWorld(EventWorldLoad eventWorldLoad) {
        timerUtils.reset();
    }

    @EventTarget
    private void onMotion(EventTickMotion tickMotion) {
        if (modeValue.getValue() == mode.Golden_Apple) {
            if (appleModeValue.getValue().equals(appleMode.Stuck)) {
                tickMotion.cancelEvent();
            }
        }
    }

    @EventTarget
    private void onPacket(EventPacket eventPacket) {
        Packet<?> packet = eventPacket.getPacket();

        if (mc.player == null) return;

        if (modeValue.getValue() == mode.Golden_Apple) {
            if (packet instanceof CPacketPlayer
                    || packet instanceof CPacketConfirmTransaction
                    || packet instanceof CPacketAnimation
                    || packet instanceof CPacketUseEntity
                    || packet instanceof CPacketEntityAction) {
                eventPacket.cancelEvent();
                packets.add(packet);
            }

            if (packet instanceof CPacketClickWindow || packet instanceof CPacketPlayerDigging || packet instanceof CPacketHeldItemChange || packet instanceof CPacketCloseWindow) {
                eventPacket.cancelEvent();
            }

            if (packet instanceof CPacketPlayer) tick++;
        }
    }

    @EventTarget
    private void onMotion(EventMotion eventMotion) {
        if (modeValue.getValue() == mode.Golden_Apple) {
            if (getAppleGold() < 0) {
                sb++;
            }

            if (sb > 7) {
                DebugHelper.sendMessage("背包没有任何金苹果！");
                setEnable(false);
            }

            if (tick > 0 && tick % 6 == 0) {
                DebugHelper.sendMessage(tick);
            }

            if (eventMotion.getType() == EventMotion.Type.Pre) {
                if (tick > 32) {
                    mc.player.connection.sendPacketNoEvent(new CPacketHeldItemChange(getAppleGold()));
                    mc.player.connection.sendPacketNoEvent(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));
                    release();
                    mc.player.connection.sendPacketNoEvent(new CPacketHeldItemChange(mc.player.inventory.currentItem));
                    tick = 0;
                } else {
                    if (KillAura.INSTANCE.isEnable() && KillAura.INSTANCE.target != null && health2.getValue().floatValue() < mc.player.getHealth()) {
                        if (mc.player.ticksExisted % ticks.getValue().intValue() == 0) {
                            while (!packets.isEmpty()) {
                                Packet<?> packet = packets.poll();

                                if (packet instanceof CPacketChatMessage) break;

                                if (packet instanceof CPacketPlayer) {
                                    tick--;
                                }

                                PacketUtils.sendPacketNoEvent(packet);
                            }
                        }
                    }
                }
            }
            if (eventMotion.getType() == EventMotion.Type.Post) {
                packets.add(new CPacketChatMessage());
            }
        }
    }

    @EventTarget
    private void onUpdate(EventUpdate eventUpdate) {
        if (modeValue.getValue() == mode.Mushroom_Stew) {
            for (int i = 0; i < 9; i++) {
                Item stack = mc.player.inventoryContainer.getSlot(i + 36).getStack().getItem();

                if (Items.BOWL.getUnlocalizedName().equals(stack.getUnlocalizedName())) {
                    InvUtils.drop(i + 36);
                    break;
                }
            }

            for (int i = 0; i < 36; i++) {
                ItemStack stack = mc.player.getSlotFromPlayerContainer(i).getStack();

                if (stack.getItem() == Items.MUSHROOM_STEW && getNoItemSlot() >= 0) {
                    InvUtils.swap(i, getNoItemSlot());
                    break;
                }
            }
        }

        if (mc.player.getHealth() < health.getValue().floatValue()) {
            if (timerUtils.hasTimePassed((long) (delay.getValue().floatValue() * 1000))) {
                switch ((mode) ((Object) modeValue.getValue())) {
                    case Golden_Apple -> {
                        if (getGolden_Apple() >= 0) {
                            mc.player.connection.sendPacket(new CPacketHeldItemChange(getGolden_Apple()));
                            mc.player.connection.sendPacket(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));
                            mc.player.connection.sendPacket(new CPacketHeldItemChange(mc.player.inventory.currentItem));
                            timerUtils.reset();
                        }
                    }
                    case Mushroom_Stew -> {
                        if (getMushroom_Stew() >= 0) {
                            mc.player.connection.sendPacket(new CPacketHeldItemChange(getMushroom_Stew()));
                            mc.player.connection.sendPacket(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));
                            mc.player.connection.sendPacket(new CPacketHeldItemChange(mc.player.inventory.currentItem));
                            timerUtils.reset();
                        }
                    }
                }
            }
        }
    }

    private int getGolden_Apple() {
        for (int i = 0; i < 9; i++) {
            if (!mc.player.inventoryContainer.getSlot(i + 36).getHasStack() || mc.player.inventoryContainer.getSlot(i + 36).getStack().getItem() != Item.getByNameOrId("skull")) continue;

            return i;
        }
        return -1;
    }

    private int getMushroom_Stew() {
        for (int i = 0; i < 9; i++) {
            if (!mc.player.inventoryContainer.getSlot(i + 36).getHasStack() || mc.player.inventoryContainer.getSlot(i + 36).getStack().getItem() != Items.MUSHROOM_STEW) continue;
            return i;
        }
        return -1;
    }

    private int getNoItemSlot() {
        for (int i = 0; i < 9; i++) {
            if (mc.player.inventoryContainer.getSlot(i + 36).getHasStack()) continue;

            return i;
        }
        return -1;
    }

    private int getAppleGold() {
        if (mc.player != null) {
            for (int i = 0; i < 9; ++i) {
                if (!mc.player.inventoryContainer.getSlot(i + 36).getHasStack() || !(mc.player.inventoryContainer.getSlot(i + 36).getStack().getItem() instanceof ItemAppleGold))
                    continue;
                return i;
            }
        }
        return -1;
    }

    private void release() {
        while (!packets.isEmpty()) {
            Packet<?> packet = packets.poll();

            if (packet instanceof CPacketChatMessage) continue;

            mc.player.connection.sendPacketNoEvent(packet);
        }
    }
}
