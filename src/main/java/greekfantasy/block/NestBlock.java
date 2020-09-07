package greekfantasy.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;

public class NestBlock extends Block {
  
  public static final BooleanProperty NORTH = BooleanProperty.create("north");
  public static final BooleanProperty SOUTH = BooleanProperty.create("south");
  public static final BooleanProperty EAST = BooleanProperty.create("east");
  public static final BooleanProperty WEST = BooleanProperty.create("west");
  
  protected static final VoxelShape AABB_SLAB_BOTTOM = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D);
  protected static final VoxelShape AABB_QUAD_NORTH = Block.makeCuboidShape(0.0D, 8.0D, 0.0D, 16.0D, 16.0D, 8.0D);
  protected static final VoxelShape AABB_QUAD_SOUTH = Block.makeCuboidShape(0.0D, 8.0D, 8.0D, 16.0D, 16.0D, 16.0D);
  protected static final VoxelShape AABB_QUAD_EAST = Block.makeCuboidShape(8.0D, 8.0D, 0.0D, 16.0D, 16.0D, 16.0D);
  protected static final VoxelShape AABB_QUAD_WEST = Block.makeCuboidShape(0.0D, 8.0D, 0.0D, 8.0D, 16.0D, 16.0D);

  public NestBlock() {
    super(Block.Properties.from(Blocks.HAY_BLOCK).notSolid().variableOpacity());
    this.setDefaultState(this.getStateContainer().getBaseState()
        .with(NORTH, false).with(EAST, true).with(SOUTH, false).with(WEST, true));
  }
  
  @Override
  protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
    builder.add(NORTH).add(EAST).add(SOUTH).add(WEST);
  }

  @Override
  public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn,
      BlockPos currentPos, BlockPos facingPos) {
   
    final boolean north = worldIn.getBlockState(currentPos.offset(Direction.NORTH)).getBlock() == this;
    final boolean east = worldIn.getBlockState(currentPos.offset(Direction.EAST)).getBlock() == this;
    final boolean south = worldIn.getBlockState(currentPos.offset(Direction.SOUTH)).getBlock() == this;
    final boolean west = worldIn.getBlockState(currentPos.offset(Direction.WEST)).getBlock() == this; 
    return stateIn.with(NORTH, north).with(EAST, east).with(SOUTH, south).with(WEST, west);
  }
  
  @Override
  public VoxelShape getShape(final BlockState state, final IBlockReader worldIn, final BlockPos pos, final ISelectionContext cxt) {
    VoxelShape shape = AABB_SLAB_BOTTOM;
    if(!state.get(NORTH)) shape = VoxelShapes.combine(shape, AABB_QUAD_NORTH, IBooleanFunction.OR);
    if(!state.get(SOUTH)) shape = VoxelShapes.combine(shape, AABB_QUAD_SOUTH, IBooleanFunction.OR);
    if(!state.get(EAST)) shape = VoxelShapes.combine(shape, AABB_QUAD_EAST, IBooleanFunction.OR);
    if(!state.get(WEST)) shape = VoxelShapes.combine(shape, AABB_QUAD_WEST, IBooleanFunction.OR);
    
    return shape.simplify();
  }
}
