package greekfantasy.entity.monster;

import greekfantasy.entity.Elpis;
import greekfantasy.entity.ai.IntervalRangedAttackGoal;
import greekfantasy.entity.misc.Curse;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowMobGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomFlyingGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class Fury extends Monster implements FlyingAnimal, RangedAttackMob {
    public static final int MAX_AGGRO_TIME = 45;
    private float flyingTime0;
    private float flyingTime;
    private int aggroTime0;
    private int aggroTime;

    public Fury(final EntityType<? extends Fury> type, final Level level) {
        super(type, level);
        this.moveControl = new FlyingMoveControl(this, 10, false);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 24.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.26D)
                .add(Attributes.ATTACK_DAMAGE, 3.5D)
                .add(Attributes.FLYING_SPEED, 1.28D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.4D);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new IntervalRangedAttackGoal<>(this, 90, 2, 200, 14.0F));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.addGoal(3, new FollowMobGoal(this, 1.0D, 6.0F, 12.0F) {
            @Override
            public boolean canUse() {
                return Fury.this.getRandom().nextInt(110) == 0 && super.canUse();
            }
        });
        this.goalSelector.addGoal(4, new WaterAvoidingRandomFlyingGoal(this, 0.9D) {
            @Override
            public boolean canUse() {
                return Fury.this.getRandom().nextInt(120) == 0 && super.canUse();
            }
        });
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Elpis.class, true));
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
        if (this.isEffectiveAi() && !this.onGround && m.y < 0.0D) {
            final double multY = this.getTarget() != null ? 0.9D : 0.6D;
            setDeltaMovement(m.multiply(1.0D, multY, 1.0D));
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level.isClientSide()) {
            // update flying counter
            flyingTime0 = flyingTime;
            if (this.isFlying()) {
                flyingTime = Math.min(1.0F, flyingTime + 0.05F);
            } else {
                flyingTime = Math.max(0.0F, flyingTime - 0.05F);
            }
            // update aggro counter
            aggroTime0 = aggroTime;
            if (this.isAggressive()) {
                aggroTime = Math.min(aggroTime + 1, MAX_AGGRO_TIME);
            } else {
                aggroTime = Math.max(aggroTime - 1, 0);
            }
        }
    }

    // Sounds

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.SPIDER_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.GHAST_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.GHAST_DEATH;
    }

    @Override
    protected float getSoundVolume() {
        return 0.8F;
    }

    @Override
    public float getVoicePitch() {
        return 1.0F + random.nextFloat() * 0.2F;
    }

    // Flying

    // Flying methods

    @Override
    public boolean causeFallDamage(float distance, float damageMultiplier, DamageSource source) {
        return false;
    }

    @Override
    protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
    }

    @Override
    public boolean isFlying() {
        return !this.onGround || this.getDeltaMovement().lengthSqr() > 0.06D;
    }

    public float getFlyingTime(final float partialTick) {
        return Mth.clamp(Mth.lerp(partialTick, flyingTime0, flyingTime), 0.0F, 1.0F);
    }

    // Aggro

    public float getAggroPercent(final float partialTick) {
        return Mth.lerp(partialTick, aggroTime0, aggroTime) / (float) MAX_AGGRO_TIME;
    }

    //Ranged Attack //

    @Override
    public void performRangedAttack(LivingEntity target, float distanceFactor) {
        if (!level.isClientSide()) {
            Curse curse = Curse.create(level, this);
            level.addFreshEntity(curse);
        }
        this.playSound(SoundEvents.LLAMA_SPIT, 1.2F, 1.0F);
    }
}
