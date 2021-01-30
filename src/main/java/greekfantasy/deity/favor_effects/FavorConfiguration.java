package greekfantasy.deity.favor_effects;

import java.util.Map;

import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import greekfantasy.GreekFantasy;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;

public class FavorConfiguration {

  public static final ResourceLocation NAME = new ResourceLocation(GreekFantasy.MODID, "favor_configuration");
  public static final FavorConfiguration EMPTY = new FavorConfiguration(Maps.newHashMap());
  
  public static final Codec<FavorConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
      Codec.unboundedMap(ResourceLocation.CODEC, ConfiguredFavorRange.CODEC).optionalFieldOf("entity_favor_configuration", Maps.newHashMap()).forGetter(FavorConfiguration::getEntityTargetMap)
    ).apply(instance, FavorConfiguration::new));

  private final Map<ResourceLocation, ConfiguredFavorRange> entityTargetMap;
  
  public FavorConfiguration(Map<ResourceLocation, ConfiguredFavorRange> entityTargetMapIn) {
    super();
    entityTargetMap = entityTargetMapIn;
  }

  public Map<ResourceLocation, ConfiguredFavorRange> getEntityTargetMap() { return entityTargetMap; }

  public ConfiguredFavorRange get(final EntityType<?> type) {
    return getEntityTargetMap().getOrDefault(type.getRegistryName(), ConfiguredFavorRange.EMPTY);
  }
  
  public boolean has(final EntityType<?> type) {
    return getEntityTargetMap().containsKey(type.getRegistryName());
  }

  @Override
  public String toString() {
    return entityTargetMap.toString();
  }
}
