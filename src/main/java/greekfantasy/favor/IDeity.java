package greekfantasy.favor;

import java.util.List;
import java.util.Map;
import java.util.Random;

import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
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
   * @param entity the entity type to check
   * @return true if a favor modifier exists for the entity
   */
  default boolean hasKillFavorModifier(final EntityType<?> entity) {
    return getKillFavorModifiers().containsKey(entity.getRegistryName());
  }
  
  /**
   * @param entity the entity type to check
   * @return the favor modifier, or zero if no entity is found
   */
  default int getKillFavorModifier(final EntityType<?> entity) {
    return getKillFavorModifiers().getOrDefault(entity.getRegistryName(), 0);
  }
  
  /**
   * @return a map of item offerings and favor modifiers
   */
  public Map<ResourceLocation, Integer> getItemFavorModifiers();
  
  /**
   * @param item the item to check
   * @return true if a favor modifier exists for the item
   */
  default boolean hasItemFavorModifier(final Item item) {
    return getItemFavorModifiers().containsKey(item.getRegistryName());
  }
  
  /**
   * @param item the item to check
   * @return the favor modifier, or zero if no item is found
   */
  default int getItemFavorModifier(final Item item) {
    return getItemFavorModifiers().getOrDefault(item.getRegistryName(), 0);
  }
  
  /** @return a list of favor effects associated with the deity **/
  public List<FavorEffect> getFavorEffects();
  
  /**
   * @param rand a random instance
   * @return a favor effect chosen at random
   */
  default FavorEffect getRandomEffect(final Random rand, final int playerLevel) {
    final List<FavorEffect> effects = getFavorEffects();
    if(!effects.isEmpty()) {
      int tries = 10;
      while(tries-- >= 0) {
        final FavorEffect effect = effects.get(rand.nextInt(effects.size()));
        if(effect.isInRange(playerLevel)) {
          return effect;
        }
      }
    }
    return FavorEffect.EMPTY;
  }
  
  /** @return true if the statue model is female **/
  public boolean isFemale();
  
  /** @return the item in the statue's right hand **/
  public ItemStack getRightHandItem();
  
  /** @return the item in the statue's left hand **/
  public ItemStack getLeftHandItem();
}
