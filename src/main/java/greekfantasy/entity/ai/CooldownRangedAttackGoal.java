package greekfantasy.entity.ai;

import greekfantasy.entity.util.HasCustomCooldown;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.monster.RangedAttackMob;

public class CooldownRangedAttackGoal<T extends PathfinderMob & HasCustomCooldown & RangedAttackMob> extends RangedAttackGoal {
    protected final T entity;
    protected final int cooldown;

    public CooldownRangedAttackGoal(T entity, int interval, float attackDistance, final int cooldown) {
        super(entity, 1.0F, interval, attackDistance);
        this.entity = entity;
        this.cooldown = cooldown;
    }

    @Override
    public boolean canUse() {
        final LivingEntity target = entity.getTarget();
        double disSq = (target != null) ? entity.distanceToSqr(target) : 0.0D;
        return entity.hasNoCustomCooldown() && disSq > 9.0D && super.canUse();
    }

    @Override
    public void start() {
        super.start();
        entity.setCustomCooldown(this.cooldown);
    }

    @Override
    public void tick() {
        super.tick();
    }
}
