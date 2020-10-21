package greekfantasy.block;

import greekfantasy.GFRegistry;
import greekfantasy.tileentity.MobHeadTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;

public class MobHeadBlock extends HorizontalBlock {
  
  public static final BooleanProperty WALL = BooleanProperty.create("wall");
  
  private static final VoxelShape SHAPE = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 15.9D, 16.0D);
    
  protected final MobHeadTileEntity.HeadType headType;
  
  public MobHeadBlock(final MobHeadTileEntity.HeadType head, Properties prop) {
    super(prop);
    headType = head;
    this.setDefaultState(this.getStateContainer().getBaseState().with(WALL, Boolean.valueOf(false)).with(HORIZONTAL_FACING, Direction.NORTH));
  }

  @Override
  protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
    builder.add(WALL).add(HORIZONTAL_FACING);
  }

  @Override
  public BlockState getStateForPlacement(BlockItemUseContext context) {
    final Direction facing = context.getPlacementHorizontalFacing().getOpposite();
    final boolean wall = context.getFace() != Direction.UP && context.getFace() != Direction.DOWN;
    return this.getDefaultState().with(WALL, wall).with(HORIZONTAL_FACING, facing);
  }
  
  @Override
  public VoxelShape getShape(final BlockState state, final IBlockReader worldIn, final BlockPos pos, final ISelectionContext cxt) {
    return SHAPE;
  }
  
  @Override
  public boolean hasTileEntity(final BlockState state) {
    return true;
  }
  
  @Override
  public TileEntity createTileEntity(final BlockState state, final IBlockReader world) {
    final MobHeadTileEntity te = GFRegistry.BOSS_HEAD_TE.create();
    te.setHeadType(headType);
    te.setWall(state.get(WALL));
    return te;
  }
}
