package greekfantasy.block;

import greekfantasy.util.MysteriousBoxManager;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Random;

public class MysteriousBoxBlock extends HorizontalBlock implements IWaterLoggable {

    public static final BooleanProperty OPEN = BooleanProperty.create("open");
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    protected static final VoxelShape SHAPE_CLOSED_Z = VoxelShapes.joinUnoptimized(
            VoxelShapes.joinUnoptimized(
                    Block.box(1.0D, 0.0D, 3.0D, 15.0D, 8.0D, 13.0D),
                    Block.box(3.0D, 0.0D, 3.0D, 13.0D, 2.0D, 13.0D),
                    IBooleanFunction.ONLY_FIRST),
            Block.box(1.0D, 0.0D, 5.0D, 15.0D, 2.0D, 11.0D),
            IBooleanFunction.ONLY_FIRST
    );
    protected static final VoxelShape SHAPE_OPEN_Z = VoxelShapes.joinUnoptimized(SHAPE_CLOSED_Z, Block.box(2.0D, 3.0D, 4.0D, 14.0D, 8.0D, 12.0D), IBooleanFunction.ONLY_FIRST);

    protected static final VoxelShape SHAPE_CLOSED_X = VoxelShapes.joinUnoptimized(
            VoxelShapes.joinUnoptimized(
                    Block.box(3.0D, 0.0D, 1.0D, 13.0D, 8.0D, 15.0D),
                    Block.box(5.0D, 0.0D, 1.0D, 11.0D, 2.0D, 15.0D),
                    IBooleanFunction.ONLY_FIRST),
            Block.box(1.0D, 0.0D, 3.0D, 15.0D, 2.0D, 13.0D),
            IBooleanFunction.ONLY_FIRST
    );
    protected static final VoxelShape SHAPE_OPEN_X = VoxelShapes.joinUnoptimized(SHAPE_CLOSED_X, Block.box(4.0D, 3.0D, 2.0D, 12.0D, 8.0D, 14.0D), IBooleanFunction.ONLY_FIRST);

    public MysteriousBoxBlock(final Block.Properties properties) {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any()
                .setValue(FACING, Direction.NORTH)
                .setValue(OPEN, Boolean.valueOf(false))
                .setValue(WATERLOGGED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING).add(OPEN).add(WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        FluidState fluid = context.getLevel().getFluidState(context.getClickedPos());
        return super.getStateForPlacement(context).setValue(WATERLOGGED, fluid.is(FluidTags.WATER)).setValue(FACING, context.getHorizontalDirection().getOpposite());
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
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : Fluids.EMPTY.defaultFluidState();
    }

    @Override
    public ActionResultType use(final BlockState state, final World worldIn, final BlockPos pos,
                                final PlayerEntity playerIn, final Hand handIn, final BlockRayTraceResult hit) {
        if (!state.getValue(OPEN).booleanValue()) {
            addSmokeParticles(worldIn, pos, worldIn.random, 32);
            if (playerIn.isEffectiveAi()) {
                // open the box
                final boolean open = MysteriousBoxManager.onBoxOpened(worldIn, playerIn, state, pos);
                if (open) {
                    worldIn.setBlock(pos, state.setValue(OPEN, Boolean.valueOf(true)), 2);
                }
            }
            return ActionResultType.CONSUME;
        }

        return ActionResultType.SUCCESS;
    }

    @Override
    public VoxelShape getShape(final BlockState state, final IBlockReader worldIn, final BlockPos pos, final ISelectionContext cxt) {
        final boolean axisX = state.getValue(FACING).getAxis() == Direction.Axis.X;
        final boolean open = state.getValue(OPEN).booleanValue();
        if (axisX && open) return SHAPE_OPEN_X;
        else if (axisX && !open) return SHAPE_CLOSED_X;
        else if (!axisX && open) return SHAPE_OPEN_Z;
        else return SHAPE_CLOSED_Z;
    }

    // Comparator methods

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, World worldIn, BlockPos pos) {
        return state.getValue(OPEN) ? 1 : 0;
    }

    /**
     * Called periodically clientside on blocks near the player to show effects (like furnace fire particles). Note that
     * this method is unrelated to randomTick and #needsRandomTick, and will always be called regardless
     * of whether the block can receive random update ticks
     */
    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState stateIn, World world, BlockPos pos, Random rand) {
        if (stateIn.getValue(OPEN).booleanValue()) {
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
                    x + (world.random.nextDouble() - 0.5D) * radius, y, z + (world.random.nextDouble() - 0.5D) * radius,
                    (world.random.nextDouble() - 0.5D) * motion,
                    (world.random.nextDouble() - 0.5D) * motion * 0.5D,
                    (world.random.nextDouble() - 0.5D) * motion);
        }
    }
}
