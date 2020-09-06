package greekfantasy.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.world.World;

public class CyclopesEntity extends GiganteEntity {
  
  public CyclopesEntity(final EntityType<? extends CyclopesEntity> type, final World worldIn) {
    super(type, worldIn);
  }

  public static AttributeModifierMap.MutableAttribute getAttributes() {
    return GiganteEntity.getAttributes();
  }

  @Override
  protected void registerGoals() {
    super.registerGoals();
  }
}
