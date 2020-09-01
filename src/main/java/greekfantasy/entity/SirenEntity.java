package greekfantasy.entity;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.world.World;

public class SirenEntity extends CreatureEntity {
  
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
  
}
