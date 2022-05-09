package greekfantasy.block;

import greekfantasy.GFRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DoublePlantBlock;
import net.minecraft.block.IGrowable;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.PlantType;

import javax.annotation.Nullable;
import java.util.Random;

public class ReedsBlock extends DoublePlantBlock implements IWaterLoggable, IGrowable {

    public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    protected static final VoxelShape SHAPE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 16.0D, 14.0D);

    public ReedsBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(WATERLOGGED, false).setValue(HALF, DoubleBlockHalf.LOWER));
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(HALF).add(WATERLOGGED);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
        return SHAPE;
    }

    @Override
    public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand) {
        if (!state.canSurvive(worldIn, pos)) {
            worldIn.destroyBlock(pos, true);
        }
    }

    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, IWorld world, BlockPos currentPos, BlockPos facingPos) {
        if (!state.canSurvive(world, currentPos)) {
            world.getBlockTicks().scheduleTick(currentPos, this, 1);
        }
        if (state.getValue(WATERLOGGED)) {
            world.getLiquidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
        }
        return super.updateShape(state, facing, facingState, world, currentPos, facingPos);
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        BlockPos blockpos = context.getClickedPos();
        FluidState fluid = context.getLevel().getFluidState(blockpos);
        BlockState blockstate = super.getStateForPlacement(context);
        return blockstate != null ? blockstate.setValue(WATERLOGGED, fluid.is(FluidTags.WATER)) : null;
    }

    @Override
    public boolean canSurvive(BlockState state, IWorldReader world, BlockPos pos) {
        BlockState stateDown = world.getBlockState(pos.below());
        // upper block only checks that a lower block is present
        if (state.getValue(HALF) == DoubleBlockHalf.UPPER) {
            return stateDown.is(this) && stateDown.getValue(HALF) == DoubleBlockHalf.LOWER;
        }
        // lower block checks for near water and compatible soil
        final boolean nearWater = isInWater(world, pos) || isNextToWater(world, pos.below());
        final boolean belowAir = world.getFluidState(pos.above()).isEmpty();
        final boolean aboveSoil = stateDown.canOcclude() && (stateDown.canSustainPlant(world, pos.below(), Direction.UP, this) || mayPlaceOn(stateDown, world, pos.below()));
        return nearWater && belowAir && aboveSoil;
    }

    @Override
    public void placeAt(IWorld worldIn, BlockPos pos, int flags) {
        final boolean water = worldIn.getFluidState(pos).is(FluidTags.WATER);
        worldIn.setBlock(pos, this.defaultBlockState().setValue(HALF, DoubleBlockHalf.LOWER).setValue(WATERLOGGED, water), flags);
        worldIn.setBlock(pos.above(), this.defaultBlockState().setValue(HALF, DoubleBlockHalf.UPPER), flags);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : Fluids.EMPTY.defaultFluidState();
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, IBlockReader worldIn, BlockPos pos) {
        return state.is(Blocks.GRAVEL) || state.is(Blocks.SAND) || super.mayPlaceOn(state, worldIn, pos);
    }

    @Override
    public ItemStack getCloneItemStack(IBlockReader worldIn, BlockPos pos, BlockState state) {
        return new ItemStack(GFRegistry.BlockReg.REEDS);
    }

    @Override
    public PlantType getPlantType(IBlockReader world, BlockPos pos) {
        return PlantType.WATER;
    }

    @Override
    public boolean canBeReplaced(BlockState state, BlockItemUseContext context) {
        return false;
    }

    @Override
    public boolean isValidBonemealTarget(IBlockReader reader, BlockPos pos, BlockState state, boolean isClient) {
        return true;
    }

    @Override
    public boolean isBonemealSuccess(World world, Random rand, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void performBonemeal(ServerWorld world, Random rand, BlockPos pos, BlockState state) {
        popResource(world, pos, new ItemStack(this));
    }

    /**
     * @param worldIn the world
     * @param pos     the position to check
     * @return true if the given position has water (or frosted ice)
     **/
    public static boolean isInWater(final IBlockReader worldIn, final BlockPos pos) {
        return worldIn.getFluidState(pos).is(FluidTags.WATER) || worldIn.getBlockState(pos).is(Blocks.FROSTED_ICE);
    }

    /**
     * @param worldIn the world
     * @param pos     the position to check
     * @return true if one of the four adjacent blocks are water
     **/
    public static boolean isNextToWater(final IBlockReader worldIn, final BlockPos pos) {
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            if (isInWater(worldIn, pos.relative(direction))) {
                return true;
            }
        }
        return false;
    }
}
