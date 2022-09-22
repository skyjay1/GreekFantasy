package greekfantasy.entity.ai;

import greekfantasy.entity.Triton;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.Dolphin;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class DolphinTemptByTritonGoal extends Goal {

    protected final Dolphin entity;
    protected final double speedModifier;
    protected final Ingredient ingredient;
    protected final int maxCooldown = 250;

    protected Triton triton;
    protected int cooldown;

    public DolphinTemptByTritonGoal(Dolphin dolphin, double speedModifier, Ingredient ingredient) {
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        this.entity = dolphin;
        this.speedModifier = speedModifier;
        this.ingredient = ingredient;
        this.cooldown = maxCooldown / 2;
    }

    @Override
    public boolean canUse() {
        // check cooldown
        if (cooldown-- > 0) {
            return false;
        }
        // ensure entity is in water and idle
        if (!entity.isInWaterOrBubble() || entity.getTarget() != null && !entity.gotFish() && entity.getAirSupply() > 200) {
            return false;
        }
        // locate nearest triton
        triton = entity.level.getNearestEntity(Triton.class, TargetingConditions.forNonCombat()
                        .selector(t -> ingredient.test(t.getOffhandItem()) && t.isInWaterOrBubble()),
                entity, entity.getX(), entity.getY(), entity.getZ(), entity.getBoundingBox().inflate(10.0D));
        if (null == triton || triton.getTarget() != null) {
            return false;
        }
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        // check entity conditions
        if (!entity.isInWaterOrBubble() || entity.getTarget() != null || entity.getNavigation().isStuck() || entity.getAirSupply() < 200) {
            return false;
        }
        // check triton conditions
        if (null == triton || !triton.isAlive() || !triton.isInWaterOrBubble()
                || triton.getTarget() != null || !ingredient.test(triton.getOffhandItem())) {
            return false;
        }
        // check random condition
        if (entity.getRandom().nextInt(300) == 0) {
            return false;
        }
        // all checks passed
        return true;
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return triton != null || cooldown > 0;
    }

    @Override
    public void tick() {
        // ensure triton exists
        if (null == triton) {
            stop();
            return;
        }
        // move entity to triton
        double disSq = entity.distanceToSqr(triton);
        if (disSq < 6.25D) {
            entity.getNavigation().stop();
            // look at triton (oscillating)
            double oscillatingY = Mth.cos(entity.tickCount * 0.25F) * 1.1F;
            Vec3 lookPos = triton.getEyePosition().add(0.0D, oscillatingY, 0.0D);
            entity.getLookControl().setLookAt(lookPos);
        } else {
            entity.getNavigation().moveTo(triton.getX(), triton.getEyeY(), triton.getZ(), speedModifier);
            entity.lookAt(triton, 100.0F, 100.0F);
        }
    }

    @Override
    public void stop() {
        entity.getNavigation().stop();
        cooldown = maxCooldown + entity.getRandom().nextInt(maxCooldown) / 4;
        triton = null;
    }
}
