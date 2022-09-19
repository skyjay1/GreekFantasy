package greekfantasy.entity.ai;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;

public abstract class SwirlGoal extends Goal {

    protected static final Predicate<Entity> CAN_TARGET = e -> e.isInWaterOrBubble()
            && !e.isSpectator() && !(e instanceof Player p && p.isCreative())
            && (e instanceof LivingEntity || e instanceof Boat || e instanceof ItemEntity);

    protected final LivingEntity entity;
    protected final int duration;
    protected final int cooldown;
    protected final double range;
    protected final float strength;
    protected final boolean breakBoats;
    protected final Predicate<Entity> targetPredicate;

    protected List<Entity> trackedEntities = new ArrayList<>();
    protected int progressTime;
    protected int cooldownTime;

    /**
     * @param entity     the water mob entity
     * @param duration   the maximum amount of time this goal will run
     * @param cooldown   the minimum amount of time before this goal runs again
     * @param range      the distance at which entities should be swirled
     * @param deltaAngle   determines the radius of the swirl
     * @param breakBoats true if this goal should break boats
     * @param targetPredicate additional predicate for entities to target
     **/
    public SwirlGoal(final LivingEntity entity, final int duration, final int cooldown, final double range,
                     final float deltaAngle, final boolean breakBoats, final Predicate<Entity> targetPredicate) {
        this.setFlags(EnumSet.noneOf(Goal.Flag.class));
        this.entity = entity;
        this.duration = duration;
        this.cooldown = cooldown;
        this.range = range;
        this.strength = deltaAngle;
        this.cooldownTime = 60;
        this.breakBoats = breakBoats;
        this.targetPredicate = CAN_TARGET.and(targetPredicate);
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public boolean canUse() {
        if (cooldownTime > 0) {
            cooldownTime--;
        } else {
            trackedEntities = getEntitiesInRange(entity, range, this.targetPredicate);
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
            trackedEntities = getEntitiesInRange(entity, range, this.targetPredicate);
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
            if (e instanceof Boat) {
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
                swirlEntity(entity, range, strength, e, horizDisSq);
            }
        }
    }

    @Override
    public void stop() {
        if(cooldown > 0) {
            progressTime = 0;
            cooldownTime = cooldown;
            trackedEntities.clear();
        } else {
            // restart progress without stopping this goal
            progressTime = 1;
        }
    }

    protected abstract void onCollideWith(final Entity e);


    protected static List<Entity> getEntitiesInRange(final LivingEntity entity, final double range, final Predicate<Entity> predicate) {
        return entity.level.getEntities(entity, entity.getBoundingBox().inflate(range, range / 2, range), predicate);
    }

    protected static void swirlEntity(final Entity entity, final double range, final float deltaAngle, final Entity target, final double disSq) {
        // calculate the amount of motion to apply based on distance
        final double motion = 0.062D + 0.11D * (1.0D - (disSq / (range * range)));
        final Vec3 normalVec = entity.position().multiply(1.0D, 0, 1.0D).subtract(target.position().multiply(1.0D, 0, 1.0D)).normalize();
        final Vec3 rotatedVec = normalVec.yRot(1.5707963267F - deltaAngle).scale(motion);
        final Vec3 motionVec = target.getDeltaMovement().add(normalVec.scale(0.028D)).add(rotatedVec).multiply(0.65D, 1.0D, 0.65D);
        target.setDeltaMovement(motionVec);
        target.push(0, 0.0068D, 0);
        target.hurtMarked = true;
        target.hasImpulse = true;
    }
}
