package greekfantasy.entity.monster;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.Random;

public class Empusa extends Monster {

    private static final byte DRAINING_START = 4;
    private static final byte DRAINING_END = 5;

    private boolean isDraining;

    public Empusa(final EntityType<? extends Empusa> type, final Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 24.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.22D)
                .add(Attributes.ATTACK_DAMAGE, 3.0D);
    }

    public static boolean checkEmpusaSpawnRules(EntityType<? extends Monster> entityType, ServerLevelAccessor level, MobSpawnType mobSpawnType, BlockPos pos, RandomSource rand) {
        return !level.getLevel().isDay() && checkMonsterSpawnRules(entityType, level, mobSpawnType, pos, rand);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new DrainAttackGoal(this));
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.0D, false));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 0.8D));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public void aiStep() {
        super.aiStep();
        // hurt in daytime, then remove when at half health if not currently attacking
        if (!this.level.isClientSide() && this.level.isDay() && this.hurtTime == 0 && this.tickCount % 24 == 0) {
            this.hurt(DamageSource.STARVE, 2.0F);
        }

        // spawn particles
        if (level.isClientSide() && this.isDraining()) {
            particleRay();
        }
    }

    @Override
    public void handleEntityEvent(byte id) {
        switch (id) {
            case DRAINING_START:
                this.isDraining = true;
                this.playSound(SoundEvents.ENDERMAN_SCREAM, this.getSoundVolume(), this.getVoicePitch());
                break;
            case DRAINING_END:
                this.isDraining = false;
                break;
            default:
                super.handleEntityEvent(id);
                break;
        }
    }

    @Override
    public float getLightLevelDependentMagicValue() {
        return Math.min(1.0F, super.getLightLevelDependentMagicValue() + 0.5F);
    }

    public void setDraining(final boolean draining) {
        this.isDraining = draining;
        if (!this.level.isClientSide()) {
            this.level.broadcastEntityEvent(this, draining ? DRAINING_START : DRAINING_END);
        }
    }

    public boolean isDraining() {
        return this.isDraining;
    }

    /**
     * Only used client-side to draw particles from this entity to its attack target
     */
    public void particleRay() {
        Vec3 pos = this.getEyePosition(1.0F).add(0, -this.getBbHeight() * 0.15D, 0);
        Vec3 lookVec = this.getLookAngle();
        Vec3 scaled;
        for (double i = 0, l = lookVec.scale(12.0D).length(), stepSize = 0.25F; i < l; i += stepSize) {
            scaled = lookVec.scale(i);
            final double x = pos.x + scaled.x;
            final double y = pos.y + scaled.y;
            final double z = pos.z + scaled.z;
            final AABB aabb = new AABB(x - 0.1D, y - 0.1D, z - 0.1D, x + 0.1D, y + 0.1D, z + 0.1D);
            if (!this.level.getEntities(this, aabb).isEmpty()) {
                return;
            }
            this.level.addParticle(ParticleTypes.CRIT, x, y, z, 0, 0, 0);
        }
    }

    static class DrainAttackGoal extends Goal {

        private final Empusa entity;
        private final int MAX_DRAIN_TIME = 60;
        private final int HARD_DELTA_DRAIN_TIME = 40;
        private final int MAX_COOLDOWN = 180;
        private final int HARD_DELTA_COOLDOWN = -40;
        private int drainingTime;
        private int cooldown;

        protected DrainAttackGoal(final Empusa entityIn) {
            this.setFlags(EnumSet.allOf(Goal.Flag.class));
            this.entity = entityIn;
            this.cooldown = MAX_COOLDOWN / 2;
        }

        @Override
        public boolean canUse() {
            if (this.cooldown > 0) {
                cooldown--;
            } else return this.entity.getTarget() != null
                    && entity.distanceToSqr(this.entity.getTarget()) < 36.0D
                    && this.entity.getSensing().hasLineOfSight(this.entity.getTarget());
            return false;
        }

        @Override
        public boolean canContinueToUse() {
            return this.entity.isDraining() && this.entity.getTarget() != null
                    && this.entity.getSensing().hasLineOfSight(this.entity.getTarget());
        }

        @Override
        public void start() {
            this.drainingTime = MAX_DRAIN_TIME;
            if(entity.level.getCurrentDifficultyAt(entity.blockPosition()).isHard()) {
                this.drainingTime += HARD_DELTA_DRAIN_TIME;
            }
            this.entity.setDraining(true);
        }

        @Override
        public void tick() {
            if (drainingTime > 0 && this.entity.getTarget() != null) {
                drainingTime--;
                // stop the entity from moving, and adjust look vecs
                this.entity.getNavigation().stop();
                this.entity.lookAt(this.entity.getTarget(), 100.0F, 100.0F);
                // drain health from targetPos
                if (drainingTime > (MAX_DRAIN_TIME / 3) && this.entity.getTarget().hurtTime == 0) {
                    final DamageSource src = DamageSource.indirectMagic(this.entity, this.entity);
                    float amount = (float) this.entity.getAttribute(Attributes.ATTACK_DAMAGE).getValue() * 0.5F;
                    if (this.entity.getTarget().hurt(src, amount)) {
                        this.entity.heal(amount * 1.5F);
                    }
                }
            } else {
                stop();
            }
        }

        @Override
        public void stop() {
            this.entity.setDraining(false);
            this.drainingTime = 0;
            this.cooldown = MAX_COOLDOWN;
            if(entity.level.getCurrentDifficultyAt(entity.blockPosition()).isHard()) {
                this.cooldown += HARD_DELTA_COOLDOWN;
            }
        }
    }

}
