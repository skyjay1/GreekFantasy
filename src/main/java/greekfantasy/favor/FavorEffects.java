package greekfantasy.favor;

import java.util.function.Function;
import java.util.function.Supplier;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;

public class FavorEffects {
  public static final IFavorEffect NONE = (p, f) -> 1000;
  
  public static final IFavorEffect REGEN = new PotionFavorEffect(
      () -> new EffectInstance(Effects.REGENERATION, 600), f -> 24000L - f.getLevel() * 200);
  
  public static final IFavorEffect FOOD = new GiveItemEffect(() -> new ItemStack(Items.BREAD), f -> 24000L - f.getLevel() * 200);
  
  public static final IFavorEffect LIGHTNING = (p, f) -> {
    if(p.getEntityWorld().canBlockSeeSky(p.getPosition().up())) {
      final LightningBoltEntity e = EntityType.LIGHTNING_BOLT.create(p.getEntityWorld());
      e.setPosition(p.getPosX() + (p.getRNG().nextDouble() - 0.5D) * 8.0D, p.getPosY(), p.getPosZ() + (p.getRNG().nextDouble() - 0.5D) * 8.0D);
      p.getEntityWorld().addEntity(e);
      return 48000 + f.getLevel() * 500;
    }
    return -1;
  };
  
  public static class GiveItemEffect implements IFavorEffect {

    private final Supplier<ItemStack> itemSupplier;
    private final Function<FavorInfo, Long> effectCooldown;
    
    public GiveItemEffect(final Supplier<ItemStack> item, final Function<FavorInfo, Long> cooldown) {
      itemSupplier = item;
      effectCooldown = cooldown;
    }

    @Override
    public long performEffect(PlayerEntity player, FavorInfo favor) {
      ItemEntity item = player.entityDropItem(itemSupplier.get());
      if(item != null) {
        item.setNoPickupDelay();
        return effectCooldown.apply(favor).longValue();
      }
      return -1;
    }
    
  }
  
  public static class PotionFavorEffect implements IFavorEffect {
    private final Supplier<EffectInstance> potionSupplier;
    private final Function<FavorInfo, Long> effectCooldown;
    
    public PotionFavorEffect(final Supplier<EffectInstance> potion, final Function<FavorInfo, Long> cooldown) {
      potionSupplier = potion;
      effectCooldown = cooldown;
    }

    @Override
    public long performEffect(PlayerEntity player, FavorInfo favor) {
      if(player.addPotionEffect(potionSupplier.get())) {
        return effectCooldown.apply(favor).longValue();
      }
      return -1;
    }
  }
  
  @FunctionalInterface
  public static interface IFavorEffect {
    /**
     * Performs a Favor effect for the given player
     * @param player the player
     * @param favor the player's favor info
     * @return the cooldown time for further effects, or -1 if the effect was not performed
     */
    long performEffect(final PlayerEntity player, final FavorInfo favor);
  }
}
