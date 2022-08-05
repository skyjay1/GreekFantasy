package greekfantasy.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.TallFlowerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.PlantType;

import javax.annotation.Nullable;
import java.util.Random;

public class ReedsBlock extends TallFlowerBlock implements SimpleWaterloggedBlock, BonemealableBlock {

    public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    protected static final VoxelShape SHAPE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 16.0D, 14.0D);

    public ReedsBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(WATERLOGGED, false)
                .setValue(HALF, DoubleBlockHalf.LOWER));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HALF).add(WATERLOGGED);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        Vec3 vec3 = state.getOffset(level, pos);
        return SHAPE.move(vec3.x, vec3.y, vec3.z);
    }

    @Override
    public void tick(BlockState state, ServerLevel worldIn, BlockPos pos, RandomSource rand) {
        if (!state.canSurvive(worldIn, pos)) {
            worldIn.destroyBlock(pos, true);
        }
    }

    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor world, BlockPos currentPos, BlockPos facingPos) {
        if (!state.canSurvive(world, currentPos)) {
            world.scheduleTick(currentPos, this, 1);
        }
        if (state.getValue(WATERLOGGED)) {
            world.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
        }
        return super.updateShape(state, facing, facingState, world, currentPos, facingPos);
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState blockstate = super.getStateForPlacement(context);
        if(blockstate != null) {
            blockstate = copyWaterloggedFrom(context.getLevel(), context.getClickedPos(), blockstate);
        }
        return blockstate;
    }

    @Override
    public void onPlace(BlockState p_60566_, Level p_60567_, BlockPos p_60568_, BlockState p_60569_, boolean p_60570_) {
        super.onPlace(p_60566_, p_60567_, p_60568_, p_60569_, p_60570_);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : Fluids.EMPTY.defaultFluidState();
    }

    @Override
    public boolean canSurvive(BlockState blockState, LevelReader level, BlockPos pos) {
        BlockPos below = pos.below();
        BlockState blockBelow = level.getBlockState(below);
        if(blockState.getValue(HALF) == DoubleBlockHalf.LOWER) {
            return this.mayPlaceOn(blockBelow, level, below);
        }
        return blockBelow.is(this);
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        boolean hasSupport = state.is(Blocks.GRAVEL) || state.is(Blocks.SAND) || super.mayPlaceOn(state, level, pos);
        if(hasSupport) {
            final boolean nextToWater = isWater(level, pos.above()) || isNextToWater(level, pos);
            final boolean belowReplaceable = level.getBlockState(pos.above(1)).getMaterial().isReplaceable()
                    && level.getBlockState(pos.above(2)).getMaterial().isReplaceable()
                    && level.getFluidState(pos.above(2)).is(Fluids.EMPTY);
            return nextToWater && belowReplaceable;
        }
        return false;
    }

    @Override
    public PlantType getPlantType(BlockGetter world, BlockPos pos) {
        return PlantType.BEACH;
    }

    /**
     * @param level the world
     * @param pos     the position to check
     * @return true if the given position has water (or frosted ice)
     **/
    public static boolean isWater(final BlockGetter level, final BlockPos pos) {
        return level.getFluidState(pos).is(FluidTags.WATER) || level.getBlockState(pos).is(Blocks.FROSTED_ICE);
    }

    /**
     * @param level the world
     * @param pos     the position to check
     * @return true if one of the four adjacent blocks are water
     **/
    public static boolean isNextToWater(final BlockGetter level, final BlockPos pos) {
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            if (isWater(level, pos.relative(direction))) {
                return true;
            }
        }
        return false;
    }
}
