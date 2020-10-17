package greekfantasy.item;

import greekfantasy.GreekFantasy;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTier;
import net.minecraft.item.SwordItem;
import net.minecraft.util.DamageSource;

public class SwordOfTheHuntItem extends SwordItem {

  public SwordOfTheHuntItem(final Item.Properties properties) {
    super(ItemTier.IRON, 3, -2.4F, properties);
  }
  
  @Override
  public boolean hitEntity(final ItemStack item, final LivingEntity target, final LivingEntity attacker) {
    if(super.hitEntity(item, target, attacker)) {
      // if it's an animal, use high attack damage
      if(target instanceof AnimalEntity) {
        float amount = this.getAttackDamage();
        DamageSource source = DamageSource.causeMobDamage(attacker);
        // if config option is enabled, use max damage
        if(GreekFantasy.CONFIG.doesSwordOfHuntBypassArmor()) {
          amount = target.getMaxHealth() * 1.25F;
          source.setDamageBypassesArmor().setDamageIsAbsolute();
        }
        target.attackEntityFrom(source, amount);
      }
      return true;
    }
    return false;
  }
}
