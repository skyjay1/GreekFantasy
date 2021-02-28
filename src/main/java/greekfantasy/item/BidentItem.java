package greekfantasy.item;

import greekfantasy.GFRegistry;
import greekfantasy.GreekFantasy;
import greekfantasy.entity.SpartiEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.IItemTier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeMod;

public class BidentItem extends SpearItem {
  
  public BidentItem(IItemTier tier, Item.Properties properties) { 
    super(tier, properties, e -> e.setFire(4)); 
  }

  @Override
  protected void throwSpear(final World world, final PlayerEntity thrower, final ItemStack stack) {
    // Special behavior when enchanted
    if(EnchantmentHelper.getEnchantmentLevel(GFRegistry.RAISING_ENCHANTMENT, stack) > 0) {
      // Attempt to spawn a Sparti where the player is looking
      final RayTraceResult raytrace = ThunderboltItem.raytraceFromEntity(world, thrower, (float)thrower.getAttribute(ForgeMod.REACH_DISTANCE.get()).getValue());
      if(raytrace.getType() != RayTraceResult.Type.MISS) {
        stack.damageItem(25, thrower, e -> e.sendBreakAnimation(thrower.getActiveHand()));
        // spawn a sparti and set location
        final SpartiEntity sparti = GFRegistry.SPARTI_ENTITY.create(world);
        sparti.setPosition(raytrace.getHitVec().getX(), raytrace.getHitVec().getY(), raytrace.getHitVec().getZ());
        sparti.rotationPitch = MathHelper.wrapDegrees(thrower.rotationYaw + 180.0F);
        sparti.setOwner((PlayerEntity) thrower);
        // Lifespan is 1/3 the usual amount
        sparti.setLimitedLife(GreekFantasy.CONFIG.getSpartiLifespan() * 20 / 3);
        sparti.setEquipmentOnSpawn();
        thrower.playSound(SoundEvents.BLOCK_LAVA_EXTINGUISH, 0.8F, 0.9F + thrower.getRNG().nextFloat() * 0.2F);
        world.addEntity(sparti);
        // update spawning flag
        sparti.setSpawning(true);
      }
      return;
    }
    // Default behavior when not enchanted
    super.throwSpear(world, thrower, stack);
  }
}
