package greekfantasy.entity;

import greekfantasy.GreekFantasy;
import greekfantasy.capability.FriendlyGuardian;
import greekfantasy.entity.ai.GoToWaterGoal;
import greekfantasy.entity.ai.MoveToStructureGoal;
import greekfantasy.entity.ai.TridentRangedAttackGoal;
import greekfantasy.entity.ai.WaterAnimalMoveControl;
import greekfantasy.entity.boss.Charybdis;
import greekfantasy.entity.boss.Scylla;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.ForgeMod;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class Triton extends PathfinderMob implements RangedAttackMob, NeutralMob {

    protected static final EntityDataAccessor<Boolean> SLIM = SynchedEntityData.defineId(Triton.class, EntityDataSerializers.BOOLEAN);
    protected static final String KEY_SLIM = "Slim";
    protected static final String KEY_TIMESTAMP = "GuardianTimestamp";

    protected static final UniformInt ANGER_RANGE = TimeUtil.rangeOfSeconds(4, 10);
    protected int angerTime;
    protected UUID angerTarget;

    protected static final int GUARDIAN_COOLDOWN = 400;
    protected long guardianTimestamp;

    protected EntityDimensions swimmingDimensions;

    protected Component description;

    public Triton(final EntityType<? extends Triton> type, final Level level) {
        super(type, level);
        this.moveControl = new WaterAnimalMoveControl(this);
        this.swimmingDimensions = EntityDimensions.scalable(0.48F, 0.48F);
        this.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 30.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.ATTACK_DAMAGE, 3.0D)
                .add(Attributes.ARMOR, 1.0D)
                .add(ForgeMod.STEP_HEIGHT_ADDITION.get(), 0.6D);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.getEntityData().define(SLIM, true);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new GoToWaterGoal(this, 1.0D, false));
        this.goalSelector.addGoal(2, new TridentRangedAttackGoal(this, 1.0D, 40, 10.0F));
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.1D, false));
        this.goalSelector.addGoal(4, new AvoidEntityGoal<>(this, Charybdis.class, 12.0F, 1.0D, 1.0D));
        this.goalSelector.addGoal(4, new AvoidEntityGoal<>(this, Scylla.class, 12.0F, 1.0D, 1.0D));
        this.goalSelector.addGoal(5, new MoveToStructureGoal(this, 1.0D, 6, 8, 10, new ResourceLocation(GreekFantasy.MODID, "ocean_village"), BehaviorUtils::getRandomSwimmablePos));
        this.goalSelector.addGoal(7, new RandomSwimmingGoal(this, 0.8D, 120));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(10, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false, this::isAngryAt));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Drowned.class, false));
        this.targetSelector.addGoal(4, new ResetUniversalAngerTargetGoal<>(this, true));
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        return new WaterBoundPathNavigation(this, level);
    }

    @Override
    public void tick() {
        super.tick();
        boolean inWater = this.isInWaterRainOrBubble();
        // random motion when not in water
        if (!inWater && this.onGround) {
            this.setDeltaMovement(this.getDeltaMovement().add((this.random.nextFloat() * 2.0F - 1.0F) * 0.2F, 0.5D, (this.random.nextFloat() * 2.0F - 1.0F) * 0.2F));
            this.setYRot(this.random.nextFloat() * 360.0F);
            this.onGround = false;
            this.hasImpulse = true;
        }
        // update pose
        if (!inWater || this.getDeltaMovement().horizontalDistanceSqr() > 0.0012D) {
            this.setPose(Pose.SWIMMING);
        } else if (this.getPose() == Pose.SWIMMING) {
            this.setPose(Pose.STANDING);
        }
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        return (pose == Pose.SWIMMING) ? swimmingDimensions : super.getDimensions(pose);
    }

    // Slim methods

    public boolean isSlim() {
        return getEntityData().get(SLIM);
    }

    public void setSlim(final boolean slim) {
        getEntityData().set(SLIM, slim);
    }

    @Override
    protected Component getTypeName() {
        if(null == description) {
            String descriptionId = this.getType().getDescriptionId();
            if(isSlim()) {
                descriptionId += ".slim";
            }
            this.description = new TranslatableComponent(descriptionId);
        }
        return description;
    }

    // Guardian methods

    public boolean wantsToSpawnGuardian(long gameTime) {
        return gameTime - this.guardianTimestamp > GUARDIAN_COOLDOWN;
    }

    public void onSpawnGuardian(long gameTime) {
        this.guardianTimestamp = gameTime;
    }

    public Optional<Guardian> trySpawnGuardian(ServerLevel level) {
        BlockPos blockpos = this.blockPosition();

        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int i = 0; i < 10; ++i) {
            pos.setWithOffset(blockpos,
                    level.random.nextInt(16) - 8,
                    level.random.nextInt(8) - 4,
                    level.random.nextInt(16) - 8);
            if (level.getBlockState(pos).getMaterial() == Material.WATER) {
                Guardian guardian = EntityType.GUARDIAN.create(level, null, null, null, pos, MobSpawnType.MOB_SUMMONED, false, false);
                if (guardian != null) {
                    if (guardian.checkSpawnRules(level, MobSpawnType.MOB_SUMMONED) && guardian.checkSpawnObstruction(level)) {
                        level.addFreshEntityWithPassengers(guardian);
                        guardian.setPersistenceRequired();
                        guardian.getCapability(GreekFantasy.FRIENDLY_GUARDIAN_CAP).ifPresent(c -> c.setEnabled(true));
                        return Optional.of(guardian);
                    }

                    guardian.discard();
                }
            }
        }

        return Optional.empty();
    }

    // NeutralMob methods

    @Override
    public void startPersistentAngerTimer() {
        this.setRemainingPersistentAngerTime(ANGER_RANGE.sample(this.random));
    }

    @Override
    public void setRemainingPersistentAngerTime(int time) {
        this.angerTime = time;
    }

    @Override
    public int getRemainingPersistentAngerTime() {
        return this.angerTime;
    }

    @Override
    public void setPersistentAngerTarget(@Nullable UUID target) {
        this.angerTarget = target;
    }

    @Override
    public UUID getPersistentAngerTarget() {
        return this.angerTarget;
    }

    @Override
    public boolean hurt(final DamageSource source, final float amount) {
        // attempt to summon friendly guardian when hurt
        if (super.hurt(source, amount) && source.getEntity() instanceof Enemy) {
            if (this.level instanceof ServerLevel serverLevel) {
                // determine if there are enough nearby tritons
                final AABB aabb = this.getBoundingBox().inflate(10.0D);
                final long gameTime = level.getGameTime();
                List<Triton> nearbyTriton = level.getEntitiesOfClass(Triton.class, aabb);
                List<Triton> wantsToSpawnGuardian = nearbyTriton.stream()
                        .filter(m -> m.wantsToSpawnGuardian(gameTime))
                        .limit(4L).toList();
                if (wantsToSpawnGuardian.size() >= 2) {
                    // determine if there are any guardians in range
                    List<Guardian> guardians = level.getEntitiesOfClass(Guardian.class, aabb, p -> p.getCapability(GreekFantasy.FRIENDLY_GUARDIAN_CAP).orElse(FriendlyGuardian.EMPTY).isEnabled());
                    if (guardians.isEmpty()) {
                        // attempt to spawn guardian
                        Optional<Guardian> oGuardian = trySpawnGuardian(serverLevel);
                        if (oGuardian.isPresent()) {
                            nearbyTriton.forEach(m -> m.onSpawnGuardian(gameTime));
                        }
                    }
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean(KEY_SLIM, isSlim());
        compound.putLong(KEY_TIMESTAMP, guardianTimestamp);
        this.addPersistentAngerSaveData(compound);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setSlim(compound.getBoolean(KEY_SLIM));
        this.guardianTimestamp = compound.getLong(KEY_TIMESTAMP);
        this.readPersistentAngerSaveData(this.level, compound);
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType mobType,
                                        @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag) {
        SpawnGroupData data = super.finalizeSpawn(worldIn, difficultyIn, mobType, spawnDataIn, dataTag);
        boolean slim = worldIn.getRandom().nextBoolean();
        this.setSlim(slim);
        populateDefaultEquipmentSlots(difficultyIn);
        return data;
    }

    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }

    @Override
    public boolean canBeLeashed(Player player) {
        return false;
    }

    @Override
    protected int decreaseAirSupply(int airSupply) {
        return airSupply;
    }

    @Override
    public MobType getMobType() {
        return MobType.WATER;
    }

    @Override
    public boolean isInvulnerableTo(final DamageSource source) {
        // immune to damage from other centaurs
        if (source.getDirectEntity() instanceof ThrownTrident && source.getEntity() != null && source.getEntity().getType() == this.getType()) {
            return true;
        }
        return super.isInvulnerableTo(source);
    }

    /*protected SoundEvent getAmbientSound() {
        return this.isInWater() ? SoundEvents.DROWNED_AMBIENT_WATER : SoundEvents.DROWNED_AMBIENT;
    }

    protected SoundEvent getHurtSound(DamageSource p_32386_) {
        return this.isInWater() ? SoundEvents.DROWNED_HURT_WATER : SoundEvents.DROWNED_HURT;
    }

    protected SoundEvent getDeathSound() {
        return this.isInWater() ? SoundEvents.DROWNED_DEATH_WATER : SoundEvents.DROWNED_DEATH;
    }

    protected SoundEvent getStepSound() {
        return SoundEvents.DROWNED_STEP;
    }

    protected SoundEvent getSwimSound() {
        return SoundEvents.DROWNED_SWIM;
    }
*/

    @Override
    protected void populateDefaultEquipmentSlots(DifficultyInstance difficulty) {
        float tridentChance = 0.31F;
        if (this.random.nextFloat() < tridentChance) {
            this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.TRIDENT));
        }

    }

    @Override
    public boolean checkSpawnObstruction(LevelReader level) {
        return level.isUnobstructed(this);
    }

    @Override
    public boolean isPushedByFluid() {
        return !this.isSwimming();
    }

    @Override
    public void updateSwimming() {
        if (!this.level.isClientSide) {
            this.setSwimming(true);
        }
    }

    @Override
    public void performRangedAttack(LivingEntity target, float distanceFactor) {
        ThrownTrident throwntrident = new ThrownTrident(this.level, this, new ItemStack(Items.TRIDENT));
        double d0 = target.getX() - this.getX();
        double d1 = target.getY(0.33D) - throwntrident.getY();
        double d2 = target.getZ() - this.getZ();
        double d3 = Math.sqrt(d0 * d0 + d2 * d2);
        throwntrident.shoot(d0, d1 + d3 * (double) 0.2F, d2, 1.6F, (float) (14 - this.level.getDifficulty().getId() * 4));
        this.playSound(SoundEvents.TRIDENT_THROW, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
        this.level.addFreshEntity(throwntrident);
    }
}
