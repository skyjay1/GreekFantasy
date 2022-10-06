package greekfantasy.block;

import greekfantasy.GFRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.portal.PortalShape;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Optional;

public class OliveOilBlock extends Block implements LiquidBlockContainer {

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final BooleanProperty LIT = BlockStateProperties.LIT;

    protected final float fireDamage;
    protected static final VoxelShape shape = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D);
    protected static final VoxelShape shapeWaterlogged = Block.box(0.0D, 14.0D, 0.0D, 16.0D, 15.0D, 16.0D);

    public OliveOilBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any()
                .setValue(LIT, false).setValue(WATERLOGGED, false));
        this.fireDamage = 2.5F;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LIT, WATERLOGGED);
    }

    @Override
    public InteractionResult use(final BlockState state, final Level level, final BlockPos pos,
                                 final Player player, final InteractionHand handIn, final BlockHitResult hit) {
        ItemStack heldItem = player.getItemInHand(handIn);
        if (!level.isClientSide() && !state.getValue(LIT) && !heldItem.isEmpty()) {
            // use the item to interact with this block
            if (heldItem.getItem() == Items.GLASS_BOTTLE) {
                // remove this block
                level.destroyBlock(pos, false);
                // shrink held item stack
                if (!player.isCreative()) {
                    heldItem.shrink(1);
                }
                // spawn oil bottle
                player.addItem(new ItemStack(GFRegistry.ItemReg.OLIVE_OIL.get()));
                return InteractionResult.CONSUME;
            } else if (heldItem.getItem() == Items.FLINT_AND_STEEL) {
                // replace this block with fire
                setFire(level, state, pos);
                // play sound effect and damage item
                player.playSound(SoundEvents.FLINTANDSTEEL_USE, 0.4F, 1.0F);
                heldItem.hurtAndBreak(1, player, i -> i.broadcastBreakEvent(EquipmentSlot.MAINHAND));
                return InteractionResult.CONSUME;
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource rand) {
        // if invalid position, remove this block
        if (!state.canSurvive(level, pos)) {
            level.destroyBlock(pos, false);
        }
        // if waterlogged, attempt to "float" up
        if (state.getValue(WATERLOGGED)) {
            if (level.getBlockState(pos.above()).getBlock() == Blocks.WATER) {
                // remove this block and set above block to this state
                level.setBlockAndUpdate(pos.above(), state);
                level.setBlock(pos, state.getFluidState().createLegacyBlock(), 2);
                // schedule another tick to continue moving upward
                level.scheduleTick(pos.above(), this, 40);
                return;
            } else if (level.getBlockState(pos.above()).getBlock() == this) {
                // remove this block and "merge" with the one above it
                level.setBlock(pos, state.getFluidState().createLegacyBlock(), 2);
                level.scheduleTick(pos.above(), this, 40);
                return;
            }
        }

        // determine if this block is adjacent to fire
        boolean nextToFire = nextToFire(level, pos);
        // if the block is next to fire, either ignite or schedule another update
        if (nextToFire) {
            if (level.getRandom().nextInt(10) == 0) {
                // replace this block with fire
                setFire(level, state, pos);
            } else {
                // schedule another tick to replace self with fire
                level.scheduleTick(pos, this, 2 + rand.nextInt(5));
            }
        }
    }

    @Override
    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor level,
                                  BlockPos currentPos, BlockPos facingPos) {
        // if invalid position, remove this block
        if (!this.canSurvive(stateIn, level, currentPos)) {
            return stateIn.getFluidState().createLegacyBlock();
        }
        // if a fire block is placed nearby, begin catching fire
        if (isFire(level, facingState, facingPos)) {
            level.scheduleTick(currentPos, this, 5);
        }
        // if waterlogged, schedule water ticks
        if (stateIn.getValue(WATERLOGGED)) {
            // if waterlogged AND lit, remove self if soul fire is not above this block
            if (stateIn.getValue(LIT) && level.getBlockState(currentPos.above()).getBlock() != Blocks.SOUL_FIRE) {
                level.destroyBlock(currentPos, false);
            } else {
                level.scheduleTick(currentPos, this, 2);
                level.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
            }
        }
        return stateIn;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        // determine if block is waterlogged
        FluidState fluid = context.getLevel().getFluidState(context.getClickedPos());
        boolean waterlogged = fluid.is(FluidTags.WATER);
        // if waterlogged OR next to fire, schedule an update tick
        if (waterlogged || nextToFire(context.getLevel(), context.getClickedPos())) {
            context.getLevel().scheduleTick(context.getClickedPos(), this, 5);
        }
        return super.defaultBlockState().setValue(WATERLOGGED, waterlogged);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
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
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        return state.getValue(LIT) ? 15 : 0;
    }

    // Fire methods

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        // requires no fluid above
        if(!level.getFluidState(pos.above()).isEmpty()) {
            return false;
        }
        // when not waterlogged, requires solid block below
        if(!state.getValue(WATERLOGGED) && !level.getBlockState(pos.below()).isFaceSturdy(level, pos.below(), Direction.UP)) {
            return false;
        }
        // all checks passed
        return true;
    }

    @Override
    public boolean canPlaceLiquid(BlockGetter level, BlockPos pos, BlockState state, Fluid fluid) {
        return fluid == Fluids.FLOWING_WATER || fluid == Fluids.WATER;
    }

    @Override
    public boolean placeLiquid(LevelAccessor level, BlockPos pos, BlockState state, FluidState fluidState) {
        level.setBlock(pos, fluidState.createLegacyBlock(), Block.UPDATE_ALL);
        level.scheduleTick(pos, fluidState.getType(), fluidState.getType().getTickDelay(level));
        return true;
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entityIn) {
        if (state.getValue(LIT) && !entityIn.fireImmune()) {
            entityIn.setRemainingFireTicks(level.getRandom().nextInt(5) + 5);
            entityIn.hurt(DamageSource.IN_FIRE, this.fireDamage);
        }

        super.entityInside(state, level, pos, entityIn);
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (!oldState.is(state.getBlock())) {
            if (state.getValue(LIT) && inPortalDimension(level)) {
                Optional<PortalShape> optional = PortalShape.findEmptyPortalShape(level, pos, Direction.Axis.X);
                optional = net.minecraftforge.event.ForgeEventFactory.onTrySpawnPortal(level, pos, optional);
                if (optional.isPresent()) {
                    optional.get().createPortalBlocks();
                    return;
                }
            }

            if (!state.canSurvive(level, pos)) {
                level.removeBlock(pos, false);
            }

        }
    }

    /**
     * Called before the Block is set to air in the world. Called regardless of if the player's tool can actually collect this block
     */
    @Override
    public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (state.getValue(LIT)) {
            if (!level.isClientSide()) {
                level.levelEvent(null, 1009, pos, 0);
            }
        } else {
            super.playerWillDestroy(level, pos, state, player);
        }
    }

    @Override
    public void animateTick(BlockState stateIn, Level level, BlockPos pos, RandomSource rand) {
        if (stateIn.getValue(LIT)) {
            // play fire sound
            if (rand.nextInt(24) == 0) {
                level.playLocalSound((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D,
                        SoundEvents.FIRE_AMBIENT, SoundSource.BLOCKS, 1.0F + rand.nextFloat(), rand.nextFloat() * 0.7F + 0.3F, false);
            }
            // add smoke particles
            for (int i = 0; i < 2; ++i) {
                double d0 = (double) pos.getX() + rand.nextDouble();
                double d1 = pos.getY() + rand.nextDouble() * 0.5D + 0.5D;
                double d2 = (double) pos.getZ() + rand.nextDouble();
                level.addParticle(ParticleTypes.LARGE_SMOKE, d0, d1, d2, 0.0D, 0.0D, 0.0D);
            }
        }
    }

    private static boolean inPortalDimension(Level world) {
        return world.dimension() == Level.OVERWORLD || world.dimension() == Level.NETHER;
    }

    /**
     * @param world the world
     * @param pos   the block position
     * @return true if the block is next to fire
     */
    private static boolean nextToFire(LevelReader world, BlockPos pos) {
        BlockPos p;
        BlockState s;
        for (final Direction d : Direction.Plane.HORIZONTAL) {
            p = pos.relative(d);
            s = world.getBlockState(p);
            if (isFire(world, s, p)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isFire(LevelReader world, BlockState state, BlockPos pos) {
        return state.is(BlockTags.FIRE) || (state.is(GFRegistry.BlockReg.OLIVE_OIL.get()) && state.getValue(LIT));
    }

    public static void setFire(Level level, BlockState state, BlockPos pos) {
        level.setBlock(pos, state.setValue(LIT, true), 3);
        if (state.getValue(WATERLOGGED)) {
            // when waterlogged, place soul fire
            if (level.isEmptyBlock(pos.above())) {
                level.setBlock(pos.above(), Blocks.SOUL_FIRE.defaultBlockState(), 3);
            }
        } else if(inPortalDimension(level)) {
            // attempt to light portal (when not waterlogged)
            Optional<PortalShape> optional = PortalShape.findEmptyPortalShape(level, pos, Direction.Axis.X);
            optional = net.minecraftforge.event.ForgeEventFactory.onTrySpawnPortal(level, pos, optional);
            if (optional.isPresent()) {
                optional.get().createPortalBlocks();
                return;
            }

            if (!state.canSurvive(level, pos)) {
                level.removeBlock(pos, false);
            }
        }


    }
}
