package greekfantasy.block;

import greekfantasy.GFRegistry;
import greekfantasy.tileentity.VaseTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.FluidTags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class VaseBlock extends HorizontalBlock implements IWaterLoggable {
  
  protected static final VoxelShape AABB = Block.makeCuboidShape(4.0D, 0.0D, 4.0D, 12.0D, 15.0D, 12.0D);
  public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

  public VaseBlock(final Block.Properties properties) {
    super(properties);
    this.setDefaultState(this.getStateContainer().getBaseState()
        .with(WATERLOGGED, false).with(HORIZONTAL_FACING, Direction.NORTH));
  }
  
  @Override
  protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
    builder.add(HORIZONTAL_FACING, WATERLOGGED);
  }

  @Override
  public BlockState getStateForPlacement(BlockItemUseContext context) {
    FluidState fluid = context.getWorld().getFluidState(context.getPos());
    return this.getDefaultState().with(WATERLOGGED, fluid.isTagged(FluidTags.WATER)).with(HORIZONTAL_FACING, context.getPlacementHorizontalFacing());
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
  public ActionResultType onBlockActivated(final BlockState state, final World worldIn, final BlockPos pos,
      final PlayerEntity playerIn, final Hand handIn, final BlockRayTraceResult hit) {
    final TileEntity te = worldIn.getTileEntity(pos);
    // item / inventory interaction
    if(playerIn.isServerWorld() && te instanceof VaseTileEntity) {
      final VaseTileEntity teVase = (VaseTileEntity)te;
      final ItemStack teStack = teVase.getStackInSlot(0);
      final ItemStack heldItem = playerIn.getHeldItem(handIn);
      if(teStack.isEmpty()) {
        // attempt to add item to inventory
        teVase.setInventorySlotContents(0, heldItem.copy());
        if(!playerIn.isCreative()) {
          // remove from player
          playerIn.setHeldItem(handIn, ItemStack.EMPTY);
        }
      } else if(playerIn.isSneaking() || heldItem.isEmpty()) {
        // attempt to drop item from inventory
        ItemEntity dropItem = new ItemEntity(worldIn, playerIn.getPosX(), playerIn.getPosY(), playerIn.getPosZ(), teVase.removeStackFromSlot(0));
        dropItem.setPickupDelay(0);
        worldIn.addEntity(dropItem);
      }
      return ActionResultType.CONSUME;
    }
    return ActionResultType.SUCCESS;
  }
  
  @Override
  public void onBlockHarvested(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
    // drop items from inventory
    TileEntity tileentity = worldIn.getTileEntity(pos);
    if (!worldIn.isRemote() && tileentity instanceof VaseTileEntity) {
      InventoryHelper.dropItems(worldIn, pos, ((VaseTileEntity) tileentity).getInventory());
    }
  }
  
  @Override
  public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
    if (!state.isIn(newState.getBlock())) {
      // drop items from inventory
      TileEntity tileentity = worldIn.getTileEntity(pos);
      if (!worldIn.isRemote() && tileentity instanceof VaseTileEntity) {
        InventoryHelper.dropItems(worldIn, pos, ((VaseTileEntity) tileentity).getInventory());
      }
    }
  }
  
  @Override
  public FluidState getFluidState(BlockState state) {
    return state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : Fluids.EMPTY.getDefaultState();
  }

  @Override
  public VoxelShape getShape(final BlockState state, final IBlockReader worldIn, final BlockPos pos, final ISelectionContext cxt) {
    return AABB;
  }
  
  @Override
  public boolean hasTileEntity(final BlockState state) {
    return true;
  }
  
  @Override
  public TileEntity createTileEntity(final BlockState state, final IBlockReader world) {
    final VaseTileEntity te = GFRegistry.VASE_TE.create();
    return te;
  }
}
