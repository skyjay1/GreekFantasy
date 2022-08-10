package greekfantasy.entity.monster;

import greekfantasy.entity.misc.BronzeFeather;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowMobGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomFlyingGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class Stymphalian extends Monster implements FlyingAnimal, RangedAttackMob {
    private float flyingTime0;
    private float flyingTime;
    protected boolean isFlyingUp;

    public Stymphalian(final EntityType<? extends Stymphalian> type, final Level level) {
        super(type, level);
        this.moveControl = new FlyingMoveControl(this, 10, true);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 10.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.26D)
                .add(Attributes.ATTACK_DAMAGE, 2.0D)
                .add(Attributes.FLYING_SPEED, 1.35D)
                .add(Attributes.ARMOR, 6.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.32D);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new Stymphalian.FlyingRangedAttackGoal(this, 1.0D, 60, 80, 10.0F));
        //this.goalSelector.addGoal(1, new RangedAttackGoal(this, 1.0D, 60, 80, 10.0F));
        this.goalSelector.addGoal(3, new FollowMobGoal(this, 1.0D, 6.0F, 12.0F) {
            @Override
            public boolean canUse() {
                return Stymphalian.this.getRandom().nextInt(70) == 0 && super.canUse();
            }
        });
        this.goalSelector.addGoal(4, new WaterAvoidingRandomFlyingGoal(this, 0.9D) {
            @Override
            public boolean canUse() {
                return Stymphalian.this.getRandom().nextInt(20) == 0 && super.canUse();
            }
        });
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Wolf.class, true));
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        FlyingPathNavigation flyingpathnavigator = new FlyingPathNavigation(this, level);
        flyingpathnavigator.setCanOpenDoors(false);
        flyingpathnavigator.setCanFloat(true);
        flyingpathnavigator.setCanPassDoors(true);
        return flyingpathnavigator;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        // update falling moveSpeed
        Vec3 m = getDeltaMovement();
        if (this.isEffectiveAi() && !this.onGround && m.y() > 0) {
            setDeltaMovement(m.add(0.0D, -0.01D, 0.0D));
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level.isClientSide()) {
            // update flying counter
            flyingTime0 = flyingTime;
            if (this.isFlying()) {
                flyingTime = Math.min(1.0F, flyingTime + 0.09F);
            } else {
                flyingTime = Math.max(0.0F, flyingTime - 0.09F);
            }
        }
    }

    @Override
    public void travel(Vec3 vec3) {
        super.travel(vec3);
        if (this.isFlyingUp) {
            this.move(MoverType.SELF, new Vec3(0, 0.032D, 0));
        } else if (this.isFlying()) {
            this.move(MoverType.SELF, new Vec3(0, -0.030, 0));
        }
    }

    // Sounds

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.PARROT_IMITATE_PHANTOM;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.PARROT_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.PARROT_DEATH;
    }

    @Override
    protected float getSoundVolume() {
        return 1.0F;
    }

    @Override
    public float getVoicePitch() {
        return 1.0F + random.nextFloat() * 0.2F;
    }

    // Flying

    @Override
    public boolean causeFallDamage(float distance, float damageMultiplier, DamageSource source) {
        return false;
    }

    @Override
    protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
    }

    @Override
    public boolean isFlying() {
        return !isOnGround() || this.getDeltaMovement().lengthSqr() > 0.06D;
    }

    public float getFlyingTime(final float partialTick) {
        return Mth.clamp(Mth.lerp(partialTick, flyingTime0, flyingTime), 0.0F, 1.0F);
    }

    // Ranged Attack

    @Override
    public void performRangedAttack(LivingEntity target, float distanceFactor) {
        if (!level.isClientSide()) {
            BronzeFeather bronzeFeather = BronzeFeather.create(level, this);
            double dx = target.getX() - bronzeFeather.getX();
            double dy = target.getY(0.67D) - bronzeFeather.getY();
            double dz = target.getZ() - bronzeFeather.getZ();
            double dis = Math.sqrt(dx * dx + dz * dz);
            bronzeFeather.shoot(dx, dy + dis * (double) 0.02F, dz, 1.6F, (float) (14 - this.level.getDifficulty().getId() * 4));
            this.level.addFreshEntity(bronzeFeather);
        }
        this.playSound(SoundEvents.TRIDENT_THROW, 1.2F, 1.2F + this.random.nextFloat() * 0.2F);
    }

    @Override
    public boolean isInvulnerableTo(DamageSource damageSource) {
        if (damageSource.isProjectile() && damageSource.getDirectEntity() instanceof BronzeFeather) {
            return true;
        }
        return super.isInvulnerableTo(damageSource);
    }

    static class FlyingRangedAttackGoal extends RangedAttackGoal {
        protected final Stymphalian entity;

        /**
         * @param entity the entity
         */
        public FlyingRangedAttackGoal(Stymphalian entity, double speedModifier, int attackIntervalMin, int attackIntervalMax, float maxDistance) {
            super(entity, speedModifier, attackIntervalMin, attackIntervalMax, maxDistance);
            this.entity = entity;
        }

        @Override
        public void start() {
            super.start();
            this.entity.setAggressive(true);
        }

        @Override
        public void tick() {
            super.tick();
            if (this.entity.getTarget() != null) {
                LivingEntity target = this.entity.getTarget();
                Vec3 targetPos = target.position();
                double dy = this.entity.position().y() - (targetPos.y() + target.getDimensions(target.getPose()).height);
                double desiredHeight = 1.0F + 1.5F * ((this.entity.getId() % 16) / 16.0D);
                this.entity.isFlyingUp = (dy < desiredHeight);
            }
        }

        @Override
        public void stop() {
            super.stop();
            this.entity.isFlyingUp = false;
            this.entity.setAggressive(false);
        }
    }
}
