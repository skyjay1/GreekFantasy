package greekfantasy.enchantment;

import greekfantasy.GreekFantasy;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;

public class HuntingEnchantment extends Enchantment {
  
  public HuntingEnchantment(final Enchantment.Rarity rarity) {
    super(rarity, EnchantmentType.WEAPON, new EquipmentSlotType[] { EquipmentSlotType.MAINHAND });
  }
  
  private boolean isApplicableItem(final ItemStack item) {
    return item.getItem() instanceof SwordItem || item.getItem() instanceof AxeItem;
  }
  
  @Override
  public void doPostAttack(LivingEntity user, Entity target, int level) {
    // check for cooldown
    final ItemStack item = user.getItemInHand(Hand.MAIN_HAND);
    if(!isApplicableItem(item)) {
      return;
    }
    // if it's an animal, use high attack damage
    if(target.canChangeDimensions() && target instanceof AnimalEntity && (level >= 3 || user.getRandom().nextInt(4 - level) == 0)) {
      float amount = 1.0F;
      DamageSource source = DamageSource.mobAttack(user);
      // if config option is enabled, use max damage
      if(GreekFantasy.CONFIG.doesSwordOfHuntBypassArmor()) {
        // max damage is 128 (see issue #20)
        amount = Math.min(128.0F, ((AnimalEntity)target).getMaxHealth() * 1.25F);
        source.bypassArmor().bypassMagic();
      }
      target.hurt(source, amount);
    }
  }

  @Override 
  public int getMinCost(int level) { return 10 + super.getMinCost(level); }
  @Override
  public int getMaxCost(int level) { return 10 + super.getMaxCost(level); }
  @Override
  public boolean isTreasureOnly() { return GreekFantasy.CONFIG.isHuntingEnabled(); }
  @Override
  public boolean isTradeable() { return GreekFantasy.CONFIG.isHuntingEnabled(); }
  @Override
  public boolean isDiscoverable() { return GreekFantasy.CONFIG.isHuntingEnabled(); }
  @Override
  public int getMaxLevel() { return 3; }
  @Override
  public boolean canApplyAtEnchantingTable(ItemStack stack) { 
    return GreekFantasy.CONFIG.isHuntingEnabled() && isApplicableItem(stack) && super.canApplyAtEnchantingTable(stack); 
  }
}
