package greekfantasy.block;

import greekfantasy.GFRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;

import java.util.HashMap;
import java.util.Map;

public class GoldenStringBlock extends Block implements IWaterLoggable {

    public static final BooleanProperty NORTH = BooleanProperty.create("north");
    public static final BooleanProperty SOUTH = BooleanProperty.create("south");
    public static final BooleanProperty EAST = BooleanProperty.create("east");
    public static final BooleanProperty WEST = BooleanProperty.create("west");
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    protected static final VoxelShape SHAPE_CENTER = Block.box(3, 0, 3, 13, 1, 13);
    protected static final VoxelShape SHAPE_NORTH = Block.box(3, 0, 0, 13, 1, 3);
    protected static final VoxelShape SHAPE_EAST = Block.box(13, 0, 3, 16, 1, 13);
    protected static final VoxelShape SHAPE_SOUTH = Block.box(3, 0, 13, 13, 1, 16);
    protected static final VoxelShape SHAPE_WEST = Block.box(0, 0, 3, 3, 1, 13);
    protected static final Map<BlockState, VoxelShape> SHAPE_MAP = new HashMap<>();

    public GoldenStringBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any()
                .setValue(NORTH, false).setValue(EAST, false).setValue(SOUTH, false).setValue(WEST, false).setValue(WATERLOGGED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(NORTH).add(EAST).add(SOUTH).add(WEST).add(WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        FluidState fluid = context.getLevel().getFluidState(context.getClickedPos());
        return this.defaultBlockState().setValue(WATERLOGGED, fluid.is(FluidTags.WATER));
    }

    @Override
    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn,
                                  BlockPos currentPos, BlockPos facingPos) {
        if (!stateIn.canSurvive(worldIn, currentPos)) {
            return stateIn.getFluidState().getType().defaultFluidState().createLegacyBlock();
        }
        if (stateIn.getValue(WATERLOGGED)) {
            worldIn.getLiquidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(worldIn));
        }
        final boolean north = worldIn.getBlockState(currentPos.relative(Direction.NORTH)).getBlock() == this;
        final boolean east = worldIn.getBlockState(currentPos.relative(Direction.EAST)).getBlock() == this;
        final boolean south = worldIn.getBlockState(currentPos.relative(Direction.SOUTH)).getBlock() == this;
        final boolean west = worldIn.getBlockState(currentPos.relative(Direction.WEST)).getBlock() == this;
        return stateIn.setValue(NORTH, north).setValue(EAST, east).setValue(SOUTH, south).setValue(WEST, west);
    }

    @Override
    public boolean canSurvive(BlockState state, IWorldReader worldIn, BlockPos pos) {
        BlockPos blockpos = pos.below();
        BlockState blockstate = worldIn.getBlockState(blockpos);
        return blockstate.isFaceSturdy(worldIn, blockpos, Direction.UP) || blockstate.is(Blocks.HOPPER);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : Fluids.EMPTY.defaultFluidState();
    }

    @Override
    public VoxelShape getShape(final BlockState blockstate, final IBlockReader worldIn, final BlockPos pos, final ISelectionContext cxt) {
        final BlockState state = blockstate.setValue(WATERLOGGED, false);
        if (SHAPE_MAP.containsKey(state)) {
            return SHAPE_MAP.get(state);
        }
        // create the shape and store it in the map
        VoxelShape shape = SHAPE_CENTER;
        if (state.getValue(NORTH)) shape = VoxelShapes.joinUnoptimized(shape, SHAPE_NORTH, IBooleanFunction.OR);
        if (state.getValue(EAST)) shape = VoxelShapes.joinUnoptimized(shape, SHAPE_EAST, IBooleanFunction.OR);
        if (state.getValue(SOUTH)) shape = VoxelShapes.joinUnoptimized(shape, SHAPE_SOUTH, IBooleanFunction.OR);
        if (state.getValue(WEST)) shape = VoxelShapes.joinUnoptimized(shape, SHAPE_WEST, IBooleanFunction.OR);
        shape = shape.optimize();
        SHAPE_MAP.put(state, shape);
        return shape;
    }

    /**
     * Returns the translation key of the item form of this block
     */
    @Override
    public String getDescriptionId() {
        return GFRegistry.ItemReg.GOLDEN_STRING.getDescriptionId();
    }

    @Override
    public Item asItem() {
        return GFRegistry.ItemReg.GOLDEN_STRING;
    }
}
