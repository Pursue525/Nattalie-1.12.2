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
import net.pursue.event.EventTarget;
import net.pursue.event.packet.EventPacket;
import net.pursue.event.update.EventTick;
import net.pursue.utils.category.Category;
import net.pursue.mode.Mode;
import net.pursue.utils.Block.BezierUtil;
import net.pursue.utils.category.MoveCategory;
import net.pursue.utils.TimerUtils;
import net.pursue.utils.rotation.RotationUtils;
import net.pursue.utils.rotation.SilentRotation;
import net.pursue.value.values.BooleanValue;
import net.pursue.value.values.NumberValue;

import javax.vecmath.Vector2f;
import java.util.*;

public class Stealer extends Mode {

    public static Stealer instance;

    public final BooleanValue<Boolean> silent = new BooleanValue<>(this, "SilentGui", false);
    private final NumberValue<Number> firstDelay = new NumberValue<>(this, "First Delay", 0,0,100,5);
    private final NumberValue<Number> delayValue = new NumberValue<>(this, "Delay", 0,0,100,5);
    private final NumberValue<Number> closeDelayValue = new NumberValue<>(this, "Close Delay", 0,0,100,5);
    public final BooleanValue<Boolean> aura = new BooleanValue<>(this, "Aura", false);
    private final NumberValue<Number> auraDelay = new NumberValue<>(this, "AuraDelay", 0,0,100,5);

    public Stealer() {
        super("Stealer", "容器小偷", "偷走可打开容器的物品", Category.PLAYER);
        instance = this;
    }

    private final TimerUtils firstDelayTimer = new TimerUtils();
    private final TimerUtils delayTimer = new TimerUtils();
    private final TimerUtils closeTimer = new TimerUtils();
    private final TimerUtils auraDelayTimer = new TimerUtils();

    public final HashSet<BlockPos> stolen = new HashSet<>();

    public BlockPos currentChest = null;
    private BlockPos lastC08 = null;

    public int count = 0;
    public BezierUtil progress = new BezierUtil(4, 0);

    @EventTarget
    private void onSteal(EventTick eventTick) {
        if (mc.player.openContainer.windowId == 0) {
            firstDelayTimer.reset();
            /*
             * Chest Aura
             */
            if (aura.getValue() && !Blink.instance.isEnable() && !Scaffold.INSTANCE.isEnable() && !AutoHeal.instance.isEnable() && AutoHeal.instance.modeValue.getValue().equals(AutoHeal.mode.Golden_Apple)) {
                final var tile = mc.world.loadedTileEntityList.stream()
                        .filter(container -> container instanceof TileEntityChest || container instanceof TileEntityFurnace || container instanceof TileEntityBrewingStand)
                        .filter(entity -> !stolen.contains(entity.getPos()))
                        .filter(tileEntity -> mc.player.getDistance(tileEntity.getPos().getX(), tileEntity.getPos().getY(), tileEntity.getPos().getZ()) <= 4.5F).min(Comparator.comparingDouble(entity -> mc.player.getDistanceSq(entity.getPos())));
                if (tile.isPresent() && auraDelayTimer.hasTimePassed(auraDelay.getValue().intValue())) {
                    final var container = tile.get();
                    if (mc.currentScreen == null) {

                        float[] rot = RotationUtils.getRotationBlock(container.getPos());
                        SilentRotation.setRotation(new Vector2f(rot), MoveCategory.Silent);

                        CPacketPlayerTryUseItemOnBlock packet = new CPacketPlayerTryUseItemOnBlock(container.getPos(), Stealer.getFacingDirection(container.getPos()), EnumHand.MAIN_HAND, 0, 0, 0);
                        packet.placeDisabler = true;
                        Objects.requireNonNull(mc.getConnection()).sendPacket(packet);
                        stolen.add(container.getPos());
                        auraDelayTimer.reset();
                    }
                }
            }

        } else {
            mc.currentScreen = null;
            if (!firstDelayTimer.hasTimePassed(firstDelay.getValue().intValue())) return;
            if (mc.player.openContainer instanceof ContainerChest || mc.player.openContainer instanceof ContainerFurnace || mc.player.openContainer instanceof ContainerBrewingStand) {
                int lowerChestSize = 0;
                if (mc.player.openContainer instanceof ContainerChest chest) {
                    lowerChestSize = chest.getLowerChestInventory().getSizeInventory();
                }
                if (mc.player.openContainer instanceof ContainerFurnace furnace) {
                    lowerChestSize = 3;
                }
                if (mc.player.openContainer instanceof ContainerBrewingStand brewingStand) {
                    lowerChestSize = 5;
                }
                List<Integer> slots = new ArrayList<>();
                for (int i = 0; i < lowerChestSize; i++) {
                    ItemStack is = mc.player.openContainer.getInventory().get(i);
                    if (!is.func_190926_b()) {
                        slots.add(i);
                    }
                }
                if (slots.isEmpty() || isInventoryFull()) {
                    if (closeTimer.hasTimePassed(closeDelayValue.getValue().intValue())) {
                        mc.player.closeScreen();
                    }
                } else {
                    Collections.shuffle(slots);
                    slots.forEach(slot -> {
                        if (!delayTimer.hasTimePassed(delayValue.getValue().intValue())) {
                            return;
                        }
                        mc.player.connection.sendPacket(new CPacketClickWindow(mc.player.openContainer.windowId, slot, 0, ClickType.QUICK_MOVE, mc.player.openContainer.getSlot(slot).getStack(), mc.player.openContainer.getNextTransactionID(mc.player.inventory)));
                        delayTimer.reset();
                    });
                    closeTimer.reset();
                }
            }
        }
    }

    @EventTarget
    public void onPacket(EventPacket event) {
        if (!silent.getValue()) return;

        if (event.getPacket() instanceof CPacketPlayerTryUseItemOnBlock packet) {
            if (mc.currentScreen instanceof GuiContainer) {
                event.setCancelled(true);
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

    public static boolean checkTitle(ITextComponent textComponent) {
        return textComponent.getFormattedText().toLowerCase().contains(new ItemStack(Blocks.CHEST).getDisplayName().toLowerCase());
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
