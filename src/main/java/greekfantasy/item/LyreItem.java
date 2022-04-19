package greekfantasy.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class LyreItem extends InstrumentItem {

    protected SoundEvent sound;

    public LyreItem(final SoundEvent soundIn, final Properties properties) {
        super(properties);
        sound = soundIn;
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new TranslationTextComponent(TOOLTIP).withStyle(TextFormatting.GRAY));
    }

    // Instrument //

    @Override
    public SoundEvent getSound() {
        return sound;
    }
}
