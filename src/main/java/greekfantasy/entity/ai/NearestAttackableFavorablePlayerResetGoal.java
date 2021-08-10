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
    this.setMutexFlags(EnumSet.of(Goal.Flag.TARGET));
    entity = entityIn;
    interval = intervalIn;
  }

  @Override
  public boolean shouldExecute() {
    final LivingEntity target = entity.getAttackTarget();
    if(entity.ticksExisted % interval == 0 && entity.isAlive() && target instanceof PlayerEntity
        && target.getCapability(GreekFantasy.FAVOR).orElse(GreekFantasy.FAVOR.getDefaultInstance()).isEnabled() 
        && target != entity.getRevengeTarget() 
        && !(entity instanceof IAngerable && target.getUniqueID().equals(((IAngerable)entity).getAngerTarget()))) {
      // reset target if it is not in the favor range
      ConfiguredFavorRange range = GreekFantasy.PROXY.getFavorConfiguration().getEntity(entity.getType());
      return range.hasHostileRange() && !range.getHostileRange().isInFavorRange((PlayerEntity)target);
    }
    return false;
  }
  
  @Override
  public boolean shouldContinueExecuting() { return false; }
  
  @Override
  public void startExecuting() {
    entity.setAttackTarget(null);
  }
}

