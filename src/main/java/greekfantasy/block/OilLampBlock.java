package greekfantasy.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.EquipmentSlotType;
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
    
  protected static final VoxelShape[] SHAPES;
  
  static {
    VoxelShape bodyX = Block.box(4.0D, 0.0D, 4.0D, 12.0D, 4.0D, 12.0D);
    VoxelShape bodyZ = Block.box(4.0D, 0.0D, 4.0D, 12.0D, 4.0D, 12.0D);
    VoxelShape handleN = VoxelShapes.joinUnoptimized(
        Block.box(12.0D, 0.0D, 7.0D, 14.0D, 4.0D, 9.0D),
        Block.box(12.0D, 1.0D, 7.0D, 13.0D, 3.0D, 9.0D), IBooleanFunction.ONLY_FIRST);
    VoxelShape handleS = VoxelShapes.joinUnoptimized(
        Block.box(2.0D, 0.0D, 7.0D, 4.0D, 4.0D, 9.0D),
        Block.box(3.0D, 1.0D, 7.0D, 4.0D, 3.0D, 9.0D), IBooleanFunction.ONLY_FIRST);
    VoxelShape handleW = VoxelShapes.joinUnoptimized(
        Block.box(7.0D, 0.0D, 2.0D, 9.0D, 4.0D, 4.0D),
        Block.box(7.0D, 1.0D, 3.0D, 9.0D, 3.0D, 4.0D), IBooleanFunction.ONLY_FIRST);
    VoxelShape handleE = VoxelShapes.joinUnoptimized(
        Block.box(7.0D, 0.0D, 12.0D, 9.0D, 4.0D, 14.0D),
        Block.box(7.0D, 1.0D, 12.0D, 9.0D, 3.0D, 13.0D), IBooleanFunction.ONLY_FIRST);
    // args: VoxelShapes.or(body, handle, spout)
    VoxelShape shapeN = VoxelShapes.or(bodyX, handleN, Block.box(1.0D, 2.0D, 6.0D, 4.0D, 4.0D, 10.0D));
    VoxelShape shapeS = VoxelShapes.or(bodyX, handleS, Block.box(12.0D, 2.0D, 6.0D, 15.0D, 4.0D, 10.0D));
    VoxelShape shapeW = VoxelShapes.or(bodyZ, handleW, Block.box(6.0D, 2.0D, 12.0D, 10.0D, 4.0D, 15.0D));
    VoxelShape shapeE = VoxelShapes.or(bodyZ, handleE, Block.box(6.0D, 2.0D, 1.0D, 10.0D, 4.0D, 4.0D));
    // use the built shapes to populate the array
    SHAPES = new VoxelShape[] { shapeS, shapeW, shapeN, shapeE };
  }
  
  public OilLampBlock(final Block.Properties properties) {
    super(properties);
    this.registerDefaultState(this.getStateDefinition().any()
        .setValue(LIT, true).setValue(WATERLOGGED, false).setValue(FACING, Direction.NORTH));
  }
  
  @Override
  protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
    builder.add(FACING, WATERLOGGED, LIT);
  }

  @Override
  public BlockState getStateForPlacement(BlockItemUseContext context) {
    FluidState fluid = context.getLevel().getFluidState(context.getClickedPos());
    boolean waterlogged = fluid.is(FluidTags.WATER);
    return this.defaultBlockState().setValue(LIT, !waterlogged).setValue(WATERLOGGED, waterlogged).setValue(FACING, context.getHorizontalDirection());
  }
  
  @Override
  public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn,
      BlockPos currentPos, BlockPos facingPos) {
    if (stateIn.getValue(WATERLOGGED)) {
      worldIn.getLiquidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(worldIn));
      if(stateIn.getValue(LIT)) {
        worldIn.setBlock(currentPos, stateIn.setValue(LIT, false), 2);
      }
    }
    return stateIn;
  }

  @Override
  public ActionResultType use(final BlockState state, final World worldIn, final BlockPos pos,
      final PlayerEntity playerIn, final Hand handIn, final BlockRayTraceResult hit) {
    ItemStack heldItem = playerIn.getItemInHand(handIn);
    if(heldItem.isEmpty() && state.getValue(LIT)) {
      // extinguish the block
      worldIn.setBlock(pos, state.setValue(LIT, false), 2);
      // play sound effect
      playerIn.playSound(SoundEvents.FIRE_EXTINGUISH, 0.4F, 1.0F);
    } else if(heldItem.getItem() == Items.FLINT_AND_STEEL && !state.getValue(LIT) && !state.getValue(WATERLOGGED)) {
      // light the block
      worldIn.setBlock(pos, state.setValue(LIT, true), 2);
      // play sound effect and damage item
      playerIn.playSound(SoundEvents.FLINTANDSTEEL_USE, 0.4F, 1.0F);
      heldItem.hurtAndBreak(1, playerIn, i -> i.broadcastBreakEvent(EquipmentSlotType.MAINHAND));
    }
    return ActionResultType.SUCCESS;
  }
  
  @Override
  public FluidState getFluidState(BlockState state) {
    return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : Fluids.EMPTY.defaultFluidState();
  }

  @Override
  public VoxelShape getShape(final BlockState state, final IBlockReader worldIn, final BlockPos pos, final ISelectionContext cxt) {
    int horizIndex = state.getValue(FACING).get2DDataValue();
    return horizIndex < 0 ? VoxelShapes.block() : SHAPES[horizIndex] ;
  }
  
  @OnlyIn(Dist.CLIENT)
  public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
    if(stateIn.getValue(LIT).booleanValue()) {
      Direction d = stateIn.getValue(FACING).getCounterClockWise();
      double addX = 0.32D * d.getStepX();
      double addY = 0.40D;
      double addZ = 0.32D * d.getStepZ();
      Vector3d vec = Vector3d.atBottomCenterOf(pos).add(addX, addY, addZ);
      worldIn.addParticle(ParticleTypes.SMOKE, vec.x(), vec.y(), vec.z(), 0.0D, 0.0D, 0.0D);
      worldIn.addParticle(ParticleTypes.FLAME, vec.x(), vec.y(), vec.z(), 0.0D, 0.0D, 0.0D);
    }
  }
  
  // Comparator methods

  @Override
  public boolean hasAnalogOutputSignal(BlockState state) {
    return true;
  }

  @Override
  public int getAnalogOutputSignal(BlockState state, World worldIn, BlockPos pos) {
    return state.getValue(LIT) ? 15 : 0;
  }
}
