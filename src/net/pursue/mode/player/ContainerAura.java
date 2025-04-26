package net.pursue.mode.player;

import net.minecraft.block.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBrewingStand;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.pursue.event.EventTarget;
import net.pursue.event.render.EventRender3D;
import net.pursue.event.update.EventUpdate;
import net.pursue.event.world.EventWorldLoad;
import net.pursue.mode.Mode;
import net.pursue.mode.combat.KillAura;
import net.pursue.utils.Block.BlockData;
import net.pursue.utils.TimerUtils;
import net.pursue.utils.category.Category;
import net.pursue.utils.category.MoveCategory;
import net.pursue.utils.render.RenderUtils;
import net.pursue.utils.rotation.RotationUtils;
import net.pursue.utils.rotation.SilentRotation;
import net.pursue.value.values.NumberValue;

import javax.vecmath.Vector2f;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ContainerAura extends Mode {

    private final NumberValue<Number> range = new NumberValue<>(this, "Range", 3.0, 1.0, 7.0, 0.1);

    public ContainerAura() {
        super("ChestAura", "箱子光环", "自动打开距离你最近的箱子", Category.PLAYER);
    }

    private final List<BlockPos> blockPos = new ArrayList<>();
    private final TimerUtils timerUtils = new TimerUtils();
    public static BlockPos pos = null;

    @Override
    public void enable() {
        pos = null;
        timerUtils.reset();
        blockPos.clear();
    }

    @EventTarget
    private void onUpdate(EventUpdate eventUpdate) {
        if (Stealer.isScreen || mc.currentScreen != null) {
            return;
        }

        if (AutoHeal.instance.isEnable() && AutoHeal.instance.modeValue.getValue().equals(AutoHeal.mode.Golden_Apple)) {
            return;
        }

        if (KillAura.INSTANCE.isEnable() && KillAura.INSTANCE.target != null) {
            return;
        }

        if (Scaffold.INSTANCE.isEnable() && Scaffold.INSTANCE.isScaffold) {
            return;
        }

        if (pos == null) {
            for (TileEntity tileEntity : mc.world.loadedTileEntityList) {
                if (!(tileEntity instanceof TileEntityChest || tileEntity instanceof TileEntityFurnace || tileEntity instanceof TileEntityBrewingStand))
                    continue;

                if (blockPos.contains(tileEntity.getPos())) continue;

                if (mc.player.getDistance(tileEntity.getPos()) > range.getValue().doubleValue()) continue;

                if (checkContainerOpenable(tileEntity.getPos())) {
                    pos = tileEntity.getPos();
                    break;
                }
            }
        } else {
            if (!blockPos.contains(pos)) {
                RayTraceResult mop = mc.world.rayTraceBlocks(
                        new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ),
                        new Vec3d(pos).add(new Vec3d(0.5, 0, 0.5)),
                        false, true, true);

                float[] r = RotationUtils.getRotationBlock(pos);

                SilentRotation.setRotation(new Vector2f(r), MoveCategory.Silent);

                if (mop != null && mop.typeOfHit == RayTraceResult.Type.BLOCK) {
                    if (mc.playerController.processRightClickBlock(mc.player, mc.world, pos, mop.sideHit, get_vec_position(pos, mop.sideHit), EnumHand.MAIN_HAND) == EnumActionResult.SUCCESS) {
                        blockPos.add(pos);
                    }
                }
            }
        }

        if (pos != null) {
            if (timerUtils.hasTimePassed(500)) {
                pos = null;
                timerUtils.reset();
            }
        } else {
            timerUtils.reset();
        }
    }

    @EventTarget
    private void onRender3D(EventRender3D render3D) {
        for (TileEntity tileEntity : mc.world.loadedTileEntityList) {
            if (tileEntity instanceof TileEntityChest || tileEntity instanceof TileEntityFurnace || tileEntity instanceof TileEntityBrewingStand) {

                if (mc.world.getBlockState(tileEntity.getPos()) instanceof BlockAir) continue;

                Color color = blockPos.contains(tileEntity.getPos()) ? new Color(0,0,0,50) : new Color(255,255,255,50);

                RenderUtils.drawBlockBox(tileEntity.getPos(), color, true);
            }
        }
    }


    @EventTarget
    private void onWorld(EventWorldLoad worldLoad) {
        timerUtils.reset();
        blockPos.clear();
        pos = null;
    }

    public static Vec3d get_vec_position(BlockPos pos, EnumFacing face) {
        double x = (double) pos.getX() + 0.5;
        double y = (double) pos.getY() + 0.5;
        double z = (double) pos.getZ() + 0.5;
        switch (face) {
            case NORTH:
                z -= 0.5;
                break;
            case SOUTH:
                z += 0.5;
                break;
            case EAST:
                x += 0.5;
                break;
            case WEST:
                x -= 0.5;
                break;
            case UP:
                y += 0.5;
                break;
            case DOWN:
                y -= 0.5;
                break;
        }
        return new Vec3d(x, y, z);
    }

    public EnumFacing getPlaceSide(BlockPos blockPos) {
        List<BlockData> blockData = new ArrayList<>();

        BlockPos pos = new BlockPos(mc.player.posX, mc.player.posY - 1, mc.player.posZ);

        if (isAirBlock(blockPos.up()) && !blockPos.up().equals(pos)) {
            blockData.add(new BlockData(blockPos.up(), EnumFacing.UP));
        } else if (isAirBlock(blockPos.east()) && !blockPos.east().equals(pos)) {
            blockData.add(new BlockData(blockPos.east(), EnumFacing.EAST));
        } else if (isAirBlock(blockPos.north()) && !blockPos.north().equals(pos)) {
            blockData.add(new BlockData(blockPos.north(), EnumFacing.NORTH));
        } else if (isAirBlock(blockPos.south()) && !blockPos.south().equals(pos)) {
            blockData.add(new BlockData(blockPos.south(), EnumFacing.SOUTH));
        } else if (isAirBlock(blockPos.west()) && !blockPos.west().equals(pos)) {
            blockData.add(new BlockData(blockPos.west(), EnumFacing.WEST));
        }

        if (blockData.isEmpty()) return null;

        blockData.sort(Comparator.comparingDouble(vec3 -> {
            final double d0 = pos.getX() - vec3.pos().getX();
            final double d1 = pos.getY() - vec3.pos().getY();
            final double d2 = pos.getZ() - vec3.pos().getZ();
            return MathHelper.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
        }));

        return blockData.getFirst().facing();
    }

    public boolean isAirBlock(BlockPos blockPos) {
        Block block = Minecraft.getMinecraft().world.getBlockState(blockPos).getBlock();
        return block instanceof BlockAir;
    }

    private boolean isContainer(Block block) {
        return block instanceof BlockChest || block instanceof BlockFurnace || block instanceof BlockBrewingStand;
    }

    private boolean checkContainerOpenable(BlockPos blockPos) {
        IBlockState blockState = mc.world.getBlockState(blockPos);
        if (!(blockState.getBlock() instanceof BlockChest)) return true;
        IBlockState upBlockState = mc.world.getBlockState(blockPos.add(0, 1, 0));
        return !upBlockState.getBlock().isFullBlock(upBlockState) || upBlockState.getBlock() instanceof BlockGlass || blockState.getBlock() instanceof BlockBrewingStand;
    }
}

