package greekfantasy.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;

public class ConnectedPillarBlock extends Block {
  
  public static final EnumProperty<ConnectionType> CONNECTION = EnumProperty.create("connection", ConnectionType.class);

  protected static final VoxelShape AABB_SLAB_TOP = Block.makeCuboidShape(0.0D, 8.0D, 0.0D, 16.0D, 16.0D, 16.0D);
  protected static final VoxelShape AABB_SLAB_BOTTOM = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D);
  protected static final VoxelShape AABB_PILLAR = Block.makeCuboidShape(3.0D, 0.0D, 3.0D, 13.0D, 16.0D, 13.0D);
  
  public ConnectedPillarBlock(final Properties properties) {
    super(properties);
    this.setDefaultState(this.getStateContainer().getBaseState()
        .with(CONNECTION, ConnectionType.LOWER));
  }
  
  @Override
  protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
    builder.add(CONNECTION);
  }
  
  @Override
  public BlockState getStateForPlacement(BlockItemUseContext context) {
    final ConnectionType connectionType = getConnectionTypeForPos(this.getDefaultState(), context.getWorld(), context.getPos());
    return this.getDefaultState().with(CONNECTION, connectionType);
  }

  @Override
  public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn,
      BlockPos currentPos, BlockPos facingPos) {
    return stateIn.with(CONNECTION, getConnectionTypeForPos(stateIn, worldIn, currentPos));
  }
  
  @Override
  public VoxelShape getShape(final BlockState state, final IBlockReader worldIn, final BlockPos pos, final ISelectionContext cxt) {
    final ConnectionType connection = state.get(CONNECTION);
    switch(connection) {
    case LOWER: return VoxelShapes.combine(AABB_PILLAR, AABB_SLAB_BOTTOM, IBooleanFunction.OR);
    case UPPER: return VoxelShapes.combine(AABB_PILLAR, AABB_SLAB_TOP, IBooleanFunction.OR);
    case NONE: default: return AABB_PILLAR;
    }
  }
  
  private ConnectionType getConnectionTypeForPos(final BlockState state, final IBlockReader worldIn, final BlockPos pos) {
    final boolean isPillarAbove = worldIn.getBlockState(pos.up()).getBlock() == state.getBlock();
    final boolean isPillarBelow = worldIn.getBlockState(pos.down()).getBlock() == state.getBlock();
    if(isPillarAbove && isPillarBelow) {
      return ConnectionType.NONE;
    } else if(isPillarBelow) {
      return ConnectionType.UPPER;
    } else {
      return ConnectionType.LOWER;
    } 
  }
  
  public static enum ConnectionType implements IStringSerializable {
    NONE("none"), 
    LOWER("lower"), 
    UPPER("upper");
    
    private final String name;
    
    private ConnectionType(final String nameIn) {
      this.name = nameIn;
    }

    @Override
    public String getString() {
      return name;
    }
  }

}
