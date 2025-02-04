package net.pursue.mode.player;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.pursue.event.EventTarget;
import net.pursue.event.player.EventSlot;
import net.pursue.event.player.EventStrafe;
import net.pursue.event.update.EventMotion;
import net.pursue.event.update.EventTick;
import net.pursue.event.update.EventUpdate;
import net.pursue.utils.category.Category;
import net.pursue.mode.Mode;
import net.pursue.mode.combat.KillAura;
import net.pursue.shield.IsShield;
import net.pursue.utils.*;
import net.pursue.utils.Block.BlockData;
import net.pursue.utils.category.MoveCategory;
import net.pursue.utils.player.InvUtils;
import net.pursue.utils.player.MovementUtils;
import net.pursue.utils.player.SpoofSlotUtils;
import net.pursue.utils.rotation.RotationUtils;
import net.pursue.utils.rotation.SilentRotation;
import net.pursue.value.values.BooleanValue;
import net.pursue.value.values.ColorValue;
import net.pursue.value.values.ModeValue;
import net.pursue.value.values.NumberValue;

import javax.vecmath.Vector2f;
import java.awt.*;
import java.util.*;
import java.util.List;

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
        mc.player.setSneaking(false);
    }

    @Override
    public void disable() {
        if (mc.player == null) return;

        mc.player.inventory.currentItem = oldSlot;
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), false);
        SpoofSlotUtils.stopSpoofSlot();
        isScaffold = false;
        blockData = null;
    }

    @EventTarget
    private void onSlot(EventSlot event) {
        if (autoBlockModeValue.getValue() == autoBlock.Silence) {
            oldSlot = event.getSlot();
        }
    }

    @EventTarget
    private void onUpdate(EventUpdate event) {
        if (mc.player.onGround) {
            keepYCoord = Math.floor(mc.player.posY - 1);
        }

        slot = InvUtils.getBlockSlot();

        keepy = keepYModeValue.getValue().equals(keepY.Auto) ? !mc.gameSettings.keyBindJump.isKeyDown() : keepYModeValue.getValue().equals(keepY.Normal);

        blockData = !modeValue.getValue().equals(mode.Legit) ? mc.world.getBlockState(mc.player.getPos().down()).getBlock() instanceof BlockAir ? getBlockData(new BlockPos(mc.player.posX, getPosY(), mc.player.posZ)) : null : getBlockData(new BlockPos(mc.player.posX, getPosY(), mc.player.posZ));

        isScaffold = (modeValue.getValue().equals(mode.Normal) || modeValue.getValue().equals(mode.Legit) || mc.player.offGroundTicks >= tickDelay.getValue().intValue()) && slot >= 0;

        switch (autoBlockModeValue.getValue()) {
            case Normal -> {
                if (isScaffold) {
                    mc.player.inventory.currentItem = slot;
                }
            }

            case Spoof -> {
                if (isScaffold) {
                    mc.player.inventory.currentItem = slot;
                } else {
                    mc.player.inventory.currentItem = oldSlot;
                }
            }
        }

        if (isScaffold) {
            if (blockData != null) {
                SilentRotation.setRotation(new Vector2f(RotationUtils.getRotationBlock(blockData.pos())), MoveCategory.Silent);
            }
        } else {
            if (!KillAura.INSTANCE.isEnable() || KillAura.INSTANCE.target == null) {
                SilentRotation.setTargetRotation(null);
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
    }

    @EventTarget
    private void onTick(EventTick event) {
        place();
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
        if (blockData != null && isScaffold) {
            if (mc.world.getBlockState(new BlockPos(mc.player.getPos().down())).getBlock() instanceof BlockAir) {
                if (autoBlockModeValue.getValue() == autoBlock.Silence) mc.player.inventory.currentItem = slot;
                mc.playerController.processRightClickBlock(mc.player, mc.world, blockData.pos(), blockData.facing(), getVec3d(blockData.pos(), blockData.facing()), EnumHand.MAIN_HAND);
                if (swing.getValue()) mc.player.swingArm(EnumHand.MAIN_HAND);

                if (autoBlockModeValue.getValue() == autoBlock.Silence) mc.player.inventory.currentItem = oldSlot;
                blockData = null;
            }
        }
    }

    private Vec3d getVec3d(BlockPos pos, EnumFacing face) {
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

    private BlockData getBlockData(BlockPos pos) {
        if (getPos(pos) == null) {
            if (getBlockPos() == null) return null;

            if (getPlaceSide(getBlockPos()) == null) return null;

            return new BlockData(getBlockPos(), getPlaceSide(getBlockPos()));
        } else {
            return getPos(pos);
        }
    }

    private EnumFacing getPlaceSide(BlockPos blockPos) {
        List<BlockData> blockData = new ArrayList<>();

        BlockPos pos = new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ);

        if (isAirBlock(blockPos.east()) && !blockPos.east().equals(pos)) {
            blockData.add(new BlockData(blockPos.east(), EnumFacing.EAST));
        }


        if (isAirBlock(blockPos.north()) && !blockPos.north().equals(pos)) {
            blockData.add(new BlockData(blockPos.north(), EnumFacing.NORTH));
        }

        if (isAirBlock(blockPos.south()) && !blockPos.south().equals(pos)) {
            blockData.add(new BlockData(blockPos.south(), EnumFacing.SOUTH));
        }

        if (isAirBlock(blockPos.west()) && !blockPos.west().equals(pos)) {
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


    private BlockPos getBlockPos() {

        BlockPos playerPos = new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ);

        ArrayList<BlockPos> positions = new ArrayList<>();

        Map<BlockPos, Block> searchBlock = searchBlocks(5);
        for (Map.Entry<BlockPos, Block> block : searchBlock.entrySet()) {
            if (isPosSolid(block.getKey())) {
                positions.add(block.getKey());
            }
        }

        positions.removeIf(pos -> mc.player.getDistance(pos) > mc.playerController.getBlockReachDistance() || pos.getY() >= playerPos.getY());

        if (positions.isEmpty()) return null;

        positions.sort(Comparator.comparingDouble(vec3 -> {
            final double d0 = playerPos.getX() - vec3.getX();
            final double d1 = playerPos.getY() - vec3.getY();
            final double d2 = playerPos.getZ() - vec3.getZ();
            return MathHelper.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
        }));

        return positions.getFirst();
    }

    public boolean isAirBlock(BlockPos blockPos) {
        Block block = Minecraft.getMinecraft().world.getBlockState(blockPos).getBlock();
        return block instanceof BlockAir;
    }

    public Block getBlock(BlockPos pos) {
        return mc.world.getBlockState(pos).getBlock();
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

    /**
     * Fix UP
     *
     * @return EnumFacing-UP
     */

    public BlockData getPos(BlockPos pos) {
        if (isPosSolid(pos.add(-1, 0, 0))) {
            return new BlockData(pos.add(-1, 0, 0), EnumFacing.EAST);
        } else if (isPosSolid(pos.add(1, 0, 0))) {
            return new BlockData(pos.add(1, 0, 0), EnumFacing.WEST);
        } else if (isPosSolid(pos.add(0, 0, 1))) {
            return new BlockData(pos.add(0, 0, 1), EnumFacing.NORTH);
        } else if (isPosSolid(pos.add(0, 0, -1))) {
            return new BlockData(pos.add(0, 0, -1), EnumFacing.SOUTH);
        } else if (isPosSolid(pos.add(0, -1, 0))) {
            return new BlockData(pos.add(0, -1, 0), EnumFacing.UP);
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
