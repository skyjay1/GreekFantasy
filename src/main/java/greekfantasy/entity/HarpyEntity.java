package greekfantasy.entity;

import greekfantasy.GFRegistry;
import greekfantasy.entity.ai.FindBlockGoal;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.FlyingMovementController;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomFlyingGoal;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.passive.IFlyingAnimal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.FlyingPathNavigator;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

import java.util.EnumSet;
import java.util.Optional;

public class HarpyEntity extends MonsterEntity implements IFlyingAnimal {

    private static final DataParameter<Optional<BlockPos>> DATA_NEST = EntityDataManager.defineId(HarpyEntity.class, DataSerializers.OPTIONAL_BLOCK_POS);
    private static final String KEY_NEST = "Nest";

    public float flyingTime;
    private boolean isGoingToNest;

    public HarpyEntity(final EntityType<? extends HarpyEntity> type, final World worldIn) {
        super(type, worldIn);
        this.moveControl = new FlyingMovementController(this, 10, false);
        this.setPathfindingMalus(PathNodeType.DANGER_FIRE, -1.0F);
        this.setPathfindingMalus(PathNodeType.DAMAGE_FIRE, -1.0F);
    }

    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return MobEntity.createMobAttributes()
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
        this.goalSelector.addGoal(0, new SwimGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.addGoal(2, new HarpyEntity.FindNestGoal(6, 10, 60));
        this.goalSelector.addGoal(3, new HarpyEntity.GoToNestGoal(0.9D, 120));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomFlyingGoal(this, 0.9D) {
            @Override
            public boolean canUse() {
                return !HarpyEntity.this.isGoingToNest && HarpyEntity.this.getRandom().nextInt(200) == 0 && super.canUse();
            }
        });
        this.goalSelector.addGoal(5, new LookAtGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.addGoal(6, new LookRandomlyGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, DryadEntity.class, true));
    }

    @Override
    protected PathNavigator createNavigation(World worldIn) {
        FlyingPathNavigator flyingpathnavigator = new FlyingPathNavigator(this, worldIn);
        flyingpathnavigator.setCanOpenDoors(false);
        flyingpathnavigator.setCanFloat(true);
        flyingpathnavigator.setCanPassDoors(true);
        return flyingpathnavigator;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        // update falling moveSpeed
        Vector3d m = getDeltaMovement();
        if (this.isEffectiveAi() && !this.onGround && m.y < 0.0D) {
            final double multY = this.getTarget() != null ? 0.9D : 0.6D;
            setDeltaMovement(m.multiply(1.0D, multY, 1.0D));
        }
        // update flying counter
        if (this.isFlying()) {
            flyingTime = Math.min(1.0F, flyingTime + 0.1F);
        } else {
            flyingTime = Math.max(0.0F, flyingTime - 0.1F);
        }
        // nest checker
        if (this.tickCount % 90 == 0) {
            // check nest
            final Optional<BlockPos> nestPos = HarpyEntity.this.getNestPos();
            // check if there is still a nest at the position
            if (nestPos.isPresent() && !level.getBlockState(nestPos.get()).is(GFRegistry.NEST_BLOCK)) {
                HarpyEntity.this.setNestPos(Optional.empty());
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
    protected float getVoicePitch() {
        return 0.7F + random.nextFloat() * 0.2F;
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT compound) {
        super.addAdditionalSaveData(compound);
        final Optional<BlockPos> nestPos = this.getNestPos();
        if (nestPos.isPresent()) {
            compound.putInt(KEY_NEST + ".x", nestPos.get().getX());
            compound.putInt(KEY_NEST + ".y", nestPos.get().getY());
            compound.putInt(KEY_NEST + ".z", nestPos.get().getZ());
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT compound) {
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
    public boolean causeFallDamage(float distance, float damageMultiplier) {
        return false;
    }

    @Override
    protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
    }

    @Override
    protected boolean makeFlySound() {
        return true;
    }

    @Override
    protected float playFlySound(float volume) {
        this.playSound(SoundEvents.PARROT_FLY, 0.25F, 0.9F);
        return volume;
    }

    public boolean isFlying() {
        return !this.onGround || this.getDeltaMovement().lengthSqr() > 0.06D;
    }

    public void setNestPos(final Optional<BlockPos> pos) {
        this.getEntityData().set(DATA_NEST, pos);
        if (pos.isPresent()) {
            this.restrictTo(pos.get(), (int) (this.getAttribute(Attributes.FOLLOW_RANGE).getBaseValue() * 1.75D));
        } else {
            this.restrictTo(BlockPos.ZERO, -1);
        }
    }

    public Optional<BlockPos> getNestPos() {
        return this.getEntityData().get(DATA_NEST);
    }

    class FindNestGoal extends FindBlockGoal {

        public FindNestGoal(final int radiusXZ, final int radiusY, final int cooldown) {
            super(HarpyEntity.this, radiusXZ, radiusY, cooldown);
        }

        @Override
        public boolean canUse() {
            return !isNearTarget(2.5D) && super.canUse();
        }

        @Override
        protected Optional<BlockPos> findNearbyBlock() {
            final Optional<BlockPos> nestPos = HarpyEntity.this.getNestPos();
            if (nestPos.isPresent() && isTargetBlock(HarpyEntity.this.level, nestPos.get())) {
                return nestPos;
            }
            return super.findNearbyBlock();
        }

        @Override
        public boolean isTargetBlock(IWorldReader worldIn, BlockPos pos) {
            return worldIn.getBlockState(pos).is(GFRegistry.NEST_BLOCK);
        }

        @Override
        public void onFoundBlock(final IWorldReader worldIn, final BlockPos pos) {
            HarpyEntity.this.setNestPos(Optional.of(pos));
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
            return HarpyEntity.this.getNestPos().isPresent() && !isNearNest(4.0D)
                    && HarpyEntity.this.getTarget() == null && HarpyEntity.this.getRandom().nextInt(chance) == 0;
        }

        @Override
        public void start() {
            final Optional<BlockPos> nestPos = HarpyEntity.this.getNestPos();
            if (nestPos.isPresent()) {
                final Vector3d vec = new Vector3d(nestPos.get().getX() + 0.5D, nestPos.get().getY(), nestPos.get().getZ() + 0.5D);
                HarpyEntity.this.getNavigation().moveTo(vec.x(), vec.y(), vec.z(), this.speed);
                HarpyEntity.this.isGoingToNest = true;
            }
        }

        @Override
        public boolean canContinueToUse() {
            return HarpyEntity.this.getNestPos().isPresent() && !HarpyEntity.this.getNavigation().isDone() && HarpyEntity.this.getTarget() == null;
        }

        @Override
        public void stop() {
            HarpyEntity.this.getNavigation().stop();
            HarpyEntity.this.isGoingToNest = false;
        }

        protected boolean isNearNest(final double distance) {
            final Optional<BlockPos> nestPos = HarpyEntity.this.getNestPos();
            if (nestPos.isPresent()) {
                final Vector3d vec = new Vector3d(nestPos.get().getX() + 0.5D, nestPos.get().getY() + 0.5D, nestPos.get().getZ() + 0.5D);
                return vec.closerThan(HarpyEntity.this.position(), distance);
            }
            return false;
        }

    }
}
