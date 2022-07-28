package greekfantasy.entity.ai;


import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.RangedAttackMob;

import java.util.EnumSet;

public class IntervalRangedAttackGoal<T extends Mob & RangedAttackMob> extends Goal {
    protected final T entity;
    protected final int maxDuration;
    protected final int maxCooldown;
    protected final int interval;
    protected int progressTimer;
    protected int cooldown;

    /**
     * @param entityIn the entity
     * @param interval the number of ticks between ranged attacks
     * @param count the number of ranged attacks before cooldown begins
     * @param maxCooldownIn the maximum cooldown for this attack
     */
    public IntervalRangedAttackGoal(final T entityIn, final int interval, final int count, final int maxCooldownIn) {
        this.setFlags(EnumSet.allOf(Goal.Flag.class));
        this.entity = entityIn;
        this.interval = Math.max(1, interval);
        this.maxDuration = interval * count;
        this.maxCooldown = maxCooldownIn;
        this.cooldown = 30;
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public boolean canUse() {
        if (this.cooldown > 0) {
            cooldown--;
        } else return entity.getTarget() != null
                && entity.getSensing().hasLineOfSight(entity.getTarget())
                && isWithinRange(entity.getTarget());
        return false;
    }

    @Override
    public boolean canContinueToUse() {
        return progressTimer > 0 && entity.getTarget() != null
                && entity.getSensing().hasLineOfSight(entity.getTarget())
                && isWithinRange(entity.getTarget());
    }

    @Override
    public void start() {
        progressTimer = 1;
        entity.setAggressive(true);
    }

    @Override
    public void tick() {
        // stop the entity from moving, and adjust look vecs
        final LivingEntity target = entity.getTarget();
        entity.getNavigation().stop();
        entity.lookAt(target, 100.0F, 100.0F);
        entity.getLookControl().setLookAt(target, 100.0F, 100.0F);
        // ranged attack on interval
        if (progressTimer % interval == 0) {
            entity.performRangedAttack(target, 0.1F);
        }
        // increase timer and finish goal when timer reaches max
        if (progressTimer++ > maxDuration) {
            stop();
        }
    }

    @Override
    public void stop() {
        entity.setAggressive(false);
        this.progressTimer = 0;
        this.cooldown = maxCooldown;
    }

    protected boolean isWithinRange(final LivingEntity target) {
        if (target != null) {
            final double disSq = entity.distanceToSqr(target);
            final double maxRange = entity.getAttributeValue(Attributes.FOLLOW_RANGE) * 0.85D;
            return disSq > 4.0D && disSq < (maxRange * maxRange);
        }
        return false;
    }
}
