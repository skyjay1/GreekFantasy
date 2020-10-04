package greekfantasy.block;

import java.util.Random;

import greekfantasy.util.MysteriousBoxManager;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class MysteriousBoxBlock extends HorizontalBlock {
  
  public static final BooleanProperty OPEN = BooleanProperty.create("open");
  
  protected static final VoxelShape AABB_BOX_X = Block.makeCuboidShape(1.0D, 0.0D, 3.0D, 15.0D, 8.0D, 13.0D);
  protected static final VoxelShape AABB_BOX_Z = Block.makeCuboidShape(3.0D, 0.0D, 1.0D, 13.0D, 8.0D, 15.0D);

  public MysteriousBoxBlock(final Block.Properties properties) {
    super(properties);
    this.setDefaultState(this.getStateContainer().getBaseState()
        .with(HORIZONTAL_FACING, Direction.NORTH)
        .with(OPEN, Boolean.valueOf(false)));
  }
  
  @Override
  protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
    builder.add(HORIZONTAL_FACING).add(OPEN);
  }
  
  @Override
  public BlockState getStateForPlacement(BlockItemUseContext context) {
    return this.getDefaultState().with(HORIZONTAL_FACING, context.getPlacementHorizontalFacing().getOpposite());
  }
  
  @Override
  public ActionResultType onBlockActivated(final BlockState state, final World worldIn, final BlockPos pos,
      final PlayerEntity playerIn, final Hand handIn, final BlockRayTraceResult hit) {
    if(!state.get(OPEN).booleanValue()) {
      addSmokeParticles(worldIn, pos, worldIn.rand, 32);
      if(playerIn.isServerWorld()) {
        // open the box
        final boolean open = MysteriousBoxManager.onBoxOpened(worldIn, playerIn, state, pos);
        if(open) {
          worldIn.setBlockState(pos, state.with(OPEN, Boolean.valueOf(true)), 2);
        }
      }
      return ActionResultType.CONSUME;
    }
    
    return ActionResultType.SUCCESS;
  }
  
  @Override
  public VoxelShape getShape(final BlockState state, final IBlockReader worldIn, final BlockPos pos, final ISelectionContext cxt) {
    final Direction.Axis axis = state.get(HORIZONTAL_FACING).getAxis();
    return axis == Direction.Axis.X ? AABB_BOX_X : AABB_BOX_Z;
  }
  

  /**
   * Called periodically clientside on blocks near the player to show effects (like furnace fire particles). Note that
   * this method is unrelated to randomTick and #needsRandomTick, and will always be called regardless
   * of whether the block can receive random update ticks
   */
  @OnlyIn(Dist.CLIENT)
  public void animateTick(BlockState stateIn, World world, BlockPos pos, Random rand) {
    if(stateIn.get(OPEN).booleanValue()) {
      addSmokeParticles(world, pos, rand, 3);
    }
  }
  
  private void addSmokeParticles(final World world, final BlockPos pos, final Random rand, final int count) {
    final double x = pos.getX() + 0.5D;
    final double y = pos.getY() + 0.22D;
    final double z = pos.getZ() + 0.5D;
    final double motion = 0.08D;
    final double radius = 0.25D;
    for (int i = 0; i < count; i++) {
      world.addParticle(ParticleTypes.SMOKE, 
          x + (world.rand.nextDouble() - 0.5D) * radius, y, z + (world.rand.nextDouble() - 0.5D) * radius,
          (world.rand.nextDouble() - 0.5D) * motion, 
          (world.rand.nextDouble() - 0.5D) * motion * 0.5D,
          (world.rand.nextDouble() - 0.5D) * motion);
    }
  }
}
