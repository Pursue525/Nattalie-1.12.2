package net.pursue.mode.move;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockWeb;
import net.minecraft.init.Blocks;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.pursue.event.EventTarget;
import net.pursue.event.packet.EventPacket;
import net.pursue.event.player.EventTickMotion;
import net.pursue.event.update.EventMotion;
import net.pursue.event.world.EventWorldLoad;
import net.pursue.mode.Mode;
import net.pursue.utils.Block.BlockUtils;
import net.pursue.utils.category.Category;
import net.pursue.utils.player.PacketUtils;
import net.pursue.value.values.BooleanValue;
import net.pursue.value.values.ModeValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MoveFix extends Mode {

    public static MoveFix instance;

    private final ModeValue<mode> modeValue = new ModeValue<>(this, "Mode", mode.values(), mode.OldGrim);

    enum mode {
        OldGrim,
        Normal
    }

    public final BooleanValue<Boolean> noJumpDelay = new BooleanValue<>(this, "No Jump Delay", false);
    public final BooleanValue<Boolean> noLiquidSlow = new BooleanValue<>(this, "No Liquid Slow", false);
    public final BooleanValue<Boolean> noWebSlow = new BooleanValue<>(this, "No Web Slow", false);

    public final BooleanValue<Boolean> fixLiquidSlow = new BooleanValue<>(this, "(1.14+)Fix Liquid", false);

    public MoveFix() {
        super("MoveFix", "灵活移动", "让你无蜘蛛网/水/减速，无跳跃延迟等", Category.MOVE);
        instance = this;
    }

    public static final List<BlockPos> blocks = new ArrayList<>();

    @EventTarget
    public void onWorld(EventWorldLoad worldLoad) {
        blocks.clear();
    }

    @EventTarget
    private void onMotion(EventMotion eventMotion) {
        if (eventMotion.getType() == EventMotion.Type.Pre) {

            Map<BlockPos, Block> searchBlock = BlockUtils.searchBlocks(7);

            if (noLiquidSlow.getValue()) {
                for (Map.Entry<BlockPos, Block> block : searchBlock.entrySet()) {
                    if (mc.world.getBlockState(block.getKey()).getBlock() instanceof BlockLiquid || mc.world.getBlockState(block.getKey()).getBlock() == Blocks.LAVA) {
                        if (noMine()) {
                            if (modeValue.getValue().equals(mode.OldGrim)) {
                                PacketUtils.sendPacketNoEvent(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, block.getKey(), EnumFacing.UP));
                            }
                            mc.world.setBlockToAir(block.getKey());
                            blocks.add(block.getKey());
                        }
                    }
                }
            }

            if (noWebSlow.getValue()) {
                searchBlock = BlockUtils.searchBlocks(5);

                for (Map.Entry<BlockPos, Block> block : searchBlock.entrySet()) {
                    if (mc.world.getBlockState(block.getKey()).getBlock() instanceof BlockWeb) {
                        if (noMine()) {
                            if (modeValue.getValue().equals(mode.OldGrim)) {
                                PacketUtils.sendPacketNoEvent(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, block.getKey(), EnumFacing.UP));
                            }
                            mc.player.isInWeb = false;
                        }
                    }
                }
            }
        }
    }

    @EventTarget
    private void onTickMove(EventTickMotion eventTickMotion) {
        if (fixLiquidSlow.getValue()) {
            if (mc.player.isInLava() || mc.player.isInWater()) {
                eventTickMotion.cancelEvent();
            }
        }
    }

    @EventTarget
    private void onPacket(EventPacket eventPacket) {

        if (mc.world == null || mc.player == null) return;

        Packet<?> packet = eventPacket.getPacket();

        if (packet instanceof SPacketBlockChange blockChange) {
            if (!blocks.isEmpty() && blocks.contains(blockChange.getBlockPosition())) {
                blocks.removeIf(blockPos -> blockPos.equals(blockChange.getBlockPosition()));
                mc.world.setBlockState(blockChange.getBlockPosition(), Blocks.WATER.getDefaultState(), 1);
            }
        }
    }

    private boolean noMine() {
        RayTraceResult rayTraceResult = mc.objectMouseOver;

        if (rayTraceResult != null) {
            return !mc.gameSettings.keyBindAttack.isKeyDown() || rayTraceResult.typeOfHit != RayTraceResult.Type.BLOCK;
        } else {
            return true;
        }
    }

    public Block getBlock(BlockPos pos) {
        return mc.world.getBlockState(pos).getBlock();
    }
}
