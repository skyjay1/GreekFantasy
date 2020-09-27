package greekfantasy.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.FollowMobGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public class AraEntity extends MonsterEntity {
  
  // possible names:
  // Propoetide (original)
  // Penthus (mourning)
  // Pothus (longing)
  // Thrasus (rashness)
  // Ara (curse)
  // Dysnomia (lawlessness)
  // Coalemus (stupidity)
  
  public AraEntity(final EntityType<? extends AraEntity> type, final World worldIn) {
    super(type, worldIn);
  }
  
  public static AttributeModifierMap.MutableAttribute getAttributes() {
    return MobEntity.func_233666_p_()
        .createMutableAttribute(Attributes.MAX_HEALTH, 24.0D)
        .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.25D)
        .createMutableAttribute(Attributes.ATTACK_DAMAGE, 1.0D);
  }
  
  @Override
  protected void registerGoals() {
    super.registerGoals();
    // TODO: add FollowMobGoal to travel in groups
    // from parrot:
    // this.goalSelector.addGoal(3, new FollowMobGoal(this, 1.0D, 3.0F, 7.0F));
    this.goalSelector.addGoal(5, new LookAtGoal(this, PlayerEntity.class, 6.0F));
    this.goalSelector.addGoal(6, new LookRandomlyGoal(this));
    
  }

  @Override
  public boolean canDespawn(double distanceToClosestPlayer) {
    return this.ticksExisted > 2400;
  }

}
