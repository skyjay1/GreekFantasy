package greekfantasy.entity.ai;


import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class ShootFireGoal extends Goal {
    private final Mob entity;
    private final int maxCooldown;
    private final int maxShootFireTime;
    private final double fireRange;

    private int fireBreathingTime;
    private int cooldown;

    protected ShootFireGoal(final Mob entityIn, final int fireTimeIn, final int maxCooldownIn, final double fireRangeIn) {
        this.setFlags(EnumSet.allOf(Goal.Flag.class));
        this.entity = entityIn;
        this.maxShootFireTime = fireTimeIn;
        this.fireRange = fireRangeIn;
        this.maxCooldown = maxCooldownIn;
        this.cooldown = 30;
    }

    @Override
    public boolean canUse() {
        if (this.cooldown > 0) {
            cooldown--;
        } else return entity.getTarget() != null
                && entity.distanceToSqr(entity.getTarget()) < (fireRange * fireRange)
                && entity.getSensing().hasLineOfSight(entity.getTarget());
        return false;
    }

    @Override
    public boolean canContinueToUse() {
        return entity.getTarget() != null
                && entity.getSensing().hasLineOfSight(entity.getTarget())
                && entity.distanceToSqr(entity.getTarget()) < (fireRange * fireRange);
    }

    @Override
    public void start() {
        this.fireBreathingTime = 1;
        entity.playSound(SoundEvents.CREEPER_PRIMED, 1.0F, 1.2F);
    }

    @Override
    public void tick() {
        if (fireBreathingTime > 0 && fireBreathingTime < maxShootFireTime) {
            fireBreathingTime++;
            // stop the entity from moving, and adjust look vecs
            entity.getNavigation().stop();
            entity.lookAt(entity.getTarget(), 100.0F, 100.0F);
            entity.getLookControl().setLookAt(entity.getTarget(), 100.0F, 100.0F);
            // set fire to targetPos
            if (fireBreathingTime > 18 && fireBreathingTime % 7 == 0) {
                final Vec3 entityPos = entity.getEyePosition();
                setFireToIntersectingEntities(entityPos, entity.getTarget().position(), 0.65D, 5);
                entity.playSound(SoundEvents.FIRECHARGE_USE, 1.0F, 1.0F);
            }
        } else {
            stop();
        }
    }

    @Override
    public void stop() {
        this.fireBreathingTime = 0;
        this.cooldown = maxCooldown;
    }

    /**
     * Sets fire to all entities along a raytrace given the start and end positions
     *
     * @param startPos the starting position
     * @param endPos   the ending position
     * @param radius   the radius around each point in the ray to check for entities
     * @param fireTime the amount of time to set fire to the entity
     **/
    private void setFireToIntersectingEntities(final Vec3 startPos, final Vec3 endPos, final double radius, final int fireTime) {
        Vec3 vecDifference = endPos.subtract(startPos);
        Vec3 scaled;
        AABB aabb;
        int randFireTime = fireTime + entity.getRandom().nextInt(5) - 2;
        // step along the vector created by adding the start position and the difference vector
        for (double i = 0.1, l = vecDifference.length(), stepSize = radius * 0.75D; i < l; i += stepSize) {
            scaled = startPos.add(vecDifference.scale(i));
            // make a box at this position along the vector
            aabb = new AABB(scaled.x - radius, scaled.y - radius, scaled.z - radius, scaled.x + radius, scaled.y + radius, scaled.z + radius);
            for (final Entity e : entity.level.getEntities(entity, aabb)) {
                // set fire to any entities inside the box
                e.setSecondsOnFire(randFireTime);
            }
        }
    }
}
