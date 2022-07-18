package greekfantasy.entity.monster;

import greekfantasy.GFRegistry;
import greekfantasy.entity.Dryad;
import greekfantasy.entity.ai.FindBlockGoal;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.Optional;

public class Harpy extends Monster implements FlyingAnimal {

    private static final EntityDataAccessor<Optional<BlockPos>> DATA_NEST = SynchedEntityData.defineId(Harpy.class, EntityDataSerializers.OPTIONAL_BLOCK_POS);
    private static final String KEY_NEST = "Nest";

    public float flyingTime;
    public float flap;
    public float flapSpeed;
    public float oFlapSpeed;
    public float oFlap;
    private float flapping = 1.0F;
    private float nextFlap = 1.0F;
    private boolean isGoingToNest;

    public Harpy(final EntityType<? extends Harpy> type, final Level level) {
        super(type, level);
        this.moveControl = new FlyingMoveControl(this, 10, false);
        this.setPathfindingMalus(BlockPathTypes.DANGER_FIRE, -1.0F);
        this.setPathfindingMalus(BlockPathTypes.DAMAGE_FIRE, -1.0F);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 24.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.22D)
                .add(Attributes.ATTACK_DAMAGE, 3.0D)
                .add(Attributes.FLYING_SPEED, 1.29D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.32D);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.getEntityData().define(DATA_NEST, Optional.empty());
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.addGoal(2, new Harpy.FindNestGoal(6, 10, 60));
        this.goalSelector.addGoal(3, new Harpy.GoToNestGoal(0.9D, 120));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomFlyingGoal(this, 0.9D) {
            @Override
            public boolean canUse() {
                return !Harpy.this.isGoingToNest && Harpy.this.getRandom().nextInt(200) == 0 && super.canUse();
            }
        });
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Dryad.class, true));
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
        // update flying counter
        if (this.isFlying()) {
            flyingTime = Math.min(1.0F, flyingTime + 0.09F);
        } else {
            flyingTime = Math.max(0.0F, flyingTime - 0.09F);
        }
        // update flapping
        this.calculateFlapping();
        // nest checker
        if (this.tickCount % 90 == 0) {
            // check nest
            final Optional<BlockPos> nestPos = Harpy.this.getNestPos();
            // check if there is still a nest at the position
            if (nestPos.isPresent() && !level.getBlockState(nestPos.get()).is(GFRegistry.BlockReg.NEST.get())) {
                this.setNestPos(Optional.empty());
            }
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return random.nextFloat() < 0.18F ? SoundEvents.WITCH_AMBIENT : SoundEvents.PARROT_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return random.nextFloat() < 0.09F ? SoundEvents.WITCH_HURT : SoundEvents.PARROT_HURT;
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
        return 0.7F + random.nextFloat() * 0.2F;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        final Optional<BlockPos> nestPos = this.getNestPos();
        if (nestPos.isPresent()) {
            compound.putInt(KEY_NEST + ".x", nestPos.get().getX());
            compound.putInt(KEY_NEST + ".y", nestPos.get().getY());
            compound.putInt(KEY_NEST + ".z", nestPos.get().getZ());
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains(KEY_NEST + ".x")) {
            final int x = compound.getInt(KEY_NEST + ".x");
            final int y = compound.getInt(KEY_NEST + ".y");
            final int z = compound.getInt(KEY_NEST + ".z");
            this.setNestPos(Optional.of(new BlockPos(x, y, z)));
        }
    }

    // Flying methods

    @Override
    public boolean causeFallDamage(float distance, float damageMultiplier, DamageSource source) {
        return false;
    }

    @Override
    protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
    }

    @Override
    protected boolean isFlapping() {
        return this.flyDist > this.nextFlap;
    }

    @Override
    protected void onFlap() {
        this.playSound(SoundEvents.PARROT_FLY, 0.25F, 0.9F);
        this.nextFlap = this.flyDist + this.flapSpeed / 2.0F;
    }

    @Override
    public boolean isFlying() {
        return !this.onGround || this.getDeltaMovement().lengthSqr() > 0.06D;
    }

    private void calculateFlapping() {
        this.oFlap = this.flap;
        this.oFlapSpeed = this.flapSpeed;
        this.flapSpeed += (float)(!this.onGround && !this.isPassenger() ? 4 : -1) * 0.3F;
        this.flapSpeed = Mth.clamp(this.flapSpeed, 0.0F, 1.0F);
        if (!this.onGround && this.flapping < 1.0F) {
            this.flapping = 1.0F;
        }

        this.flapping *= 0.9F;
        Vec3 vec3 = this.getDeltaMovement();
        if (!this.onGround && vec3.y < 0.0D) {
            this.setDeltaMovement(vec3.multiply(1.0D, 0.6D, 1.0D));
        }

        this.flap += this.flapping * 2.0F;
    }

    // Nest methods

    public void setNestPos(final Optional<BlockPos> pos) {
        this.getEntityData().set(DATA_NEST, pos);
        if (pos.isPresent()) {
            this.restrictTo(pos.get(), (int) Math.ceil(this.getAttribute(Attributes.FOLLOW_RANGE).getBaseValue() * 1.75D));
        } else {
            this.clearRestriction();
        }
    }

    public Optional<BlockPos> getNestPos() {
        return this.getEntityData().get(DATA_NEST);
    }

    // Goals

    class FindNestGoal extends FindBlockGoal {

        public FindNestGoal(final int radiusXZ, final int radiusY, final int cooldown) {
            super(Harpy.this, radiusXZ, radiusY, cooldown);
        }

        @Override
        public boolean canUse() {
            return !isNearTarget(2.5D) && super.canUse();
        }

        @Override
        protected Optional<BlockPos> findNearbyBlock() {
            final Optional<BlockPos> nestPos = Harpy.this.getNestPos();
            if (nestPos.isPresent() && isTargetBlock(Harpy.this.level, nestPos.get())) {
                return nestPos;
            }
            return super.findNearbyBlock();
        }

        @Override
        public boolean isTargetBlock(LevelReader level, BlockPos pos) {
            return level.getBlockState(pos).is(GFRegistry.BlockReg.NEST.get());
        }

        @Override
        public void onFoundBlock(final LevelReader level, final BlockPos pos) {
            Harpy.this.setNestPos(Optional.of(pos));
        }
    }

    class GoToNestGoal extends Goal {

        private final double speed;
        private final int chance;

        public GoToNestGoal(final double speedIn, final int chanceIn) {
            setFlags(EnumSet.of(Goal.Flag.MOVE));
            speed = speedIn;
            chance = chanceIn;
        }

        @Override
        public boolean canUse() {
            return Harpy.this.getNestPos().isPresent() && !isNearNest(4.0D)
                    && Harpy.this.getTarget() == null && Harpy.this.getRandom().nextInt(chance) == 0;
        }

        @Override
        public void start() {
            final Optional<BlockPos> nestPos = Harpy.this.getNestPos();
            if (nestPos.isPresent()) {
                final Vec3 vec = new Vec3(nestPos.get().getX() + 0.5D, nestPos.get().getY(), nestPos.get().getZ() + 0.5D);
                Harpy.this.getNavigation().moveTo(vec.x(), vec.y(), vec.z(), this.speed);
                Harpy.this.isGoingToNest = true;
            }
        }

        @Override
        public boolean canContinueToUse() {
            return Harpy.this.getNestPos().isPresent() && !Harpy.this.getNavigation().isDone() && Harpy.this.getTarget() == null;
        }

        @Override
        public void stop() {
            Harpy.this.getNavigation().stop();
            Harpy.this.isGoingToNest = false;
        }

        protected boolean isNearNest(final double distance) {
            final Optional<BlockPos> nestPos = Harpy.this.getNestPos();
            if (nestPos.isPresent()) {
                final Vec3 vec = new Vec3(nestPos.get().getX() + 0.5D, nestPos.get().getY() + 0.5D, nestPos.get().getZ() + 0.5D);
                return vec.closerThan(Harpy.this.position(), distance);
            }
            return false;
        }

    }
}
