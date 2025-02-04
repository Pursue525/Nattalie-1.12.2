package net.pursue.mode.move;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockWeb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.pursue.event.EventTarget;
import net.pursue.event.player.EventTickMotion;
import net.pursue.event.update.EventMotion;
import net.pursue.utils.category.Category;
import net.pursue.mode.Mode;
import net.pursue.utils.player.PacketUtils;
import net.pursue.value.values.BooleanValue;
import net.pursue.value.values.ModeValue;

import java.util.HashMap;
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
        super("MoveFix", "修复移动类", "让你无蜘蛛网/水/减速，无跳跃延迟等", Category.MOVE);
        instance = this;
    }

    @EventTarget
    private void onMotion(EventMotion eventMotion) {
        if (eventMotion.getType() == EventMotion.Type.Pre) {

            Map<BlockPos, Block> searchBlock = searchBlocks(7);

            if (noLiquidSlow.getValue()) {
                for (Map.Entry<BlockPos, Block> block : searchBlock.entrySet()) {
                    if (mc.world.getBlockState(block.getKey()).getBlock() instanceof BlockLiquid || mc.world.getBlockState(block.getKey()).getBlock() == Blocks.LAVA) {
                        if (noMine()) {
                            if (modeValue.getValue().equals(mode.OldGrim)) {
                                PacketUtils.sendPacketNoEvent(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, block.getKey(), EnumFacing.UP));
                            }
                            mc.world.setBlockToAir(block.getKey());
                        }
                    }
                }
            }

            if (noWebSlow.getValue()) {
                searchBlock = searchBlocks(5);

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

    private boolean noMine() {
        RayTraceResult rayTraceResult = mc.objectMouseOver;

        if (rayTraceResult != null) {
            return !mc.gameSettings.keyBindAttack.isKeyDown() || rayTraceResult.typeOfHit != RayTraceResult.Type.BLOCK;
        } else {
            return true;
        }
    }

    public Map<BlockPos, Block> searchBlocks(int radius) {
        Map<BlockPos, Block> blocks = new HashMap<>();
        EntityPlayer player = mc.player;
        if (player == null) {
            return blocks;
        }
        for (int x = radius; x >= -radius + 1; x--) {
            for (int y = radius; y >= -radius + 1; y--) {
                for (int z = radius; z >= -radius + 1; z--) {
                    BlockPos blockPos = new BlockPos(player.posX + x, player.posY + y, player.posZ + z);
                    Block block = getBlock(blockPos);
                    if (block == null) {
                        continue;
                    }
                    blocks.put(blockPos, block);
                }
            }
        }
        return blocks;
    }

    public Block getBlock(BlockPos pos) {
        return mc.world.getBlockState(pos).getBlock();
    }
}
