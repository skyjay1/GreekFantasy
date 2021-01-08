package greekfantasy.item;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class GorgonBloodItem extends Item {
  
  public static final Food GORGON_BLOOD_GOOD = (new Food.Builder().hunger(2).saturation(0.1F).setAlwaysEdible()
      .effect(() -> new EffectInstance(Effects.REGENERATION, 180), 1.0F)).build();
  
  public static final Food GORGON_BLOOD_BAD = (new Food.Builder().hunger(2).saturation(0.1F).setAlwaysEdible()
      .effect(() -> new EffectInstance(Effects.POISON, 180), 1.0F)).build();

  public GorgonBloodItem(final Item.Properties properties) {
    super(properties);
  }
  
  @Override
  public boolean isFood() {
    return true;
  }
  
  @Override
  public Food getFood() {
    return Math.random() < 0.5D ? GORGON_BLOOD_BAD : GORGON_BLOOD_GOOD;
  }
  
  @OnlyIn(Dist.CLIENT)
  public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
    tooltip.add(new TranslationTextComponent("item.greekfantasy.gorgon_blood.tooltip_heal").mergeStyle(TextFormatting.AQUA));
    tooltip.add(new TranslationTextComponent("item.greekfantasy.gorgon_blood.tooltip_poison").mergeStyle(TextFormatting.AQUA));
  }
}
