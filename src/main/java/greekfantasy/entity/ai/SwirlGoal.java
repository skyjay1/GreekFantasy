package greekfantasy.entity.ai;

import greekfantasy.GFRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.item.BoatEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.passive.WaterMobEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.math.vector.Vector3d;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public abstract class SwirlGoal<T extends WaterMobEntity> extends Goal {

    protected final T entity;
    protected final int duration;
    protected final int cooldown;
    protected final double range;
    protected final boolean breakBoats;

    protected static final Predicate<Entity> target = (EntityPredicates.NO_CREATIVE_OR_SPECTATOR.or(e -> e instanceof BoatEntity || e instanceof ItemEntity))
            .and(e -> e.getType() != GFRegistry.EntityReg.WHIRL_ENTITY && e.canChangeDimensions() && e.isInWaterOrBubble());

    protected List<Entity> trackedEntities = new ArrayList<>();
    protected int progressTime;
    protected int cooldownTime;

    /**
     * @param lEntity     the water mob entity
     * @param lDuration   the maximum amount of time this goal will run
     * @param lCooldown   the minimum amount of time before this goal runs again
     * @param lRange      the distance at which entities should be swirled
     * @param lBreakBoats true if this goal should break boats
     **/
    public SwirlGoal(final T lEntity, final int lDuration, final int lCooldown, final double lRange,
                     final boolean lBreakBoats) {
        entity = lEntity;
        duration = lDuration;
        cooldown = lCooldown;
        range = lRange;
        cooldownTime = 60;
        breakBoats = lBreakBoats;
    }

    @Override
    public boolean canUse() {
        if (cooldownTime > 0) {
            cooldownTime--;
        } else if (entity.tickCount % 10 == 0) {
            trackedEntities = getEntitiesInRange(entity, range, e -> canSwirl(e));
            return trackedEntities.size() > 0;
        }
        return false;
    }

    @Override
    public void start() {
        progressTime = 1;
    }

    @Override
    public boolean canContinueToUse() {
        if (entity.tickCount % 11 == 0) {
            trackedEntities = getEntitiesInRange(entity, range, e -> canSwirl(e));
        }
        return progressTime > 0 && trackedEntities.size() > 0;
    }

    @Override
    public void tick() {
        // goal timer
        if (progressTime++ >= duration) {
            stop();
            return;
        }
        // move tracked entities
        for (final Entity e : trackedEntities) {
            // try to break boats
            if (e instanceof BoatEntity) {
                if (breakBoats && entity.getRandom().nextInt(8) == 0) {
                    e.hurt(DamageSource.mobAttack(entity), 3.0F);
                }
                continue;
            }
            // distance math
            double dx = entity.getX() - e.position().x;
            //double dy = entity.getPosY() - e.getPositionVec().y;
            double dz = entity.getZ() - e.position().z;
            final double horizDisSq = dx * dx + dz * dz;
            if (entity.getBoundingBox().inflate(1.0D).intersects(e.getBoundingBox())) {
                // collide with the entity
                onCollideWith(e);
            } else {
                // move the target toward this entity
                swirlEntity(entity, range, e, horizDisSq);
            }
        }
    }

    @Override
    public void stop() {
        progressTime = 0;
        cooldownTime = cooldown;
        trackedEntities.clear();
    }

    protected abstract void onCollideWith(final Entity e);

    protected abstract boolean canSwirl(final Entity e);

    protected static List<Entity> getEntitiesInRange(final WaterMobEntity entity, final double range, final Predicate<Entity> predicate) {
        return entity.getCommandSenderWorld().getEntities(entity, entity.getBoundingBox().inflate(range, range / 2, range), predicate);
    }

    protected static void swirlEntity(final Entity entity, final double range, final Entity target, final double disSq) {
        // calculate the amount of motion to apply based on distance
        final double motion = 0.062D + 0.11D * (1.0D - (disSq / (range * range)));
        final Vector3d normalVec = entity.position().multiply(1.0D, 0, 1.0D).subtract(target.position().multiply(1.0D, 0, 1.0D)).normalize();
        final Vector3d rotatedVec = normalVec.yRot(1.5707963267F).scale(motion);
        final Vector3d motionVec = target.getDeltaMovement().add(normalVec.scale(0.028D)).add(rotatedVec).multiply(0.65D, 1.0D, 0.65D);
        target.setDeltaMovement(motionVec);
        target.push(0, 0.0068D, 0);
        target.hurtMarked = true;
    }
}
