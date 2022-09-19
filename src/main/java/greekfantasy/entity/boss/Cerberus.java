package greekfantasy.entity.boss;

import greekfantasy.GFRegistry;
import greekfantasy.entity.Orthus;
import greekfantasy.entity.ai.ShootFireGoal;
import greekfantasy.entity.ai.SummonMobGoal;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;

import javax.annotation.Nullable;
import java.util.EnumSet;

public class Cerberus extends PathfinderMob implements Enemy {

    private static final EntityDataAccessor<Byte> STATE = SynchedEntityData.defineId(Cerberus.class, EntityDataSerializers.BYTE);
    private static final String KEY_STATE = "CerberusState";
    // bytes to use in STATE
    private static final byte NONE = (byte) 0;
    private static final byte SPAWNING = (byte) 1;
    private static final byte FIRING = (byte) 2;
    private static final byte SUMMONING = (byte) 4;

    private static final double FIRE_RANGE = 6.0D;
    private static final int MAX_SPAWN_TIME = 90;
    private static final int MAX_FIRING_TIME = 66;
    private static final int MAX_SUMMON_TIME = 35;

    // bytes to use in World#setEntityState
    private static final byte SPAWN_CLIENT = 9;
    private static final byte SUMMON_CLIENT = 10;

    private int spawnTime0;
    private int spawnTime;
    private int summonTime0;
    private int summonTime;

    private final ServerBossEvent bossInfo = new ServerBossEvent(this.getDisplayName(), BossEvent.BossBarColor.RED, BossEvent.BossBarOverlay.PROGRESS);

    public Cerberus(final EntityType<? extends Cerberus> type, final Level level) {
        super(type, level);
        this.xpReward = 50;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 190.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.26D)
                .add(Attributes.FOLLOW_RANGE, 32.0D)
                .add(Attributes.ATTACK_DAMAGE, 8.0D)
                .add(ForgeMod.STEP_HEIGHT_ADDITION.get(), 0.6D);
    }

    public static Cerberus spawnCerberus(final Level level, final Vec3 pos) {
        Cerberus entity = GFRegistry.EntityReg.CERBERUS.get().create(level);
        entity.moveTo(pos.x(), pos.y(), pos.z(), 0.0F, 0.0F);
        entity.yBodyRot = 0.0F;
        level.addFreshEntity(entity);
        entity.setSpawning(true);
        // trigger spawn for nearby players
        for (ServerPlayer player : level.getEntitiesOfClass(ServerPlayer.class, entity.getBoundingBox().inflate(25.0D))) {
            CriteriaTriggers.SUMMONED_ENTITY.trigger(player, entity);
        }
        // play sound
        entity.playSound(SoundEvents.WITHER_SPAWN, 1.2F, 1.0F);
        return entity;
    }

    @Override
    public void defineSynchedData() {
        super.defineSynchedData();
        this.getEntityData().define(STATE, NONE);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new Cerberus.SpawningGoal());
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new Cerberus.FireAttackGoal(MAX_FIRING_TIME, 120));
        this.goalSelector.addGoal(2, new Cerberus.SummonOrthusGoal(MAX_SUMMON_TIME, 310, 300));
        this.goalSelector.addGoal(3, new LeapAtTargetGoal(this, 0.6F));
        this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.8D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractSkeleton.class, false));
    }

    @Override
    public void aiStep() {
        super.aiStep();

        // boss info
        this.bossInfo.setProgress(this.getHealth() / this.getMaxHealth());

        // update spawning
        this.spawnTime0 = spawnTime;
        if (isSpawning()) {
            // update timer
            if (--spawnTime <= 0) {
                setSpawning(false);
            }
            // spawn particles
            addSpawningParticles(ParticleTypes.FLAME, 4);
            addSpawningParticles(ParticleTypes.SMOKE, 1);
            addSpawningParticles(ParticleTypes.LARGE_SMOKE, 1);
        }

        // update summoning
        this.summonTime0 = summonTime;
        if (this.isSummoning()) {
            summonTime++;
        } else if (summonTime > 0) {
            summonTime = 0;
        }

        // update firing
        if (this.isEffectiveAi() && this.isFiring() && this.getTarget() == null) {
            this.setFiring(false);
        }

        // spawn particles
        if (level.isClientSide() && this.isFiring()) {
            spawnFireParticles();
        }
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficulty, MobSpawnType mobSpawnType,
                                        @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag) {
        final SpawnGroupData data = super.finalizeSpawn(worldIn, difficulty, mobSpawnType, spawnDataIn, dataTag);
        setSpawning(true);
        return data;
    }

    @Override
    public boolean isInvulnerableTo(final DamageSource source) {
        return isSpawning() || source == DamageSource.IN_WALL || source == DamageSource.WITHER || super.isInvulnerableTo(source);
    }

    @Override
    public void push(Entity entity) {
        if (this.isPushable() && !this.isSpawning()) {
            super.push(entity);
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.WOLF_GROWL;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.WOLF_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.WOLF_DEATH;
    }

    @Override
    protected float getSoundVolume() {
        return 1.2F;
    }

    @Override
    public float getVoicePitch() {
        return 0.4F + random.nextFloat() * 0.2F;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        this.playSound(SoundEvents.WOLF_STEP, 0.15F, 0.6F);
    }

    // Boss logic

    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        if(this.hasCustomName()) {
            this.bossInfo.setName(this.getCustomName());
        }
        this.bossInfo.addPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        this.bossInfo.removePlayer(player);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putByte(KEY_STATE, this.getCerberusState());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setCerberusState(compound.getByte(KEY_STATE));
    }

    // State logic

    public byte getCerberusState() {
        return this.getEntityData().get(STATE).byteValue();
    }

    public void setCerberusState(final byte state) {
        this.getEntityData().set(STATE, Byte.valueOf(state));
    }

    public boolean isNoneState() {
        return getCerberusState() == NONE;
    }

    public boolean isFiring() {
        return getCerberusState() == FIRING;
    }

    public void setFiring(final boolean firing) {
        setCerberusState(firing ? FIRING : NONE);
    }

    public boolean isSummoning() {
        return getCerberusState() == SUMMONING;
    }

    public void setSummoning(final boolean summoning) {
        setCerberusState(summoning ? SUMMONING : NONE);
        this.summonTime = summoning ? 1 : 0;
        if (summoning && !level.isClientSide()) {
            level.broadcastEntityEvent(this, SUMMON_CLIENT);
        }
    }

    public boolean isSpawning() {
        return spawnTime > 0 || getCerberusState() == SPAWNING;
    }

    public void setSpawning(final boolean spawning) {
        spawnTime = spawning ? MAX_SPAWN_TIME : 0;
        setCerberusState(spawning ? SPAWNING : NONE);
        if (spawning && !level.isClientSide()) {
            level.broadcastEntityEvent(this, SPAWN_CLIENT);
        }
    }

    // Animation methods

    public float getSpawnPercent(final float partialTick) {
        return 1.0F - Mth.lerp(partialTick, spawnTime0, spawnTime) / (float) MAX_SPAWN_TIME;
    }

    public float getSummonPercent(final float partialTick) {
        return Mth.lerp(partialTick, summonTime0, summonTime) / (float) MAX_SUMMON_TIME;
    }

    // Fire-breathing particles

    public void spawnFireParticles() {
        if (!level.isClientSide()) {
            return;
        }
        Vec3 lookVec = this.getLookAngle();
        Vec3 pos = this.getEyePosition(1.0F);
        final double motion = 0.06D;
        final double radius = 0.75D;

        for (int i = 0; i < 5; i++) {
            level.addParticle(ParticleTypes.FLAME,
                    pos.x + (level.random.nextDouble() - 0.5D) * radius,
                    pos.y + (level.random.nextDouble() - 0.5D) * radius,
                    pos.z + (level.random.nextDouble() - 0.5D) * radius,
                    lookVec.x * motion * FIRE_RANGE,
                    lookVec.y * motion * 0.5D,
                    lookVec.z * motion * FIRE_RANGE);
        }
    }

    private void addSpawningParticles(final ParticleOptions particle, final int count) {
        if (!this.level.isClientSide()) {
            return;
        }
        final double x = this.getX();
        final double y = this.getY() + 0.1D;
        final double z = this.getZ();
        final double motion = 0.08D;
        final double radius = this.getBbWidth();
        for (int i = 0; i < count; i++) {
            level.addParticle(particle,
                    x + (level.random.nextDouble() - 0.5D) * radius,
                    y + (level.random.nextDouble() - 0.5D) * radius,
                    z + (level.random.nextDouble() - 0.5D) * radius,
                    (level.random.nextDouble() - 0.5D) * motion,
                    0.15D,
                    (level.random.nextDouble() - 0.5D) * motion);
        }
    }

    @Override
    public void handleEntityEvent(byte id) {
        switch(id) {
            case SPAWN_CLIENT:
                setSpawning(true);
                break;
            case SUMMON_CLIENT:
                this.level.playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.WOLF_HOWL, this.getSoundSource(), 1.1F, 0.9F + this.getRandom().nextFloat() * 0.2F, false);
                break;
            default:
                super.handleEntityEvent(id);
                break;
        }
    }

    // Custom goals

    class SpawningGoal extends Goal {

        public SpawningGoal() {
            setFlags(EnumSet.allOf(Goal.Flag.class));
        }

        @Override
        public boolean canUse() {
            return Cerberus.this.isSpawning();
        }

        @Override
        public void tick() {
            Cerberus.this.getNavigation().stop();
            Cerberus.this.setTarget(null);
        }
    }

    class FireAttackGoal extends ShootFireGoal {

        protected FireAttackGoal(final int fireTimeIn, final int maxCooldownIn) {
            super(Cerberus.this, fireTimeIn, maxCooldownIn, FIRE_RANGE);
        }

        @Override
        public boolean canUse() {
            return super.canUse() && Cerberus.this.isNoneState();
        }

        @Override
        public boolean canContinueToUse() {
            return super.canContinueToUse() && Cerberus.this.isFiring();
        }

        @Override
        public void start() {
            super.start();
            Cerberus.this.setFiring(true);
        }

        @Override
        public void stop() {
            super.stop();
            Cerberus.this.setFiring(false);
        }
    }

    class SummonOrthusGoal extends SummonMobGoal<Orthus> {

        private final int lifespan;

        public SummonOrthusGoal(int summonProgressIn, int summonCooldownIn, int lifespanIn) {
            super(Cerberus.this, summonProgressIn, summonCooldownIn, GFRegistry.EntityReg.ORTHUS.get());
            lifespan = lifespanIn;
        }

        @Override
        public boolean canUse() {
            return super.canUse() && Cerberus.this.isNoneState();
        }

        @Override
        public void start() {
            super.start();
            Cerberus.this.setSummoning(true);
        }

        @Override
        public void stop() {
            super.stop();
            Cerberus.this.setSummoning(false);
        }

        @Override
        public void tick() {
            super.tick();
            if (this.progressTimer == 8) {
                Cerberus.this.level.broadcastEntityEvent(Cerberus.this, SUMMON_CLIENT);
            }
        }

        @Override
        protected void onSummonMob(final Orthus mobEntity) {
            mobEntity.setLimitedLife(lifespan);
        }
    }
}
