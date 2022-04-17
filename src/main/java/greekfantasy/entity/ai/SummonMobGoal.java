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
    this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    summoner = entity;
    maxProgress = summonProgressIn;
    maxCooldown = summonCooldownIn;
    mobSupplier = mob;
    count = mobCount;
    cooldown = 60;
  }
  
  @Override
  public boolean canUse() {
    if(cooldown > 0) {
      cooldown--;
    } else {
      return summoner.getTarget() != null;
    }
    return false;
  }
  
  @Override
  public void start() {
    this.progress = 1;
  }
  
  @Override
  public boolean canContinueToUse() {
    return this.progress > 0 && summoner.getTarget() != null;
  }
  
  @Override
  public void tick() {
    super.tick();
    summoner.getNavigation().stop();
    summoner.getLookControl().setLookAt(summoner.getTarget(), 100.0F, 100.0F);
    if(progress++ > maxProgress) {
      // create entity
      for(int i = 0; i < count; i++) {
        final T mobEntity = mobSupplier.create(summoner.getCommandSenderWorld());
        summonMob(mobEntity);
      }
      stop();
    }
  }
  
  @Override
  public void stop() {
    this.progress = 0;
    this.cooldown = maxCooldown;
  }
  
  protected void summonMob(final T mobEntity) {
    final float yaw = summoner.yRot;
    final float pitch = summoner.xRot;
    mobEntity.moveTo(summoner.getX(), summoner.getY() + 0.5D, summoner.getZ(), yaw, pitch);
    mobEntity.setTarget(summoner.getTarget());
    summoner.getCommandSenderWorld().addFreshEntity(mobEntity);
  }
}
