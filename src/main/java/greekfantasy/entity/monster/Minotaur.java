package greekfantasy.entity.monster;

import greekfantasy.GFRegistry;
import greekfantasy.GreekFantasy;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
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
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class Minotaur extends Monster {
    protected static final EntityDataAccessor<Byte> STATE = SynchedEntityData.defineId(Minotaur.class, EntityDataSerializers.BYTE);
    protected static final String KEY_STATE = "MinotaurState";
    //bytes to use in STATE
    protected static final byte NONE = (byte) 0;
    protected static final byte CHARGING = (byte) 1;
    protected static final byte STUNNED = (byte) 2;

    protected static final int STUN_DURATION = 80;

    protected final AttributeModifier knockbackModifier = new AttributeModifier("Charge knockback bonus", 2.25F, AttributeModifier.Operation.MULTIPLY_TOTAL);
    protected final AttributeModifier knockbackResistanceModifier = new AttributeModifier("Charge knockback resistance bonus", 1.0F, AttributeModifier.Operation.ADDITION);
    protected final AttributeModifier attackModifier = new AttributeModifier("Charge attack bonus", 2.5F, AttributeModifier.Operation.MULTIPLY_TOTAL);

    public Minotaur(final EntityType<? extends Minotaur> type, final Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 24.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.26D)
                .add(Attributes.ATTACK_DAMAGE, 3.5D)
                .add(Attributes.ATTACK_KNOCKBACK, 1.25D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.57D)
                .add(Attributes.ARMOR, 2.0D);
    }

    @Override
    public void defineSynchedData() {
        super.defineSynchedData();
        this.getEntityData().define(STATE, NONE);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new StunnedGoal());
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(5, new MeleeAttackGoal(this, 1.0D, false));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 0.8D));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        registerChargeGoal();
    }

    protected void registerChargeGoal() {
        this.goalSelector.addGoal(2, new ChargeAttackGoal(2.78D));
    }

    @Override
    public void aiStep() {
        super.aiStep();

        // spawn particles
        if (level.isClientSide() && this.isStunned()) {
            spawnStunnedParticles();
        }
    }

    @Override
    public boolean canAttackType(EntityType<?> entityType) {
        return entityType != GFRegistry.EntityReg.MINOTAUR.get()
                && entityType != GFRegistry.EntityReg.CRETAN_MINOTAUR.get()
                && super.canAttackType(entityType);
    }

    // Sound methods

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.COW_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.COW_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.COW_DEATH;
    }

    @Override
    protected float getSoundVolume() {
        return 0.8F;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        this.playSound(SoundEvents.COW_STEP, 0.15F, 1.0F);
    }

    @Override
    public boolean hurt(final DamageSource source, final float amount) {
        if (this.isEffectiveAi()) {
            this.setCharging(false);
            this.setStunned(false);
        }
        return super.hurt(source, amount);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putByte(KEY_STATE, this.getMinotaurState());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setMinotaurState(compound.getByte(KEY_STATE));
    }

    // States

    public byte getMinotaurState() {
        return this.getEntityData().get(STATE);
    }

    public void setMinotaurState(final byte state) {
        this.getEntityData().set(STATE, state);
    }

    public boolean isNoneState() {
        return getMinotaurState() == NONE;
    }

    public boolean isCharging() {
        return getMinotaurState() == CHARGING;
    }

    public boolean isStunned() {
        return getMinotaurState() == STUNNED;
    }

    public void setCharging(final boolean charging) {
        setMinotaurState(charging ? CHARGING : NONE);
        if(!this.level.isClientSide()) {
            // determine if knockback resistance modifier was added
            AttributeInstance knockbackResist = this.getAttribute(Attributes.KNOCKBACK_RESISTANCE);
            boolean hasModifier = knockbackResist.hasModifier(knockbackResistanceModifier);
            if(!charging && hasModifier) {
                // remove modifier when no longer charging
                knockbackResist.removeModifier(knockbackResistanceModifier);
            } else if (charging && !hasModifier) {
                // add modifier when charging
                knockbackResist.addTransientModifier(knockbackResistanceModifier);
            }
        }
    }

    public void setStunned(final boolean stunned) {
        setMinotaurState(stunned ? STUNNED : NONE);
    }

    public void spawnStunnedParticles() {
        final double motion = 0.09D;
        final double radius = 0.7D;
        for (int i = 0; i < 2; i++) {
            level.addParticle(ParticleTypes.INSTANT_EFFECT,
                    this.getX() + (level.random.nextDouble() - 0.5D) * radius,
                    this.getEyeY() + (level.random.nextDouble() - 0.5D) * radius * 0.75D,
                    this.getZ() + (level.random.nextDouble() - 0.5D) * radius,
                    (level.random.nextDouble() - 0.5D) * motion,
                    (level.random.nextDouble() - 0.5D) * motion * 0.5D,
                    (level.random.nextDouble() - 0.5D) * motion);
        }
    }

    public void applyChargeAttack(final LivingEntity target) {
        // temporarily increase knockback attack
        this.getAttribute(Attributes.ATTACK_DAMAGE).addTransientModifier(attackModifier);
        this.getAttribute(Attributes.ATTACK_KNOCKBACK).addTransientModifier(this.knockbackModifier);
        this.doHurtTarget(target);
        this.getAttribute(Attributes.ATTACK_DAMAGE).removeModifier(attackModifier);
        this.getAttribute(Attributes.ATTACK_KNOCKBACK).removeModifier(this.knockbackModifier);
        // apply stunned effect
        if (GreekFantasy.CONFIG.STUNNED_NERF.get()) {
            target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, STUN_DURATION, 0));
            target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, STUN_DURATION, 0));
        } else {
            target.addEffect(new MobEffectInstance(GFRegistry.MobEffectReg.STUNNED.get(), STUN_DURATION, 0));
        }
    }

    class StunnedGoal extends Goal {

        private final int maxStunTime = 50;
        private int stunTime;

        protected StunnedGoal() {
            this.setFlags(EnumSet.allOf(Flag.class));
        }

        @Override
        public boolean canUse() {
            return Minotaur.this.isStunned();
        }

        @Override
        public void start() {
            this.stunTime = 1;
            Minotaur.this.addEffect(new MobEffectInstance(GFRegistry.MobEffectReg.STUNNED.get(), maxStunTime - 1));
        }

        @Override
        public void tick() {
            if (stunTime > 0 && stunTime < maxStunTime) {
                stunTime++;
                Minotaur.this.getNavigation().stop();
                Minotaur.this.getLookControl().setLookAt(Minotaur.this, 0, 0);
            } else {
                stop();
            }
        }

        @Override
        public void stop() {
            Minotaur.this.setStunned(false);
            this.stunTime = 0;
        }
    }

    public class ChargeAttackGoal extends Goal {

        private final int maxCooldown = 200;
        private final int maxCharging = 40;
        private final double minRange = 2.5D;
        private final double speed;

        private int chargingTimer;
        private int cooldown = maxCooldown;
        private Vec3 targetPos;

        public ChargeAttackGoal(final double speedIn) {
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
            speed = speedIn;
            targetPos = null;
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public boolean canUse() {
            // this method returns true only when the cooldown is 0 and the other conditions are met
            if (this.cooldown > 0) {
                cooldown--;
            } else return Minotaur.this.isNoneState() && Minotaur.this.getTarget() != null
                    && !Minotaur.this.getMoveControl().hasWanted() && hasDirectPath(Minotaur.this.getTarget())
                    && Minotaur.this.distanceToSqr(Minotaur.this.getTarget()) > (minRange * minRange);
            return false;
        }

        @Override
        public boolean canContinueToUse() {
            return (Minotaur.this.isCharging() && Minotaur.this.getTarget() != null
                    && Minotaur.this.getTarget().isAlive()) && hasDirectPath(Minotaur.this.getTarget());
        }

        @Override
        public void start() {
            Minotaur.this.setCharging(true);
            this.chargingTimer = 1;
        }

        @Override
        public void tick() {
            LivingEntity target = Minotaur.this.getTarget();
            final double disSqToTargetEntity = Minotaur.this.distanceToSqr(target);
            final boolean hitTarget = disSqToTargetEntity < 1.1D;
            final boolean hasTarget = targetPos != null;
            final boolean finished = hitTarget || (hasTarget && Minotaur.this.distanceToSqr(targetPos) < 0.9D);
            final boolean isCharging = chargingTimer > 0 && chargingTimer++ < maxCharging;
            if (finished) {
                // if charge attack hit the player
                if (hitTarget) {
                    Minotaur.this.applyChargeAttack(target);
                } else {
                    Minotaur.this.setStunned(true);
                }
                // reset values
                stop();
            } else if (isCharging) {
                // prevent the entity from moving while preparing to charge attack
                Minotaur.this.getNavigation().stop();
                Minotaur.this.getLookControl().setLookAt(target.getEyePosition(1.0F));
            } else if (hasTarget) {
                // continue moving toward the target that was set earlier
                Minotaur.this.getMoveControl().setWantedPosition(targetPos.x, targetPos.y, targetPos.z, speed);
                Minotaur.this.getLookControl().setLookAt(targetPos.add(0, target.getEyeHeight(), 0));

            } else {
                // determine where the charge attack should target
                this.targetPos = getExtendedTarget(target, disSqToTargetEntity + 16.0D);
            }
        }

        @Override
        public void stop() {
            if (Minotaur.this.isCharging()) {
                Minotaur.this.setCharging(false);
            }
            this.chargingTimer = 0;
            this.cooldown = maxCooldown;
            this.targetPos = null;
        }

        /**
         * Extends a direct path along a straight line until it reaches max length or intersects a block.
         * @param targetEntity the target entity
         * @param maxDistanceSq the maximum length of the path
         * @return the position of the block at the end of the path
         */
        private Vec3 getExtendedTarget(final LivingEntity targetEntity, final double maxDistanceSq) {
            Vec3 start = Minotaur.this.position().add(0, 0.1D, 0);
            Vec3 target = targetEntity.position().add(0, 0.1D, 0);
            Vec3 end = target;
            Vec3 vecDiff = end.subtract(start);
            double length = vecDiff.length();
            vecDiff = vecDiff.normalize();
            // repeatedly scale the vector
            do {
                target = end;
                end = start.add(vecDiff.scale(++length));
            } while ((length * length) < maxDistanceSq && hasDirectPath(end));
            // the vector has either reached max length, or encountered a block
            return target;
        }

        /**
         * @return whether there is an unobstructed straight path from the entity to the target entity
         **/
        private boolean hasDirectPath(final LivingEntity target) {
            return hasDirectPath(target.position().add(0, 0.1D, 0));
        }

        /**
         * @return whether there is an unobstructed straight path from the entity to the target position
         **/
        private boolean hasDirectPath(final Vec3 target) {
            Vec3 start = Minotaur.this.position().add(0, 0.1D, 0);
            return Minotaur.this.level.clip(new ClipContext(start, target, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, Minotaur.this)).getType() == HitResult.Type.MISS;
        }
    }
}
