package greekfantasy.favor;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;

import greekfantasy.favor.FavorEffects.IFavorEffect;
import greekfantasy.tileentity.AltarTileEntity;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public interface IDeity {

  /**
   * @return the name of the deity
   */
  public ResourceLocation getName();
  
  default IFormattableTextComponent getText() {
    final ResourceLocation name = getName();
    return new TranslationTextComponent("deity." + name.getNamespace() + "." + name.getPath());
  }

  /**
   * @return the texture to apply to the altar used by the deity
   */
  public ResourceLocation getTexture();
  
  /**
   * @return a map of entity type and favor modifiers
   */
  public Map<ResourceLocation, Integer> getKillFavorModifiers();
  
  /**
   * @return a map of item offerings and favor modifiers
   */
  public Map<Item, Integer> getItemFavorModifiers();
  
  /**
   * @return a list of positive favor effects associated with the deity
   */
  public List<WeightedFavorEffect> getGoodFavorEffects();
  
  public int getGoodFavorTotalWeight();
  
  /**
   * @return a list of positive favor effects associated with the deity
   */
  public List<WeightedFavorEffect> getBadFavorEffects();
  
  public int getBadFavorTotalWeight();
  
  default IFavorEffect getRandomEffect(final boolean good, final Random rand) {
    final List<WeightedFavorEffect> effects = good ? getGoodFavorEffects() : getBadFavorEffects();
    final int weights = good ? getGoodFavorTotalWeight() : getBadFavorTotalWeight();
    if(!effects.isEmpty()) {
      boolean chosen = false;
      WeightedFavorEffect effect = WeightedFavorEffect.EMPTY;
      while(!chosen) {
        effect = effects.get(rand.nextInt(effects.size()));
        chosen = effect.choose(weights, rand);
      }
      return effect.favorEffect;
    }
    return FavorEffects.NONE;
  }
  
  /**
   * Called when a new AltarTileEntity is created.
   * Used to set pose, male/female, etc.
   * @return a consumer that handles the creation of a new altar
   */
  public Consumer<AltarTileEntity> initAltar();
  
  default int calculateTotalWeights(final List<WeightedFavorEffect> list) {
    int total = 0;
    for(final WeightedFavorEffect w : list) {
      total += w.favorWeight;
    }
    return total;
  }
}
