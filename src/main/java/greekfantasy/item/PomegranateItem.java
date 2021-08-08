package greekfantasy.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.world.World;

public class PomegranateItem extends Item {
  
  public static final Food POMEGRANATE = (new Food.Builder().hunger(1).saturation(0.1F)
      .effect(() -> new EffectInstance(Effects.REGENERATION, 180), 1.0F)).build();
  
  public PomegranateItem(final Item.Properties properties) {
    super(properties);
  }
  
  @Override
  public ItemStack onItemUseFinish(ItemStack item, World world, LivingEntity entity) {
    ItemStack stack = super.onItemUseFinish(item, world, entity);
    // TODO special effects go here
    return stack;
  }
}
