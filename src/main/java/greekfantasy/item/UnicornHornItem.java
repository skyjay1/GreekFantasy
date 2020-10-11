package greekfantasy.item;

import java.util.List;
import java.util.stream.Collectors;

import greekfantasy.GreekFantasy;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectType;
import net.minecraft.potion.Effects;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class UnicornHornItem extends Item {
  
  private final int USE_DURATION = 50;
  private final int USE_COOLDOWN = 240;
  
  public UnicornHornItem(final Item.Properties properties) {
    super(properties);
  }

  @Override
  public int getUseDuration(final ItemStack stack) {
    return USE_DURATION;
  }
  
  @Override
  public ItemStack onItemUseFinish(final ItemStack stack, final World worldIn, final LivingEntity entityLiving) {
    if(entityLiving instanceof PlayerEntity) {
      ((PlayerEntity)entityLiving).getCooldownTracker().setCooldown(this, USE_COOLDOWN);
    }
    // remove negative potion effects
    if(GreekFantasy.CONFIG.UNICORN_HORN_CURES_EFFECTS.get()) {
      final List<Effect> list = entityLiving.getActivePotionEffects().stream()
        .filter(ei -> ei.getPotion().getEffectType() == EffectType.HARMFUL).map(ei -> ei.getPotion())
        .collect(Collectors.toList());
      // note: we do the for-each separately to avoid CME  
      list.forEach(e -> entityLiving.removeActivePotionEffect(e));
    }
    // give brief regen effect
    entityLiving.addPotionEffect(new EffectInstance(Effects.REGENERATION, 80, 0));
    stack.damageItem(2, entityLiving, (entity) -> entity.sendBreakAnimation(EquipmentSlotType.MAINHAND));
    return stack;
  }
  
  @Override
  public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
    ItemStack itemstack = player.getHeldItem(hand);
    player.setActiveHand(hand);
    return ActionResult.resultConsume(itemstack);
  }
  
  @Override
  public UseAction getUseAction(final ItemStack stack) {
    return UseAction.BOW;
  }
  
}
