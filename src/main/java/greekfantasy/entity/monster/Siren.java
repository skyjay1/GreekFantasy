package greekfantasy.entity.monster;

import greekfantasy.GFRegistry;
import greekfantasy.GreekFantasy;
import greekfantasy.entity.ai.GoToWaterGoal;
import greekfantasy.entity.ai.SwimUpGoal;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.control.SmoothSwimmingLookControl;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.animal.Pufferfish;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;

import java.util.EnumSet;

public class Siren extends WaterAnimal implements Enemy {

    private static final EntityDataAccessor<Boolean> CHARMING = SynchedEntityData.defineId(Siren.class, EntityDataSerializers.BOOLEAN);
    private final AttributeModifier attackModifier = new AttributeModifier("Charm attack bonus", 2.0D, AttributeModifier.Operation.MULTIPLY_TOTAL);

    private static final int STUN_DURATION = 80;

    protected EntityDimensions swimmingDimensions;

    public Siren(final EntityType<? extends Siren> type, final Level worldIn) {
        super(type, worldIn);
        this.moveControl = new SirenMoveControl(this);
        this.lookControl = new SmoothSwimmingLookControl(this, 10);
        this.swimmingDimensions = EntityDimensions.scalable(0.6F, 0.6F);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 24.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.ATTACK_DAMAGE, 3.0D)
                .add(ForgeMod.STEP_HEIGHT_ADDITION.get(), 0.6D);
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        return new WaterBoundPathNavigation(this, level);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new GoToWaterGoal(this, 1.0D, false));
        this.goalSelector.addGoal(2, new Siren.CharmAttackGoal(250, 100, 12));
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.1D, false));
        this.goalSelector.addGoal(4, new SwimUpGoal(this, 1.0D, this.level.getSeaLevel(), -1));
        this.goalSelector.addGoal(5, new AvoidEntityGoal<>(this, Pufferfish.class, 10.0F, 1.2D, 1.0D));
        this.goalSelector.addGoal(6, new RandomSwimmingGoal(this, 0.9D, 140));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public void defineSynchedData() {
        super.defineSynchedData();
        this.getEntityData().define(CHARMING, Boolean.FALSE);
    }

    @Override
    public void aiStep() {
        super.aiStep();

        // singing
        if (this.isCharming() && random.nextInt(8) == 0) {
            final float color = 0.065F + random.nextFloat() * 0.025F;
            this.playSound(SoundEvents.NOTE_BLOCK_CHIME, 1.8F, color * 15);
            level.addParticle(ParticleTypes.NOTE, this.getX(), this.getEyeY() + 0.15D, this.getZ(), color, 0.0D, 0.0D);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.getDeltaMovement().horizontalDistanceSqr() > 0.0012D) {
            this.setPose(Pose.SWIMMING);
        } else if (this.getPose() == Pose.SWIMMING) {
            this.setPose(Pose.STANDING);
        }
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        return (pose == Pose.SWIMMING) ? swimmingDimensions : super.getDimensions(pose);
    }

    @Override
    public void updateSwimming() {
        if (!this.level.isClientSide) {
            if (this.isEffectiveAi()/* && this.isInWater()*/ && this.wantsToSwim()) {
                this.setSwimming(true);
            } else {
                this.setSwimming(false);
            }
        }
    }

    // Swimming methods

    @Override
    protected void handleAirSupply(int air) {
        // do nothing
    }

    @Override
    public void travel(final Vec3 vec) {
        if (isEffectiveAi() && isInWater() && wantsToSwim()) {
            moveRelative(0.01F, vec);
            move(MoverType.SELF, getDeltaMovement());
            setDeltaMovement(getDeltaMovement().scale(0.9D));
        } else {
            super.travel(vec);
        }
    }

    @Override
    public boolean isPushedByFluid() {
        return !isSwimming();
    }

    boolean wantsToSwim() {
        LivingEntity livingentity = this.getTarget();
        return null == livingentity || livingentity.isInWater();
    }

    // Charming methods

    public void setCharming(final boolean isCharming) {
        this.getEntityData().set(CHARMING, isCharming);
    }

    public boolean isCharming() {
        return this.getEntityData().get(CHARMING);
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        boolean charming = isCharming();
        if (charming) {
            this.getAttribute(Attributes.ATTACK_DAMAGE).addTransientModifier(attackModifier);
        }
        boolean success = super.doHurtTarget(target);
        if (charming) {
            this.getAttribute(Attributes.ATTACK_DAMAGE).removeModifier(attackModifier);
        }
        // add mob effect
        if (charming && success && target instanceof LivingEntity livingEntity) {
            if (GreekFantasy.CONFIG.STUNNED_NERF.get()) {
                livingEntity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, STUN_DURATION, 0));
                livingEntity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, STUN_DURATION, 0));
            } else {
                livingEntity.addEffect(new MobEffectInstance(GFRegistry.MobEffectReg.STUNNED.get(), STUN_DURATION, 0));
            }
        }
        return success;
    }

    // Move control

    static class SirenMoveControl extends MoveControl {
        private final Siren siren;

        public SirenMoveControl(Siren entity) {
            super(entity);
            this.siren = entity;
        }

        public void tick() {
            LivingEntity livingentity = this.siren.getTarget();
            if (this.siren.wantsToSwim() && this.siren.isInWater()) {
                if (livingentity != null && livingentity.getY() > this.siren.getY()) {
                    this.siren.setDeltaMovement(this.siren.getDeltaMovement().add(0.0D, 0.002D, 0.0D));
                }

                if (this.operation != MoveControl.Operation.MOVE_TO || this.siren.getNavigation().isDone()) {
                    this.siren.setSpeed(0.0F);
                    return;
                }

                double d0 = this.wantedX - this.siren.getX();
                double d1 = this.wantedY - this.siren.getY();
                double d2 = this.wantedZ - this.siren.getZ();
                double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
                d1 /= d3;
                float f = (float) (Mth.atan2(d2, d0) * (double) (180F / (float) Math.PI)) - 90.0F;
                this.siren.setYRot(this.rotlerp(this.siren.getYRot(), f, 90.0F));
                this.siren.yBodyRot = this.siren.getYRot();
                float f1 = (float) (this.speedModifier * this.siren.getAttributeValue(Attributes.MOVEMENT_SPEED));
                float f2 = Mth.lerp(0.125F, this.siren.getSpeed(), f1);
                this.siren.setSpeed(f2);
                this.siren.setDeltaMovement(this.siren.getDeltaMovement().add((double) f2 * d0 * 0.05D, (double) f2 * d1 * 0.1D, (double) f2 * d2 * 0.05D));
            } else {
                if (!this.siren.onGround) {
                    this.siren.setDeltaMovement(this.siren.getDeltaMovement().add(0.0D, -0.008D, 0.0D));
                }

                super.tick();
            }
        }
    }

    // Charm attack goal

    class CharmAttackGoal extends Goal {

        protected final int maxDuration;
        protected final int maxCooldown;
        protected final float range;

        protected int duration;
        protected int cooldown;

        public CharmAttackGoal(final int duration, final int cooldown, final int range) {
            this.setFlags(EnumSet.noneOf(Goal.Flag.class));
            this.duration = 0;
            this.maxDuration = duration;
            this.maxCooldown = cooldown;
            this.cooldown = 60;
            this.range = range;
        }

        @Override
        public boolean canUse() {
            if (cooldown > 0) {
                cooldown--;
            } else {
                return Siren.this.getTarget() != null && Siren.this.closerThan(Siren.this.getTarget(), range);
            }
            return false;
        }

        @Override
        public void start() {
            Siren.this.setCharming(true);
            this.duration = maxDuration;
        }

        @Override
        public boolean canContinueToUse() {
            return this.duration > 0 && Siren.this.getTarget() != null
                    && Siren.this.closerThan(Siren.this.getTarget(), range);
        }

        @Override
        public void tick() {
            super.tick();
            if (duration-- > 0) {
                // determine target
                final LivingEntity target = Siren.this.getTarget();
                // apply swim speed slowdown
                target.addEffect(new MobEffectInstance(GFRegistry.MobEffectReg.SLOW_SWIM.get(), maxDuration, 2));
            } else {
                stop();
            }
        }

        @Override
        public void stop() {
            this.duration = 0;
            this.cooldown = maxCooldown;
            Siren.this.setCharming(false);
        }
    }
}
