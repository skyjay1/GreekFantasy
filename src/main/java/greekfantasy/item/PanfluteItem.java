package greekfantasy.item;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import net.minecraft.item.Item.Properties;

public class PanfluteItem extends InstrumentItem {

  public PanfluteItem(final Properties properties) {
    super(properties);
  }
  
  @OnlyIn(Dist.CLIENT)
  public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
    tooltip.add(new TranslationTextComponent(TOOLTIP).withStyle(TextFormatting.GRAY));
  }
  
  // Instrument //
  
  @Override
  public SoundEvent getSound() { return SoundEvents.NOTE_BLOCK_FLUTE; }
}
