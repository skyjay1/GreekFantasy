package greekfantasy.entity.ai;

import java.util.EnumSet;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.Goal;

public class SummonMobGoal<T extends MobEntity> extends Goal {
  protected final MobEntity summoner;
  protected final EntityType<T> mobSupplier;
  protected final int maxProgress;
  protected final int maxCooldown;
  protected final int count;
  
  protected int progress;
  protected int cooldown;
  
  public SummonMobGoal(final MobEntity entity, final int summonProgressIn, final int summonCooldownIn,
      final EntityType<T> mob) {
    this(entity, summonProgressIn, summonCooldownIn, mob, 1);
  }
  
  public SummonMobGoal(final MobEntity entity, final int summonProgressIn, final int summonCooldownIn,
      final EntityType<T> mob, final int mobCount) {
    this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
    summoner = entity;
    maxProgress = summonProgressIn;
    maxCooldown = summonCooldownIn;
    mobSupplier = mob;
    count = mobCount;
    cooldown = 60;
  }
  
  @Override
  public boolean shouldExecute() {
    if(cooldown > 0) {
      cooldown--;
    } else {
      return summoner.getAttackTarget() != null;
    }
    return false;
  }
  
  @Override
  public void startExecuting() {
    this.progress = 1;
  }
  
  @Override
  public boolean shouldContinueExecuting() {
    return this.progress > 0 && summoner.getAttackTarget() != null;
  }
  
  @Override
  public void tick() {
    super.tick();
    summoner.getNavigator().clearPath();
    summoner.getLookController().setLookPositionWithEntity(summoner.getAttackTarget(), 100.0F, 100.0F);
    if(progress++ > maxProgress) {
      // create entity
      for(int i = 0; i < count; i++) {
        final T mobEntity = mobSupplier.create(summoner.getEntityWorld());
        summonMob(mobEntity);
      }
      resetTask();
    }
  }
  
  @Override
  public void resetTask() {
    this.progress = 0;
    this.cooldown = maxCooldown;
  }
  
  protected void summonMob(final T mobEntity) {
    final float yaw = summoner.rotationYaw;
    final float pitch = summoner.rotationPitch;
    mobEntity.setLocationAndAngles(summoner.getPosX(), summoner.getPosY() + 0.5D, summoner.getPosZ(), yaw, pitch);
    mobEntity.setAttackTarget(summoner.getAttackTarget());
    summoner.getEntityWorld().addEntity(mobEntity);
  }
}
