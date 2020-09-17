package greekfantasy.entity;

import java.util.Objects;
import java.util.Optional;
import java.util.Random;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.passive.DolphinEntity;
import net.minecraft.entity.passive.WaterMobEntity;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;

public class SirenEntity extends WaterMobEntity {
  
  // hostile version should "pull" or "attract" players irresistibly (kinda) while singing

  public SirenEntity(final EntityType<? extends SirenEntity> type, final World worldIn) {
    super(type, worldIn);
  }
  
  @Override
  protected void registerGoals() {
    super.registerGoals();
  }

  public static AttributeModifierMap.MutableAttribute getAttributes() {
    return MobEntity.func_233666_p_()
        .createMutableAttribute(Attributes.MAX_HEALTH, 24.0D)
        .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.25D)
        .createMutableAttribute(Attributes.ATTACK_DAMAGE, 1.0D);
  }

  // copied from DolphinEntity
  public static boolean canSirenSpawnOn(final EntityType<? extends WaterMobEntity> entity, final IWorld world, final SpawnReason reason, 
      final BlockPos pos, final Random rand) {
    if (pos.getY() <= 45 || pos.getY() >= world.getSeaLevel()) {
      return false;
    }

    Optional<RegistryKey<Biome>> biome = world.func_242406_i(pos);
    return ((!Objects.equals(biome, Optional.of(Biomes.OCEAN)) || !Objects.equals(biome, Optional.of(Biomes.DEEP_OCEAN)))
        && world.getFluidState(pos).isTagged(FluidTags.WATER));
  }

}
