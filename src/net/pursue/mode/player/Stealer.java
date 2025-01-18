package net.pursue.mode.player;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBrewingStand;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockFurnace;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemAir;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketClickWindow;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.server.SPacketBlockAction;
import net.minecraft.network.play.server.SPacketOpenWindow;
import net.minecraft.tileentity.TileEntityBrewingStand;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.pursue.Nattalie;
import net.pursue.event.EventTarget;
import net.pursue.event.packet.EventPacket;
import net.pursue.event.update.EventTick;

import net.pursue.event.update.EventUpdate;
import net.pursue.event.world.EventWorldLoad;
import net.pursue.mode.Mode;
import net.pursue.mode.combat.KillAura;
import net.pursue.mode.hud.ClickGUI;
import net.pursue.ui.notification.NotificationType;
import net.pursue.utils.Block.BezierUtil;
import net.pursue.utils.TimerUtils;
import net.pursue.utils.category.Category;
import net.pursue.utils.client.DebugHelper;
import net.pursue.utils.player.PacketUtils;
import net.pursue.value.values.BooleanValue;
import net.pursue.value.values.NumberValue;

import java.util.*;

public class Stealer extends Mode {

    public static Stealer instance;

    public final BooleanValue<Boolean> silent = new BooleanValue<>(this, "SilentGui", false);
    private final NumberValue<Number> delay = new NumberValue<>(this, "Delay", 0,0,100,10);
    private final BooleanValue<Boolean> arua = new BooleanValue<>(this, "ChestAura",false);

    public Stealer() {
        super("Stealer", "容器小偷", "偷走可打开容器的物品", Category.PLAYER);
        instance = this;
    }

    private final TimerUtils timer = new TimerUtils();

    public BlockPos currentChest = null;
    private BlockPos lastC08 = null;

    public int count = 0;
    public BezierUtil progress = new BezierUtil(4, 0);

    public final HashSet<BlockPos> stolen = new HashSet<>();
    
    @Override
    public void enable() {
        timer.reset();
        stolen.clear();
    }

    @EventTarget
    private void onUpdate(EventUpdate eventUpdate) {
        if (mc.player.openContainer.windowId == 0) {
            if (arua.getValue() && (!Blink.instance.isEnable() || KillAura.INSTANCE.target != null || Scaffold.INSTANCE.isEnable())) {
                final var tile = mc.world.loadedTileEntityList.stream()
                        .filter(container -> container instanceof TileEntityChest || container instanceof TileEntityFurnace || container instanceof TileEntityBrewingStand)
                        .filter(entity -> !stolen.contains(entity.getPos()))
                        .filter(tileEntity -> mc.player.getDistance(tileEntity.getPos().getX(), tileEntity.getPos().getY(), tileEntity.getPos().getZ()) <= 4.5F).min(Comparator.comparingDouble(entity -> mc.player.getDistanceSq(entity.getPos())));
                if (tile.isPresent()) {
                    final var container = tile.get();
                    if (mc.currentScreen == null) {
                        mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(container.getPos(), Stealer.getFacingDirection(container.getPos()), EnumHand.MAIN_HAND, 0, 0, 0));
                        stolen.add(container.getPos());
                    }
                }
            }
        }


        if (mc.player.openContainer instanceof ContainerFurnace container) {
            if (isFurnaceEmpty(container) || isInventoryFull()) {
                mc.player.closeScreen();
                Nattalie.instance.getNotificationManager().post(ClickGUI.instance.chinese.getValue() ? "熔炉" : "Furnace", ClickGUI.instance.chinese.getValue() ? "关闭" : "Closed", 1000, NotificationType.WARNING);
                return;
            }
            for (int i = 0; i < container.tileFurnace.getSizeInventory(); ++i) {
                if (container.tileFurnace.getStackInSlot(i).func_190926_b() || !timer.hasTimePassed(delay.getValue().intValue())) continue;
                mc.player.connection.sendPacket(new CPacketClickWindow(container.windowId, i, 0, ClickType.QUICK_MOVE, container.getSlot(i).getStack(), container.getNextTransactionID(mc.player.inventory)));
                timer.reset();
            }
        }
        if (mc.player.openContainer instanceof ContainerBrewingStand container && timer.hasTimePassed(delay.getValue().intValue())) {
            if (isBrewingStandEmpty(container) || isInventoryFull()) {
                mc.player.closeScreen();
                Nattalie.instance.getNotificationManager().post(ClickGUI.instance.chinese.getValue() ? "酿造台" : "BrewingStand", ClickGUI.instance.chinese.getValue() ? "关闭" : "Closed", 1000, NotificationType.WARNING);
                return;
            }
            for (int i = 0; i < container.tileBrewingStand.getSizeInventory(); ++i) {
                if (container.tileBrewingStand.getStackInSlot(i).func_190926_b() || !timer.hasTimePassed(delay.getValue().intValue())) continue;
                mc.player.connection.sendPacket(new CPacketClickWindow(container.windowId, i, 0, ClickType.QUICK_MOVE, container.getSlot(i).getStack(), container.getNextTransactionID(mc.player.inventory)));
                timer.reset();
            }
        }
        if (mc.player.openContainer instanceof ContainerChest container && timer.hasTimePassed(delay.getValue().intValue())) {
            if (isChestEmpty(container) || isInventoryFull()) {
                mc.player.closeScreen();
                Nattalie.instance.getNotificationManager().post(ClickGUI.instance.chinese.getValue() ? "箱子" : "Chest", ClickGUI.instance.chinese.getValue() ? "关闭" : "Closed", 1000, NotificationType.WARNING);
                return;
            }
            for (int i = 0; i < container.getLowerChestInventory().getSizeInventory(); ++i) {
                if (container.getLowerChestInventory().getStackInSlot(i).func_190926_b() || !timer.hasTimePassed(delay.getValue().intValue())) continue;
                mc.player.connection.sendPacket(new CPacketClickWindow(container.windowId, i, 0, ClickType.QUICK_MOVE, container.getSlot(i).getStack(), container.getNextTransactionID(mc.player.inventory)));
                timer.reset();
            }
        }
    }

    @EventTarget
    public void onPacket(EventPacket event) {
        if (!silent.getValue()) return;

        if (event.getPacket() instanceof CPacketPlayerTryUseItemOnBlock packet) {
            if (mc.currentScreen instanceof GuiContainer) {
                event.setCancelled(true);
                DebugHelper.sendMessage("哎呀呀，你已经开启这个容器了呢，不小心又开了一次啦~");
                return;
            }
            lastC08 = packet.getPos();
            count = -2;
        }
        if (event.getPacket() instanceof SPacketOpenWindow packet && lastC08 != null) {
            Block block = mc.world.getBlockState(lastC08).getBlock();
            if (block instanceof BlockFurnace || block instanceof BlockBrewingStand || (block instanceof BlockChest && checkTitle(packet.getWindowTitle()))) {
                currentChest = lastC08;
                count = -1;
            }
        }
    }

    @EventTarget
    private void onWorld(EventWorldLoad eventWorldLoad) {
        stolen.clear();
    }

    public static boolean checkTitle(ITextComponent textComponent) {
        return textComponent.getFormattedText().toLowerCase().contains(new ItemStack(Blocks.CHEST).getDisplayName().toLowerCase());
    }

    private boolean isChestEmpty(ContainerChest c) {
        for (int i = 0; i < 27; ++i) {

            if (!c.getLowerChestInventory().getStackInSlot(i).func_190926_b()) {
                return false;
            }
        }
        return true;
    }

    private boolean isFurnaceEmpty(ContainerFurnace c) {
        for (int i = 0; i < 3; ++i) {
            if (!c.tileFurnace.getStackInSlot(i).func_190926_b()) {
                return false;
            }
        }
        return true;
    }

    private boolean isBrewingStandEmpty(ContainerBrewingStand c) {
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

    public static EnumFacing getFacingDirection(final BlockPos pos) {
        EnumFacing direction = null;
        if (!mc.world.getBlockState(pos.add(0, 1, 0)).getBlock().isFullBlock(null)) {
            direction = EnumFacing.UP;
        }
        final RayTraceResult rayResult = mc.world.rayTraceBlocks(new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ), new Vec3d(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5));
        if (rayResult != null) {
            return rayResult.sideHit;
        }
        return direction;
    }
}
