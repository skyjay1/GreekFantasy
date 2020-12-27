package greekfantasy.favor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.google.common.collect.ImmutableList;
import com.ibm.icu.impl.locale.XCldrStub.ImmutableMap;

import greekfantasy.GreekFantasy;
import greekfantasy.tileentity.AltarTileEntity;
import greekfantasy.util.StatuePose;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.HandSide;
import net.minecraft.util.ResourceLocation;

/**
 * This class contains information about a deity.
 * Create using the {@link Deity.Builder}
 * @author skyjay1
 **/
public class Deity implements IDeity {
  
  private final ResourceLocation name;
  private final ResourceLocation texture;
  private final Map<ResourceLocation, Integer> killFavorMap;
  private final Map<Item, Integer> itemFavorMap;
  private final List<IFavorEffect> favorEffects;
  private final Consumer<AltarTileEntity> initAltar;

  public Deity(final ResourceLocation lName, final ResourceLocation lTexture,
      final Map<ResourceLocation, Integer> lKillFavorMap, final Map<Item, Integer> lItemFavorMap,
      final List<IFavorEffect> lFavorEffects, final Consumer<AltarTileEntity> lInitAltar) {
    name = lName;
    texture = lTexture;
    killFavorMap = ImmutableMap.copyOf(lKillFavorMap);
    itemFavorMap = ImmutableMap.copyOf(lItemFavorMap);
    favorEffects = ImmutableList.copyOf(lFavorEffects);
    initAltar = lInitAltar;
  }
  
  @Override
  public ResourceLocation getName() { return name; }

  @Override
  public ResourceLocation getTexture() { return texture; }

  @Override
  public Map<ResourceLocation, Integer> getKillFavorModifiers() { return killFavorMap; }

  @Override
  public Map<Item, Integer> getItemFavorModifiers() { return itemFavorMap; }

  @Override
  public List<IFavorEffect> getFavorEffects() { return favorEffects; }

  @Override
  public Consumer<AltarTileEntity> initAltar() { return initAltar; }
  
  @Override
  public int hashCode() {
    return name.hashCode();
  }

  @Override
  public boolean equals(final Object other) {
    if (this == other) {
      return true;
    } else if (!(other instanceof IDeity)) {
      return false;
    } else {
      IDeity ideity = (IDeity) other;
      return name.equals(ideity.getName());
    }
  }
  
  public static class Builder {
    private final ResourceLocation name;
    private ResourceLocation texture;
    private final Map<ResourceLocation, Integer> killFavorMap = new HashMap<>();
    private final Map<Item, Integer> itemFavorMap = new HashMap<>();
    private final List<IFavorEffect> favorEffects = new ArrayList<>();
    private Consumer<AltarTileEntity> initAltar = e -> {};
    
    public Builder(final String modid, final String name) {
      this(new ResourceLocation(modid, name));
    }
    
    public Builder(final ResourceLocation id) {
      name = id;
      texture = new ResourceLocation(id.getNamespace(), "textures/entity/deity/" + id.getPath() + ".png");
    }
    
    /**
     * Adds a favor modifier when the given entity is killed
     * @param entity the entity type
     * @param the favor modifier (positive or negative)
     * @return instance to allow chaining of methods
     */
    public Builder addEntity(final EntityType<?> entity, int favor) {
      killFavorMap.put(entity.getRegistryName(), Integer.valueOf(favor));
      return this;
    }
    
    /**
     * Adds a favor modifier when the given item is sacrificed
     * @param item the item
     * @param the favor modifier (positive or negative)
     * @return instance to allow chaining of methods
     */
    public Builder addItem(final Item item, int favor) {
      itemFavorMap.put(item, Integer.valueOf(favor));
      return this;
    }
    
    /**
     * Adds a favor effect to the list
     * @param effect
     * @return instance to allow chaining of methods
     */
    public Builder addEffect(final IFavorEffect effect) {
      favorEffects.add(effect);
      return this;
    }
    
    /**
     * Sets the altar texture
     * @param textureIn a ResourceLocation of the texture to use
     * @return instance to allow chaining of methods
     */
    public Builder setTexture(final ResourceLocation textureIn) {
      texture = textureIn;
      return this;
    }
    
    /**
     * Sets the altar model as female
     * @return instance to allow chaining of methods
     */
    public Builder setFemale() {
      initAltar = initAltar.andThen(e -> e.setStatueFemale(true));
      return this;
    }
    
    /**
     * Sets the altar pose
     * @param pose the statue pose
     * @return instance to allow chaining of methods
     */
    public Builder setPose(final StatuePose pose) {
      initAltar = initAltar.andThen(e -> e.setStatuePose(pose));
      return this;
    }
    
    /**
     * Sets an item for the altar to hold
     * @param item the item
     * @param side the hand side (right or left)
     * @return instance to allow chaining of methods
     */
    public Builder setHeldItem(final ItemStack item, final HandSide side) {
      initAltar = initAltar.andThen(e -> e.setItem(item, side));
      return this;
    }
    
    /**
     * @return the fully built Deity
     */
    public Deity build() {
      return new Deity(name, texture, killFavorMap, itemFavorMap, favorEffects, initAltar);
    }
  }





}
