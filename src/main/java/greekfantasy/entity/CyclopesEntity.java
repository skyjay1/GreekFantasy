package greekfantasy.entity;

import java.util.Random;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class CyclopesEntity extends GiganteEntity {
  
  public CyclopesEntity(final EntityType<? extends CyclopesEntity> type, final World worldIn) {
    super(type, worldIn);
  }

  public static AttributeModifierMap.MutableAttribute getAttributes() {
    return GiganteEntity.getAttributes();
  }
  
  public static boolean canCyclopesSpawnOn(final EntityType<? extends MobEntity> entity, final IWorld world, final SpawnReason reason, 
      final BlockPos pos, final Random rand) {
    // TODO refine
    return GiganteEntity.canGiganteSpawnOn(entity, world, reason, pos, rand);
  }

  @Override
  protected void registerGoals() {
    super.registerGoals();
  }
}
