package greekfantasy.block;

import greekfantasy.GFRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class GoldenStringBlock extends Block {

    public static final BooleanProperty NORTH = BooleanProperty.create("north");
    public static final BooleanProperty SOUTH = BooleanProperty.create("south");
    public static final BooleanProperty EAST = BooleanProperty.create("east");
    public static final BooleanProperty WEST = BooleanProperty.create("west");
    public static final IntegerProperty AGE = BlockStateProperties.AGE_1;

    protected static final VoxelShape SHAPE_CENTER = Block.box(3, 0, 3, 13, 1, 13);
    protected static final VoxelShape SHAPE_NORTH = Block.box(3, 0, 0, 13, 1, 3);
    protected static final VoxelShape SHAPE_EAST = Block.box(13, 0, 3, 16, 1, 13);
    protected static final VoxelShape SHAPE_SOUTH = Block.box(3, 0, 13, 13, 1, 16);
    protected static final VoxelShape SHAPE_WEST = Block.box(0, 0, 3, 3, 1, 13);
    protected static final Map<BlockState, VoxelShape> SHAPE_MAP = new HashMap<>();

    public GoldenStringBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any()
                .setValue(NORTH, false).setValue(EAST, false)
                .setValue(SOUTH, false).setValue(WEST, false)
                .setValue(AGE, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(NORTH).add(EAST).add(SOUTH).add(WEST).add(AGE);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return super.getStateForPlacement(context);
    }

    @Override
    public void tick(BlockState blockState, ServerLevel level, BlockPos pos, RandomSource random) {
        if (blockState.getValue(AGE) <= 0) {
            level.setBlock(pos, blockState.setValue(AGE, 1), Block.UPDATE_CLIENTS);
        }
    }

    @Override
    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor level,
                                  BlockPos currentPos, BlockPos facingPos) {
        if (!stateIn.canSurvive(level, currentPos)) {
            return stateIn.getFluidState().getType().defaultFluidState().createLegacyBlock();
        }
        if (stateIn.getValue(AGE) <= 0) {
            level.scheduleTick(currentPos, this, 80);
        }
        final boolean north = level.getBlockState(currentPos.relative(Direction.NORTH)).getBlock() == this;
        final boolean east = level.getBlockState(currentPos.relative(Direction.EAST)).getBlock() == this;
        final boolean south = level.getBlockState(currentPos.relative(Direction.SOUTH)).getBlock() == this;
        final boolean west = level.getBlockState(currentPos.relative(Direction.WEST)).getBlock() == this;
        return stateIn.setValue(NORTH, north).setValue(EAST, east).setValue(SOUTH, south).setValue(WEST, west);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader worldIn, BlockPos pos) {
        BlockPos blockpos = pos.below();
        BlockState blockstate = worldIn.getBlockState(blockpos);
        return blockstate.isFaceSturdy(worldIn, blockpos, Direction.UP);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return super.getFluidState(state);
    }

    @Override
    public VoxelShape getShape(final BlockState blockstate, final BlockGetter level, final BlockPos pos, final CollisionContext context) {
        return SHAPE_MAP.computeIfAbsent(blockstate.setValue(AGE, 0), state -> computeShape(state, level, pos, context));
    }

    private static VoxelShape computeShape(final BlockState blockstate, final BlockGetter level, final BlockPos pos, final CollisionContext context) {
        // create the shape and store it in the map
        VoxelShape shape = SHAPE_CENTER;
        if (blockstate.getValue(NORTH)) shape = Shapes.joinUnoptimized(shape, SHAPE_NORTH, BooleanOp.OR);
        if (blockstate.getValue(EAST)) shape = Shapes.joinUnoptimized(shape, SHAPE_EAST, BooleanOp.OR);
        if (blockstate.getValue(SOUTH)) shape = Shapes.joinUnoptimized(shape, SHAPE_SOUTH, BooleanOp.OR);
        if (blockstate.getValue(WEST)) shape = Shapes.joinUnoptimized(shape, SHAPE_WEST, BooleanOp.OR);
        shape = shape.optimize();
        return shape;
    }

    @Override
    public Item asItem() {
        return GFRegistry.ItemReg.GOLDEN_STRING.get();
    }
}
