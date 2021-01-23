package greekfantasy.favor;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import greekfantasy.GreekFantasy;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

/**
 * This class contains information about a deity.
 * @author skyjay1
 **/
public class Deity implements IDeity {
  
  public static final Deity EMPTY = new Deity(
      new ResourceLocation(GreekFantasy.MODID, "null"), false, ItemStack.EMPTY, ItemStack.EMPTY, 
      Arrays.asList(), Arrays.asList(), Maps.newHashMap(), Maps.newHashMap());
  
  public static final Codec<Deity> CODEC = RecordCodecBuilder.create(instance -> instance.group(
      ResourceLocation.CODEC.fieldOf("name").forGetter(Deity::getName),
      Codec.BOOL.optionalFieldOf("female", false).forGetter(Deity::isFemale),
      ItemStack.CODEC.optionalFieldOf("left_hand", ItemStack.EMPTY).forGetter(Deity::getLeftHandItem),
      ItemStack.CODEC.optionalFieldOf("right_hand", ItemStack.EMPTY).forGetter(Deity::getRightHandItem),
      FavorEffect.CODEC.listOf().optionalFieldOf("effects", Arrays.asList()).forGetter(Deity::getFavorEffects),
      TriggeredFavorEffect.CODEC.listOf().optionalFieldOf("triggered_effects", Arrays.asList()).forGetter(Deity::getTriggeredFavorEffects),
      Codec.unboundedMap(ResourceLocation.CODEC, Codec.INT).optionalFieldOf("kill_favor_map", Maps.newHashMap()).forGetter(Deity::getKillFavorModifiers),
      Codec.unboundedMap(ResourceLocation.CODEC, Codec.INT).optionalFieldOf("item_favor_map", Maps.newHashMap()).forGetter(Deity::getItemFavorModifiers)
    ).apply(instance, Deity::new));
  
  public static final ResourceLocation ZEUS = new ResourceLocation(GreekFantasy.MODID, "zeus");
  public static final ResourceLocation HADES = new ResourceLocation(GreekFantasy.MODID, "hades");
  public static final ResourceLocation POSEIDON = new ResourceLocation(GreekFantasy.MODID, "poseidon");
  
  private final ResourceLocation name;
  private final ResourceLocation texture;
  private final boolean isFemale;
  private final ItemStack leftHandItem;
  private final ItemStack rightHandItem;
  private final Map<ResourceLocation, Integer> killFavorMap;
  private final Map<ResourceLocation, Integer> itemFavorMap;
  private final List<FavorEffect> favorEffects;
  private final List<TriggeredFavorEffect> triggeredFavorEffects;

  private Deity(final ResourceLocation lName,  final boolean lIsFemale, 
      final ItemStack lLeftHandItem, final ItemStack lRightHandItem, 
      final List<FavorEffect> lFavorEffects, final List<TriggeredFavorEffect> lTriggeredFavorEffects, 
      final Map<ResourceLocation, Integer> lKillFavorMap, final Map<ResourceLocation, Integer> lItemFavorMap) {
    name = lName;
    texture = new ResourceLocation(lName.getNamespace(), "textures/entity/deity/" + lName.getPath() + ".png");
    killFavorMap = ImmutableMap.copyOf(lKillFavorMap);
    itemFavorMap = ImmutableMap.copyOf(lItemFavorMap);
    favorEffects = ImmutableList.copyOf(lFavorEffects);
    triggeredFavorEffects = ImmutableList.copyOf(lTriggeredFavorEffects);
    isFemale = lIsFemale;
    leftHandItem = lLeftHandItem;
    rightHandItem = lRightHandItem;
  }
  
  @Override
  public ResourceLocation getName() { return name; }

  @Override
  public ResourceLocation getTexture() { return texture; }
  
  @Override
  public boolean isFemale() { return isFemale; }

  @Override
  public ItemStack getRightHandItem() { return rightHandItem; }

  @Override
  public ItemStack getLeftHandItem() { return leftHandItem; }

  @Override
  public Map<ResourceLocation, Integer> getKillFavorModifiers() { return killFavorMap; }

  @Override
  public Map<ResourceLocation, Integer> getItemFavorModifiers() { return itemFavorMap; }

  @Override
  public List<FavorEffect> getFavorEffects() { return favorEffects; }
  
  @Override
  public List<TriggeredFavorEffect> getTriggeredFavorEffects() { return triggeredFavorEffects; }
 
  @Override
  public String toString() {
    final StringBuilder b = new StringBuilder("Deity:");
    b.append(" name[").append(name.toString()).append("]");
    b.append(" leftHand[").append(leftHandItem.toString()).append("]");
    b.append(" rightHand[").append(rightHandItem.toString()).append("]");
    b.append(" female[").append(isFemale).append("]");
    b.append("\nfavorEffects[").append(favorEffects.toString()).append("]");
    b.append("\ntriggeredFavorEffects[").append(triggeredFavorEffects.toString()).append("]");
//    b.append("\nkillFavorMap[").append(killFavorMap.toString()).append("]");
//    b.append("\nitemFavorMap[").append(itemFavorMap.toString()).append("]");
    return b.toString();
  }
}
