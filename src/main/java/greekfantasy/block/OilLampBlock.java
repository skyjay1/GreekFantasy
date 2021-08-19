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
    VoxelShape bodyX = Block.makeCuboidShape(4.0D, 0.0D, 4.0D, 12.0D, 4.0D, 12.0D);
    VoxelShape bodyZ = Block.makeCuboidShape(4.0D, 0.0D, 4.0D, 12.0D, 4.0D, 12.0D);
    VoxelShape handleN = VoxelShapes.combine(
        Block.makeCuboidShape(12.0D, 0.0D, 7.0D, 14.0D, 4.0D, 9.0D),
        Block.makeCuboidShape(12.0D, 1.0D, 7.0D, 13.0D, 3.0D, 9.0D), IBooleanFunction.ONLY_FIRST);
    VoxelShape handleS = VoxelShapes.combine(
        Block.makeCuboidShape(2.0D, 0.0D, 7.0D, 4.0D, 4.0D, 9.0D),
        Block.makeCuboidShape(3.0D, 1.0D, 7.0D, 4.0D, 3.0D, 9.0D), IBooleanFunction.ONLY_FIRST);
    VoxelShape handleW = VoxelShapes.combine(
        Block.makeCuboidShape(7.0D, 0.0D, 2.0D, 9.0D, 4.0D, 4.0D),
        Block.makeCuboidShape(7.0D, 1.0D, 3.0D, 9.0D, 3.0D, 4.0D), IBooleanFunction.ONLY_FIRST);
    VoxelShape handleE = VoxelShapes.combine(
        Block.makeCuboidShape(7.0D, 0.0D, 12.0D, 9.0D, 4.0D, 14.0D),
        Block.makeCuboidShape(7.0D, 1.0D, 12.0D, 9.0D, 3.0D, 13.0D), IBooleanFunction.ONLY_FIRST);
    // args: VoxelShapes.or(body, handle, spout)
    VoxelShape shapeN = VoxelShapes.or(bodyX, handleN, Block.makeCuboidShape(1.0D, 2.0D, 6.0D, 4.0D, 4.0D, 10.0D));
    VoxelShape shapeS = VoxelShapes.or(bodyX, handleS, Block.makeCuboidShape(12.0D, 2.0D, 6.0D, 15.0D, 4.0D, 10.0D));
    VoxelShape shapeW = VoxelShapes.or(bodyZ, handleW, Block.makeCuboidShape(6.0D, 2.0D, 12.0D, 10.0D, 4.0D, 15.0D));
    VoxelShape shapeE = VoxelShapes.or(bodyZ, handleE, Block.makeCuboidShape(6.0D, 2.0D, 1.0D, 10.0D, 4.0D, 4.0D));
    // use the built shapes to populate the array
    SHAPES = new VoxelShape[] { shapeS, shapeW, shapeN, shapeE };
  }
  
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
      // extinguish the block
      worldIn.setBlockState(pos, state.with(LIT, false), 2);
      // play sound effect
      playerIn.playSound(SoundEvents.BLOCK_FIRE_EXTINGUISH, 0.4F, 1.0F);
    } else if(heldItem.getItem() == Items.FLINT_AND_STEEL && !state.get(LIT) && !state.get(WATERLOGGED)) {
      // light the block
      worldIn.setBlockState(pos, state.with(LIT, true), 2);
      // play sound effect and damage item
      playerIn.playSound(SoundEvents.ITEM_FLINTANDSTEEL_USE, 0.4F, 1.0F);
      heldItem.damageItem(1, playerIn, i -> i.sendBreakAnimation(EquipmentSlotType.MAINHAND));
    }
    return ActionResultType.SUCCESS;
  }
  
  @Override
  public FluidState getFluidState(BlockState state) {
    return state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : Fluids.EMPTY.getDefaultState();
  }

  @Override
  public VoxelShape getShape(final BlockState state, final IBlockReader worldIn, final BlockPos pos, final ISelectionContext cxt) {
    int horizIndex = state.get(HORIZONTAL_FACING).getHorizontalIndex();
    return horizIndex < 0 ? VoxelShapes.fullCube() : SHAPES[horizIndex] ;
  }
  
  @OnlyIn(Dist.CLIENT)
  public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
    if(stateIn.get(LIT).booleanValue()) {
      Direction d = stateIn.get(HORIZONTAL_FACING).rotateYCCW();
      double addX = 0.32D * d.getXOffset();
      double addY = 0.40D;
      double addZ = 0.32D * d.getZOffset();
      Vector3d vec = Vector3d.copyCenteredHorizontally(pos).add(addX, addY, addZ);
      worldIn.addParticle(ParticleTypes.SMOKE, vec.getX(), vec.getY(), vec.getZ(), 0.0D, 0.0D, 0.0D);
      worldIn.addParticle(ParticleTypes.FLAME, vec.getX(), vec.getY(), vec.getZ(), 0.0D, 0.0D, 0.0D);
    }
  }
  
  // Comparator methods

  @Override
  public boolean hasComparatorInputOverride(BlockState state) {
    return true;
  }

  @Override
  public int getComparatorInputOverride(BlockState state, World worldIn, BlockPos pos) {
    return state.get(LIT) ? 15 : 0;
  }
}