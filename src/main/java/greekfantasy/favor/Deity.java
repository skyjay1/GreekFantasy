package greekfantasy.favor;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.ibm.icu.impl.locale.XCldrStub.ImmutableMap;
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
      new ResourceLocation(GreekFantasy.MODID, "null"),
      Maps.newHashMap(), Maps.newHashMap(), Arrays.asList(), 
      false, ItemStack.EMPTY, ItemStack.EMPTY);
  
  public static final Codec<Deity> CODEC = RecordCodecBuilder.create(instance -> instance.group(
      ResourceLocation.CODEC.fieldOf("name").forGetter(Deity::getName),
      Codec.unboundedMap(ResourceLocation.CODEC, Codec.INT).fieldOf("kill_favor_map").forGetter(Deity::getKillFavorModifiers),
      Codec.unboundedMap(ResourceLocation.CODEC, Codec.INT).fieldOf("item_favor_map").forGetter(Deity::getItemFavorModifiers),
      FavorEffect.CODEC.listOf().fieldOf("effects").forGetter(Deity::getFavorEffects),
      Codec.BOOL.fieldOf("female").forGetter(Deity::isFemale),
      ItemStack.CODEC.fieldOf("left_hand").forGetter(Deity::getLeftHandItem),
      ItemStack.CODEC.fieldOf("right_hand").forGetter(Deity::getRightHandItem)
    ).apply(instance, Deity::new));
  
  private final ResourceLocation name;
  private final ResourceLocation texture;
  private final Map<ResourceLocation, Integer> killFavorMap;
  private final Map<ResourceLocation, Integer> itemFavorMap;
  private final List<FavorEffect> favorEffects;
  private final boolean isFemale;
  private final ItemStack leftHandItem;
  private final ItemStack rightHandItem;

  private Deity(final ResourceLocation lName, final Map<ResourceLocation, Integer> lKillFavorMap, 
      final Map<ResourceLocation, Integer> lItemFavorMap,
      final List<FavorEffect> lFavorEffects, final boolean lIsFemale, 
      final ItemStack lLeftHandItem, final ItemStack lRightHandItem) {
    name = lName;
    texture = new ResourceLocation(lName.getNamespace(), "textures/entity/deity/" + lName.getPath() + ".png");
    killFavorMap = ImmutableMap.copyOf(lKillFavorMap);
    itemFavorMap = ImmutableMap.copyOf(lItemFavorMap);
    favorEffects = ImmutableList.copyOf(lFavorEffects);
    isFemale = lIsFemale;
    leftHandItem = lLeftHandItem;
    rightHandItem = lRightHandItem;
  }
  
  @Override
  public ResourceLocation getName() { return name; }

  @Override
  public ResourceLocation getTexture() { return texture; }

  @Override
  public Map<ResourceLocation, Integer> getKillFavorModifiers() { return killFavorMap; }

  @Override
  public Map<ResourceLocation, Integer> getItemFavorModifiers() { return itemFavorMap; }

  @Override
  public List<FavorEffect> getFavorEffects() { return favorEffects; }
  
  @Override
  public boolean isFemale() { return isFemale; }

  @Override
  public ItemStack getRightHandItem() { return rightHandItem; }

  @Override
  public ItemStack getLeftHandItem() { return leftHandItem; }
  
  @Override
  public String toString() {
    final StringBuilder b = new StringBuilder("Deity:");
    b.append(" name[").append(name.toString()).append("]");
//    b.append(" female[").append(isFemale).append("]");
//    b.append(" leftHand[").append(leftHandItem.toString()).append("]");
//    b.append(" rightHand[").append(rightHandItem.toString()).append("]");
//    b.append("\nfavorEffects[").append(favorEffects.toString()).append("]");
//    b.append("\nkillFavorMap[").append(killFavorMap.toString()).append("]");
//    b.append("\nitemFavorMap[").append(itemFavorMap.toString()).append("]");
    return b.toString();
  }
}
