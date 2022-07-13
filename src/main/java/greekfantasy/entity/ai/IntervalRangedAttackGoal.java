package greekfantasy.entity.ai;


import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.RangedAttackMob;

import java.util.EnumSet;

public class IntervalRangedAttackGoal<T extends Mob & RangedAttackMob> extends Goal {
    protected T entity;
    protected int goalTime;
    protected int maxTime;
    protected int maxCooldown;
    protected int interval;
    protected int cooldown;

    public IntervalRangedAttackGoal(final T entityIn, final int duration, final int count, final int maxCooldownIn) {
        this.setFlags(EnumSet.allOf(Goal.Flag.class));
        this.entity = entityIn;
        this.maxTime = duration;
        this.maxCooldown = maxCooldownIn;
        this.cooldown = 30;
        this.interval = Math.floorDiv(Math.max(count, duration - 20), count);
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
        return goalTime > 0 && entity.getTarget() != null
                && entity.getSensing().hasLineOfSight(entity.getTarget())
                && isWithinRange(entity.getTarget());
    }

    @Override
    public void start() {
        goalTime = 1;
        entity.setAggressive(true);
    }

    @Override
    public void tick() {
        // stop the entity from moving, and adjust look vecs
        final LivingEntity target = entity.getTarget();
        entity.getNavigation().stop();
        entity.lookAt(target, 100.0F, 100.0F);
        entity.getLookControl().setLookAt(target, 100.0F, 100.0F);
        // spit attack on interval
        if (goalTime % interval == 0) {
            entity.performRangedAttack(target, 0.1F);
        }
        // finish the spit attack
        if (goalTime++ >= maxTime) {
            stop();
        }
    }

    @Override
    public void stop() {
        entity.setAggressive(false);
        this.goalTime = 0;
        this.cooldown = maxCooldown;
    }

    protected boolean isWithinRange(final LivingEntity target) {
        if (target != null) {
            final double disSq = entity.distanceToSqr(target);
            final double maxRange = entity.getAttributeValue(Attributes.FOLLOW_RANGE) * 0.75D;
            return disSq > 9.0D && disSq < (maxRange * maxRange);
        }
        return false;
    }
}
