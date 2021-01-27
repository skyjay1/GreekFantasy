package greekfantasy.favor;

import java.util.Map;

import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import greekfantasy.GreekFantasy;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;

public class FavorRangeTarget {

  public static final ResourceLocation NAME = new ResourceLocation(GreekFantasy.MODID, "favor_range_target_criteria");
  public static final FavorRangeTarget EMPTY = new FavorRangeTarget(Maps.newHashMap());
  
  public static final Codec<FavorRangeTarget> CODEC = RecordCodecBuilder.create(instance -> instance.group(
      Codec.unboundedMap(ResourceLocation.CODEC, FavorRange.CODEC).optionalFieldOf("entity_target_map", Maps.newHashMap()).forGetter(FavorRangeTarget::getEntityTargetMap)
    ).apply(instance, FavorRangeTarget::new));

  private final Map<ResourceLocation, FavorRange> entityTargetMap;
  
  public FavorRangeTarget(Map<ResourceLocation, FavorRange> entityTargetMapIn) {
    super();
    entityTargetMap = entityTargetMapIn;
  }

  public Map<ResourceLocation, FavorRange> getEntityTargetMap() { return entityTargetMap; }

  public FavorRange get(final EntityType<?> type) {
    return getEntityTargetMap().getOrDefault(type.getRegistryName(), FavorRange.EMPTY);
  }
  
  public boolean has(final EntityType<?> type) {
    return getEntityTargetMap().containsKey(type.getRegistryName());
  }

  @Override
  public String toString() {
    return entityTargetMap.toString();
  }
}
