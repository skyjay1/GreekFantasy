package greekfantasy.deity;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import greekfantasy.GFRegistry;
import greekfantasy.block.StatueBlock;
import greekfantasy.deity.favor_effect.ConfiguredSpecialFavorEffect;
import greekfantasy.deity.favor_effect.FavorEffect;
import greekfantasy.deity.favor_effect.FavorEffectTrigger;
import greekfantasy.deity.favor_effect.SpecialFavorEffect;
import greekfantasy.deity.favor_effect.TriggeredFavorEffect;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.registries.ForgeRegistries;

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
  
  /** @return self **/
  default IDeity getIDeity() { return this; }
  
  /** @return a list of favor effects associated with the deity **/
  public List<FavorEffect> getFavorEffects();
  
  /** @return a list of triggered favor effects associated with the deity **/
  public List<TriggeredFavorEffect> getTriggeredFavorEffects();
  
  /** @return a list of special favor effects associated with the deity **/
  public List<SpecialFavorEffect> getSpecialFavorEffects();
  
  /**
   * @param type the type of configured special favor effect
   * @return a list of special favor effects of the given type
   * @see #getSpecialFavorEffects()
   */
  default List<ConfiguredSpecialFavorEffect> getSpecialFavorEffects(SpecialFavorEffect.Type type) {
    return getSpecialFavorEffects().stream()
        .filter(e -> e.getType() == type)
        .map(f -> new ConfiguredSpecialFavorEffect(this, f))
        .collect(Collectors.toList());
  }
  
  /**
   * @param rand a random instance
   * @param playerLevel the player's favor level for this deity
   * @return a favor effect chosen at random
   */
  default FavorEffect getRandomEffect(final Random rand, final int playerLevel) {
    final List<FavorEffect> effects = getFavorEffects();
    if(!effects.isEmpty()) {
      int tries = Math.min(effects.size(), 10);
      while(tries-- >= 0) {
        final FavorEffect effect = effects.get(rand.nextInt(effects.size()));
        if(effect.isInRange(playerLevel)) {
          return effect;
        }
      }
    }
    return FavorEffect.EMPTY;
  }
  
  default TriggeredFavorEffect getTriggeredFavorEffect(final Random rand, final FavorEffectTrigger.Type type, 
      final ResourceLocation data, final int playerLevel) {
    final List<TriggeredFavorEffect> effects = getTriggeredFavorEffects();
    if(!effects.isEmpty()) {
      int tries = Math.min(effects.size(), 10);
      while(tries-- > 0) {
        final TriggeredFavorEffect effect = effects.get(rand.nextInt(effects.size()));
        if(effect.getTrigger().getType() == type && data.equals(effect.getTrigger().getData()) 
            && effect.getEffect().isInRange(playerLevel) && rand.nextFloat() < effect.getAdjustedChance(playerLevel)) {
          return effect;
        }
      }
    }
    return TriggeredFavorEffect.EMPTY;
  }
  
  /** @return true if the deity should be shown in-game **/
  public boolean isEnabled();
  
  /** @return true if the statue model is female **/
  public boolean isFemale();
  
  /** @return the item in the statue's right hand **/
  public ItemStack getRightHandItem();
  
  /** @return the item in the statue's left hand **/
  public ItemStack getLeftHandItem();
  
  /** @return the id of the slab block **/
  public ResourceLocation getBase();

  /** @return the id of the overlay texture **/
  public String getOverlayString();
  
  /** @return the id of the overlay texture **/
  public ResourceLocation getOverlay();
  
  /** 
   * @param overlay the statue material name
   * @return the overlay texture location, or null if none should be used 
   **/
  @Nullable
  public default ResourceLocation makeOverlay(final String overlay) {
    if("none".equals(overlay)) {
      return null;
    }
    return StatueBlock.StatueMaterial.getByName(overlay).getStoneTexture();
  }

  /** @return the blockstate of the slab block **/
  public default BlockState getBaseBlock() {
    Block b = GFRegistry.POLISHED_MARBLE_SLAB;
    if(ForgeRegistries.BLOCKS.containsKey(getBase())) {
      b = ForgeRegistries.BLOCKS.getValue(getBase());
    }
    return b.defaultBlockState();
  }
}
