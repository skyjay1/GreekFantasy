package greekfantasy.deity;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import greekfantasy.GreekFantasy;
import greekfantasy.deity.favor_effect.FavorEffect;
import greekfantasy.deity.favor_effect.SpecialFavorEffect;
import greekfantasy.deity.favor_effect.TriggeredFavorEffect;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

/**
 * This class contains information about a deity.
 * @author skyjay1
 **/
public class Deity implements IDeity {
  
  public static final Deity EMPTY = new Deity(
      new ResourceLocation(GreekFantasy.MODID, "null"), false, false, ItemStack.EMPTY, ItemStack.EMPTY, 
      new ResourceLocation(GreekFantasy.MODID, "null"), "none", Arrays.asList(), Arrays.asList(), Arrays.asList(), 
      Maps.newHashMap(), Maps.newHashMap());
  
  public static final Codec<Deity> CODEC = RecordCodecBuilder.create(instance -> instance.group(
      ResourceLocation.CODEC.fieldOf("name").forGetter(Deity::getName),
      Codec.BOOL.fieldOf("enabled").forGetter(Deity::isEnabled),
      Codec.BOOL.optionalFieldOf("female", false).forGetter(Deity::isFemale),
      ItemStack.CODEC.optionalFieldOf("left_hand", ItemStack.EMPTY).forGetter(Deity::getLeftHandItem),
      ItemStack.CODEC.optionalFieldOf("right_hand", ItemStack.EMPTY).forGetter(Deity::getRightHandItem),
      ResourceLocation.CODEC.optionalFieldOf("base", new ResourceLocation(GreekFantasy.MODID, "polished_marble_slab")).forGetter(Deity::getBase),
      Codec.STRING.optionalFieldOf("overlay", "none").forGetter(Deity::getOverlayString),
      FavorEffect.CODEC.listOf().optionalFieldOf("effects", Arrays.asList()).forGetter(Deity::getFavorEffects),
      TriggeredFavorEffect.CODEC.listOf().optionalFieldOf("triggered_effects", Arrays.asList()).forGetter(Deity::getTriggeredFavorEffects),
      SpecialFavorEffect.CODEC.listOf().optionalFieldOf("special_effects", Lists.newArrayList()).forGetter(Deity::getSpecialFavorEffects),
      Codec.unboundedMap(ResourceLocation.CODEC, Codec.INT).optionalFieldOf("kill_favor_map", Maps.newHashMap()).forGetter(Deity::getKillFavorModifiers),
      Codec.unboundedMap(ResourceLocation.CODEC, Codec.INT).optionalFieldOf("item_favor_map", Maps.newHashMap()).forGetter(Deity::getItemFavorModifiers)
    ).apply(instance, Deity::new));
  
  private final ResourceLocation name;
  private final ResourceLocation texture;
  private final boolean isEnabled;
  private final boolean isFemale;
  private final ItemStack leftHandItem;
  private final ItemStack rightHandItem;
  private final ResourceLocation base;
  private final ResourceLocation overlay;
  private final String overlayString;
  private final Map<ResourceLocation, Integer> killFavorMap;
  private final Map<ResourceLocation, Integer> itemFavorMap;
  private final List<FavorEffect> favorEffects;
  private final List<TriggeredFavorEffect> triggeredFavorEffects;
  private final List<SpecialFavorEffect> specialFavorEffects;

  private Deity(final ResourceLocation lName, final boolean lIsEnabled, final boolean lIsFemale, 
      final ItemStack lLeftHandItem, final ItemStack lRightHandItem, 
      final ResourceLocation lBase, final String lOverlay,
      final List<FavorEffect> lFavorEffects, final List<TriggeredFavorEffect> lTriggeredFavorEffects, 
      final List<SpecialFavorEffect> lSpecialFavorEffects, 
      final Map<ResourceLocation, Integer> lKillFavorMap, final Map<ResourceLocation, Integer> lItemFavorMap) {
    name = lName;
    texture = new ResourceLocation(lName.getNamespace(), "textures/deity/" + lName.getPath() + ".png");
    killFavorMap = ImmutableMap.copyOf(lKillFavorMap);
    itemFavorMap = ImmutableMap.copyOf(lItemFavorMap);
    favorEffects = ImmutableList.copyOf(lFavorEffects);
    triggeredFavorEffects = ImmutableList.copyOf(lTriggeredFavorEffects);
    specialFavorEffects = ImmutableList.copyOf(lSpecialFavorEffects);
    isFemale = lIsFemale;
    isEnabled = lIsEnabled;
    leftHandItem = lLeftHandItem;
    rightHandItem = lRightHandItem;
    base = lBase;
    overlayString = lOverlay;
    overlay = makeOverlay(lOverlay);
  }
  
  @Override
  public ResourceLocation getName() { return name; }

  @Override
  public ResourceLocation getTexture() { return texture; }

  @Override
  public boolean isEnabled() { return isEnabled; }
  
  @Override
  public boolean isFemale() { return isFemale; }

  @Override
  public ItemStack getRightHandItem() { return rightHandItem; }

  @Override
  public ItemStack getLeftHandItem() { return leftHandItem; }
  
  @Override
  public ResourceLocation getBase() { return base; }

  @Override
  public String getOverlayString() { return overlayString; }
  
  @Override
  public ResourceLocation getOverlay() { return overlay; }

  @Override
  public Map<ResourceLocation, Integer> getKillFavorModifiers() { return killFavorMap; }

  @Override
  public Map<ResourceLocation, Integer> getItemFavorModifiers() { return itemFavorMap; }

  @Override
  public List<FavorEffect> getFavorEffects() { return favorEffects; }
  
  @Override
  public List<TriggeredFavorEffect> getTriggeredFavorEffects() { return triggeredFavorEffects; }
  
  @Override
  public List<SpecialFavorEffect> getSpecialFavorEffects() { return specialFavorEffects; }
 
  @Override
  public String toString() {
    final StringBuilder b = new StringBuilder("Deity:");
    b.append(" name[").append(name.toString()).append("]");
    b.append(" leftHand[").append(leftHandItem.toString()).append("]");
    b.append(" rightHand[").append(rightHandItem.toString()).append("]");
    b.append(" enabled[").append(isEnabled).append("]");
    b.append(" female[").append(isFemale).append("]");
//    b.append("\nfavorEffects[").append(favorEffects.toString()).append("]");
//    b.append("\ntriggeredFavorEffects[").append(triggeredFavorEffects.toString()).append("]");
//    b.append("\nkillFavorMap[").append(killFavorMap.toString()).append("]");
//    b.append("\nitemFavorMap[").append(itemFavorMap.toString()).append("]");
    return b.toString();
  }
}
