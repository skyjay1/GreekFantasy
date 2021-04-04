package greekfantasy.deity.favor_effect;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import greekfantasy.GreekFantasy;
import greekfantasy.deity.Deity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;

public class FavorConfiguration {

  public static final ResourceLocation NAME = new ResourceLocation(GreekFantasy.MODID, "favor_configuration");
  public static final FavorConfiguration EMPTY = new FavorConfiguration(Maps.newHashMap(), Maps.newHashMap());
  
  public static final String FLYING_RANGE = "flying_enchantment";
  public static final String LORD_OF_THE_SEA_RANGE = "lord_of_the_sea_enchantment";
  public static final String FIREFLASH_RANGE = "fireflash_enchantment";
  public static final String DAYBREAK_RANGE = "daybreak_enchantment";
  public static final String RAISING_RANGE = "raising_enchantment";
  public static final String APOLLO_BOW_RANGE = "apollo_bow";
  public static final String ARTEMIS_BOW_RANGE = "artemis_bow";
  
  public static final Codec<FavorConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
      Codec.unboundedMap(ResourceLocation.CODEC, ConfiguredFavorRange.CODEC)
        .optionalFieldOf("entity_favor_configuration", Maps.newHashMap())
        .forGetter(FavorConfiguration::getEntityTargetMap),
      Codec.unboundedMap(Codec.STRING, FavorRange.CODEC)
        .optionalFieldOf("enchantment_favor_configuration", Maps.newHashMap())
        .forGetter(FavorConfiguration::getEnchantmentMap)
    ).apply(instance, FavorConfiguration::new));

  private final Map<ResourceLocation, ConfiguredFavorRange> entityTargetMap;
  private final Map<String, FavorRange> enchantmentMap;
  private final EnumMap<SpecialFavorEffect.Type, List<ConfiguredSpecialFavorEffect>> specialFavorEffectMap;
  
  public FavorConfiguration(Map<ResourceLocation, ConfiguredFavorRange> entityTargetMapIn,
      Map<String, FavorRange> enchantmentMapIn) {
    super();
    entityTargetMap = ImmutableMap.copyOf(entityTargetMapIn);
    enchantmentMap = ImmutableMap.copyOf(enchantmentMapIn);
    // map the special favor effects based on type
    specialFavorEffectMap = Maps.newEnumMap(SpecialFavorEffect.Type.class);
  }

  public Map<ResourceLocation, ConfiguredFavorRange> getEntityTargetMap() { return entityTargetMap; }
  
  public Map<String, FavorRange> getEnchantmentMap() { return enchantmentMap; }
    
  public FavorRange getSpecialRange(final String name) { return getEnchantmentMap().getOrDefault(name, FavorRange.EMPTY); }

  public ConfiguredFavorRange getEntity(final EntityType<?> type) {
    return getEntityTargetMap().getOrDefault(type.getRegistryName(), ConfiguredFavorRange.EMPTY);
  }
  
  public List<ConfiguredSpecialFavorEffect> getSpecials(final SpecialFavorEffect.Type type) {
    // get or create the list
    if(!specialFavorEffectMap.containsKey(type)) {
      // iterate through each deity to populate the list
      final List<ConfiguredSpecialFavorEffect> list = new ArrayList<>();
      for(final Optional<Deity> deity : GreekFantasy.PROXY.DEITY.getValues()) {
        deity.ifPresent(d -> list.addAll(d.getSpecialFavorEffects(type)) );
      }
      // add the list to the map
      specialFavorEffectMap.put(type, list);
    }
    return specialFavorEffectMap.get(type);
  }
  
  public boolean hasEntity(final EntityType<?> type) {
    return getEntityTargetMap().containsKey(type.getRegistryName());
  }
  
//  public boolean hasSpecials(final SpecialFavorEffect.Type type) {
//    return specialFavorEffectMap.containsKey(type) && !specialFavorEffectMap.get(type).isEmpty();
//  }

  @Override
  public String toString() {
    return "FavorConfiguration:\n" + entityTargetMap.toString() + "\n" + enchantmentMap.toString() + "\n" + specialFavorEffectMap.toString();
  }
}
