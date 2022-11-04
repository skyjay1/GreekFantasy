package greekfantasy.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
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
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.EnumSet;

public class Makhai extends TamableAnimal {
    protected static final EntityDataAccessor<Byte> STATE = SynchedEntityData.defineId(Makhai.class, EntityDataSerializers.BYTE);
    protected static final String KEY_STATE = "MakhaiState";
    protected static final String KEY_TAME = "MakhaiTame";
    // bytes to use in STATE
    private static final byte NONE = (byte) 0;
    private static final byte SPAWNING = (byte) 1;
    private static final byte DESPAWNING = (byte) 2;
    // bytes to use in Level#broadcastEntityEvent
    private static final byte SPAWN_EVENT = 9;
    private static final byte DESPAWN_EVENT = 10;

    /**
     * The max time spent spawning or despawning
     **/
    protected final int maxSpawnTime = 15;
    protected int spawnTime;
    protected int despawnTime;

    public Makhai(final EntityType<? extends Makhai> type, final Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 12.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.29D)
                .add(Attributes.ATTACK_DAMAGE, 5.0D)
                .add(Attributes.ARMOR, 3.0D);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new Makhai.SpawningGoal());
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.addGoal(3, new FollowOwnerGoal(this, 1.0D, 10.0F, 2.0F, false));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 0.78D));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(0, new NearestAttackableTargetGoal<>(this, Player.class, 5, false, false, e -> !Makhai.this.isAlliedTo(e)));
        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Mob.class, 5, false, false, e -> e instanceof Enemy && !Makhai.this.isAlliedTo(e)));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.getEntityData().define(STATE, NONE);
    }

    @Override
    public void aiStep() {
        super.aiStep();

        // update spawn time
        if (isSpawning()) {
            if (--spawnTime <= 0) {
                setSpawning(false);
            }
        }

        // update despawn time
        if (isDespawning()) {
            if (--despawnTime <= 0) {
                setDespawning(false);
                discard();
                return;
            }
        }

        // determine when to despawn
        if (!level.isClientSide() && !this.isNoAi() && (getTarget() == null || getNavigation().isDone()) && !isDespawning() && random.nextInt(280) == 0) {
            setDespawning(true);
        }

    }

    @Override
    public void tick() {
        super.tick();

        // spawn particles
        if (level.isClientSide() && (isSpawning() || isDespawning())) {
            final double x = this.getX();
            final double y = this.getY() + 0.5D;
            final double z = this.getZ();
            final double motion = 0.06D;
            final double radius = this.getBbWidth() * 1.15D;
            for (int i = 0; i < 4; i++) {
                level.addParticle(ParticleTypes.LARGE_SMOKE,
                        x + (level.random.nextDouble() - 0.5D) * radius,
                        y + (level.random.nextDouble() - 0.5D) * radius,
                        z + (level.random.nextDouble() - 0.5D) * radius,
                        (level.random.nextDouble() - 0.5D) * motion,
                        (level.random.nextDouble() - 0.5D) * 0.07D,
                        (level.random.nextDouble() - 0.5D) * motion);
            }
        }
    }

    @Override
    public void populateDefaultEquipmentSlots(RandomSource random, DifficultyInstance difficulty) {
        setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.IRON_SWORD));
        setItemInHand(InteractionHand.OFF_HAND, new ItemStack(Items.GOLDEN_SWORD));
        setDropChance(EquipmentSlot.MAINHAND, 0);
        setDropChance(EquipmentSlot.OFFHAND, 0);
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficulty, MobSpawnType mobSpawnType,
                                        @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag) {
        final SpawnGroupData data = super.finalizeSpawn(worldIn, difficulty, mobSpawnType, spawnDataIn, dataTag);
        this.populateDefaultEquipmentSlots(getRandom(), difficulty);
        setSpawning(true);
        setBaby(false);
        return data;
    }

    @Override
    public boolean isFood(final ItemStack item) {
        return false;
    }

    // State methods //

    public byte getState() {
        return this.getEntityData().get(STATE);
    }

    public void setState(final byte state) {
        this.getEntityData().set(STATE, state);
    }

    public boolean isNoneState() {
        return getState() == NONE;
    }

    public boolean isSpawning() {
        return getState() == SPAWNING;
    }

    public boolean isDespawning() {
        return getState() == DESPAWNING;
    }

    public void setSpawning(final boolean spawning) {
        if(spawning) {
            spawnTime = maxSpawnTime;
            setState(SPAWNING);
            if(!this.level.isClientSide()) {
                this.level.broadcastEntityEvent(this, SPAWN_EVENT);
            }
        } else {
            spawnTime = 0;
            setState(NONE);
        }
    }

    public void setDespawning(final boolean despawning) {
        if(despawning) {
            despawnTime = maxSpawnTime;
            setState(DESPAWNING);
            if(!this.level.isClientSide()) {
                this.level.broadcastEntityEvent(this, DESPAWN_EVENT);
            }
        } else {
            despawnTime = 0;
            setState(NONE);
        }
    }

    @Override
    public void handleEntityEvent(byte id) {
        switch (id) {
            case SPAWN_EVENT:
                setSpawning(true);
                break;
            case DESPAWN_EVENT:
                setDespawning(true);
                break;
            default:
                super.handleEntityEvent(id);
                break;
        }
    }

    // Misc. methods //

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.DROWNED_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.DROWNED_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.PHANTOM_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        this.playSound(SoundEvents.ZOMBIE_STEP, 0.15F, 1.0F);
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
    public AgeableMob getBreedOffspring(ServerLevel world, AgeableMob mate) {
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
        compound.putByte(KEY_STATE, getState());
        compound.putBoolean(KEY_TAME, isTame());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        setState(compound.getByte(KEY_STATE));
        setTame(compound.getBoolean(KEY_TAME));
    }

    // Attack predicate methods //

    @Override
    public boolean canAttack(LivingEntity entity) {
        // do not attack owner
        if (isOwnedBy(entity)) {
            return false;
        }
        // do not attack players while tame
        if (this.isTame() && entity instanceof Player) {
            return false;
        }
        return super.canAttack(entity);
    }

    @Override
    public boolean wantsToAttack(LivingEntity target, LivingEntity owner) {
        if (target instanceof Ghast) {
            return false;
        } else if (target instanceof TamableAnimal tamable) {
            return !tamable.isTame() || tamable.getOwner() != owner;
        } else if (this.isTame() && target instanceof Player playerTarget && owner instanceof Player playerOwner
                && !playerOwner.canHarmPlayer(playerTarget)) {
            return false;
        } else if (target instanceof AbstractHorse horse && horse.isTamed()) {
            return false;
        }
        return true;
    }

    // Goals //

    class SpawningGoal extends Goal {

        public SpawningGoal() {
            setFlags(EnumSet.allOf(Goal.Flag.class));
        }

        @Override
        public boolean canUse() {
            return Makhai.this.isSpawning() || Makhai.this.isDespawning();
        }

        @Override
        public void tick() {
            Makhai.this.getNavigation().stop();
        }

    }
}
