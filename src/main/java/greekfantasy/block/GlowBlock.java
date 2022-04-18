package greekfantasy.block;

import greekfantasy.entity.misc.PalladiumEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class GlowBlock extends Block implements IWaterLoggable {

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public GlowBlock(final Properties prop) {
        super(prop);
        this.registerDefaultState(this.getStateDefinition().any().setValue(BlockStateProperties.WATERLOGGED, false));
    }

    protected static boolean removeGlowBlock(final World worldIn, final BlockState state, final BlockPos pos, final int flag) {
        // remove this block and replace with air or water
        final BlockState replaceWith = state.getValue(BlockStateProperties.WATERLOGGED) ? Fluids.WATER.defaultFluidState().createLegacyBlock()
                : Blocks.AIR.defaultBlockState();
        // replace with air OR water depending on waterlogged state
        return worldIn.setBlock(pos, replaceWith, flag);
    }

    @Override
    protected void createBlockStateDefinition(final StateContainer.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.WATERLOGGED);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : Fluids.EMPTY.defaultFluidState();
    }

    @Override
    public void onPlace(final BlockState state, final World worldIn, final BlockPos pos, final BlockState oldState, final boolean isMoving) {
        state.setValue(WATERLOGGED, oldState.getFluidState().is(FluidTags.WATER));
        // schedule next tick
        worldIn.getBlockTicks().scheduleTick(pos, this, 4);
        if (state.getValue(BlockStateProperties.WATERLOGGED)) {
            worldIn.getLiquidTicks().scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(worldIn));
        }
    }

    @Override
    public void tick(final BlockState state, final ServerWorld worldIn, final BlockPos pos, final Random rand) {
        super.tick(state, worldIn, pos, rand);
        // schedule next tick
        worldIn.getBlockTicks().scheduleTick(pos, this, 4);
        if (state.getValue(BlockStateProperties.WATERLOGGED)) {
            worldIn.getLiquidTicks().scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(worldIn));
        }
        // check for palladium entity
        AxisAlignedBB aabb = new AxisAlignedBB(pos);
        List<PalladiumEntity> list = worldIn.getEntitiesOfClass(PalladiumEntity.class, aabb);
        // remove this block if no palladium found
        if(list.isEmpty()) {
            removeGlowBlock(worldIn, state, pos, Constants.BlockFlags.DEFAULT);
        }
    }

    @Override
    public boolean isAir(BlockState state, IBlockReader world, BlockPos pos) {
        return true;
    }

    @Override
    public VoxelShape getShape(final BlockState state, final IBlockReader worldIn, final BlockPos pos, final ISelectionContext cxt) {
        return VoxelShapes.empty();
    }

    @Override
    public VoxelShape getCollisionShape(final BlockState state, final IBlockReader worldIn, final BlockPos pos, final ISelectionContext cxt) {
        return VoxelShapes.empty();
    }

    @Override
    public VoxelShape getOcclusionShape(final BlockState state, final IBlockReader worldIn, final BlockPos pos) {
        return VoxelShapes.empty();
    }

    @Override
    public VoxelShape getInteractionShape(final BlockState state, final IBlockReader worldIn, final BlockPos pos) {
        return VoxelShapes.empty();
    }

    @Override
    public ItemStack getCloneItemStack(final IBlockReader worldIn, final BlockPos pos, final BlockState state) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canBeReplaced(final BlockState state, final BlockItemUseContext useContext) {
        return true;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(final BlockItemUseContext context) {
        return defaultBlockState();
    }

    @Override
    public BlockRenderType getRenderShape(final BlockState state) {
        return BlockRenderType.INVISIBLE;
    }

    @Override
    public void fallOn(final World worldIn, final BlockPos pos, final Entity entityIn, final float fallDistance) {
        // do nothing
    }
}
