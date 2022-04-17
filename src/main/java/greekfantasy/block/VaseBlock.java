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
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.tags.FluidTags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class VaseBlock extends HorizontalBlock implements IWaterLoggable {
  
  public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
  
  protected static final VoxelShape BODY = VoxelShapes.or(
    Block.box(4.0D, 0.0D, 4.0D, 12.0D, 8.0D, 12.0D),
    Block.box(6.0D, 8.0D, 6.0D, 10.0D, 10.0D, 10.0D),
    VoxelShapes.joinUnoptimized(
        Block.box(5.0D, 10.0D, 5.0D, 11.0D, 11.0D, 11.0D),
        Block.box(6.0D, 10.0D, 6.0D, 10.0D, 11.0D, 10.0D), IBooleanFunction.ONLY_FIRST));
  
  protected static final VoxelShape HANDLE_Z = VoxelShapes.or(
    VoxelShapes.joinUnoptimized(
        Block.box(12.0D, 1.0D, 7.0D, 14.0D, 7.0D, 9.0D),
        Block.box(12.0D, 2.0D, 7.0D, 13.0D, 6.0D, 9.0D),
        IBooleanFunction.ONLY_FIRST),
    VoxelShapes.joinUnoptimized(
        Block.box(2.0D, 1.0D, 7.0D, 4.0D, 7.0D, 9.0D),
        Block.box(3.0D, 2.0D, 7.0D, 4.0D, 6.0D, 9.0D),
        IBooleanFunction.ONLY_FIRST));
  
  protected static final VoxelShape HANDLE_X = VoxelShapes.or(
    VoxelShapes.joinUnoptimized(
        Block.box(7.0D, 1.0D, 2.0D, 9.0D, 7.0D, 4.0D),
        Block.box(7.0D, 2.0D, 3.0D, 9.0D, 6.0D, 4.0D),
        IBooleanFunction.ONLY_FIRST),
    VoxelShapes.joinUnoptimized(
        Block.box(7.0D, 1.0D, 12.0D, 9.0D, 7.0D, 14.0D),
        Block.box(7.0D, 2.0D, 12.0D, 9.0D, 6.0D, 13.0D),
        IBooleanFunction.ONLY_FIRST));
  
  protected static final VoxelShape SHAPE_X = VoxelShapes.joinUnoptimized(BODY, HANDLE_X, IBooleanFunction.OR);
  protected static final VoxelShape SHAPE_Z = VoxelShapes.joinUnoptimized(BODY, HANDLE_Z, IBooleanFunction.OR);
  
  public VaseBlock(final Block.Properties properties) {
    super(properties);
    this.registerDefaultState(this.getStateDefinition().any()
        .setValue(WATERLOGGED, false).setValue(FACING, Direction.NORTH));
  }
  
  @Override
  protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
    builder.add(FACING, WATERLOGGED);
  }

  @Override
  public BlockState getStateForPlacement(BlockItemUseContext context) {
    FluidState fluid = context.getLevel().getFluidState(context.getClickedPos());
    return this.defaultBlockState().setValue(WATERLOGGED, fluid.is(FluidTags.WATER)).setValue(FACING, context.getHorizontalDirection());
  }
  
  @Override
  public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn,
      BlockPos currentPos, BlockPos facingPos) {
    if (stateIn.getValue(WATERLOGGED)) {
      worldIn.getLiquidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(worldIn));
    }
    return stateIn;
  }

  @Override
  public ActionResultType use(final BlockState state, final World worldIn, final BlockPos pos,
      final PlayerEntity playerIn, final Hand handIn, final BlockRayTraceResult hit) {
    final TileEntity te = worldIn.getBlockEntity(pos);
    // item / inventory interaction
    if(playerIn.isEffectiveAi() && te instanceof VaseTileEntity) {
      final VaseTileEntity teVase = (VaseTileEntity)te;
      final ItemStack teStack = teVase.getItem(0);
      final ItemStack heldItem = playerIn.getItemInHand(handIn);
      if(teStack.isEmpty()) {
        // attempt to add item to inventory
        teVase.setItem(0, heldItem.copy());
        // remove from player
        playerIn.setItemInHand(handIn, ItemStack.EMPTY);
      } else if(playerIn.isShiftKeyDown() || heldItem.isEmpty()) {
        // attempt to drop item from inventory
        ItemEntity dropItem = new ItemEntity(worldIn, playerIn.getX(), playerIn.getY(), playerIn.getZ(), teVase.removeItemNoUpdate(0));
        dropItem.setPickUpDelay(0);
        worldIn.addFreshEntity(dropItem);
      }
      return ActionResultType.CONSUME;
    }
    return ActionResultType.SUCCESS;
  }
  
  @Override
  public void playerWillDestroy(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
    // drop items from inventory
    TileEntity tileentity = worldIn.getBlockEntity(pos);
    if (!worldIn.isClientSide() && tileentity instanceof VaseTileEntity) {
      InventoryHelper.dropContents(worldIn, pos, ((VaseTileEntity) tileentity).getInventory());
    }
  }
  
  @Override
  public void onRemove(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
    if (!state.is(newState.getBlock())) {
      // drop items from inventory
      TileEntity tileentity = worldIn.getBlockEntity(pos);
      if (!worldIn.isClientSide() && tileentity instanceof VaseTileEntity) {
        InventoryHelper.dropContents(worldIn, pos, ((VaseTileEntity) tileentity).getInventory());
      }
    }
  }
  
  @Override
  public FluidState getFluidState(BlockState state) {
    return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : Fluids.EMPTY.defaultFluidState();
  }

  @Override
  public VoxelShape getShape(final BlockState state, final IBlockReader worldIn, final BlockPos pos, final ISelectionContext cxt) {
    return (state.getValue(FACING).getAxis() == Axis.X) ? SHAPE_X : SHAPE_Z;
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
  
  // Comparator methods

  @Override
  public boolean hasAnalogOutputSignal(BlockState state) {
    return true;
  }

  @Override
  public int getAnalogOutputSignal(BlockState state, World worldIn, BlockPos pos) {
    final TileEntity te = worldIn.getBlockEntity(pos);
    if(te instanceof IInventory) {
      return Container.getRedstoneSignalFromContainer((IInventory)te);
    }
    return 0;
  }
}
