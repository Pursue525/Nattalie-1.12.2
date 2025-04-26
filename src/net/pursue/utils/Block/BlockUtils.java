package net.pursue.utils.Block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.chunk.Chunk;
import net.pursue.utils.MathUtils;
import net.pursue.utils.client.UtilsManager;

import java.util.HashMap;
import java.util.Map;

public class BlockUtils extends UtilsManager {

    public static Block getBlock(BlockPos pos) {
        return mc.world.getBlockState(pos).getBlock();
    }

    public static Map<BlockPos, Block> searchBlocks(int radius) {
        Map<BlockPos, Block> blocks = new HashMap<>();
        EntityPlayer thePlayer = mc.player;
        if (thePlayer == null) {
            return blocks;
        }
        for (int x = radius; x >= -radius + 1; x--) {
            for (int y = radius; y >= -radius + 1; y--) {
                for (int z = radius; z >= -radius + 1; z--) {
                    BlockPos blockPos = new BlockPos(thePlayer.posX + x, thePlayer.posY + y, thePlayer.posZ + z);
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

    public static RayTraceResult raytrace(float yaw, float pitch) {
        float partialTicks = mc.timer.field_194147_b;
        float blockReachDistance = mc.playerController.getBlockReachDistance();
        Vec3d vec3 = mc.player.getPositionEyes(partialTicks);
        Vec3d vec31 = mc.player.getVectorForRotation(pitch, yaw);
        Vec3d vec32 = vec3.add(vec31.xCoord * blockReachDistance, vec31.yCoord * blockReachDistance, vec31.zCoord * blockReachDistance);
        return mc.world.rayTraceBlocks(vec3, vec32, false, false, true);
    }

    public static Vec3d getVec3d(BlockPos pos, EnumFacing face) {
        double x = (double) pos.getX() + 0.5;
        double y = (double) pos.getY() + 0.5;
        double z = (double) pos.getZ() + 0.5;
        if (face == EnumFacing.UP || face == EnumFacing.DOWN) {
            x += MathUtils.getRandomInRange(0.3, -0.3);
            z += MathUtils.getRandomInRange(0.3, -0.3);
        } else {
            y += 0.08;
        }
        if (face == EnumFacing.WEST || face == EnumFacing.EAST) {
            z += MathUtils.getRandomInRange(0.3, -0.3);
        }
        if (face == EnumFacing.SOUTH || face == EnumFacing.NORTH) {
            x += MathUtils.getRandomInRange(0.3, -0.3);
        }
        return new Vec3d(x, y, z);
    }



    private static Chunk getChunk(BlockPos blockPos) {
        return mc.world.getChunkFromBlockCoords(blockPos);
    }

    private static IBlockState getState(BlockPos blockPos) {
        return mc.world.getBlockState(blockPos);
    }

    public static boolean isAirBlock(BlockPos add) {
        return mc.world.getBlockState(add).getBlock() instanceof BlockAir;
    }
}
