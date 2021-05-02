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

public class GoldenAppleOfDiscordItem extends Item {
    
  public static final Food GOLDEN_APPLE_OF_DISCORD = new Food.Builder()
      .hunger(4).saturation(1.2F)
      .effect(() -> new EffectInstance(Effects.REGENERATION, 100, 1), 1.0F)
      .effect(() -> new EffectInstance(Effects.ABSORPTION, 2400, 0), 1.0F)
      .effect(() -> new EffectInstance(Effects.NAUSEA, 600, 0), 0.5F)
      .effect(() -> new EffectInstance(Effects.POISON, 200, 0), 0.5F)
      .setAlwaysEdible().build();

  public GoldenAppleOfDiscordItem(Item.Properties properties) {
    super(properties);
  }
  
  @OnlyIn(Dist.CLIENT)
  public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
    tooltip.add(new TranslationTextComponent("item.greekfantasy.golden_apple_of_discord.tooltip").mergeStyle(TextFormatting.GRAY));
    if(net.minecraft.client.gui.screen.Screen.hasShiftDown()) {
      tooltip.add(new TranslationTextComponent("deity.greekfantasy.aphrodite").appendString(", ")
          .appendSibling(new TranslationTextComponent("deity.greekfantasy.athena")).appendString(", ")
          .appendSibling(new TranslationTextComponent("deity.greekfantasy.hera")));
    }
  }

}
