package greekfantasy.item;

import greekfantasy.GreekFantasy;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class ConchItem extends Item {

    public ConchItem(final Item.Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand handIn) {
        final ItemStack item = player.getItemInHand(handIn);
        if (!level.isClientSide()) {
            // raytrace
            final BlockHitResult raytrace = ThunderboltItem.raytraceFromEntity(player, 32.0F, ClipContext.Fluid.SOURCE_ONLY);
            BlockPos hitPos = new BlockPos(raytrace.getLocation());
            // attempt to place water at the hit position
            if (raytrace.getType() == HitResult.Type.BLOCK) {
                // locate block and fluid at this position
                BlockState hitBlock = level.getBlockState(hitPos);
                if (!hitBlock.isAir() && !(hitBlock.getBlock() instanceof BucketPickup)) {
                    hitPos = hitPos.relative(raytrace.getDirection());
                }
                hitBlock = level.getBlockState(hitPos);
                FluidState hitFluid = level.getFluidState(hitPos);
                // determine whether to take or place water
                if (hitFluid.isEmpty() || !hitFluid.isSource()) {
                    placeWater(level, hitBlock, hitFluid, hitPos, player, item);
                } else {
                    takeWater(level, hitBlock, hitFluid, hitPos, player, item);
                }
                // add cooldown
                player.getCooldowns().addCooldown(this, 10);
                // item damage
                int damage = GreekFantasy.CONFIG.CONCH_DAMAGE_ON_USE.get();
                if(!player.isCreative() && damage > 0) {
                    item.hurtAndBreak(damage, player, e -> e.broadcastBreakEvent(handIn));
                }
                return InteractionResultHolder.success(item);
            }
        }
        return InteractionResultHolder.sidedSuccess(item, level.isClientSide());
    }

    /**
     * Attempts to place water at or in the given position. Copied from bucket behavior.
     *
     * @param level    the level
     * @param hitBlock the selected block
     * @param hitFluid the existing fluid in the selected block
     * @param hitPos   the selected position
     * @param player   the player
     * @param item     the held item
     * @return true if water was placed
     */
    private static boolean placeWater(Level level, BlockState hitBlock, FluidState hitFluid, BlockPos hitPos, Player player, ItemStack item) {
        // check if this dimension does not allow water
        if (level.dimensionType().ultraWarm()) {
            int i = hitPos.getX();
            int j = hitPos.getY();
            int k = hitPos.getZ();
            level.playSound(player, hitPos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5F,
                    2.6F + (player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 0.8F);
            // spawn "extinguish" particles
            for (int l = 0; l < 8; ++l) {
                level.addParticle(ParticleTypes.LARGE_SMOKE, (double) i + Math.random(), (double) j + Math.random(),
                        (double) k + Math.random(), 0.0D, 0.0D, 0.0D);
            }
            return false;
        } else if (hitBlock.getBlock() instanceof LiquidBlockContainer liquidBlockContainer && liquidBlockContainer.canPlaceLiquid(level, hitPos, hitBlock, Fluids.WATER)) {
            // if this block can receive liquid, attempt to do that first
            liquidBlockContainer.placeLiquid(level, hitPos, hitBlock, Fluids.WATER.getSource(false));
            level.playSound(player, hitPos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
            return true;
        } else {
            // destroy the block that's already here if it's replaceable
            if (!level.isClientSide() && hitBlock.canBeReplaced(Fluids.WATER) && !hitBlock.getMaterial().isLiquid()) {
                level.destroyBlock(hitPos, true);
            }
            // finally, attempt to place water directly
            if (!level.setBlock(hitPos, Fluids.WATER.defaultFluidState().createLegacyBlock(), 11)
                    && !hitFluid.isSource()) {
                return false;
            } else {
                level.playSound(player, hitPos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
                return true;
            }
        }
    }

    /**
     * Attempts to take water from the given position. Copied from bucket behavior.
     *
     * @param level    the level
     * @param hitBlock the selected block
     * @param hitFluid the existing fluid in the selected block
     * @param hitPos   the selected position
     * @param player   the player
     * @param item     the held item
     * @return true if water was removed
     */
    private static boolean takeWater(Level level, BlockState hitBlock, FluidState hitFluid, BlockPos hitPos, Player player, ItemStack item) {
        // ensure block can be picked up with bucket
        if (hitBlock.getBlock() instanceof BucketPickup bucketpickup
                && level.mayInteract(player, hitPos)
                && player.mayUseItemAt(hitPos, null, item)
                && hitFluid.is(FluidTags.WATER)) {
            // attempt to pick up block
            ItemStack result = bucketpickup.pickupBlock(level, hitPos, hitBlock);
            if (!result.isEmpty()) {
                bucketpickup.getPickupSound(hitBlock).ifPresent((sound) -> {
                    player.playSound(sound, 1.0F, 1.0F);
                });
                level.gameEvent(player, GameEvent.FLUID_PICKUP, hitPos);
                return true;
            }
        }
        return false;
    }
}
