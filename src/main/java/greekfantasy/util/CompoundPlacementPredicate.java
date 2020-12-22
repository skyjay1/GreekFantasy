package greekfantasy.util;

import java.util.List;
import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IServerWorld;

public class CompoundPlacementPredicate<T extends Entity> implements EntitySpawnPlacementRegistry.IPlacementPredicate<T> {

  private final EntitySpawnPlacementRegistry.IPlacementPredicate<T> placementPredicate;
  private final boolean whitelist;
  private final List<String> dimensions;
  
  public CompoundPlacementPredicate(final boolean whitelisted, final List<String> dims, final EntitySpawnPlacementRegistry.IPlacementPredicate<T> placement) {
    whitelist = whitelisted;
    dimensions = dims;
    placementPredicate = placement;
  }
  
  @Override
  public boolean test(final EntityType<T> entity, final IServerWorld world, final SpawnReason reason, 
      final BlockPos pos, final Random rand) {
    final String dim = world.getWorld().getDimensionKey().getLocation().toString();
    if(dimensions.contains(dim) == whitelist) {
      return placementPredicate.test(entity, world, reason, pos, rand);
    }
    return false;
  }

}
