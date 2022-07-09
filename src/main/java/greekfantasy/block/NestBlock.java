package greekfantasy.block;



import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
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

import java.util.HashMap;
import java.util.Map;

public class NestBlock extends Block implements SimpleWaterloggedBlock {

    // TODO random different texture for slab-only variant

    public static final BooleanProperty NORTH = BooleanProperty.create("north");
    public static final BooleanProperty SOUTH = BooleanProperty.create("south");
    public static final BooleanProperty EAST = BooleanProperty.create("east");
    public static final BooleanProperty WEST = BooleanProperty.create("west");
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    protected static final VoxelShape AABB_SLAB_BOTTOM = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D);
    protected static final VoxelShape AABB_OCTAL_NE = Block.box(8.0D, 8.0D, 0.0D, 16.0D, 16.0D, 8.0D);
    protected static final VoxelShape AABB_OCTAL_SE = Block.box(8.0D, 8.0D, 8.0D, 16.0D, 16.0D, 16.0D);
    protected static final VoxelShape AABB_OCTAL_SW = Block.box(0.0D, 8.0D, 8.0D, 8.0D, 16.0D, 16.0D);
    protected static final VoxelShape AABB_OCTAL_NW = Block.box(0.0D, 8.0D, 0.0D, 8.0D, 16.0D, 8.0D);

    protected static final Map<BlockState, VoxelShape> SHAPE_MAP = new HashMap<>();

    public NestBlock(final Block.Properties builder) {
        super(builder);
        this.registerDefaultState(this.getStateDefinition().any()
                .setValue(NORTH, false).setValue(EAST, false).setValue(SOUTH, false).setValue(WEST, false).setValue(WATERLOGGED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(NORTH).add(EAST).add(SOUTH).add(WEST).add(WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState fluid = context.getLevel().getFluidState(context.getClickedPos());
        return this.defaultBlockState().setValue(WATERLOGGED, fluid.is(FluidTags.WATER));
    }

    @Override
    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor worldIn,
                                  BlockPos currentPos, BlockPos facingPos) {
        if (stateIn.getValue(WATERLOGGED)) {
            worldIn.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(worldIn));
        }
        final boolean north = worldIn.getBlockState(currentPos.relative(Direction.NORTH)).getBlock() != this;
        final boolean east = worldIn.getBlockState(currentPos.relative(Direction.EAST)).getBlock() != this;
        final boolean south = worldIn.getBlockState(currentPos.relative(Direction.SOUTH)).getBlock() != this;
        final boolean west = worldIn.getBlockState(currentPos.relative(Direction.WEST)).getBlock() != this;
        return stateIn.setValue(NORTH, north).setValue(EAST, east).setValue(SOUTH, south).setValue(WEST, west);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : Fluids.EMPTY.defaultFluidState();
    }

    @Override
    public VoxelShape getShape(final BlockState blockstate, final BlockGetter level, final BlockPos pos, final CollisionContext context) {
        return SHAPE_MAP.computeIfAbsent(blockstate, state -> computeShape(state, level, pos, context));
    }

    private static VoxelShape computeShape(final BlockState blockstate, final BlockGetter level, final BlockPos pos, final CollisionContext context) {
        VoxelShape shape = AABB_SLAB_BOTTOM;
        final boolean north = blockstate.getValue(NORTH);
        final boolean east = blockstate.getValue(EAST);
        final boolean south = blockstate.getValue(SOUTH);
        final boolean west = blockstate.getValue(WEST);
        if (north || east) shape = Shapes.joinUnoptimized(shape, AABB_OCTAL_NE, BooleanOp.OR);
        if (north || west) shape = Shapes.joinUnoptimized(shape, AABB_OCTAL_NW, BooleanOp.OR);
        if (south || east) shape = Shapes.joinUnoptimized(shape, AABB_OCTAL_SE, BooleanOp.OR);
        if (south || west) shape = Shapes.joinUnoptimized(shape, AABB_OCTAL_SW, BooleanOp.OR);
        shape = shape.optimize();
        return shape;
    }

    @Override
    public int getFireSpreadSpeed(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
        if (face == Direction.UP) {
            return 0;
        }
        return 10;
    }

    @Override
    public int getFlammability(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
        if (face == Direction.UP) {
            return 0;
        }
        return 10;
    }
}
