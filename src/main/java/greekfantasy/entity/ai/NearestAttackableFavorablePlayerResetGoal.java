package greekfantasy.entity.ai;

import java.util.EnumSet;

import greekfantasy.GreekFantasy;
import greekfantasy.deity.favor_effect.ConfiguredFavorRange;
import net.minecraft.entity.IAngerable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;

public class NearestAttackableFavorablePlayerResetGoal extends Goal {

  protected MobEntity entity;
  protected int interval;
  
  public NearestAttackableFavorablePlayerResetGoal(final MobEntity entityIn) { this(entityIn, 10); }
  
  public NearestAttackableFavorablePlayerResetGoal(final MobEntity entityIn, int intervalIn) {
    this.setFlags(EnumSet.of(Goal.Flag.TARGET));
    entity = entityIn;
    interval = intervalIn;
  }

  @Override
  public boolean canUse() {
    final LivingEntity target = entity.getTarget();
    if(entity.tickCount % interval == 0 && entity.isAlive() && target instanceof PlayerEntity
        && target.getCapability(GreekFantasy.FAVOR).orElse(GreekFantasy.FAVOR.getDefaultInstance()).isEnabled() 
        && target != entity.getLastHurtByMob() 
        && !(entity instanceof IAngerable && target.getUUID().equals(((IAngerable)entity).getPersistentAngerTarget()))) {
      // reset target if it is not in the favor range
      ConfiguredFavorRange range = GreekFantasy.PROXY.getFavorConfiguration().getEntity(entity.getType());
      return range.hasHostileRange() && !range.getHostileRange().isInFavorRange((PlayerEntity)target);
    }
    return false;
  }
  
  @Override
  public boolean canContinueToUse() { return false; }
  
  @Override
  public void start() {
    entity.setTarget(null);
  }
}

