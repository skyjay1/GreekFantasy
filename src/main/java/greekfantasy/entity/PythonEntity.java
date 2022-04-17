package greekfantasy.entity;

import greekfantasy.entity.ai.IntervalRangedAttackGoal;
import greekfantasy.entity.misc.PoisonSpitEntity;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.BossInfo;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerBossInfo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Predicate;

public class PythonEntity extends MonsterEntity implements IRangedAttackMob {

    private static final DataParameter<Byte> STATE = EntityDataManager.defineId(PythonEntity.class, DataSerializers.BYTE);
    private static final String KEY_STATE = "PythonState";
    // bytes to use in STATE
    private static final byte NONE = (byte) 0;
    private static final byte SPAWNING = (byte) 1;
    private static final byte SPIT = (byte) 2;
    // bytes to use in World#setEntityState
    private static final byte SPIT_CLIENT = 9;

    private static final Predicate<LivingEntity> CAN_TARGET = e -> {
        if (e != null && e.canChangeDimensions() && EntityPredicates.LIVING_ENTITY_STILL_ALIVE.test(e) && EntityPredicates.ATTACK_ALLOWED.test(e)) {
            final CreatureAttribute attr = e.getMobType();
            return !(attr == CreatureAttribute.ARTHROPOD || attr == CreatureAttribute.UNDEAD || attr == CreatureAttribute.WATER);
        }
        return false;
    };

    // other constants for attack, spawn, etc.
    private static final int MAX_SPAWN_TIME = 110;
    private static final int MAX_SPIT_TIME = 66;

    private final ServerBossInfo bossInfo = new ServerBossInfo(this.getDisplayName(), BossInfo.Color.GREEN, BossInfo.Overlay.PROGRESS);

    private int spawnTime;
    private int spitTime;

    public PythonEntity(final EntityType<? extends PythonEntity> type, final World worldIn) {
        super(type, worldIn);
        this.maxUpStep = 1.0F;
        this.xpReward = 50;
    }

    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return MobEntity.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 70.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.31D)
                .add(Attributes.ATTACK_DAMAGE, 4.5D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.8D)
                .add(Attributes.FOLLOW_RANGE, 32.0D)
                .add(Attributes.ARMOR, 1.5D);
    }

    @Override
    public void defineSynchedData() {
        super.defineSynchedData();
        this.getEntityData().define(STATE, Byte.valueOf(NONE));
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new SwimGoal(this));
        this.goalSelector.addGoal(3, new PythonEntity.PoisonSpitAttackGoal(this, MAX_SPIT_TIME, 3, 165));
        this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomWalkingGoal(this, 0.7D));
        this.goalSelector.addGoal(6, new LookAtGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.addGoal(7, new LookRandomlyGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AnimalEntity.class, 10, false, false, CAN_TARGET));
    }

    @Override
    public void aiStep() {
        super.aiStep();

        // boss info
        this.bossInfo.setPercent(this.getHealth() / this.getMaxHealth());

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
    public boolean onClimbable() {
        return false;
    }

    // Boss //

    @Override
    public void startSeenByPlayer(ServerPlayerEntity player) {
        super.startSeenByPlayer(player);
        this.bossInfo.addPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayerEntity player) {
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
    protected float getVoicePitch() {
        return 0.6F + random.nextFloat() * 0.2F;
    }

    // NBT //

    @Override
    public void addAdditionalSaveData(CompoundNBT compound) {
        super.addAdditionalSaveData(compound);
        compound.putByte(KEY_STATE, this.getPythonState());
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT compound) {
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
    public void onSyncedDataUpdated(final DataParameter<?> key) {
        super.onSyncedDataUpdated(key);
        if (key == STATE) {
            this.spawnTime = isSpawning() ? MAX_SPAWN_TIME : 0;
        }
    }

    @OnlyIn(Dist.CLIENT)
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
            PoisonSpitEntity healingSpell = PoisonSpitEntity.create(level, this);
            level.addFreshEntity(healingSpell);
        }
        this.playSound(SoundEvents.LLAMA_SPIT, 1.2F, 1.0F);
    }


    // Goals //

    class PoisonSpitAttackGoal extends IntervalRangedAttackGoal {

        protected PoisonSpitAttackGoal(final IRangedAttackMob entityIn, final int duration, final int count, final int maxCooldownIn) {
            super(entityIn, duration, count, maxCooldownIn);
        }

        @Override
        public boolean canUse() {
            return super.canUse() && PythonEntity.this.isNoneState();
        }

        @Override
        public void start() {
            super.start();
            PythonEntity.this.setSpitAttack(true);
            PythonEntity.this.getCommandSenderWorld().broadcastEntityEvent(PythonEntity.this, SPIT_CLIENT);
            PythonEntity.this.playSound(SoundEvents.CREEPER_PRIMED, 1.0F, 1.2F);
        }

        @Override
        public void stop() {
            super.stop();
            PythonEntity.this.setSpitAttack(false);
        }
    }
}
