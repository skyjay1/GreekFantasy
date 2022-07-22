package greekfantasy.block;

import greekfantasy.GFRegistry;
import greekfantasy.blockentity.MobHeadBlockEntity;
import greekfantasy.util.SummonBossUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
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
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public abstract class MobHeadBlock extends HorizontalDirectionalBlock implements EntityBlock, SimpleWaterloggedBlock {

    public static final BooleanProperty WALL = BooleanProperty.create("wall");
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    private static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 15.9D, 16.0D);

    public MobHeadBlock(Properties prop) {
        super(prop);
        this.registerDefaultState(this.getStateDefinition().any()
                .setValue(WALL, Boolean.valueOf(false))
                .setValue(FACING, Direction.NORTH)
                .setValue(WATERLOGGED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(WALL).add(FACING).add(WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        final Direction facing = context.getHorizontalDirection().getOpposite();
        final boolean wall = context.getClickedFace() != Direction.UP && context.getClickedFace() != Direction.DOWN;
        FluidState fluid = context.getLevel().getFluidState(context.getClickedPos());
        return this.defaultBlockState().setValue(WATERLOGGED, fluid.is(FluidTags.WATER)).setValue(WALL, wall).setValue(FACING, facing);
    }

    @Override
    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor level,
                                  BlockPos currentPos, BlockPos facingPos) {
        if (stateIn.getValue(WATERLOGGED)) {
            level.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        return stateIn;
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : Fluids.EMPTY.defaultFluidState();
    }

    @Override
    public VoxelShape getShape(final BlockState state, final BlockGetter worldIn, final BlockPos pos, final CollisionContext cxt) {
        return SHAPE;
    }


    public static class CerberusHeadBlock extends MobHeadBlock {

        public CerberusHeadBlock(Properties prop) {
            super(prop);
        }

        @Override
        public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
            final MobHeadBlockEntity blockEntity = GFRegistry.BlockEntityReg.CERBERUS_HEAD.get().create(pos, state);
            blockEntity.setWall(state.getValue(MobHeadBlock.WALL));
            return blockEntity;
        }
    }

    public static class GiganteHeadBlock extends MobHeadBlock {

        public GiganteHeadBlock(Properties prop) {
            super(prop);
        }

        @Override
        public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
            final MobHeadBlockEntity blockEntity = GFRegistry.BlockEntityReg.GIGANTE_HEAD.get().create(pos, state);
            blockEntity.setWall(state.getValue(MobHeadBlock.WALL));
            return blockEntity;
        }
    }

    public static class OrthusHeadBlock extends MobHeadBlock {

        public OrthusHeadBlock(Properties prop) {
            super(prop);
        }

        @Override
        public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
            final MobHeadBlockEntity blockEntity = GFRegistry.BlockEntityReg.ORTHUS_HEAD.get().create(pos, state);
            blockEntity.setWall(state.getValue(MobHeadBlock.WALL));
            return blockEntity;
        }
    }
}
