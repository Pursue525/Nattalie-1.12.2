package net.minecraft.block;

import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockPistonExtension extends BlockDirectional
{
    public static final PropertyEnum<BlockPistonExtension.EnumPistonType> TYPE = PropertyEnum.<BlockPistonExtension.EnumPistonType>create("type", BlockPistonExtension.EnumPistonType.class);
    public static final PropertyBool SHORT = PropertyBool.create("short");
    protected static final AxisAlignedBB PISTON_EXTENSION_EAST_AABB = new AxisAlignedBB(0.75D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
    protected static final AxisAlignedBB PISTON_EXTENSION_WEST_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.25D, 1.0D, 1.0D);
    protected static final AxisAlignedBB PISTON_EXTENSION_SOUTH_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.75D, 1.0D, 1.0D, 1.0D);
    protected static final AxisAlignedBB PISTON_EXTENSION_NORTH_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 0.25D);
    protected static final AxisAlignedBB PISTON_EXTENSION_UP_AABB = new AxisAlignedBB(0.0D, 0.75D, 0.0D, 1.0D, 1.0D, 1.0D);
    protected static final AxisAlignedBB PISTON_EXTENSION_DOWN_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.25D, 1.0D);
    protected static final AxisAlignedBB UP_ARM_AABB = new AxisAlignedBB(0.375D, -0.25D, 0.375D, 0.625D, 0.75D, 0.625D);
    protected static final AxisAlignedBB DOWN_ARM_AABB = new AxisAlignedBB(0.375D, 0.25D, 0.375D, 0.625D, 1.25D, 0.625D);
    protected static final AxisAlignedBB SOUTH_ARM_AABB = new AxisAlignedBB(0.375D, 0.375D, -0.25D, 0.625D, 0.625D, 0.75D);
    protected static final AxisAlignedBB NORTH_ARM_AABB = new AxisAlignedBB(0.375D, 0.375D, 0.25D, 0.625D, 0.625D, 1.25D);
    protected static final AxisAlignedBB EAST_ARM_AABB = new AxisAlignedBB(-0.25D, 0.375D, 0.375D, 0.75D, 0.625D, 0.625D);
    protected static final AxisAlignedBB WEST_ARM_AABB = new AxisAlignedBB(0.25D, 0.375D, 0.375D, 1.25D, 0.625D, 0.625D);
    protected static final AxisAlignedBB field_190964_J = new AxisAlignedBB(0.375D, 0.0D, 0.375D, 0.625D, 0.75D, 0.625D);
    protected static final AxisAlignedBB field_190965_K = new AxisAlignedBB(0.375D, 0.25D, 0.375D, 0.625D, 1.0D, 0.625D);
    protected static final AxisAlignedBB field_190966_L = new AxisAlignedBB(0.375D, 0.375D, 0.0D, 0.625D, 0.625D, 0.75D);
    protected static final AxisAlignedBB field_190967_M = new AxisAlignedBB(0.375D, 0.375D, 0.25D, 0.625D, 0.625D, 1.0D);
    protected static final AxisAlignedBB field_190968_N = new AxisAlignedBB(0.0D, 0.375D, 0.375D, 0.75D, 0.625D, 0.625D);
    protected static final AxisAlignedBB field_190969_O = new AxisAlignedBB(0.25D, 0.375D, 0.375D, 1.0D, 0.625D, 0.625D);

    public BlockPistonExtension()
    {
        super(Material.PISTON);
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(TYPE, BlockPistonExtension.EnumPistonType.DEFAULT).withProperty(SHORT, Boolean.valueOf(false)));
        this.setSoundType(SoundType.STONE);
        this.setHardness(0.5F);
    }

    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        switch ((EnumFacing)state.getValue(FACING))
        {
            case DOWN:
            default:
                return PISTON_EXTENSION_DOWN_AABB;

            case UP:
                return PISTON_EXTENSION_UP_AABB;

            case NORTH:
                return PISTON_EXTENSION_NORTH_AABB;

            case SOUTH:
                return PISTON_EXTENSION_SOUTH_AABB;

            case WEST:
                return PISTON_EXTENSION_WEST_AABB;

            case EAST:
                return PISTON_EXTENSION_EAST_AABB;
        }
    }

    public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean p_185477_7_)
    {
        addCollisionBoxToList(pos, entityBox, collidingBoxes, state.getBoundingBox(worldIn, pos));
        addCollisionBoxToList(pos, entityBox, collidingBoxes, this.getArmShape(state));
    }

    private AxisAlignedBB getArmShape(IBlockState state)
    {
        boolean flag = ((Boolean)state.getValue(SHORT)).booleanValue();

        switch ((EnumFacing)state.getValue(FACING))
        {
            case DOWN:
            default:
                return flag ? field_190965_K : DOWN_ARM_AABB;

            case UP:
                return flag ? field_190964_J : UP_ARM_AABB;

            case NORTH:
                return flag ? field_190967_M : NORTH_ARM_AABB;

            case SOUTH:
                return flag ? field_190966_L : SOUTH_ARM_AABB;

            case WEST:
                return flag ? field_190969_O : WEST_ARM_AABB;

            case EAST:
                return flag ? field_190968_N : EAST_ARM_AABB;
        }
    }

    /**
     * Checks if an IBlockState represents a block that is opaque and a full cube.
     */
    public boolean isFullyOpaque(IBlockState state)
    {
        return state.getValue(FACING) == EnumFacing.UP;
    }

    public void onBlockHarvested(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player)
    {
        if (player.capabilities.isCreativeMode)
        {
            BlockPos blockpos = pos.offset(((EnumFacing)state.getValue(FACING)).getOpposite());
            Block block = worldIn.getBlockState(blockpos).getBlock();

            if (block == Blocks.PISTON || block == Blocks.STICKY_PISTON)
            {
                worldIn.setBlockToAir(blockpos);
            }
        }

        super.onBlockHarvested(worldIn, pos, state, player);
    }

    public boolean isTopSolid(IBlockState state)
    {
        return state.getValue(FACING) == EnumFacing.UP;
    }

    /**
     * Called serverside after this block is replaced with another in Chunk, but before the Tile Entity is updated
     */
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        super.breakBlock(worldIn, pos, state);
        EnumFacing enumfacing = ((EnumFacing)state.getValue(FACING)).getOpposite();
        pos = pos.offset(enumfacing);
        IBlockState iblockstate = worldIn.getBlockState(pos);

        if ((iblockstate.getBlock() == Blocks.PISTON || iblockstate.getBlock() == Blocks.STICKY_PISTON) && ((Boolean)iblockstate.getValue(BlockPistonBase.EXTENDED)).booleanValue())
        {
            iblockstate.getBlock().dropBlockAsItem(worldIn, pos, iblockstate, 0);
            worldIn.setBlockToAir(pos);
        }
    }

    /**
     * Used to determine ambient occlusion and culling when rebuilding chunks for render
     */
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    public boolean canPlaceBlockAt(World worldIn, BlockPos pos)
    {
        return false;
    }

    /**
     * Check whether this Block can be placed on the given side
     */
    public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing side)
    {
        return false;
    }

    /**
     * Returns the quantity of items to drop on block destruction.
     */
    public int quantityDropped(Random random)
    {
        return 0;
    }

    /**
     * Called when a neighboring block was changed and marks that this state should perform any checks during a neighbor
     * change. Cases may include when redstone power is updated, cactus blocks popping off due to a neighboring solid
     * block, etc.
     */
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos p_189540_5_)
    {
        EnumFacing enumfacing = (EnumFacing)state.getValue(FACING);
        BlockPos blockpos = pos.offset(enumfacing.getOpposite());
        IBlockState iblockstate = worldIn.getBlockState(blockpos);

        if (iblockstate.getBlock() != Blocks.PISTON && iblockstate.getBlock() != Blocks.STICKY_PISTON)
        {
            worldIn.setBlockToAir(pos);
        }
        else
        {
            iblockstate.neighborChanged(worldIn, blockpos, blockIn, p_189540_5_);
        }
    }

    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side)
    {
        return true;
    }

    @Nullable
    public static EnumFacing getFacing(int meta)
    {
        int i = meta & 7;
        return i > 5 ? null : EnumFacing.getFront(i);
    }

    public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state)
    {
        return new ItemStack(state.getValue(TYPE) == BlockPistonExtension.EnumPistonType.STICKY ? Blocks.STICKY_PISTON : Blocks.PISTON);
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(FACING, getFacing(meta)).withProperty(TYPE, (meta & 8) > 0 ? BlockPistonExtension.EnumPistonType.STICKY : BlockPistonExtension.EnumPistonType.DEFAULT);
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMetaFromState(IBlockState state)
    {
        int i = 0;
        i = i | ((EnumFacing)state.getValue(FACING)).getIndex();

        if (state.getValue(TYPE) == BlockPistonExtension.EnumPistonType.STICKY)
        {
            i |= 8;
        }

        return i;
    }

    /**
     * Returns the blockstate with the given rotation from the passed blockstate. If inapplicable, returns the passed
     * blockstate.
     */
    public IBlockState withRotation(IBlockState state, Rotation rot)
    {
        return state.withProperty(FACING, rot.rotate((EnumFacing)state.getValue(FACING)));
    }

    /**
     * Returns the blockstate with the given mirror of the passed blockstate. If inapplicable, returns the passed
     * blockstate.
     */
    public IBlockState withMirror(IBlockState state, Mirror mirrorIn)
    {
        return state.withRotation(mirrorIn.toRotation((EnumFacing)state.getValue(FACING)));
    }

    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, new IProperty[] {FACING, TYPE, SHORT});
    }

    public BlockFaceShape func_193383_a(IBlockAccess p_193383_1_, IBlockState p_193383_2_, BlockPos p_193383_3_, EnumFacing p_193383_4_)
    {
        return p_193383_4_ == p_193383_2_.getValue(FACING) ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
    }

    public static enum EnumPistonType implements IStringSerializable
    {
        DEFAULT("normal"),
        STICKY("sticky");

        private final String VARIANT;

        private EnumPistonType(String name)
        {
            this.VARIANT = name;
        }

        public String toString()
        {
            return this.VARIANT;
        }

        public String getName()
        {
            return this.VARIANT;
        }
    }
}
