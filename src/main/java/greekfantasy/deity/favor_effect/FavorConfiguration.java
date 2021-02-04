package greekfantasy.deity.favor_effect;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ibm.icu.impl.locale.XCldrStub.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import greekfantasy.GreekFantasy;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;

public class FavorConfiguration {

  public static final ResourceLocation NAME = new ResourceLocation(GreekFantasy.MODID, "favor_configuration");
  public static final FavorConfiguration EMPTY = new FavorConfiguration(Maps.newHashMap(), Maps.newHashMap(), Lists.newArrayList());
  
  public static final Codec<FavorConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
      Codec.unboundedMap(ResourceLocation.CODEC, ConfiguredFavorRange.CODEC)
        .optionalFieldOf("entity_favor_configuration", Maps.newHashMap())
        .forGetter(FavorConfiguration::getEntityTargetMap),
      Codec.unboundedMap(Codec.STRING, FavorRange.CODEC)
        .optionalFieldOf("enchantment_favor_configuration", Maps.newHashMap())
        .forGetter(FavorConfiguration::getEnchantmentMap),
      SpecialFavorEffect.CODEC.listOf().optionalFieldOf("special_favor_effects", Lists.newArrayList())
        .forGetter(FavorConfiguration::getSpecialFavorEffects)
    ).apply(instance, FavorConfiguration::new));

  private final Map<ResourceLocation, ConfiguredFavorRange> entityTargetMap;
  private final Map<String, FavorRange> enchantmentMap;
  private final List<SpecialFavorEffect> specialFavorEffectList;
  private final EnumMap<SpecialFavorEffect.Type, List<SpecialFavorEffect>> specialFavorEffectMap;
  
  public FavorConfiguration(Map<ResourceLocation, ConfiguredFavorRange> entityTargetMapIn,
      Map<String, FavorRange> enchantmentMapIn, List<SpecialFavorEffect> specialFavorEffectMapIn) {
    super();
    entityTargetMap = ImmutableMap.copyOf(entityTargetMapIn);
    enchantmentMap = ImmutableMap.copyOf(enchantmentMapIn);
    specialFavorEffectList = specialFavorEffectMapIn;
    // map the special favor effects based on type
    specialFavorEffectMap = Maps.newEnumMap(SpecialFavorEffect.Type.class);
    for(final SpecialFavorEffect effect : specialFavorEffectList) {
      if(!specialFavorEffectMap.containsKey(effect.getType())) {
        specialFavorEffectMap.put(effect.getType(), Lists.newArrayList());
      }
      specialFavorEffectMap.get(effect.getType()).add(effect);
    }
  }

  public Map<ResourceLocation, ConfiguredFavorRange> getEntityTargetMap() { return entityTargetMap; }
  
  public Map<String, FavorRange> getEnchantmentMap() { return enchantmentMap; }
  
  public List<SpecialFavorEffect> getSpecialFavorEffects() { return specialFavorEffectList; }
  
  public FavorRange getFlyingDeityRange() { return getEnchantmentMap().getOrDefault("flying_enchantment", FavorRange.EMPTY); }
  
  public FavorRange getLordOfTheSeaDeityRange() { return getEnchantmentMap().getOrDefault("lord_of_the_sea_enchantment", FavorRange.EMPTY); }

  public ConfiguredFavorRange getEntity(final EntityType<?> type) {
    return getEntityTargetMap().getOrDefault(type.getRegistryName(), ConfiguredFavorRange.EMPTY);
  }
  
  public List<SpecialFavorEffect> getSpecials(final SpecialFavorEffect.Type type) {
    return specialFavorEffectMap.getOrDefault(type, Lists.newArrayList());
  }
  
  public boolean hasEntity(final EntityType<?> type) {
    return getEntityTargetMap().containsKey(type.getRegistryName());
  }
  
  public boolean hasSpecials(final SpecialFavorEffect.Type type) {
    return specialFavorEffectMap.containsKey(type) && !specialFavorEffectMap.get(type).isEmpty();
  }

  @Override
  public String toString() {
    return "FavorConfiguration:\n" + entityTargetMap.toString() + "\n" + enchantmentMap.toString() + "\n" + specialFavorEffectList.toString();
  }
}
