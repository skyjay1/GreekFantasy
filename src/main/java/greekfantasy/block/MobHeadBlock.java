package greekfantasy.block;

import greekfantasy.GFRegistry;
import greekfantasy.tileentity.MobHeadTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.FluidTags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;

public class MobHeadBlock extends HorizontalBlock implements IWaterLoggable {
  
  public static final BooleanProperty WALL = BooleanProperty.create("wall");
  public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

  private static final VoxelShape SHAPE = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 15.9D, 16.0D);
  protected final MobHeadTileEntity.HeadType headType;

  public MobHeadBlock(final MobHeadTileEntity.HeadType head, Properties prop) {
    super(prop);
    headType = head;
    this.setDefaultState(this.getStateContainer().getBaseState()
        .with(WALL, Boolean.valueOf(false))
        .with(HORIZONTAL_FACING, Direction.NORTH)
        .with(WATERLOGGED, false));
  }

  @Override
  protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
    builder.add(WALL).add(HORIZONTAL_FACING).add(WATERLOGGED);
  }

  @Override
  public BlockState getStateForPlacement(BlockItemUseContext context) {
    final Direction facing = context.getPlacementHorizontalFacing().getOpposite();
    final boolean wall = context.getFace() != Direction.UP && context.getFace() != Direction.DOWN;
    FluidState fluid = context.getWorld().getFluidState(context.getPos());
    return this.getDefaultState().with(WATERLOGGED, fluid.isTagged(FluidTags.WATER)).with(WALL, wall).with(HORIZONTAL_FACING, facing);
  }
  
  @Override
  public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn,
      BlockPos currentPos, BlockPos facingPos) {
    if (stateIn.get(WATERLOGGED)) {
      worldIn.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));
    }
    return stateIn;
  }
  
  @Override
  public FluidState getFluidState(BlockState state) {
    return state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : Fluids.EMPTY.getDefaultState();
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
