package greekfantasy.block;

import java.util.Random;

import javax.annotation.Nullable;

import greekfantasy.GFRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DoublePlantBlock;
import net.minecraft.block.IGrowable;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.PlantType;

public class ReedsBlock extends DoublePlantBlock implements IWaterLoggable, IGrowable {

  public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
  public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
  
  protected static final VoxelShape SHAPE = Block.makeCuboidShape(2.0D, 0.0D, 2.0D, 14.0D, 16.0D, 14.0D);

  public ReedsBlock(Properties properties) {
    super(properties);
    this.setDefaultState(this.stateContainer.getBaseState().with(WATERLOGGED, false).with(HALF, DoubleBlockHalf.LOWER));
  }
  
  @Override
  protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
    builder.add(HALF).add(WATERLOGGED);
  }
  
  @Override
  public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) { return SHAPE; }
  
  @Override
  public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand) {
    if (!state.isValidPosition(worldIn, pos)) {
      worldIn.destroyBlock(pos, true);
    }
  }

  @Override
  public BlockState updatePostPlacement(BlockState state, Direction facing, BlockState facingState, IWorld world, BlockPos currentPos, BlockPos facingPos) {
    if (!state.isValidPosition(world, currentPos)) {
      world.getPendingBlockTicks().scheduleTick(currentPos, this, 1);
    }
    if (state.get(WATERLOGGED)) {
      world.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(world));
    }
    return super.updatePostPlacement(state, facing, facingState, world, currentPos, facingPos);
  }

  @Override
  @Nullable
  public BlockState getStateForPlacement(BlockItemUseContext context) {
    BlockPos blockpos = context.getPos();
    FluidState fluid = context.getWorld().getFluidState(blockpos);
    BlockState blockstate = super.getStateForPlacement(context);
    return blockstate != null ? blockstate.with(WATERLOGGED, fluid.isTagged(FluidTags.WATER)) : null;
  }

  @Override
  public boolean isValidPosition(BlockState state, IWorldReader world, BlockPos pos) {
    BlockState stateDown = world.getBlockState(pos.down());
    // upper block only checks that a lower block is present
    if(state.get(HALF) == DoubleBlockHalf.UPPER) {
      return stateDown.isIn(this) && stateDown.get(HALF) == DoubleBlockHalf.LOWER;
    }
    // lower block checks for near water and compatible soil
    final boolean nearWater = isInWater(world, pos) || isNextToWater(world, pos.down());
    final boolean belowAir = world.getFluidState(pos.up()).isEmpty();
    final boolean aboveSoil = stateDown.isSolid() && (stateDown.canSustainPlant(world, pos.down(), Direction.UP, this) || isValidGround(stateDown, world, pos.down()));
    return nearWater && belowAir && aboveSoil;
  }
  
  @Override
  public void placeAt(IWorld worldIn, BlockPos pos, int flags) {
    final boolean water = worldIn.getFluidState(pos).isTagged(FluidTags.WATER);
    worldIn.setBlockState(pos, this.getDefaultState().with(HALF, DoubleBlockHalf.LOWER).with(WATERLOGGED, water), flags);
    worldIn.setBlockState(pos.up(), this.getDefaultState().with(HALF, DoubleBlockHalf.UPPER), flags);
  }

  @Override
  public FluidState getFluidState(BlockState state) {
    return state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : Fluids.EMPTY.getDefaultState();
  }
  
  @Override
  protected boolean isValidGround(BlockState state, IBlockReader worldIn, BlockPos pos) {
    return state.isSolidSide(worldIn, pos, Direction.UP) || super.isValidGround(state, worldIn, pos);
  }

  @Override
  public ItemStack getItem(IBlockReader worldIn, BlockPos pos, BlockState state) { return new ItemStack(GFRegistry.REEDS); }
  
  @Override
  public PlantType getPlantType(IBlockReader world, BlockPos pos) { return PlantType.WATER; }
  
  @Override
  public boolean isReplaceable(BlockState state, BlockItemUseContext context) { return false; }
  
  @Override
  public boolean canGrow(IBlockReader reader, BlockPos pos, BlockState state, boolean isClient) { return true; }

  @Override
  public boolean canUseBonemeal(World world, Random rand, BlockPos pos, BlockState state) { return true; }

  @Override
  public void grow(ServerWorld world, Random rand, BlockPos pos, BlockState state) { 
    spawnAsEntity(world, pos, new ItemStack(this)); 
  }
  
  /**
   * @param worldIn the world
   * @param pos the position to check
   * @return true if the given position has water (or frosted ice)
   **/
  public static boolean isInWater(final IBlockReader worldIn, final BlockPos pos) {
    return worldIn.getFluidState(pos).isTagged(FluidTags.WATER) || worldIn.getBlockState(pos).isIn(Blocks.FROSTED_ICE);
  }
  
  /**
   * @param worldIn the world
   * @param pos the position to check
   * @return true if one of the four adjacent blocks are water
   **/
  public static boolean isNextToWater(final IBlockReader worldIn, final BlockPos pos) {
    for (Direction direction : Direction.Plane.HORIZONTAL) {
      if(isInWater(worldIn, pos.offset(direction))) {
        return true;
      }
    }
    return false;
  }
}
