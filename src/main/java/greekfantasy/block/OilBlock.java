package greekfantasy.block;

import greekfantasy.GFRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.block.PortalSize;
import net.minecraft.entity.Entity;
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
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Optional;
import java.util.Random;

public class OilBlock extends Block implements IWaterLoggable {

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final BooleanProperty LIT = BlockStateProperties.LIT;

    protected final float fireDamage;
    protected static final VoxelShape shape = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D);
    protected static final VoxelShape shapeWaterlogged = Block.box(0.0D, 14.0D, 0.0D, 16.0D, 15.0D, 16.0D);

    public OilBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any()
                .setValue(LIT, false).setValue(WATERLOGGED, false));
        this.fireDamage = 2.5F;
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(LIT, WATERLOGGED);
    }

    @Override
    public ActionResultType use(final BlockState state, final World worldIn, final BlockPos pos,
                                final PlayerEntity playerIn, final Hand handIn, final BlockRayTraceResult hit) {
        ItemStack heldItem = playerIn.getItemInHand(handIn);
        if (!worldIn.isClientSide() && !state.getValue(LIT) && !heldItem.isEmpty()) {
            // use the item to interact with this block
            if (heldItem.getItem() == Items.GLASS_BOTTLE) {
                // remove this block
                worldIn.destroyBlock(pos, false);
                // shrink held item stack
                if (!playerIn.isCreative()) {
                    heldItem.shrink(1);
                }
                // spawn oil bottle
                playerIn.addItem(new ItemStack(GFRegistry.OLIVE_OIL));
                return ActionResultType.CONSUME;
            } else if (heldItem.getItem() == Items.FLINT_AND_STEEL) {
                // replace this block with fire
                setFire(worldIn, state, pos);
                // play sound effect and damage item
                playerIn.playSound(SoundEvents.FLINTANDSTEEL_USE, 0.4F, 1.0F);
                heldItem.hurtAndBreak(1, playerIn, i -> i.broadcastBreakEvent(EquipmentSlotType.MAINHAND));
                return ActionResultType.CONSUME;
            }
        }
        return ActionResultType.PASS;
    }

    @Override
    public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand) {
        // if invalid position, remove this block
        if (!this.canSurvive(state, worldIn, pos)) {
            worldIn.destroyBlock(pos, false);
        }
        // if waterlogged, attempt to "float" up
        if (state.getValue(WATERLOGGED)) {
            if (worldIn.getBlockState(pos.above()).getBlock() == Blocks.WATER) {
                // remove this block and set above block to this state
                worldIn.setBlockAndUpdate(pos.above(), state);
                worldIn.setBlock(pos, state.getFluidState().createLegacyBlock(), 2);
                // schedule another tick to continue moving upward
                worldIn.getBlockTicks().scheduleTick(pos.above(), this, 40);
                return;
            } else if (worldIn.getBlockState(pos.above()).getBlock() == this) {
                // remove this block and "merge" with the one above it
                worldIn.setBlock(pos, state.getFluidState().createLegacyBlock(), 2);
                worldIn.getBlockTicks().scheduleTick(pos.above(), this, 40);
                return;
            }
        }

        // determine if this block is adjacent to fire
        boolean nextToFire = nextToFire(worldIn, pos);
        // if the block is next to fire, either ignite or schedule another update
        if (nextToFire) {
            if (worldIn.getRandom().nextInt(10) == 0) {
                // replace this block with fire
                setFire(worldIn, state, pos);
            } else {
                // schedule another tick to replace self with fire
                worldIn.getBlockTicks().scheduleTick(pos, this, 2 + rand.nextInt(5));
            }
        }
    }

    @Override
    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn,
                                  BlockPos currentPos, BlockPos facingPos) {
        // if invalid position, remove this block
        if (!this.canSurvive(stateIn, worldIn, currentPos)) {
            return stateIn.getFluidState().createLegacyBlock();
        }
        // if a fire block is placed nearby, begin catching fire
        if (isFire(worldIn, facingState, facingPos)) {
            worldIn.getBlockTicks().scheduleTick(currentPos, this, 5);
        }
        // if waterlogged, schedule water ticks
        if (stateIn.getValue(WATERLOGGED)) {
            // if waterlogged AND lit, remove self if soul fire is not above this block
            if (stateIn.getValue(LIT) && worldIn.getBlockState(currentPos.above()).getBlock() != Blocks.SOUL_FIRE) {
                worldIn.destroyBlock(currentPos, false);
            } else {
                worldIn.getBlockTicks().scheduleTick(currentPos, this, 2);
                worldIn.getLiquidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(worldIn));
            }
        }
        return stateIn;
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        // determine if block is waterlogged
        FluidState fluid = context.getLevel().getFluidState(context.getClickedPos());
        boolean waterlogged = fluid.is(FluidTags.WATER);
        // if waterlogged OR next to fire, schedule an update tick
        if (waterlogged || nextToFire(context.getLevel(), context.getClickedPos())) {
            context.getLevel().getBlockTicks().scheduleTick(context.getClickedPos(), this, 5);
        }
        return super.defaultBlockState().setValue(WATERLOGGED, waterlogged);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return !state.getValue(WATERLOGGED) ? shape : shapeWaterlogged;
    }

    // Waterlogged methods

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : Fluids.EMPTY.defaultFluidState();
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

    // Fire methods

    @Override
    public boolean canSurvive(BlockState state, IWorldReader worldIn, BlockPos pos) {
        return worldIn.getBlockState(pos).getFluidState().getType() == Fluids.WATER || worldIn.getBlockState(pos.below()).isSolidRender(worldIn, pos.below());
    }

    @Override
    public void entityInside(BlockState state, World worldIn, BlockPos pos, Entity entityIn) {
        if (state.getValue(LIT) && !entityIn.fireImmune()) {
            entityIn.setRemainingFireTicks(entityIn.getRemainingFireTicks() + 1);
            if (entityIn.getRemainingFireTicks() == 0) {
                entityIn.setSecondsOnFire(9);
            }

            entityIn.hurt(DamageSource.IN_FIRE, this.fireDamage);
        }

        super.entityInside(state, worldIn, pos, entityIn);
    }

    @Override
    public void onPlace(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (!oldState.is(state.getBlock())) {
            if (state.getValue(LIT) && canLightPortal(worldIn)) {
                Optional<PortalSize> optional = PortalSize.findEmptyPortalShape(worldIn, pos, Direction.Axis.X);
                optional = net.minecraftforge.event.ForgeEventFactory.onTrySpawnPortal(worldIn, pos, optional);
                if (optional.isPresent()) {
                    optional.get().createPortalBlocks();
                    return;
                }
            }

            if (!state.canSurvive(worldIn, pos)) {
                worldIn.removeBlock(pos, false);
            }

        }
    }

    /**
     * Called before the Block is set to air in the world. Called regardless of if the player's tool can actually collect this block
     */
    @Override
    public void playerWillDestroy(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
        if (state.getValue(LIT)) {
            if (!worldIn.isClientSide()) {
                worldIn.levelEvent(null, 1009, pos, 0);
            }
        } else {
            super.playerWillDestroy(worldIn, pos, state, player);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        if (stateIn.getValue(LIT)) {
            // play fire sound
            if (rand.nextInt(24) == 0) {
                worldIn.playLocalSound((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D,
                        SoundEvents.FIRE_AMBIENT, SoundCategory.BLOCKS, 1.0F + rand.nextFloat(), rand.nextFloat() * 0.7F + 0.3F, false);
            }
            // add smoke particles
            for (int i = 0; i < 2; ++i) {
                double d0 = (double) pos.getX() + rand.nextDouble();
                double d1 = (double) pos.getY() + rand.nextDouble() * 0.5D + 0.5D;
                double d2 = (double) pos.getZ() + rand.nextDouble();
                worldIn.addParticle(ParticleTypes.LARGE_SMOKE, d0, d1, d2, 0.0D, 0.0D, 0.0D);
            }
        }
    }

    private static boolean canLightPortal(World world) {
        return world.dimension() == World.OVERWORLD || world.dimension() == World.NETHER;
    }

    /**
     * @param world the world
     * @param pos   the block position
     * @return true if the block is next to fire
     */
    private static boolean nextToFire(IWorld world, BlockPos pos) {
        BlockPos p;
        BlockState s;
        for (final Direction d : Direction.values()) {
            p = pos.relative(d);
            s = world.getBlockState(p);
            if (isFire(world, s, p)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isFire(IWorld world, BlockState state, BlockPos pos) {
        return state.is(BlockTags.FIRE) || (state.getBlock() == GFRegistry.OIL && state.getValue(LIT));
    }

    public static void setFire(IWorld worldIn, BlockState state, BlockPos pos) {
        worldIn.setBlock(pos, state.setValue(LIT, true), 3);
        if (state.getValue(WATERLOGGED)) {
            // when waterlogged, place soul fire
            if (worldIn.isEmptyBlock(pos.above())) {
                worldIn.setBlock(pos.above(), Blocks.SOUL_FIRE.defaultBlockState(), 3);
            }
        }
    }
}
