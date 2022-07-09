package greekfantasy.block;

import greekfantasy.GFRegistry;
import greekfantasy.GreekFantasy;
import greekfantasy.blockentity.VaseBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class VaseBlock extends HorizontalDirectionalBlock implements EntityBlock, SimpleWaterloggedBlock {

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    protected static final VoxelShape BODY = Shapes.or(
            Block.box(4.0D, 0.0D, 4.0D, 12.0D, 8.0D, 12.0D),
            Block.box(6.0D, 8.0D, 6.0D, 10.0D, 10.0D, 10.0D),
            Shapes.joinUnoptimized(
                    Block.box(5.0D, 10.0D, 5.0D, 11.0D, 11.0D, 11.0D),
                    Block.box(6.0D, 10.0D, 6.0D, 10.0D, 11.0D, 10.0D), BooleanOp.ONLY_FIRST));

    protected static final VoxelShape HANDLE_Z = Shapes.or(
            Shapes.joinUnoptimized(
                    Block.box(12.0D, 1.0D, 7.0D, 14.0D, 7.0D, 9.0D),
                    Block.box(12.0D, 2.0D, 7.0D, 13.0D, 6.0D, 9.0D),
                    BooleanOp.ONLY_FIRST),
            Shapes.joinUnoptimized(
                    Block.box(2.0D, 1.0D, 7.0D, 4.0D, 7.0D, 9.0D),
                    Block.box(3.0D, 2.0D, 7.0D, 4.0D, 6.0D, 9.0D),
                    BooleanOp.ONLY_FIRST));

    protected static final VoxelShape HANDLE_X = Shapes.or(
            Shapes.joinUnoptimized(
                    Block.box(7.0D, 1.0D, 2.0D, 9.0D, 7.0D, 4.0D),
                    Block.box(7.0D, 2.0D, 3.0D, 9.0D, 6.0D, 4.0D),
                    BooleanOp.ONLY_FIRST),
            Shapes.joinUnoptimized(
                    Block.box(7.0D, 1.0D, 12.0D, 9.0D, 7.0D, 14.0D),
                    Block.box(7.0D, 2.0D, 12.0D, 9.0D, 6.0D, 13.0D),
                    BooleanOp.ONLY_FIRST));

    protected static final VoxelShape SHAPE_X = Shapes.joinUnoptimized(BODY, HANDLE_X, BooleanOp.OR);
    protected static final VoxelShape SHAPE_Z = Shapes.joinUnoptimized(BODY, HANDLE_Z, BooleanOp.OR);

    public VaseBlock(final Block.Properties properties) {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any()
                .setValue(WATERLOGGED, false)
                .setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState fluid = context.getLevel().getFluidState(context.getClickedPos());
        return this.defaultBlockState().setValue(WATERLOGGED, fluid.is(FluidTags.WATER)).setValue(FACING, context.getHorizontalDirection());
    }

    @Override
    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor worldIn,
                                  BlockPos currentPos, BlockPos facingPos) {
        if (stateIn.getValue(WATERLOGGED)) {
            worldIn.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(worldIn));
        }
        return stateIn;
    }

    @Override
    public InteractionResult use(final BlockState state, final Level worldIn, final BlockPos pos,
                                 final Player playerIn, final InteractionHand handIn, final BlockHitResult hit) {
        final BlockEntity blockEntity = worldIn.getBlockEntity(pos);
        // item / inventory interaction
        if (blockEntity instanceof VaseBlockEntity vaseBlockEntity) {
            final ItemStack teStack = vaseBlockEntity.getItem(0);
            final ItemStack heldItem = playerIn.getItemInHand(handIn);
            if (teStack.isEmpty()) {
                // attempt to add item to inventory
                vaseBlockEntity.setItem(0, heldItem.copy());
                // remove from player
                playerIn.setItemInHand(handIn, ItemStack.EMPTY);
            } else if (playerIn.isShiftKeyDown() || heldItem.isEmpty()) {
                // attempt to drop item from inventory
                ItemEntity dropItem = new ItemEntity(worldIn, playerIn.getX(), playerIn.getY(), playerIn.getZ(), vaseBlockEntity.removeItemNoUpdate(0));
                dropItem.setPickUpDelay(0);
                worldIn.addFreshEntity(dropItem);
            }
            return InteractionResult.CONSUME;
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            // drop items from inventory
            BlockEntity tileentity = level.getBlockEntity(pos);
            if (!level.isClientSide() && tileentity instanceof VaseBlockEntity vaseBlockEntity) {
                vaseBlockEntity.dropAllItems();
                level.updateNeighbourForOutputSignal(pos, this);
            }
        }
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : Fluids.EMPTY.defaultFluidState();
    }

    @Override
    public VoxelShape getShape(final BlockState state, final BlockGetter worldIn, final BlockPos pos, final CollisionContext cxt) {
        return (state.getValue(FACING).getAxis() == Direction.Axis.X) ? SHAPE_X : SHAPE_Z;
    }

    @Override
    public BlockEntity newBlockEntity(final BlockPos pos, final BlockState state) {
        final VaseBlockEntity blockEntity = GFRegistry.BlockEntityReg.VASE.get().create(pos, state);
        return blockEntity;
    }

    // Comparator methods

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level worldIn, BlockPos pos) {
        final BlockEntity blockEntity = worldIn.getBlockEntity(pos);
        if (blockEntity instanceof Container container) {
            return AbstractContainerMenu.getRedstoneSignalFromContainer(container);
        }
        return 0;
    }
}
