package greekfantasy.block;

import greekfantasy.util.MysteriousBoxManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
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
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MysteriousBoxBlock extends DirectionalBlock implements SimpleWaterloggedBlock {

    public static final BooleanProperty OPEN = BooleanProperty.create("open");
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    protected static final VoxelShape SHAPE_CLOSED_Z = Shapes.joinUnoptimized(
            Shapes.joinUnoptimized(
                    Block.box(1.0D, 0.0D, 3.0D, 15.0D, 8.0D, 13.0D),
                    Block.box(3.0D, 0.0D, 3.0D, 13.0D, 2.0D, 13.0D),
                    BooleanOp.ONLY_FIRST),
            Block.box(1.0D, 0.0D, 5.0D, 15.0D, 2.0D, 11.0D),
            BooleanOp.ONLY_FIRST
    );
    protected static final VoxelShape SHAPE_OPEN_Z = Shapes.joinUnoptimized(SHAPE_CLOSED_Z, Block.box(2.0D, 3.0D, 4.0D, 14.0D, 8.0D, 12.0D), BooleanOp.ONLY_FIRST);

    protected static final VoxelShape SHAPE_CLOSED_X = Shapes.joinUnoptimized(
            Shapes.joinUnoptimized(
                    Block.box(3.0D, 0.0D, 1.0D, 13.0D, 8.0D, 15.0D),
                    Block.box(5.0D, 0.0D, 1.0D, 11.0D, 2.0D, 15.0D),
                    BooleanOp.ONLY_FIRST),
            Block.box(1.0D, 0.0D, 3.0D, 15.0D, 2.0D, 13.0D),
            BooleanOp.ONLY_FIRST
    );
    protected static final VoxelShape SHAPE_OPEN_X = Shapes.joinUnoptimized(SHAPE_CLOSED_X, Block.box(4.0D, 3.0D, 2.0D, 12.0D, 8.0D, 14.0D), BooleanOp.ONLY_FIRST);

    protected static final Map<BlockState, VoxelShape> SHAPES = new HashMap<>();

    public MysteriousBoxBlock(final Block.Properties properties) {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any()
                .setValue(FACING, Direction.NORTH)
                .setValue(OPEN, Boolean.valueOf(false))
                .setValue(WATERLOGGED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING).add(OPEN).add(WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState fluid = context.getLevel().getFluidState(context.getClickedPos());
        return super.getStateForPlacement(context).setValue(WATERLOGGED, fluid.is(FluidTags.WATER)).setValue(FACING, context.getHorizontalDirection().getOpposite());
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
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : Fluids.EMPTY.defaultFluidState();
    }

    @Override
    public InteractionResult use(final BlockState state, final Level level, final BlockPos pos,
                                 final Player player, final InteractionHand hand, final BlockHitResult hit) {
        if (!state.getValue(OPEN).booleanValue()) {
            addSmokeParticles(level, pos, level.random, 32);
            level.playSound(player, pos, SoundEvents.BARREL_OPEN, SoundSource.BLOCKS,
                    0.8F + level.getRandom().nextFloat() * 0.4F,
                    0.8F + level.getRandom().nextFloat() * 0.4F);
            if (!player.level.isClientSide()) {
                // open the box
                final boolean open = MysteriousBoxManager.onBoxOpened(level, player, state, pos);
                if (open) {
                    level.setBlock(pos, state.setValue(OPEN, Boolean.valueOf(true)), 2);
                }
            }
            return InteractionResult.CONSUME;
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public VoxelShape getShape(final BlockState blockState, final BlockGetter level, final BlockPos pos, final CollisionContext context) {
        return SHAPES.computeIfAbsent(blockState, state -> computeShape(state, level, pos, context));
    }

    private static VoxelShape computeShape(final BlockState state, final BlockGetter level, final BlockPos pos, final CollisionContext context) {
        final boolean axisX = state.getValue(FACING).getAxis() == Direction.Axis.X;
        final boolean open = state.getValue(OPEN).booleanValue();
        if (axisX && open) {
            return SHAPE_OPEN_X;
        }
        if (axisX) { // and !open
            return SHAPE_CLOSED_X;
        }
        if (open) { // and !axisX
            return SHAPE_OPEN_Z;
        }
        return SHAPE_CLOSED_Z;
    }

    // Comparator methods

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        return state.getValue(OPEN) ? 1 : 0;
    }

    @Override
    public void animateTick(BlockState stateIn, Level level, BlockPos pos, Random rand) {
        if (stateIn.getValue(OPEN).booleanValue()) {
            addSmokeParticles(level, pos, rand, 3);
        }
    }

    private void addSmokeParticles(final Level world, final BlockPos pos, final Random rand, final int count) {
        final double x = pos.getX() + 0.5D;
        final double y = pos.getY() + 0.22D;
        final double z = pos.getZ() + 0.5D;
        final double motion = 0.08D;
        final double radius = 0.25D;
        for (int i = 0; i < count; i++) {
            world.addParticle(ParticleTypes.SMOKE,
                    x + (world.random.nextDouble() - 0.5D) * radius, y, z + (world.random.nextDouble() - 0.5D) * radius,
                    (world.random.nextDouble() - 0.5D) * motion,
                    (world.random.nextDouble() - 0.5D) * motion * 0.5D,
                    (world.random.nextDouble() - 0.5D) * motion);
        }
    }
}
