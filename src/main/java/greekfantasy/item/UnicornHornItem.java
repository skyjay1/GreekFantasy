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
  
  private final int useDuration = 50;
  private final int useCooldown = 240;
  
  public UnicornHornItem(final Item.Properties properties) {
    super(properties);
  }

  @Override
  public int getUseDuration(final ItemStack stack) {
    return useDuration;
  }
  
  @Override
  public ItemStack finishUsingItem(final ItemStack stack, final World worldIn, final LivingEntity entityLiving) {
    if(entityLiving instanceof PlayerEntity) {
      ((PlayerEntity)entityLiving).getCooldowns().addCooldown(this, useCooldown);
    }
    // remove negative potion effects
    if(GreekFantasy.CONFIG.UNICORN_HORN_CURES_EFFECTS.get()) {
      final List<Effect> list = entityLiving.getActiveEffects().stream()
        .filter(ei -> ei.getEffect().getCategory() == EffectType.HARMFUL).map(ei -> ei.getEffect())
        .collect(Collectors.toList());
      // note: we do the for-each separately to avoid CME  
      list.forEach(e -> entityLiving.removeEffectNoUpdate(e));
    }
    // give brief regen effect
    entityLiving.addEffect(new EffectInstance(Effects.REGENERATION, 80, 0));
    stack.hurtAndBreak(2, entityLiving, (entity) -> entity.broadcastBreakEvent(EquipmentSlotType.MAINHAND));
    return stack;
  }
  
  @Override
  public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
    ItemStack itemstack = player.getItemInHand(hand);
    player.startUsingItem(hand);
    return ActionResult.consume(itemstack);
  }
  
  @Override
  public UseAction getUseAnimation(final ItemStack stack) {
    return UseAction.BOW;
  }
  
}
