package greekfantasy.entity.ai;


import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class SummonMobGoal<T extends Mob> extends Goal {
    protected final PathfinderMob summoner;
    protected final EntityType<T> mobSupplier;
    protected final int maxDuration;
    protected final int maxCooldown;
    protected final int count;

    /**
     * Counts up from 0, up to maxDuration, at which point
     * the mobs are summoned and the goal stops
     */
    protected int progress;
    /**
     * Counts down from maxCooldown. When it reaches 0,
     * the goal can run
     */
    protected int cooldown;

    public SummonMobGoal(final PathfinderMob entity, final int duration, final int cooldown,
                         final EntityType<T> mob) {
        this(entity, duration, cooldown, mob, 1);
    }

    public SummonMobGoal(final PathfinderMob entity, final int duration, final int cooldown,
                         final EntityType<T> mob, final int mobCount) {
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        this.summoner = entity;
        this.maxDuration = duration;
        this.maxCooldown = cooldown;
        this.mobSupplier = mob;
        this.count = mobCount;
        this.cooldown = 100;
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
        if (progress++ > maxDuration) {
            // create entity
            for (int i = 0; i < count; i++) {
                final T mobEntity = mobSupplier.create(summoner.level);
                onSummonMob(mobEntity);
            }
            stop();
        }
    }

    @Override
    public void stop() {
        this.progress = 0;
        this.cooldown = maxCooldown;
    }

    protected void onSummonMob(final T mobEntity) {
        final float yaw = summoner.getYRot();
        final float pitch = summoner.getXRot();
        mobEntity.moveTo(summoner.getX(), summoner.getY() + 0.5D, summoner.getZ(), yaw, pitch);
        mobEntity.setTarget(summoner.getTarget());
        summoner.level.addFreshEntity(mobEntity);
    }
}
