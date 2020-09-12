package greekfantasy.block;

import greekfantasy.GFRegistry;
import greekfantasy.tileentity.StatueTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public class StatueBlock extends HorizontalBlock {
  
  public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
  
  public StatueBlock(final Block.Properties builder) {
    super(builder);
    this.setDefaultState(this.getStateContainer().getBaseState()
        .with(HORIZONTAL_FACING, Direction.NORTH)
        .with(HALF, DoubleBlockHalf.LOWER));
  }
  
  @Override
  protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
    builder.add(HORIZONTAL_FACING).add(HALF);
  }

  @Override
  public BlockState getStateForPlacement(BlockItemUseContext context) {
    return this.getDefaultState().with(HORIZONTAL_FACING, context.getPlacementHorizontalFacing().getOpposite());
  }

  @Override
  public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
    worldIn.setBlockState(pos.up(), state.with(HALF, DoubleBlockHalf.UPPER), 3);
  }
  
  @Override
  public void onBlockHarvested(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
    final DoubleBlockHalf half = state.get(HALF);
    final boolean isUpper = half == DoubleBlockHalf.UPPER;
    BlockPos blockpos = isUpper ? pos.down() : pos.up();
    BlockState blockstate = worldIn.getBlockState(blockpos);
    if (blockstate.getBlock() == state.getBlock() && blockstate.get(HALF) != half) {
      worldIn.setBlockState(blockpos, Blocks.AIR.getDefaultState(), 35);
      worldIn.playEvent(player, 2001, blockpos, Block.getStateId(blockstate));
    }

    super.onBlockHarvested(worldIn, pos, state, player);
  }

  @Override
  public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
    return worldIn.isAirBlock(pos.up());
  }

  @Override
  public ActionResultType onBlockActivated(final BlockState state, final World worldIn, final BlockPos pos, 
      final PlayerEntity player, final Hand handIn, final BlockRayTraceResult hit) {
    final BlockPos tePos = state.get(HALF) == DoubleBlockHalf.UPPER ? pos.down() : pos;
    final TileEntity te = worldIn.getTileEntity(tePos);
    if(te instanceof StatueTileEntity) {
      return ((StatueTileEntity)te).onBlockActivated(state, worldIn, tePos, player, handIn, hit);
    }
    return ActionResultType.PASS;
  }
  
  @Override
  public boolean hasTileEntity(final BlockState state) {
    return true;
  }
  
  @Override
  public TileEntity createTileEntity(final BlockState state, final IBlockReader world) {
    final StatueTileEntity te = GFRegistry.STATUE_TE.create();
    te.setUpper(state.get(HALF) == DoubleBlockHalf.UPPER);
    return te;
  }
  
}
