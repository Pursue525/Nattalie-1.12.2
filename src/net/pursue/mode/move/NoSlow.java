package net.pursue.mode.move;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.*;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketClickWindow;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.network.play.server.SPacketOpenWindow;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.pursue.event.EventTarget;
import net.pursue.event.packet.EventPacket;
import net.pursue.event.player.EventSlow;
import net.pursue.event.render.EventRender2D;
import net.pursue.event.update.EventMotion;
import net.pursue.event.update.EventTick;
import net.pursue.event.update.EventUpdate;
import net.pursue.event.world.EventWorldLoad;
import net.pursue.ui.font.FontManager;
import net.pursue.utils.TimerUtils;
import net.pursue.utils.category.Category;
import net.pursue.mode.Mode;
import net.pursue.mode.combat.KillAura;
import net.pursue.mode.player.AutoHeal;
import net.pursue.utils.client.DebugHelper;
import net.pursue.utils.player.InvUtils;
import net.pursue.utils.player.PacketUtils;
import net.pursue.utils.render.RoundedUtils;
import net.pursue.value.values.BooleanValue;
import net.pursue.value.values.ModeValue;

import java.awt.*;

public class NoSlow extends Mode {
    public static NoSlow INSTANCE;
    public final ModeValue<mode> modeValue = new ModeValue<>(this, "Mode", mode.values(), mode.Grim);

    public enum mode {
        Grim,
        HighGrim,
        Normal
    }

    private final ModeValue<swordMod> swordModModeValue = new ModeValue<>(this,"SwordMode", swordMod.values(), swordMod.DoubleC09, () -> modeValue.getValue() == mode.Grim);

    enum swordMod {
        OFF,
        PreC07,
        DoubleC09
    }

    public final ModeValue<Enum<?>> foodModeValue = new ModeValue<>(this,"FoodMode", foodMd.values(), foodMd.DropC07, () -> modeValue.getValue() == mode.Grim);

    public enum foodMd {
        GrimBug,
        DropC07,
        C0E,
        OFF
    }

    private final ModeValue<Enum<?>> bowModeValue = new ModeValue<>(this,"BowMode", bowMod.values(), bowMod.C0E, () -> modeValue.getValue() == mode.Grim);

    enum bowMod {
        DoubleC09,
        OFF,
        C0E,
    }

    private final BooleanValue<Boolean> UseSlow = new BooleanValue<>(this, "Slow", true, () -> modeValue.getValue() == mode.HighGrim);
    private final BooleanValue<Boolean> render = new BooleanValue<>(this, "RenderUseItem", true);


    public NoSlow() {
        super("NoSlow", "无使用物品减速", "移除你使用物品时的减速", Category.MOVE);
        INSTANCE = this;
    }

    public boolean slow;
    private boolean drop;
    private boolean windows;
    private boolean isHighGrim;
    private int tick;

    @EventTarget
    private void onWorld(EventWorldLoad eventWorldLoad) {
        tick = 0;
        isHighGrim = false;
    }

    @EventTarget
    private void onPacket(EventPacket event) {
        Packet<?> packet = event.getPacket();

        if (mc.player == null) return;

        ItemStack stack = mc.player.getHeldItem(EnumHand.MAIN_HAND);

        if (modeValue.getValue().equals(mode.Grim)) {
            switch ((foodMd) foodModeValue.getValue()) {
                case DropC07 -> {
                    if (InvUtils.isGoldenApple(stack)) {
                        if (packet instanceof CPacketPlayerTryUseItem) {
                            isHighGrim = true;
                            tick = 0;
                            slow = true;
                            if (!drop && stack.stackSize - 1 > 2) {
                                PacketUtils.send(new CPacketPlayerDigging(CPacketPlayerDigging.Action.DROP_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                                drop = true;
                            }
                        }

                        if (packet instanceof SPacketSetSlot && slow && drop) {
                            event.cancelEvent();
                            slow = false;
                        }

                        if (packet instanceof CPacketPlayerDigging digging && digging.getAction() == CPacketPlayerDigging.Action.RELEASE_USE_ITEM) {
                            if (stack.stackSize - 1 > 2) {
                                event.cancelEvent();
                            } else {
                                tick = 32;
                            }
                        }
                    }
                }
                case GrimBug -> {
                    if (stack.getItem() instanceof ItemFood) {
                        if (packet instanceof CPacketPlayerTryUseItem) {
                            slow = true;
                            tick++;
                        }

                        if (tick > 1 && slow) {
                            new Thread(() -> {
                                try {
                                    Thread.sleep(100L);
                                    slow = false;
                                } catch (InterruptedException ex) {
                                    ex.printStackTrace();
                                }
                            }).start();
                        }
                    }
                }

                case C0E -> {
                    if (stack.getItem() instanceof ItemFood || (stack.getItem() instanceof ItemPotion && !(mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemSplashPotion))) {
                        if (packet instanceof CPacketPlayerTryUseItem && !slow) {
                            isHighGrim = true;
                            tick = 0;
                            mc.player.sendChatMessage("/lizi open");
                            slow = true;
                            windows = true;
                        }

                        if (packet instanceof SPacketOpenWindow && slow) {
                            event.cancelEvent();
                            slow = false;
                        }
                    }
                }
            }
            if (bowModeValue.getValue().equals(bowMod.C0E) && stack.getItem() instanceof ItemBow) {
                if (packet instanceof CPacketPlayerTryUseItem && !slow) {
                    isHighGrim = true;
                    tick = 0;
                    mc.player.sendChatMessage("/lizi open");
                    slow = true;
                    windows = true;
                }

                if (packet instanceof SPacketOpenWindow && slow) {
                    event.cancelEvent();
                    slow = false;
                }
            }
        } else if (modeValue.getValue().equals(mode.HighGrim)) {
            if (mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemSword) {
                if (packet instanceof CPacketPlayerTryUseItem item && item.getHand() == EnumHand.MAIN_HAND) {
                    event.cancelEvent();
                }
            }

            if (mc.player.getHeldItem(EnumHand.OFF_HAND).getItem() instanceof ItemAppleGold) {
                if (packet instanceof CPacketPlayerTryUseItem) {
                    slow = true;
                    isHighGrim = true;
                    tick = 0;
                    mc.player.connection.sendPacket(new CPacketClickWindow(0, 36, 0, ClickType.SWAP, new ItemStack(Blocks.BARRIER), mc.player.inventoryContainer.getNextTransactionID(mc.player.inventory)));
                }

                if (packet instanceof CPacketPlayerDigging && isHighGrim) {
                    event.cancelEvent();
                }
            }
        }
    }

    @EventTarget
    private void onMotion(EventMotion eventMotion) {
        if (modeValue.getValue().equals(mode.Grim)) {
            if (AutoHeal.instance.isEnable() && AutoHeal.instance.modeValue.getValue().equals(AutoHeal.mode.Golden_Apple)) return;

            if (eventMotion.getType() == EventMotion.Type.Pre) {
                if (mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemSword && (mc.player.isHandActive() || KillAura.INSTANCE.isBlock)) {
                    switch ((swordMod) swordModModeValue.getValue()) {
                        case DoubleC09 -> {
                            PacketUtils.send(new CPacketHeldItemChange(mc.player.inventory.currentItem + 1));
                            PacketUtils.send(new CPacketPlayerTryUseItem(EnumHand.OFF_HAND));
                            PacketUtils.send(new CPacketHeldItemChange(mc.player.inventory.currentItem));
                        }
                        case PreC07 -> PacketUtils.send(new CPacketPlayerDigging(CPacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                    }
                }
                if (mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemBow && mc.player.isHandActive()) {
                    if (bowModeValue.getValue() == bowMod.DoubleC09) {
                        PacketUtils.send(new CPacketHeldItemChange(mc.player.inventory.currentItem + 1));
                        PacketUtils.send(new CPacketPlayerTryUseItem(EnumHand.OFF_HAND));
                        PacketUtils.send(new CPacketHeldItemChange(mc.player.inventory.currentItem));
                    }
                }
            }
            if (eventMotion.getType() == EventMotion.Type.Post) {
                if ((mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemBow || mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemSword) && mc.player.isHandActive()) {
                    PacketUtils.send(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));
                }
            }
        }
    }

    @EventTarget
    private void onRender2D(EventRender2D event) {

        ScaledResolution sr = event.getScaledResolution();

        if (mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemFood || mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemPotion || mc.player.getHeldItem(EnumHand.OFF_HAND).getItem() instanceof ItemFood) {
            if (isHighGrim && render.getValue()) {
                float width = tick * 3;
                float x = sr.getScaledWidth() / 2F - 106;
                float y = sr.getScaledHeight() / 2F - 2;

                RoundedUtils.drawRound(x, y, 99, 4, 0, Color.gray);
                RoundedUtils.drawRound(x, y, width, 4, 0, Color.WHITE);
            }
        } else {
            isHighGrim = false;
            tick = 0;
        }
    }

    @EventTarget
    private void onUpdate(EventUpdate eventUpdate) {
        if (isHighGrim) {
            ++tick;
        }

        if (tick > 32) {
            isHighGrim = false;
        }

        if (modeValue.getValue().equals(mode.Normal) || (mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemBow || mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemSword)) {
            slow = false;
        }

        if (!mc.gameSettings.keyBindUseItem.isKeyDown()) {
            drop = false;
            if (modeValue.getValue().equals(mode.Grim) && foodModeValue.getValue().equals(foodMd.C0E) && !mc.player.isHandActive() && windows) {
                mc.player.closeScreen();
                windows = false;
            }
            if (modeValue.getValue().equals(mode.Grim) && foodModeValue.getValue().equals(foodMd.GrimBug)) {
                tick = 0;
            }
            slow = false;
        }

        if (modeValue.getValue().equals(mode.Grim) && foodModeValue.getValue().equals(foodMd.DropC07) && mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemPotion && !(mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemSplashPotion) && mc.player.isHandActive()) {
            slow = true;
        }

        if (modeValue.getValue().equals(mode.Grim) && InvUtils.isEnchantedGoldenApple(mc.player.getHeldItem(EnumHand.MAIN_HAND)) && mc.player.isHandActive()) {
            slow = true;
        }

        if (modeValue.getValue().equals(mode.HighGrim) && (mc.player.isHandActive() || isHighGrim) && !(mc.player.getHeldItem(EnumHand.OFF_HAND).getItem() instanceof ItemFood || mc.player.getHeldItem(EnumHand.OFF_HAND).getItem() instanceof ItemPotion)) {
            slow = true;
        }
    }

    @EventTarget
    private void onSlow(EventSlow eventSlow) {
        if (modeValue.getValue().equals(mode.HighGrim) && UseSlow.getValue()) {
            eventSlow.setSlow(isHighGrim);
        }

        eventSlow.setCancelled(!slow);
    }
}
