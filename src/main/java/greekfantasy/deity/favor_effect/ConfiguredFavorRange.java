package greekfantasy.deity.favor_effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import greekfantasy.deity.favor.FavorRange;

public class ConfiguredFavorRange {

  public static final ConfiguredFavorRange EMPTY = new ConfiguredFavorRange(FavorRange.EMPTY, FavorRange.EMPTY);
  
  public static final Codec<ConfiguredFavorRange> CODEC = RecordCodecBuilder.create(instance -> instance.group(
      FavorRange.CODEC.optionalFieldOf("hostile", FavorRange.EMPTY).forGetter(ConfiguredFavorRange::getHostileRange),
      FavorRange.CODEC.optionalFieldOf("flee", FavorRange.EMPTY).forGetter(ConfiguredFavorRange::getFleeRange)
    ).apply(instance, ConfiguredFavorRange::new));

  private final FavorRange hostileRange;
  private final FavorRange fleeRange;
  
  public ConfiguredFavorRange(FavorRange hostileRangeIn, FavorRange fleeRangeIn) {
    hostileRange = hostileRangeIn;
    fleeRange = fleeRangeIn;
  }

  public FavorRange getHostileRange() { return hostileRange; }
  
  public FavorRange getFleeRange() { return fleeRange; }
  
  public boolean hasHostileRange() { return hostileRange != FavorRange.EMPTY; }
  
  public boolean hasFleeRange() { return fleeRange != FavorRange.EMPTY; }

  @Override
  public String toString() {
    return "ConfiguredFeature: hostile[" + hostileRange.toString() + "] flee[" + fleeRange.toString() + "]";
  }
}
