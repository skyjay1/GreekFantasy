package greekfantasy.block;

import com.google.common.collect.Maps;
import greekfantasy.GFRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class PillarBlock extends DirectionalBlock implements SimpleWaterloggedBlock {

    public static final BooleanProperty HIDDEN = BooleanProperty.create("hidden");
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    protected static final VoxelShape PILLAR_X = Block.box(0, 3, 3, 16, 13, 13);
    protected static final VoxelShape PILLAR_Y = Block.box(3, 0, 3, 13, 16, 13);
    protected static final VoxelShape PILLAR_Z = Block.box(3, 3, 0, 13, 13, 16);

    protected static final EnumMap<Direction, VoxelShape> SLAB_SHAPES = Maps.newEnumMap(Direction.class);

    protected static final Map<BlockState, VoxelShape> SHAPES = new HashMap<>();

    static {
        SLAB_SHAPES.put(Direction.UP, Shapes.joinUnoptimized(PILLAR_Y, Block.box(0, 8, 0, 16, 16, 16), BooleanOp.OR));
        SLAB_SHAPES.put(Direction.DOWN, Shapes.joinUnoptimized(PILLAR_Y, Block.box(0, 0, 0, 16, 8, 16), BooleanOp.OR));
        SLAB_SHAPES.put(Direction.NORTH, Shapes.joinUnoptimized(PILLAR_Z, Block.box(0, 0, 0, 16, 16, 8), BooleanOp.OR));
        SLAB_SHAPES.put(Direction.SOUTH, Shapes.joinUnoptimized(PILLAR_Z, Block.box(0, 0, 8, 16, 16, 16), BooleanOp.OR));
        SLAB_SHAPES.put(Direction.EAST, Shapes.joinUnoptimized(PILLAR_X, Block.box(8, 0, 0, 16, 16, 16), BooleanOp.OR));
        SLAB_SHAPES.put(Direction.WEST, Shapes.joinUnoptimized(PILLAR_X, Block.box(0, 0, 0, 8, 16, 16), BooleanOp.OR));
    }

    public PillarBlock(final Properties properties) {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any()
                .setValue(WATERLOGGED, false)
                .setValue(FACING, Direction.UP)
                .setValue(HIDDEN, Boolean.valueOf(false)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, HIDDEN, WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction side = context.getClickedFace().getOpposite();
        if ((context.getPlayer() != null && context.getPlayer().isCrouching()) || isPillarBlock(context.getLevel().getBlockState(context.getClickedPos().relative(side)))) {
            side = side.getOpposite();
        }
        FluidState fluid = context.getLevel().getFluidState(context.getClickedPos());
        return this.defaultBlockState().setValue(FACING, side).setValue(WATERLOGGED, fluid.is(FluidTags.WATER));
    }

    @Override
    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor worldIn,
                                  BlockPos currentPos, BlockPos facingPos) {
        if (stateIn.getValue(WATERLOGGED)) {
            worldIn.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(worldIn));
        }
        final Direction capDir = stateIn.getValue(FACING);
        final BlockState adjacent = worldIn.getBlockState(currentPos.relative(capDir));
        final boolean hidden = isPillarBlock(adjacent) && adjacent.getValue(FACING) == capDir;
        return stateIn.setValue(HIDDEN, Boolean.valueOf(hidden));
    }

    @Override
    public VoxelShape getShape(final BlockState blockState, final BlockGetter level, final BlockPos pos, final CollisionContext context) {
        return SHAPES.computeIfAbsent(blockState, state -> computeShape(state, level, pos, context));
    }

    private static VoxelShape computeShape(final BlockState blockState, final BlockGetter level, final BlockPos pos, final CollisionContext cxt) {
        final boolean hidden = blockState.getValue(HIDDEN);
        final Direction facing = blockState.getValue(FACING);
        // if slab is not hidden, return prebuilt slab+pillar shape
        if (!hidden) {
            return SLAB_SHAPES.get(facing);
        }
        // if slab is hidden, only return pillar shape
        if (facing.getAxis() == Direction.Axis.Y) {
            return PILLAR_Y;
        } else if (facing.getAxis() == Direction.Axis.X) {
            return PILLAR_X;
        } else {
            return PILLAR_Z;
        }
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : Fluids.EMPTY.defaultFluidState();
    }

    @Override
    public BlockState rotate(final BlockState state, final LevelAccessor level, final BlockPos pos, final Rotation rot) {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    @SuppressWarnings("Deprecated")
    @Override
    public BlockState mirror(final BlockState state, final Mirror mirrorIn) {
        return rotate(state, mirrorIn.getRotation(state.getValue(FACING)));
    }

    private boolean isPillarBlock(final BlockState state) {
        return state.getBlock() instanceof PillarBlock;
    }
}
