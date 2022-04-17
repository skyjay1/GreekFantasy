package greekfantasy.block;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
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

public class NestBlock extends Block implements IWaterLoggable {
  
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
    if (stateIn.getValue(WATERLOGGED)) {
      worldIn.getLiquidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(worldIn));
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
  public VoxelShape getShape(final BlockState blockstate, final IBlockReader worldIn, final BlockPos pos, final ISelectionContext cxt) {
    VoxelShape shape = AABB_SLAB_BOTTOM;
    final BlockState state = blockstate.setValue(WATERLOGGED, false);
    if(SHAPE_MAP.containsKey(state)) {
      return SHAPE_MAP.get(state);
    }
    final boolean north = state.getValue(NORTH);
    final boolean east = state.getValue(EAST);
    final boolean south = state.getValue(SOUTH);
    final boolean west = state.getValue(WEST);
    if(north || east) shape = VoxelShapes.joinUnoptimized(shape, AABB_OCTAL_NE, IBooleanFunction.OR);
    if(north || west) shape = VoxelShapes.joinUnoptimized(shape, AABB_OCTAL_NW, IBooleanFunction.OR);
    if(south || east) shape = VoxelShapes.joinUnoptimized(shape, AABB_OCTAL_SE, IBooleanFunction.OR);
    if(south || west) shape = VoxelShapes.joinUnoptimized(shape, AABB_OCTAL_SW, IBooleanFunction.OR);
    shape = shape.optimize();
    SHAPE_MAP.put(state, shape);
    return shape;
  }
}
