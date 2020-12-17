package greekfantasy.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;

public class CyprianEntity extends CentaurEntity implements IMob {

  public CyprianEntity(final EntityType<? extends CyprianEntity> type, final World worldIn) {
    super(type, worldIn);
  }

  public static AttributeModifierMap.MutableAttribute getAttributes() {
    return MobEntity.func_233666_p_()
        .createMutableAttribute(Attributes.MAX_HEALTH, 22.0D)
        .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.25D)
        .createMutableAttribute(Attributes.ATTACK_DAMAGE, 4.0D)
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
  protected SoundEvent getAmbientSound() { return SoundEvents.ENTITY_COW_AMBIENT; }

  @Override
  protected SoundEvent getHurtSound(DamageSource damageSourceIn) { return SoundEvents.ENTITY_COW_HURT; }

  @Override
  protected SoundEvent getDeathSound() { return SoundEvents.ENTITY_COW_DEATH; }

  @Override
  protected float getSoundVolume() { return 0.8F; }
  
  @Override
  public SoundCategory getSoundCategory() { return SoundCategory.HOSTILE; }
  
  @Override
  protected boolean isDespawnPeaceful() {
    return true;
  }
  
  @Override
  public boolean hasBullHead() {
    return true;
  }
}
