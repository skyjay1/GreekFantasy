package greekfantasy.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Random;

public class OilLampBlock extends DirectionalBlock implements SimpleWaterloggedBlock {

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final BooleanProperty LIT = BlockStateProperties.LIT;

    protected static final VoxelShape[] SHAPES;

    static {
        VoxelShape bodyX = Block.box(4.0D, 0.0D, 4.0D, 12.0D, 4.0D, 12.0D);
        VoxelShape bodyZ = Block.box(4.0D, 0.0D, 4.0D, 12.0D, 4.0D, 12.0D);
        VoxelShape handleN = Shapes.joinUnoptimized(
                Block.box(12.0D, 0.0D, 7.0D, 14.0D, 4.0D, 9.0D),
                Block.box(12.0D, 1.0D, 7.0D, 13.0D, 3.0D, 9.0D), BooleanOp.ONLY_FIRST);
        VoxelShape handleS = Shapes.joinUnoptimized(
                Block.box(2.0D, 0.0D, 7.0D, 4.0D, 4.0D, 9.0D),
                Block.box(3.0D, 1.0D, 7.0D, 4.0D, 3.0D, 9.0D), BooleanOp.ONLY_FIRST);
        VoxelShape handleW = Shapes.joinUnoptimized(
                Block.box(7.0D, 0.0D, 2.0D, 9.0D, 4.0D, 4.0D),
                Block.box(7.0D, 1.0D, 3.0D, 9.0D, 3.0D, 4.0D), BooleanOp.ONLY_FIRST);
        VoxelShape handleE = Shapes.joinUnoptimized(
                Block.box(7.0D, 0.0D, 12.0D, 9.0D, 4.0D, 14.0D),
                Block.box(7.0D, 1.0D, 12.0D, 9.0D, 3.0D, 13.0D), BooleanOp.ONLY_FIRST);
        // args: VoxelShapes.or(body, handle, spout)
        VoxelShape shapeN = Shapes.or(bodyX, handleN, Block.box(1.0D, 2.0D, 6.0D, 4.0D, 4.0D, 10.0D));
        VoxelShape shapeS = Shapes.or(bodyX, handleS, Block.box(12.0D, 2.0D, 6.0D, 15.0D, 4.0D, 10.0D));
        VoxelShape shapeW = Shapes.or(bodyZ, handleW, Block.box(6.0D, 2.0D, 12.0D, 10.0D, 4.0D, 15.0D));
        VoxelShape shapeE = Shapes.or(bodyZ, handleE, Block.box(6.0D, 2.0D, 1.0D, 10.0D, 4.0D, 4.0D));
        // use the built shapes to populate the array
        SHAPES = new VoxelShape[]{shapeS, shapeW, shapeN, shapeE};
    }

    public OilLampBlock(final Block.Properties properties) {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any()
                .setValue(LIT, true).setValue(WATERLOGGED, false).setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, WATERLOGGED, LIT);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState fluid = context.getLevel().getFluidState(context.getClickedPos());
        boolean waterlogged = fluid.is(FluidTags.WATER);
        return this.defaultBlockState().setValue(LIT, !waterlogged).setValue(WATERLOGGED, waterlogged).setValue(FACING, context.getHorizontalDirection());
    }

    @Override
    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor worldIn,
                                  BlockPos currentPos, BlockPos facingPos) {
        if (stateIn.getValue(WATERLOGGED)) {
            worldIn.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(worldIn));
            if (stateIn.getValue(LIT)) {
                worldIn.setBlock(currentPos, stateIn.setValue(LIT, false), 2);
            }
        }
        return stateIn;
    }

    @Override
    public InteractionResult use(final BlockState state, final Level worldIn, final BlockPos pos,
                                 final Player playerIn, final InteractionHand handIn, final BlockHitResult hit) {
        ItemStack heldItem = playerIn.getItemInHand(handIn);
        if (heldItem.isEmpty() && state.getValue(LIT)) {
            // extinguish the block
            worldIn.setBlock(pos, state.setValue(LIT, false), 2);
            // play sound effect
            playerIn.playSound(SoundEvents.FIRE_EXTINGUISH, 0.4F, 1.0F);
        } else if (heldItem.getItem() == Items.FLINT_AND_STEEL && !state.getValue(LIT) && !state.getValue(WATERLOGGED)) {
            // light the block
            worldIn.setBlock(pos, state.setValue(LIT, true), 2);
            // play sound effect and damage item
            playerIn.playSound(SoundEvents.FLINTANDSTEEL_USE, 0.4F, 1.0F);
            heldItem.hurtAndBreak(1, playerIn, i -> i.broadcastBreakEvent(EquipmentSlot.MAINHAND));
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : Fluids.EMPTY.defaultFluidState();
    }

    @Override
    public VoxelShape getShape(final BlockState state, final BlockGetter worldIn, final BlockPos pos, final CollisionContext cxt) {
        int horizIndex = state.getValue(FACING).get2DDataValue();
        return horizIndex < 0 ? Shapes.block() : SHAPES[horizIndex];
    }

    @Override
    public void animateTick(BlockState stateIn, Level worldIn, BlockPos pos, Random rand) {
        if (stateIn.getValue(LIT).booleanValue()) {
            Direction d = stateIn.getValue(FACING).getCounterClockWise();
            double addX = 0.32D * d.getStepX();
            double addY = 0.40D;
            double addZ = 0.32D * d.getStepZ();
            Vec3 vec = Vec3.atBottomCenterOf(pos).add(addX, addY, addZ);
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
    public int getAnalogOutputSignal(BlockState state, Level worldIn, BlockPos pos) {
        return state.getValue(LIT) ? 15 : 0;
    }
}
