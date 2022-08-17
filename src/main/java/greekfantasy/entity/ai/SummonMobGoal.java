package greekfantasy.entity.ai;


import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class SummonMobGoal<T extends Mob> extends Goal {
    protected final PathfinderMob summoner;
    protected final EntityType<? extends T> mobEntityType;
    protected final int maxDuration;
    protected final int maxCooldown;
    protected final int count;

    /**
     * Counts up from 0, up to maxDuration, at which point
     * the mobs are summoned and the goal stops
     */
    protected int progressTimer;
    /**
     * Counts down from maxCooldown. When it reaches 0,
     * the goal can run
     */
    protected int cooldown;

    /**
     * @param entity the entity
     * @param duration the number of ticks it takes to prepare the goal
     * @param cooldown the number of ticks to wait before the goal can execute again
     * @param entityType the entity type of the mob to summon
     */
    public SummonMobGoal(final PathfinderMob entity, final int duration, final int cooldown,
                         final EntityType<? extends T> entityType) {
        this(entity, duration, cooldown, entityType, 1);
    }

    public SummonMobGoal(final PathfinderMob entity, final int duration, final int cooldown,
                         final EntityType<? extends T> entityType, final int mobCount) {
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        this.summoner = entity;
        this.maxDuration = duration;
        this.maxCooldown = cooldown;
        this.mobEntityType = entityType;
        this.count = mobCount;
        this.cooldown = 100;
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public boolean canUse() {
        if (cooldown > 0) {
            cooldown--;
        } else {
            return summoner.getTarget() != null;
        }
        return false;
    }

    @Override
    public void start() {
        this.progressTimer = 1;
    }

    @Override
    public boolean canContinueToUse() {
        return this.progressTimer > 0 && canUse();
    }

    @Override
    public void tick() {
        super.tick();
        summoner.getNavigation().stop();
        summoner.getLookControl().setLookAt(summoner.getTarget(), 100.0F, 100.0F);
        if (progressTimer++ > maxDuration) {
            // create entity
            for (int i = 0; i < count; i++) {
                T mobEntity = summonMob();
                onSummonMob(mobEntity);
            }
            stop();
        }
    }

    @Override
    public void stop() {
        this.progressTimer = 0;
        this.cooldown = maxCooldown;
    }

    protected T summonMob() {
        final T mobEntity = mobEntityType.create(summoner.level);
        mobEntity.copyPosition(summoner);
        mobEntity.setLastHurtByMob(summoner.getLastHurtByMob());
        mobEntity.setTarget(summoner.getTarget());
        summoner.level.addFreshEntity(mobEntity);
        return mobEntity;
    }

    protected void onSummonMob(final T mobEntity) { }
}
