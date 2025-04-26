package net.pursue.utils.player;

import net.minecraft.block.*;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.pursue.utils.Block.FacingData;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PlayerUtils {

    private static final Minecraft mc = Minecraft.getMinecraft();

    public static FacingData getEnumFacing(final Vec3d position) {
        for (int x2 = -1; x2 <= 1; x2 += 2) {
            if (!(block(position.xCoord + x2, position.yCoord, position.zCoord) instanceof BlockAir)) {
                if (x2 > 0) {
                    return new FacingData(EnumFacing.WEST, new Vec3d(x2, 0, 0));
                } else {
                    return new FacingData(EnumFacing.EAST, new Vec3d(x2, 0, 0));
                }
            }
        }

        for (int y2 = -1; y2 <= 1; y2 += 2) {
            if (!(block(position.xCoord, position.yCoord + y2, position.zCoord) instanceof BlockAir)) {
                if (y2 < 0) {
                    return new FacingData(EnumFacing.UP, new Vec3d(0, y2, 0));
                }
            }
        }

        for (int z2 = -1; z2 <= 1; z2 += 2) {
            if (!(block(position.xCoord, position.yCoord, position.zCoord + z2) instanceof BlockAir)) {
                if (z2 < 0) {
                    return new FacingData(EnumFacing.SOUTH, new Vec3d(0, 0, z2));
                } else {
                    return new FacingData(EnumFacing.NORTH, new Vec3d(0, 0, z2));
                }
            }
        }

        return null;
    }

    public static Vec3d getPlacePossibility(double offsetX, double offsetY, double offsetZ, float blockRange) {
        final List<Vec3d> possibilities = new ArrayList<>();
        final int range = (int) (blockRange + (Math.abs(offsetX) + Math.abs(offsetZ)));

        for (int x = -range; x <= range; ++x) {
            for (int y = -range; y <= -1; ++y) {
                for (int z = -range; z <= range; ++z) {
                    final Block block = blockRelativeToPlayer(x, y, z);

                    if (!placable(block)) {
                        for (int x2 = -1; x2 <= 1; x2 += 1)
                            possibilities.add(new Vec3d(mc.player.posX + x + x2, mc.player.posY + y, mc.player.posZ + z));

                        for (int y2 = -1; y2 <= 1; y2 += 1)
                            possibilities.add(new Vec3d(mc.player.posX + x, mc.player.posY + y + y2, mc.player.posZ + z));

                        for (int z2 = -1; z2 <= 1; z2 += 1)
                            possibilities.add(new Vec3d(mc.player.posX + x, mc.player.posY + y, mc.player.posZ + z + z2));
                    }
                }
            }
        }

        possibilities.removeIf(vec3 -> mc.player.getDistance(vec3.xCoord, vec3.yCoord, vec3.zCoord) > blockRange || !(block(vec3.xCoord, vec3.yCoord, vec3.zCoord) instanceof BlockAir));

        if (possibilities.isEmpty()) return null;

        possibilities.sort(Comparator.comparingDouble(vec3 -> {

            final double d0 = (mc.player.posX + offsetX) - vec3.xCoord;
            final double d1 = (mc.player.posY - 1 + offsetY) - vec3.yCoord;
            final double d2 = (mc.player.posZ + offsetZ) - vec3.zCoord;
            return MathHelper.sqrt(d0 * d0 + d1 * d1 + d2 * d2);

        }));

        return possibilities.getFirst();
    }

    private static boolean placable(Block block) {
        if (block == null) return false;
        return block instanceof BlockAir || block instanceof BlockSnow || block instanceof BlockLiquid || block instanceof BlockBush || block instanceof BlockButton || block instanceof BlockContainer || block instanceof BlockFire;
    }

    private static Block block(final double x, final double y, final double z) {
        return mc.world.getBlockState(new BlockPos(x, y, z)).getBlock();
    }

    private static Block blockRelativeToPlayer(final double offsetX, final double offsetY, final double offsetZ) {
        return mc.world.getBlockState(new BlockPos(mc.player).add(offsetX, offsetY, offsetZ)).getBlock();
    }
}

