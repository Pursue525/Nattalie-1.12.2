package net.pursue.mode.player;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.pursue.event.EventTarget;
import net.pursue.event.player.EventSlot;
import net.pursue.event.player.EventStrafe;
import net.pursue.event.update.EventMotion;
import net.pursue.event.update.EventUpdate;
import net.pursue.mode.Mode;
import net.pursue.shield.IsShield;
import net.pursue.utils.Block.BlockData;
import net.pursue.utils.Block.BlockUtils;
import net.pursue.utils.Block.FacingData;
import net.pursue.utils.category.Category;
import net.pursue.utils.category.MoveCategory;
import net.pursue.utils.player.InvUtils;
import net.pursue.utils.player.MovementUtils;
import net.pursue.utils.player.PlayerUtils;
import net.pursue.utils.player.SpoofSlotUtils;
import net.pursue.utils.rotation.RotationUtils;
import net.pursue.utils.rotation.SilentRotation;
import net.pursue.value.values.BooleanValue;
import net.pursue.value.values.ColorValue;
import net.pursue.value.values.ModeValue;
import net.pursue.value.values.NumberValue;

import javax.vecmath.Vector2f;
import java.awt.*;
import java.util.Arrays;

@IsShield
public class Scaffold extends Mode {

    public static Scaffold INSTANCE;

    private final ModeValue<mode> modeValue = new ModeValue<>(this, "Mode", mode.values(), mode.Normal);

    enum mode {
        Normal,
        Telly,
        Legit
    }

    private final NumberValue<Number> tickDelay = new NumberValue<>(this, "PlaceDelay", 2,1,5,1, () -> modeValue.getValue() == mode.Telly);
    private final ModeValue<jump> jumpModeValue = new ModeValue<>(this, "JumpMode", jump.values(), jump.Normal, () -> modeValue.getValue() == mode.Telly);

    enum jump {
        Parkour,
        Normal,
        OFF
    }

    private final ModeValue<keepY> keepYModeValue = new ModeValue<>(this, "KeepYMode", keepY.values(), keepY.Normal, () -> modeValue.getValue() == mode.Telly);

    enum keepY {
        Normal,
        Auto,
        OFF
    }

    private final ModeValue<autoBlock> autoBlockModeValue = new ModeValue<>(this, "AutoBlockModeValue", autoBlock.values(), autoBlock.Normal);

    enum autoBlock {
        Normal,
        Silence,
        Spoof
    }
    public final BooleanValue<Boolean> rayTrace =new BooleanValue<>(this,"RayTrace",true);
    private final BooleanValue<Boolean> swing = new BooleanValue<>(this, "Swing", true);
    public final BooleanValue<Boolean> blocks = new BooleanValue<>(this, "Blocks", true);

    public final ColorValue<Color> color = new ColorValue<>(this, "Color", Color.WHITE, blocks::getValue);

    public Scaffold() {
        super("Scaffold", "脚手架", "我走的地方，便有路", Category.PLAYER);
        INSTANCE = this;
    }

    private double keepYCoord;
    private BlockData blockData;
    public boolean isScaffold = false;
    public int slot;
    public int oldSlot;
    private static boolean keepy;


    @Override
    public void enable() {
        if (mc.player == null) return;

        oldSlot = mc.player.inventory.currentItem;
        isScaffold = false;
        blockData = null;
    }

    @Override
    public void disable() {
        if (mc.player == null) return;

        SpoofSlotUtils.stopSpoofSlot();
        if (mc.player.inventory.currentItem != oldSlot) mc.player.inventory.currentItem = oldSlot;
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), false);
        isScaffold = false;
        blockData = null;
    }

    @EventTarget
    private void onSlot(EventSlot event) {
        if (autoBlockModeValue.getValue() == autoBlock.Silence) oldSlot = event.getSlot();
    }

    @EventTarget
    private void onUpdate(EventUpdate event) {
        setSuffix(modeValue.getValue().name());
        if (mc.player.onGround) {
            keepYCoord = Math.floor(mc.player.posY - 1);
        }

        slot = InvUtils.getBlockSlot();

        keepy = keepYModeValue.getValue().equals(keepY.Auto) ? !mc.gameSettings.keyBindJump.isKeyDown() : keepYModeValue.getValue().equals(keepY.Normal);

        blockData = !modeValue.getValue().equals(mode.Legit) ? mc.world.getBlockState(mc.player.getPos().down()).getBlock() instanceof BlockAir ? getBlockData(new BlockPos(mc.player.posX, getPosY(), mc.player.posZ)) : null : getBlockData(new BlockPos(mc.player.posX, getPosY(), mc.player.posZ));

        isScaffold = modeValue.getValue().equals(mode.Normal) || modeValue.getValue().equals(mode.Legit) || mc.player.offGroundTicks >= tickDelay.getValue().intValue();

        if (isScaffold) {
            if (blockData != null) {
                SilentRotation.setRotation(new Vector2f(RotationUtils.getRotationBlock(blockData.pos())), MoveCategory.Silent);
            }
        }

        switch (autoBlockModeValue.getValue()) {
            case Normal -> {
                SpoofSlotUtils.stopSpoofSlot();

                mc.player.inventory.currentItem = slot < 0 ? oldSlot : slot;
            }

            case Spoof -> {
                SpoofSlotUtils.setSlot(oldSlot);

                if (isScaffold) {
                    mc.player.inventory.currentItem = slot < 0 ? oldSlot : slot;
                } else {
                    mc.player.inventory.currentItem = oldSlot;
                }
            }
        }
    }

    @EventTarget
    private void onMotion(EventMotion event) {
        if (event.getType() == EventMotion.Type.Pre) {

            if (modeValue.getValue().equals(mode.Legit)) {
                if (mc.world.getBlockState(new BlockPos(mc.player.posX, mc.player.posY - 1, mc.player.posZ)).getBlock() instanceof BlockAir) {
                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), mc.player.onGround);
                } else {
                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), false);
                }
            }
        }
        if (event.getType() == EventMotion.Type.Post) {
            if (isScaffold) {
                if (blockData != null) {
                    place();
                }
            }
        }
    }



    @EventTarget
    private void onStrafe(EventStrafe event) {
        if (modeValue.getValue().equals(mode.Telly)) {
            if (mc.player.onGround && !mc.gameSettings.keyBindJump.isKeyDown()) {
                if (jumpModeValue.getValue().equals(jump.Normal)) {
                    mc.player.jump();
                } else {
                    if (mc.world.getBlockState(new BlockPos(mc.player.getPos().down())).getBlock() instanceof BlockAir) {
                        mc.player.jump();
                    }
                }
            }
        }
    }

    private double getPosY() {
        if (!keepy || keepYModeValue.getValue().equals(keepY.OFF)) {
            return mc.player.posY - 1.0;
        }
        return !MovementUtils.isMoving() ? mc.player.posY - 1.0 : keepYCoord;
    }

    private void place() {
        if (slot >= 0) {
            if (mc.world.getBlockState(new BlockPos(mc.player.getPos().down())).getBlock() instanceof BlockAir) {
                if (autoBlockModeValue.getValue().equals(autoBlock.Silence)) mc.player.inventory.currentItem = slot;

                if (rayTrace.getValue()) {
                    RayTraceResult mop = mc.world.rayTraceBlocks(
                            new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ),
                            new Vec3d(blockData.pos()).add(new Vec3d(0.5, 0, 0.5)),
                            false, true, true);

                    if (mop != null && mop.typeOfHit == RayTraceResult.Type.BLOCK) {
                        if (mc.playerController.processRightClickBlock(mc.player, mc.world, blockData.pos(), blockData.facing(), BlockUtils.getVec3d(blockData.pos(), blockData.facing()), EnumHand.MAIN_HAND) == EnumActionResult.SUCCESS) {
                            if (swing.getValue()) mc.player.swingArm(EnumHand.MAIN_HAND);
                        }
                    }
                } else {
                    if (mc.playerController.processRightClickBlock(mc.player, mc.world, blockData.pos(), blockData.facing(), BlockUtils.getVec3d(blockData.pos(), blockData.facing()), EnumHand.MAIN_HAND) == EnumActionResult.SUCCESS) {
                        if (swing.getValue()) mc.player.swingArm(EnumHand.MAIN_HAND);
                    }
                }

                if (autoBlockModeValue.getValue().equals(autoBlock.Silence)) mc.player.inventory.currentItem = oldSlot;

            }
        }
    }

    private BlockData getBlockData(BlockPos pos) {
        BlockData f = getData(pos);

        final Vec3d targetBlock = PlayerUtils.getPlacePossibility(0, 0, 0, mc.playerController.getBlockReachDistance());

        if (targetBlock == null) return null;

        FacingData enumFacing = PlayerUtils.getEnumFacing(targetBlock);

        if (enumFacing == null) return null;

        final BlockPos position = new BlockPos(targetBlock.xCoord, targetBlock.yCoord, targetBlock.zCoord);

        BlockPos blockFace = position.add(enumFacing.vec3d().xCoord, enumFacing.vec3d().yCoord, enumFacing.vec3d().zCoord);

        if (f != null) {
            return f;
        } else {
            return new BlockData(blockFace, enumFacing.facing());
        }
    }

    private BlockData getData(BlockPos pos) {
        if (isPosSolid(pos.add(0, -1, 0))) {
            return new BlockData(pos.add(0, -1, 0), EnumFacing.UP);
        } else if (isPosSolid(pos.add(-1, 0, 0))) {
            return new BlockData(pos.add(-1, 0, 0), EnumFacing.EAST);
        } else if (isPosSolid(pos.add(1, 0, 0))) {
            return new BlockData(pos.add(1, 0, 0), EnumFacing.WEST);
        } else if (isPosSolid(pos.add(0, 0, 1))) {
            return new BlockData(pos.add(0, 0, 1), EnumFacing.NORTH);
        } else if (isPosSolid(pos.add(0, 0, -1))) {
            return new BlockData(pos.add(0, 0, -1), EnumFacing.SOUTH);
        }

        return null;
    }

    public boolean isPosSolid(BlockPos pos) {
        final Block block = mc.world.getBlockState(pos).getBlock();
        return !Arrays.asList(
                Blocks.ANVIL,
                Blocks.AIR,
                Blocks.WATER,
                Blocks.FIRE,
                Blocks.FLOWING_WATER,
                Blocks.LAVA,
                Blocks.SKULL,
                Blocks.TRAPPED_CHEST,
                Blocks.FLOWING_LAVA,
                Blocks.CHEST,
                Blocks.ENCHANTING_TABLE,
                Blocks.ENDER_CHEST,
                Blocks.CRAFTING_TABLE
        ).contains(block);
    }
}
