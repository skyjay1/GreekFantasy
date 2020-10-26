package greekfantasy.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public class CyprianEntity extends CentaurEntity implements IMob {

  public CyprianEntity(final EntityType<? extends CyprianEntity> type, final World worldIn) {
    super(type, worldIn);
  }

  public static AttributeModifierMap.MutableAttribute getAttributes() {
    return MobEntity.func_233666_p_()
        .createMutableAttribute(Attributes.MAX_HEALTH, 24.0D)
        .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.25D)
        .createMutableAttribute(Attributes.ATTACK_DAMAGE, 1.0D)
        .createMutableAttribute(Attributes.KNOCKBACK_RESISTANCE, 0.5D);
  }

  @Override
  protected void registerGoals() {
    super.registerGoals();
    this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
    // this goal has a custom predicate to make sure the centaur entity is not also a cyprian entity (don't attack teammates)
    this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, CentaurEntity.class, 10, true, false, e -> e.getClass() == CentaurEntity.class));
  }
  
  @Override
  protected boolean isDespawnPeaceful() {
    return true;
  }
  
  @Override
  public boolean hasBullHead() {
    return true;
  }
}
