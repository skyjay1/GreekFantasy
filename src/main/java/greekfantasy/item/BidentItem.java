package greekfantasy.item;

import greekfantasy.GFRegistry;
import greekfantasy.GreekFantasy;
import greekfantasy.entity.SpartiEntity;
import greekfantasy.integration.RGCompat;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.IItemTier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeMod;

public class BidentItem extends SpearItem {

    public BidentItem(IItemTier tier, Item.Properties properties) {
        super(tier, properties, e -> e.setSecondsOnFire(4));
    }

    @Override
    protected void throwSpear(final World world, final PlayerEntity thrower, final ItemStack stack) {
        // Special behavior when enchanted
        if (EnchantmentHelper.getItemEnchantmentLevel(GFRegistry.EnchantmentReg.RAISING_ENCHANTMENT, stack) > 0
            && (!GreekFantasy.isRGLoaded() || RGCompat.getInstance().canUseRaising(thrower))) {
            // Attempt to spawn a Sparti where the player is looking
            final RayTraceResult raytrace = ThunderboltItem.raytraceFromEntity(world, thrower, (float) thrower.getAttribute(ForgeMod.REACH_DISTANCE.get()).getValue());
            if (raytrace.getType() != RayTraceResult.Type.MISS) {
                stack.hurtAndBreak(25, thrower, e -> e.broadcastBreakEvent(thrower.getUsedItemHand()));
                // spawn a sparti and set location
                final SpartiEntity sparti = GFRegistry.EntityReg.SPARTI_ENTITY.create(world);
                sparti.setPos(raytrace.getLocation().x(), raytrace.getLocation().y(), raytrace.getLocation().z());
                sparti.xRot = MathHelper.wrapDegrees(thrower.yRot + 180.0F);
                sparti.setTame(true);
                sparti.setOwnerUUID(thrower.getUUID());
                // Lifespan is 1/3 the usual amount
                sparti.setLimitedLife(GreekFantasy.CONFIG.getSpartiLifespan() * 20 / 3);
                sparti.setEquipmentOnSpawn();
                thrower.playSound(SoundEvents.LAVA_EXTINGUISH, 0.8F, 0.9F + thrower.getRandom().nextFloat() * 0.2F);
                world.addFreshEntity(sparti);
                // entity data on spawn
                sparti.finalizeSpawn((IServerWorld) world, world.getCurrentDifficultyAt(new BlockPos(raytrace.getLocation())), SpawnReason.MOB_SUMMONED, null, null);
            }
        } else {
            // Default behavior when not enchanted
            super.throwSpear(world, thrower, stack);
        }
    }

    /**
     * Return whether this item is repairable in an anvil.
     */
    @Override
    public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair) {
        return false;
    }
}
