package greekfantasy.item;

import greekfantasy.GreekFantasy;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;

public interface IInstrument {
  
  public static final ResourceLocation DEFAULT_SONG = new ResourceLocation(GreekFantasy.MODID, "greensleeves");
  public static final String KEY_SONG = "Song";
  
  public SoundEvent getSound();
  
  public Item getInstrument();
  
  public default float getPitch(final int note) {
    return (float)Math.pow(2.0D, (double)(note - 12) / 12.0D);
  }
  
  public default void writeSong(final ItemStack stack, final ResourceLocation song) {
    stack.getOrCreateTag().putString(KEY_SONG, song.toString());
  }
  
  public default ResourceLocation readSong(final ItemStack stack) {
    if(stack.getOrCreateTag().contains(KEY_SONG)) {
      return new ResourceLocation(stack.getTag().getString(KEY_SONG));
    }
    return DEFAULT_SONG;
  }
}
