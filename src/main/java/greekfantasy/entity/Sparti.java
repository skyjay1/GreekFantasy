package greekfantasy.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.FlyingMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.EnumSet;

public class Sparti extends TamableAnimal implements RangedAttackMob {
    protected static final String KEY_SPAWN_TIME = "Spawning";
    protected static final String KEY_LIFE_TICKS = "LifeTicks";
    //bytes to use in Level#broadcastEntityEvent
    private static final byte SPAWN_EVENT = 11;

    /**
     * The max time spent 'spawning'
     **/
    protected final int maxSpawnTime = 60;
    protected int spawnTime = 0;
    /**
     * The number of ticks until the entity starts taking damage
     **/
    protected boolean limitedLifespan;
    protected int limitedLifeTicks;

    private final EntityDimensions spawningSize;

    public Sparti(final EntityType<? extends Sparti> type, final Level level) {
        super(type, level);
        spawningSize = EntityDimensions.scalable(0.8F, 0.2F);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 54.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.28D)
                .add(Attributes.ATTACK_DAMAGE, 2.0D)
                .add(Attributes.ARMOR, 2.0D);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new SpawningGoal());
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.addGoal(3, new FollowOwnerGoal(this, 1.0D, 16.0F, 5.0F, false));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 0.78D));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Mob.class, 5, false, false, e -> e instanceof Enemy));
    }

    @Override
    public void aiStep() {
        super.aiStep();

        // lifespan
        if (this.limitedLifespan && --this.limitedLifeTicks <= 0) {
            this.limitedLifeTicks = 20;
            hurt(DamageSource.STARVE, 2.0F);
        }

        // update spawn time
        if (isSpawning() && --spawnTime <= 0) {
            refreshDimensions();
        }

    }

    @Override
    public void tick() {
        super.tick();

        // particles when spawning
        if (level.isClientSide() && isSpawning()) {
            int i = Mth.floor(this.getX());
            int j = Mth.floor(this.getY() - (double) 0.2F);
            int k = Mth.floor(this.getZ());
            BlockPos pos = new BlockPos(i, j, k);
            BlockState blockstate = level.getBlockState(pos);
            if (!level.isEmptyBlock(pos)) {
                for (int count = 0; count < 10; count++) {
                    this.level.addParticle(new BlockParticleOption(ParticleTypes.BLOCK, blockstate).setPos(pos),
                            this.getX() + ((double) this.random.nextFloat() - 0.5D) * (double) this.getBbWidth(),
                            this.getY() + 0.1D,
                            this.getZ() + ((double) this.random.nextFloat() - 0.5D) * (double) this.getBbWidth(),
                            4.0D * ((double) this.random.nextFloat() - 0.5D), 0.6D, ((double) this.random.nextFloat() - 0.5D) * 4.0D);
                }
            }
        }
    }

    @Override
    public void populateDefaultEquipmentSlots(RandomSource random, final DifficultyInstance difficulty) {
        this.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.IRON_SWORD));
        this.setDropChance(EquipmentSlot.MAINHAND, 0);
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficulty, MobSpawnType spawnType,
                                        @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag) {
        final SpawnGroupData data = super.finalizeSpawn(worldIn, difficulty, spawnType, spawnDataIn, dataTag);
        populateDefaultEquipmentSlots(getRandom(), difficulty);
        setBaby(false);
        return data;
    }

    // Spawn methods //

    public void setSpawning() {
        this.spawnTime = maxSpawnTime;
        this.refreshDimensions();
        if (!level.isClientSide()) {
            this.level.broadcastEntityEvent(this, SPAWN_EVENT);
        }
    }

    public boolean isSpawning() {
        return spawnTime > 0;
    }

    public float getSpawnPercent() {
        return spawnTime > 0 ? 1.0F - ((float) spawnTime / (float) maxSpawnTime) : 1.0F;
    }

    @Override
    public void handleEntityEvent(byte id) {
        switch (id) {
            case SPAWN_EVENT:
                setSpawning();
                break;
            default:
                super.handleEntityEvent(id);
                break;
        }
    }

    // Lifespan methods

    public void setLimitedLife(int life) {
        this.limitedLifespan = true;
        this.limitedLifeTicks = life;
    }

    // Misc. methods //

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.SKELETON_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.SKELETON_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.SKELETON_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        this.playSound(SoundEvents.SKELETON_STEP, 0.15F, 1.0F);
    }

    @Override
    public EntityDimensions getDimensions(final Pose poseIn) {
        return this.isSpawning() ? spawningSize : super.getDimensions(poseIn);
    }

    @Override
    public MobType getMobType() {
        return MobType.UNDEAD;
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return !this.isTame();
    }

    @Override
    protected float getStandingEyeHeight(Pose pose, EntityDimensions size) {
        return this.isSpawning() ? 0.05F : 1.74F;
    }

    @Override
    public double getMyRidingOffset() {
        return -0.6D;
    }

    @Override
    public boolean canBeLeashed(Player player) {
        return false;
    }

    @Override
    public boolean canMate(Animal otherAnimal) {
        return false;
    }

    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob mate) {
        return null;
    }

    @Override
    public void setOrderedToSit(boolean sitting) {
    }

    @Override
    public void die(DamageSource cause) {
        setOwnerUUID(null);
        super.die(cause);
    }

    // NBT methods //

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        if (this.limitedLifespan) {
            compound.putInt(KEY_LIFE_TICKS, this.limitedLifeTicks);
        }
        compound.putBoolean(KEY_SPAWN_TIME, isSpawning());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.getBoolean(KEY_SPAWN_TIME)) {
            setSpawning();
        }
        if (compound.contains(KEY_LIFE_TICKS)) {
            setLimitedLife(compound.getInt(KEY_LIFE_TICKS));
        }
    }

    // Attack predicate methods //

    @Override
    public boolean canAttack(LivingEntity entity) {
        if (isOwnedBy(entity)) {
            return false;
        }
        return super.canAttack(entity);
    }

    @Override
    public boolean wantsToAttack(LivingEntity target, LivingEntity owner) {
        if (target instanceof FlyingMob) {
            return false;
        } else if (target instanceof TamableAnimal tamable) {
            return !tamable.isTame() || tamable.getOwner() != owner;
        } else if (target instanceof Player targetPlayer && owner instanceof Player ownerPlayer
                && !ownerPlayer.canHarmPlayer(targetPlayer)) {
            return false;
        } else return !(target instanceof AbstractHorse abstractHorse) || !abstractHorse.isTamed();
    }

    /**
     * This method is here in order to use the Skeleton Renderer.
     * Sparti do not naturally spawn with a bow.
     *
     * @param target the target entity
     * @param damage the base damage amount
     */
    @Override
    public void performRangedAttack(LivingEntity target, float damage) {

    }

    // Goals //

    class SpawningGoal extends Goal {

        public SpawningGoal() {
            setFlags(EnumSet.allOf(Goal.Flag.class));
        }

        @Override
        public boolean canUse() {
            return Sparti.this.isSpawning();
        }

        @Override
        public void tick() {
            Sparti.this.getNavigation().stop();
        }

    }
}
