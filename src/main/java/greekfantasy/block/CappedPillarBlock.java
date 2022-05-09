package greekfantasy.block;

import com.google.common.collect.Maps;
import greekfantasy.GFRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;

import java.util.EnumMap;

public class CappedPillarBlock extends Block implements IWaterLoggable {

    public static final DirectionProperty FACING = DirectionProperty.create("facing", Direction.values());
    public static final BooleanProperty HIDDEN = BooleanProperty.create("hidden");
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    protected static final VoxelShape PILLAR_X = Block.box(0, 3, 3, 16, 13, 13);
    protected static final VoxelShape PILLAR_Y = Block.box(3, 0, 3, 13, 16, 13);
    protected static final VoxelShape PILLAR_Z = Block.box(3, 3, 0, 13, 13, 16);

    protected static final EnumMap<Direction, VoxelShape> SLAB_SHAPES = Maps.newEnumMap(Direction.class);

    static {
        SLAB_SHAPES.put(Direction.UP, VoxelShapes.joinUnoptimized(PILLAR_Y, Block.box(0, 8, 0, 16, 16, 16), IBooleanFunction.OR));
        SLAB_SHAPES.put(Direction.DOWN, VoxelShapes.joinUnoptimized(PILLAR_Y, Block.box(0, 0, 0, 16, 8, 16), IBooleanFunction.OR));
        SLAB_SHAPES.put(Direction.NORTH, VoxelShapes.joinUnoptimized(PILLAR_Z, Block.box(0, 0, 0, 16, 16, 8), IBooleanFunction.OR));
        SLAB_SHAPES.put(Direction.SOUTH, VoxelShapes.joinUnoptimized(PILLAR_Z, Block.box(0, 0, 8, 16, 16, 16), IBooleanFunction.OR));
        SLAB_SHAPES.put(Direction.EAST, VoxelShapes.joinUnoptimized(PILLAR_X, Block.box(8, 0, 0, 16, 16, 16), IBooleanFunction.OR));
        SLAB_SHAPES.put(Direction.WEST, VoxelShapes.joinUnoptimized(PILLAR_X, Block.box(0, 0, 0, 8, 16, 16), IBooleanFunction.OR));
    }

    public CappedPillarBlock(final Properties properties) {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any()
                .setValue(WATERLOGGED, false).setValue(FACING, Direction.UP).setValue(HIDDEN, Boolean.valueOf(false)));
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING, HIDDEN, WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        Direction side = context.getClickedFace().getOpposite();
        if (context.getPlayer().isShiftKeyDown() || isPillarBlock(context.getLevel().getBlockState(context.getClickedPos().relative(side)))) {
            side = side.getOpposite();
        }
        FluidState fluid = context.getLevel().getFluidState(context.getClickedPos());
        return this.defaultBlockState().setValue(FACING, side).setValue(WATERLOGGED, fluid.is(FluidTags.WATER));
    }

    @Override
    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn,
                                  BlockPos currentPos, BlockPos facingPos) {
        if (stateIn.getValue(WATERLOGGED)) {
            worldIn.getLiquidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(worldIn));
        }
        final Direction capDir = stateIn.getValue(FACING);
        final BlockState adjacent = worldIn.getBlockState(currentPos.relative(capDir));
        final boolean hidden = isPillarBlock(adjacent) && adjacent.getValue(FACING) == capDir;
        return stateIn.setValue(HIDDEN, Boolean.valueOf(hidden));
    }

    @Override
    public VoxelShape getShape(final BlockState state, final IBlockReader worldIn, final BlockPos pos, final ISelectionContext cxt) {
        final boolean hidden = state.getValue(HIDDEN);
        final Direction facing = state.getValue(FACING);
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
    public BlockState rotate(final BlockState state, final Rotation rot) {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(final BlockState state, final Mirror mirrorIn) {
        return rotate(state, mirrorIn.getRotation(state.getValue(FACING)));
    }

    private boolean isPillarBlock(final BlockState state) {
        return state.getBlock() == GFRegistry.BlockReg.MARBLE_PILLAR
                || state.getBlock() == GFRegistry.BlockReg.LIMESTONE_PILLAR
                || state.getBlock() instanceof CappedPillarBlock;
    }
}
