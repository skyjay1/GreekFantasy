package greekfantasy.block;

import java.util.HashMap;
import java.util.Map;

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

public class GoldenStringBlock extends Block implements IWaterLoggable {
  
  public static final BooleanProperty NORTH = BooleanProperty.create("north");
  public static final BooleanProperty SOUTH = BooleanProperty.create("south");
  public static final BooleanProperty EAST = BooleanProperty.create("east");
  public static final BooleanProperty WEST = BooleanProperty.create("west");
  public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
  
  protected static final VoxelShape SHAPE_CENTER = Block.makeCuboidShape(3, 0, 3, 13, 1, 13);
  protected static final VoxelShape SHAPE_NORTH = Block.makeCuboidShape(3, 0, 0, 13, 1, 3);
  protected static final VoxelShape SHAPE_EAST = Block.makeCuboidShape(13, 0, 3, 16, 1, 13);
  protected static final VoxelShape SHAPE_SOUTH = Block.makeCuboidShape(3, 0, 13, 13, 1, 16);
  protected static final VoxelShape SHAPE_WEST = Block.makeCuboidShape(0, 0, 3, 3, 1, 13);
  protected static final Map<BlockState, VoxelShape> SHAPE_MAP = new HashMap<>();

  public GoldenStringBlock(Properties properties) {
    super(properties);
    this.setDefaultState(this.getStateContainer().getBaseState()
        .with(NORTH, false).with(EAST, false).with(SOUTH, false).with(WEST, false).with(WATERLOGGED, false));
  }
  
  @Override
  protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
    builder.add(NORTH).add(EAST).add(SOUTH).add(WEST).add(WATERLOGGED);
  }
  
  @Override
  public BlockState getStateForPlacement(BlockItemUseContext context) {
    FluidState fluid = context.getWorld().getFluidState(context.getPos());
    return this.getDefaultState().with(WATERLOGGED, fluid.isTagged(FluidTags.WATER));
  }
  
  @Override
  public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn,
      BlockPos currentPos, BlockPos facingPos) {
    if(!stateIn.isValidPosition(worldIn, currentPos)) {
      return stateIn.getFluidState().getFluid().getDefaultState().getBlockState();
    }
    if (stateIn.get(WATERLOGGED)) {
      worldIn.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));
    }
    final boolean north = worldIn.getBlockState(currentPos.offset(Direction.NORTH)).getBlock() == this;
    final boolean east = worldIn.getBlockState(currentPos.offset(Direction.EAST)).getBlock() == this;
    final boolean south = worldIn.getBlockState(currentPos.offset(Direction.SOUTH)).getBlock() == this;
    final boolean west = worldIn.getBlockState(currentPos.offset(Direction.WEST)).getBlock() == this; 
    return stateIn.with(NORTH, north).with(EAST, east).with(SOUTH, south).with(WEST, west);
  }

  @Override
  public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
    BlockPos blockpos = pos.down();
    BlockState blockstate = worldIn.getBlockState(blockpos);
    return blockstate.isSolidSide(worldIn, blockpos, Direction.UP) || blockstate.isIn(Blocks.HOPPER);
  }

  @Override
  public FluidState getFluidState(BlockState state) {
    return state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : Fluids.EMPTY.getDefaultState();
  }

  @Override
  public VoxelShape getShape(final BlockState blockstate, final IBlockReader worldIn, final BlockPos pos, final ISelectionContext cxt) {
    final BlockState state = blockstate.with(WATERLOGGED, false);
    if(SHAPE_MAP.containsKey(state)) {
      return SHAPE_MAP.get(state);
    }
    // create the shape and store it in the map
    VoxelShape shape = SHAPE_CENTER;
    if(state.get(NORTH)) shape = VoxelShapes.combine(shape, SHAPE_NORTH, IBooleanFunction.OR);
    if(state.get(EAST)) shape = VoxelShapes.combine(shape, SHAPE_EAST, IBooleanFunction.OR);
    if(state.get(SOUTH)) shape = VoxelShapes.combine(shape, SHAPE_SOUTH, IBooleanFunction.OR);
    if(state.get(WEST)) shape = VoxelShapes.combine(shape, SHAPE_WEST, IBooleanFunction.OR);
    shape = shape.simplify();
    SHAPE_MAP.put(state, shape);
    return shape;
  }
  
  /**
   * Returns the translation key of the item form of this block
   */
  @Override
  public String getTranslationKey() {
    return GFRegistry.GOLDEN_STRING.getTranslationKey();
  }
  
  @Override
  public Item asItem() { return GFRegistry.GOLDEN_STRING; }
}
