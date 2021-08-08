package greekfantasy.block;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class OilLampBlock extends HorizontalBlock implements IWaterLoggable {
  
  public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
  public static final BooleanProperty LIT = BlockStateProperties.LIT;
  
  protected static final VoxelShape BODY = VoxelShapes.or(
    Block.makeCuboidShape(4.0D, 0.0D, 4.0D, 12.0D, 8.0D, 12.0D),
    Block.makeCuboidShape(6.0D, 8.0D, 6.0D, 10.0D, 10.0D, 10.0D),
    VoxelShapes.combine(
        Block.makeCuboidShape(5.0D, 10.0D, 5.0D, 11.0D, 11.0D, 11.0D),
        Block.makeCuboidShape(6.0D, 10.0D, 6.0D, 10.0D, 11.0D, 10.0D), IBooleanFunction.ONLY_FIRST));
  
  protected static final VoxelShape HANDLE_Z = VoxelShapes.or(
    VoxelShapes.combine(
        Block.makeCuboidShape(12.0D, 1.0D, 7.0D, 14.0D, 7.0D, 9.0D),
        Block.makeCuboidShape(12.0D, 2.0D, 7.0D, 13.0D, 6.0D, 9.0D),
        IBooleanFunction.ONLY_FIRST),
    VoxelShapes.combine(
        Block.makeCuboidShape(2.0D, 1.0D, 7.0D, 4.0D, 7.0D, 9.0D),
        Block.makeCuboidShape(3.0D, 2.0D, 7.0D, 4.0D, 6.0D, 9.0D),
        IBooleanFunction.ONLY_FIRST));
  
  protected static final VoxelShape HANDLE_X = VoxelShapes.or(
    VoxelShapes.combine(
        Block.makeCuboidShape(7.0D, 1.0D, 2.0D, 9.0D, 7.0D, 4.0D),
        Block.makeCuboidShape(7.0D, 2.0D, 3.0D, 9.0D, 6.0D, 4.0D),
        IBooleanFunction.ONLY_FIRST),
    VoxelShapes.combine(
        Block.makeCuboidShape(7.0D, 1.0D, 12.0D, 9.0D, 7.0D, 14.0D),
        Block.makeCuboidShape(7.0D, 2.0D, 12.0D, 9.0D, 6.0D, 13.0D),
        IBooleanFunction.ONLY_FIRST));
  
  protected static final VoxelShape SHAPE_X = VoxelShapes.combine(BODY, HANDLE_X, IBooleanFunction.OR);
  protected static final VoxelShape SHAPE_Z = VoxelShapes.combine(BODY, HANDLE_Z, IBooleanFunction.OR);
  
  public OilLampBlock(final Block.Properties properties) {
    super(properties);
    this.setDefaultState(this.getStateContainer().getBaseState()
        .with(LIT, true).with(WATERLOGGED, false).with(HORIZONTAL_FACING, Direction.NORTH));
  }
  
  @Override
  protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
    builder.add(HORIZONTAL_FACING, WATERLOGGED, LIT);
  }

  @Override
  public BlockState getStateForPlacement(BlockItemUseContext context) {
    FluidState fluid = context.getWorld().getFluidState(context.getPos());
    boolean waterlogged = fluid.isTagged(FluidTags.WATER);
    return this.getDefaultState().with(LIT, !waterlogged).with(WATERLOGGED, waterlogged).with(HORIZONTAL_FACING, context.getPlacementHorizontalFacing());
  }
  
  @Override
  public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn,
      BlockPos currentPos, BlockPos facingPos) {
    if (stateIn.get(WATERLOGGED)) {
      worldIn.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));
      if(stateIn.get(LIT)) {
        worldIn.setBlockState(currentPos, stateIn.with(LIT, false), 2);
      }
    }
    return stateIn;
  }

  @Override
  public ActionResultType onBlockActivated(final BlockState state, final World worldIn, final BlockPos pos,
      final PlayerEntity playerIn, final Hand handIn, final BlockRayTraceResult hit) {
    ItemStack heldItem = playerIn.getHeldItem(handIn);
    if(heldItem.isEmpty() && state.get(LIT)) {
      worldIn.setBlockState(pos, state.with(LIT, false), 2);
      playerIn.playSound(SoundEvents.BLOCK_FIRE_EXTINGUISH, 0.4F, 1.0F);
    } else if(heldItem.getItem() == Items.FLINT_AND_STEEL && !state.get(LIT) && !state.get(WATERLOGGED)) {
      worldIn.setBlockState(pos, state.with(LIT, true), 2);
      playerIn.playSound(SoundEvents.ITEM_FLINTANDSTEEL_USE, 0.4F, 1.0F);
    }
    return ActionResultType.SUCCESS;
  }
  
  @Override
  public FluidState getFluidState(BlockState state) {
    return state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : Fluids.EMPTY.getDefaultState();
  }

  @Override
  public VoxelShape getShape(final BlockState state, final IBlockReader worldIn, final BlockPos pos, final ISelectionContext cxt) {
    
    VoxelShape BASE = VoxelShapes.or(
        Block.makeCuboidShape(5.5D, 0.0D, 5.5D, 10.5D, 1.0D, 10.5D),
        Block.makeCuboidShape(6.5D, 1.0D, 6.5D, 9.5D, 3.0D, 9.5D));
    VoxelShape BODY_X = VoxelShapes.or(
        Block.makeCuboidShape(3.0D, 3.0D, 6.0D, 13.0D, 4.0D, 10.0D),
        Block.makeCuboidShape(3.0D, 4.0D, 5.0D, 13.0D, 6.0D, 11.0D),
        Block.makeCuboidShape(4.0D, 6.0D, 6.0D, 12.0D, 7.0D, 10.0D),
        Block.makeCuboidShape(7.0D, 7.0D, 7.0D, 9.0D, 8.0D, 9.0D));
    VoxelShape BODY_Z = VoxelShapes.or(
        Block.makeCuboidShape(6.0D, 3.0D, 3.0D, 10.0D, 4.0D, 13.0D),
        Block.makeCuboidShape(5.0D, 4.0D, 3.0D, 11.0D, 6.0D, 13.0D),
        Block.makeCuboidShape(6.0D, 6.0D, 4.0D, 10.0D, 7.0D, 12.0D),
        Block.makeCuboidShape(7.0D, 7.0D, 7.0D, 9.0D, 8.0D, 9.0D));
    VoxelShape HANDLE_NORTH = VoxelShapes.combine(
        Block.makeCuboidShape(12.0D, 3.0D, 7.0D, 15.0D, 6.0D, 9.0D),
        Block.makeCuboidShape(13.0D, 4.0D, 7.0D, 14.0D, 5.0D, 9.0D), IBooleanFunction.ONLY_FIRST);
    VoxelShape HANDLE_SOUTH = VoxelShapes.combine(
        Block.makeCuboidShape(1.0D, 3.0D, 7.0D, 4.0D, 6.0D, 9.0D),
        Block.makeCuboidShape(2.0D, 4.0D, 7.0D, 3.0D, 5.0D, 9.0D), IBooleanFunction.ONLY_FIRST);
    
    VoxelShape HANDLE_WEST = VoxelShapes.combine(
        Block.makeCuboidShape(7.0D, 3.0D, 1.0D, 9.0D, 6.0D, 4.0D),
        Block.makeCuboidShape(7.0D, 4.0D, 2.0D, 9.0D, 5.0D, 3.0D), IBooleanFunction.ONLY_FIRST);
    
    VoxelShape HANDLE_EAST = VoxelShapes.combine(
        Block.makeCuboidShape(7.0D, 3.0D, 12.0D, 9.0D, 6.0D, 15.0D),
        Block.makeCuboidShape(7.0D, 4.0D, 13.0D, 9.0D, 5.0D, 14.0D), IBooleanFunction.ONLY_FIRST);
    
    VoxelShape SHAPE_NORTH = VoxelShapes.or(BASE, BODY_X, HANDLE_NORTH, Block.makeCuboidShape(0.0D, 4.0D, 7.0D, 3.0D, 6.0D, 9.0D));
    VoxelShape SHAPE_SOUTH = VoxelShapes.or(BASE, BODY_X, HANDLE_SOUTH, Block.makeCuboidShape(13.0D, 4.0D, 7.0D, 16.0D, 6.0D, 9.0D));
    VoxelShape SHAPE_WEST = VoxelShapes.or(BASE, BODY_Z, HANDLE_WEST, Block.makeCuboidShape(7.0D, 4.0D, 13.0D, 9.0D, 6.0D, 16.0D));
    VoxelShape SHAPE_EAST = VoxelShapes.or(BASE, BODY_Z, HANDLE_EAST, Block.makeCuboidShape(7.0D, 4.0D, 0.0D, 9.0D, 6.0D, 3.0D));
    
    
    EnumMap<Direction, VoxelShape> shapeMap = new EnumMap<>(Direction.class);
    shapeMap.put(Direction.NORTH, SHAPE_NORTH);
    shapeMap.put(Direction.SOUTH, SHAPE_SOUTH);
    shapeMap.put(Direction.WEST, SHAPE_WEST);
    shapeMap.put(Direction.EAST, SHAPE_EAST);
    Direction dir = state.get(HORIZONTAL_FACING);
    return shapeMap.getOrDefault(dir, BASE);
  }
  
  @OnlyIn(Dist.CLIENT)
  public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
    if(stateIn.get(LIT).booleanValue()) {
      Direction d = stateIn.get(HORIZONTAL_FACING).rotateYCCW();
      double addX = 0.45D * d.getXOffset();
      double addY = 0.5D;
      double addZ = 0.45D * d.getZOffset();
      Vector3d vec = Vector3d.copyCenteredHorizontally(pos).add(addX, addY, addZ);
      worldIn.addParticle(ParticleTypes.SMOKE, vec.getX(), vec.getY(), vec.getZ(), 0.0D, 0.0D, 0.0D);
      worldIn.addParticle(ParticleTypes.FLAME, vec.getX(), vec.getY(), vec.getZ(), 0.0D, 0.0D, 0.0D);
    }
  }
}
