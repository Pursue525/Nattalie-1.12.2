package net.pursue.mode.player;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBrewingStand;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockFurnace;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.ContainerBrewingStand;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.ContainerFurnace;
import net.minecraft.item.ItemAir;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketClickWindow;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.server.SPacketCloseWindow;
import net.minecraft.network.play.server.SPacketOpenWindow;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.pursue.event.EventTarget;
import net.pursue.event.packet.EventPacket;
import net.pursue.event.update.EventUpdate;
import net.pursue.mode.Mode;
import net.pursue.mode.combat.KillAura;
import net.pursue.utils.Block.BezierUtil;
import net.pursue.utils.TimerUtils;
import net.pursue.utils.category.Category;
import net.pursue.value.values.BooleanValue;
import net.pursue.value.values.NumberValue;

public class Stealer extends Mode {

    public static Stealer instance;

    public final BooleanValue<Boolean> silent = new BooleanValue<>(this, "SilentGui", false);
    private final NumberValue<Number> delay = new NumberValue<>(this, "Delay", 0,0,100,10);

    public Stealer() {
        super("Stealer", "容器小偷", "偷走可打开容器的物品", Category.PLAYER);
        instance = this;
    }

    private final TimerUtils timer = new TimerUtils();
    public BlockPos currentChest = null;
    private BlockPos lastC08 = null;

    public static boolean isScreen;

    public int count = 0;
    public BezierUtil progress = new BezierUtil(4, 0);
    
    @Override
    public void enable() {
        timer.reset();
    }

    @EventTarget
    private void onUpdate(EventUpdate eventUpdate) {

        if (KillAura.INSTANCE.target != null) {
            stopSrealer();
            return;
        }

        if (Scaffold.INSTANCE.isScaffold) {
            stopSrealer();
            return;
        }

        if (Blink.instance.isEnable()) {
            stopSrealer();
            return;
        }

        if (mc.currentScreen != null) {
            if (mc.player.openContainer instanceof ContainerFurnace container) {
                if (isInventoryFull() || isFurnaceEmpty(container)) {
                    stopSrealer();
                    return;
                }
                for (int i = 0; i < container.tileFurnace.getSizeInventory(); ++i) {
                    if (container.tileFurnace.getStackInSlot(i).func_190926_b()) continue;

                    mc.player.connection.sendPacket(new CPacketClickWindow(container.windowId, i, 0, ClickType.QUICK_MOVE, container.getSlot(i).getStack(), container.getNextTransactionID(mc.player.inventory)));
                }
            } else if (mc.player.openContainer instanceof ContainerBrewingStand container && timer.hasTimePassed(delay.getValue().intValue())) {
                if (isInventoryFull() || isBrewingStandEmpty(container)) {
                    stopSrealer();
                    return;
                }
                for (int i = 0; i < container.tileBrewingStand.getSizeInventory(); ++i) {
                    if (container.tileBrewingStand.getStackInSlot(i).func_190926_b()) continue;

                    mc.player.connection.sendPacket(new CPacketClickWindow(container.windowId, i, 0, ClickType.QUICK_MOVE, container.getSlot(i).getStack(), container.getNextTransactionID(mc.player.inventory)));
                }
            } else if (mc.player.openContainer instanceof ContainerChest container && timer.hasTimePassed(delay.getValue().intValue())) {
                if (isInventoryFull() || isChestEmpty(container)) {
                    stopSrealer();
                    return;
                }
                for (int i = 0; i < container.getLowerChestInventory().getSizeInventory(); ++i) {
                    if (container.getLowerChestInventory().getStackInSlot(i).func_190926_b()) continue;

                    mc.player.connection.sendPacket(new CPacketClickWindow(container.windowId, i, 0, ClickType.QUICK_MOVE, container.getSlot(i).getStack(), container.getNextTransactionID(mc.player.inventory)));
                }
            }
        } else {
            isScreen = false;
        }
    }

    @EventTarget
    public void onPacket(EventPacket event) {
        if (!silent.getValue()) return;

        if (event.getPacket() instanceof CPacketPlayerTryUseItemOnBlock packet) {
            lastC08 = packet.getPos();
            count = -2;
        }
        if (event.getPacket() instanceof SPacketOpenWindow packet) {
            isScreen = true;

            if (lastC08 != null) {
                Block block = mc.world.getBlockState(lastC08).getBlock();
                if (block instanceof BlockFurnace || block instanceof BlockBrewingStand || (block instanceof BlockChest && checkTitle(packet.getWindowTitle()))) {
                    currentChest = lastC08;
                    count = -1;

                } else lastC08 = null;
            }
        }

        if (event.getPacket() instanceof SPacketCloseWindow) {
            isScreen = false;
        }
    }

    public static boolean checkTitle(ITextComponent textComponent) {
        return textComponent.getFormattedText().toLowerCase().contains(new ItemStack(Blocks.CHEST).getDisplayName().toLowerCase());
    }

    public static boolean isChestEmpty(ContainerChest c) {
        for (int i = 0; i < 27; ++i) {

            if (!c.getLowerChestInventory().getStackInSlot(i).func_190926_b()) {
                return false;
            }
        }
        return true;
    }

    public static boolean isFurnaceEmpty(ContainerFurnace c) {
        for (int i = 0; i < 3; ++i) {
            if (!c.tileFurnace.getStackInSlot(i).func_190926_b()) {
                return false;
            }
        }
        return true;
    }

    public static boolean isBrewingStandEmpty(ContainerBrewingStand c) {
        for (int i = 0; i < 5; ++i) {
            if (!c.tileBrewingStand.getStackInSlot(i).func_190926_b()) {
                return false;
            }
        }
        return true;
    }

    private boolean isInventoryFull() {
        for (int i = 9; i < 45; i++) {
            if (mc.player.inventoryContainer.getSlot(i).getStack().getItem() instanceof ItemAir) {
                return false;
            }
        }
        return true;
    }

    public static void stopSrealer() {
        mc.player.closeScreen();
        isScreen = false;
        ContainerAura.pos = null;
    }
}
