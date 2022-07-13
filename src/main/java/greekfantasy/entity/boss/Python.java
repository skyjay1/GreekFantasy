package greekfantasy.entity.boss;


import greekfantasy.GreekFantasy;
import greekfantasy.entity.ai.IntervalRangedAttackGoal;
import greekfantasy.entity.misc.PoisonSpit;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeMod;

import java.util.function.Predicate;

public class Python extends Monster implements RangedAttackMob {

    private static final EntityDataAccessor<Byte> STATE = SynchedEntityData.defineId(Python.class, EntityDataSerializers.BYTE);
    private static final String KEY_STATE = "PythonState";
    // bytes to use in STATE
    private static final byte NONE = (byte) 0;
    private static final byte SPAWNING = (byte) 1;
    private static final byte SPIT = (byte) 2;
    // bytes to use in Level#broadcastEntityState
    private static final byte SPIT_CLIENT = 9;

    private static final Predicate<LivingEntity> TARGET_SELECTOR = (e) -> {
        final MobType mobType = e.getMobType();
        return e.isAlive() && e.canChangeDimensions() && !(mobType == MobType.ARTHROPOD || mobType == MobType.UNDEAD || mobType == MobType.WATER);
    };

    // other constants for attack, spawn, etc.
    private static final int MAX_SPAWN_TIME = 110;
    private static final int MAX_SPIT_TIME = 66;

    private final ServerBossEvent bossInfo = new ServerBossEvent(this.getDisplayName(), BossEvent.BossBarColor.GREEN, BossEvent.BossBarOverlay.PROGRESS);

    private int spawnTime;
    private int spitTime;

    public Python(final EntityType<? extends Python> type, final Level level) {
        super(type, level);
        this.setPersistenceRequired();
        this.xpReward = 50;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 70.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.31D)
                .add(Attributes.ATTACK_DAMAGE, 4.5D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.8D)
                .add(Attributes.FOLLOW_RANGE, 32.0D)
                .add(Attributes.ARMOR, 2.0D)
                .add(ForgeMod.STEP_HEIGHT_ADDITION.get(), 0.6D);
    }

    @Override
    public void defineSynchedData() {
        super.defineSynchedData();
        this.getEntityData().define(STATE, Byte.valueOf(NONE));
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(3, new Python.PoisonSpitAttackGoal(MAX_SPIT_TIME, 3, 165));
        this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.7D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Animal.class, 10, false, false, TARGET_SELECTOR));
    }

    @Override
    public void aiStep() {
        super.aiStep();

        // boss info
        this.bossInfo.setProgress(this.getHealth() / this.getMaxHealth());

        // update spawn time
        if (isSpawning() && --spawnTime <= 0) {
            // update timer
            setSpawning(false);
        }

        // update smash attack
        if (this.isSpitAttack()) {
            spitTime++;
        } else if (spitTime > 0) {
            spitTime = 0;
        }
    }

    // Misc //

    @Override
    public boolean canChangeDimensions() {
        return false;
    }

    @Override
    public boolean removeWhenFarAway(final double disToPlayer) {
        return false;
    }

    @Override
    protected boolean canRide(Entity entityIn) {
        return false;
    }

    @Override
    public boolean isInvulnerableTo(final DamageSource source) {
        return source == DamageSource.IN_WALL || source == DamageSource.WITHER || super.isInvulnerableTo(source);
    }

    @Override
    public boolean canBeAffected(MobEffectInstance mobEffect) {
        return mobEffect.getEffect() != MobEffects.POISON && super.canBeAffected(mobEffect);
    }

    @Override
    public boolean onClimbable() {
        return false;
    }

    // Boss //

    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        this.bossInfo.addPlayer(player);
        this.bossInfo.setName(this.hasCustomName() ? this.getCustomName() : this.getDisplayName());
        this.bossInfo.setVisible(GreekFantasy.CONFIG.showPythonBossBar());
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        this.bossInfo.removePlayer(player);
    }

    // Sounds //

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.SPIDER_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.SPIDER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.SPIDER_DEATH;
    }

    @Override
    protected float getSoundVolume() {
        return 1.0F;
    }

    @Override
    public float getVoicePitch() {
        return 0.6F + random.nextFloat() * 0.2F;
    }

    // NBT //

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putByte(KEY_STATE, this.getPythonState());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setPythonState(compound.getByte(KEY_STATE));
    }

    // States //

    public byte getPythonState() {
        return this.getEntityData().get(STATE).byteValue();
    }

    public void setPythonState(final byte state) {
        this.getEntityData().set(STATE, Byte.valueOf(state));
    }

    public boolean isNoneState() {
        return getPythonState() == NONE;
    }

    public boolean isSpitAttack() {
        return getPythonState() == SPIT;
    }

    public void setSpitAttack(final boolean smash) {
        setPythonState(smash ? SPIT : NONE);
    }

    public boolean isSpawning() {
        return spawnTime > 0 || getPythonState() == SPAWNING;
    }

    public void setSpawning(final boolean spawning) {
        spawnTime = spawning ? MAX_SPAWN_TIME : 0;
        setPythonState(spawning ? SPAWNING : NONE);
    }

    @Override
    public void onSyncedDataUpdated(final EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (key == STATE) {
            this.spawnTime = isSpawning() ? MAX_SPAWN_TIME : 0;
        }
    }

    @Override
    public void handleEntityEvent(byte id) {
        switch (id) {
            case SPIT_CLIENT:

                break;
            default:
                super.handleEntityEvent(id);
                break;
        }
    }

    // Ranged Attack //

    @Override
    public void performRangedAttack(LivingEntity target, float distanceFactor) {
        if (!level.isClientSide()) {
            PoisonSpit healingSpell = PoisonSpit.create(level, this);
            level.addFreshEntity(healingSpell);
        }
        this.playSound(SoundEvents.LLAMA_SPIT, 1.2F, 1.0F);
    }


    // Goals //

    class PoisonSpitAttackGoal extends IntervalRangedAttackGoal<Python> {

        protected PoisonSpitAttackGoal(final int duration, final int count, final int maxCooldownIn) {
            super(Python.this, duration, count, maxCooldownIn);
        }

        @Override
        public boolean canUse() {
            return super.canUse() && Python.this.isNoneState();
        }

        @Override
        public void start() {
            super.start();
            Python.this.setSpitAttack(true);
            Python.this.getCommandSenderWorld().broadcastEntityEvent(Python.this, SPIT_CLIENT);
            Python.this.playSound(SoundEvents.CREEPER_PRIMED, 1.0F, 1.2F);
        }

        @Override
        public void stop() {
            super.stop();
            Python.this.setSpitAttack(false);
        }
    }
}
