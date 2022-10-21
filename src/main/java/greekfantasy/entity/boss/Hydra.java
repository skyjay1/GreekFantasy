package greekfantasy.entity.boss;

import greekfantasy.GFRegistry;
import greekfantasy.GreekFantasy;
import greekfantasy.entity.Cerastes;
import greekfantasy.entity.ai.MoveToStructureGoal;
import greekfantasy.entity.util.GFMobType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.BossEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraftforge.common.ForgeMod;

import javax.annotation.Nullable;

public class Hydra extends Monster {

    private static final EntityDataAccessor<Byte> HEADS = SynchedEntityData.defineId(Hydra.class, EntityDataSerializers.BYTE);
    private static final String KEY_HEADS = "Heads";

    public static final int MAX_HEADS = 11;

    private final ServerBossEvent bossInfo = new ServerBossEvent(this.getDisplayName(), BossEvent.BossBarColor.RED, BossEvent.BossBarOverlay.PROGRESS);

    public Hydra(final EntityType<? extends Hydra> type, final Level worldIn) {
        super(type, worldIn);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 200.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.24D)
                .add(Attributes.ATTACK_DAMAGE, 0.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.66D)
                .add(Attributes.ARMOR, 14.0D)
                .add(Attributes.FOLLOW_RANGE, 16.0D)
                .add(ForgeMod.STEP_HEIGHT_ADDITION.get(), 0.6D);
    }
    
    public static Hydra spawnHydra(final ServerLevel level, final Cerastes cerastes) {
        Hydra entity = GFRegistry.EntityReg.HYDRA.get().create(level);
        entity.copyPosition(cerastes);
        entity.yBodyRot = cerastes.yBodyRot;
        if (cerastes.hasCustomName()) {
            entity.setCustomName(cerastes.getCustomName());
            entity.setCustomNameVisible(cerastes.isCustomNameVisible());
        }
        entity.setPersistenceRequired();
        entity.setPortalCooldown();
        level.addFreshEntity(entity);
        entity.finalizeSpawn(level, level.getCurrentDifficultyAt(cerastes.blockPosition()), MobSpawnType.CONVERSION, null, null);
        // remove the old cerastes
        cerastes.discard();
        // give potion effects
        entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 60));
        entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 60));
        entity.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 60));
        // play sound
        entity.playSound(SoundEvents.WITHER_SPAWN, 1.2F, 1.0F);
        return entity;
    }

    @Override
    public void defineSynchedData() {
        super.defineSynchedData();
        this.getEntityData().define(HEADS, (byte) 0);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new Hydra.MoveToTargetGoal(this, 1.0D, false));
        if(GreekFantasy.CONFIG.HYDRA_SEEK_LAIR.get()) {
            this.goalSelector.addGoal(4, new MoveToStructureGoal(this, 1.0D, 4, 8, 4, new ResourceLocation(GreekFantasy.MODID, "hydra_lair"), DefaultRandomPos::getPos));
        }
        this.goalSelector.addGoal(5, new RandomStrollGoal(this, 0.8D) {
            @Override
            public boolean canUse() {
                return Hydra.this.random.nextInt(400) == 0 && super.canUse();
            }
        });
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Player.class, false));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Animal.class, false, false));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, false, false));
    }

    @Override
    public void aiStep() {
        super.aiStep();

        // boss info
        this.bossInfo.setProgress(this.getHealth() / this.getMaxHealth());

        if (!getPassengers().isEmpty() && !level.isClientSide()) {
            // determine if all heads are charred
            int charred = 0;
            HydraHead head;
            for (final Entity entity : getPassengers()) {
                head = (HydraHead) entity;
                if (head.isCharred()) {
                    charred++;
                }
            }
            // if all heads are charred, kill the hydra; otherwise, heal the hydra
            if (charred == getHeads()) {
                DamageSource source = this.getLastDamageSource();
                hurt(source != null ? source : DamageSource.STARVE, getMaxHealth() * 2.0F);
                getPassengers().forEach(e -> e.discard());
            } else if (getHealth() < getMaxHealth() && random.nextFloat() < 0.125F) {
                heal(1.25F * (getHeads() - charred));
            }
        }
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType,
                                        @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag) {
        SpawnGroupData data = super.finalizeSpawn(level, difficulty, spawnType, spawnDataIn, dataTag);
        addHead(0);
        addHead(1);
        addHead(2);
        setBaby(false);
        return data;
    }

    @Override
    public boolean canBeAffected(MobEffectInstance potioneffectIn) {
        if (potioneffectIn.getEffect() == MobEffects.POISON) {
            net.minecraftforge.event.entity.living.PotionEvent.PotionApplicableEvent event = new net.minecraftforge.event.entity.living.PotionEvent.PotionApplicableEvent(
                    this, potioneffectIn);
            net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event);
            return event.getResult() == net.minecraftforge.eventbus.api.Event.Result.ALLOW;
        }
        return super.canBeAffected(potioneffectIn);
    }

    @Override
    public MobType getMobType() {
        return GFMobType.SERPENT;
    }

    @Override
    public boolean requiresCustomPersistence() {
        return this.getHeads() != 3 || super.requiresCustomPersistence();
    }

    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        this.bossInfo.addPlayer(player);
        if(this.hasCustomName()) {
            bossInfo.setName(this.getCustomName());
        }
        bossInfo.setVisible(GreekFantasy.CONFIG.showHydraBossBar());
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        this.bossInfo.removePlayer(player);
    }

    // Heads //

    public int getHeads() {
        return getEntityData().get(HEADS).intValue();
    }

    public void setHeads(final int heads) {
        getEntityData().set(HEADS, (byte) heads);
    }

    /**
     * Adds a head to this hydra
     *
     * @param id a unique id of the head
     * @return the hydra head entity
     */
    public HydraHead addHead(final int id) {
        // GreekFantasy.LOGGER.debug("Adding head with id " + id);
        if (!level.isClientSide()) {
            HydraHead head = GFRegistry.EntityReg.HYDRA_HEAD.get().create(level);
            head.moveTo(getX(), getY(), getZ(), 0, 0);
            // add the entity to the world (commented out bc of errors: "trying to add entity with duplicated UUID ...")
            // world.addEntity(head);
            // update the entity data
            head.setPartId(id);
            if (head.startRiding(this)) {
                // increase the number of heads
                setHeads(getHeads() + 1);
            } else {
                head.discard();
            }
            return head;
        }
        return null;
    }

    @Override
    protected void removePassenger(Entity passenger) {
        super.removePassenger(passenger);
        setHeads(Math.max(0, getHeads() - 1));
    }

    @Override
    public void ejectPassengers() {
        super.ejectPassengers();
        setHeads(0);
    }

    @Override
    public void remove(RemovalReason reason) {
        super.remove(reason);
        for (final Entity e : getPassengers()) {
            if (e.getType() == GFRegistry.EntityReg.HYDRA_HEAD.get()) {
                e.remove(reason);
            }
        }
    }

    @Override
    protected boolean canRide(Entity entity) {
        return false;
    }

    @Override
    protected boolean canAddPassenger(Entity passenger) {
        return this.getPassengers().size() < MAX_HEADS;
    }

    public void updatePassenger(Entity passenger, int id, Entity.MoveFunction callback) {
        if (this.hasPassenger(passenger)) {
            int headsPerRow = MAX_HEADS / 2;
            int row = id / headsPerRow;
            double heads = getHeads();
            double radius = 0.08D + 0.3D * getBbWidth() + 0.35D * row;
            // the index location of the head, based on id and row, and centered based on total heads
            double index = ((double) (id % headsPerRow)) - 0.92D * getBbWidth();
            // the angle to add based on hydra rotation yaw
            double angleOff = Math.toRadians(this.getYRot() + (heads / headsPerRow) * 6.0F) + Math.PI / 2.0D;
            // determine x,y,z position for the head
            double dx = this.getX() + radius * Math.cos(index / Math.PI + angleOff);
            double dy = this.getY() + this.getPassengersRidingOffset() + passenger.getMyRidingOffset();
            double dz = this.getZ() + radius * Math.sin(index / Math.PI + angleOff);
            callback.accept(passenger, dx, dy, dz);
        }
    }

    @Override
    public double getPassengersRidingOffset() {
        return super.getPassengersRidingOffset() + 0.32D;
    }

    // Sounds //

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.BLAZE_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.GENERIC_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.GENERIC_DEATH;
    }

    @Override
    protected float getSoundVolume() {
        return 1.2F;
    }

    // NBT methods //

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putByte(KEY_HEADS, (byte) getHeads());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        setHeads(compound.getByte(KEY_HEADS));
    }

    class MoveToTargetGoal extends MeleeAttackGoal {

        public MoveToTargetGoal(PathfinderMob creature, double speedIn, boolean useLongMemoryIn) {
            super(creature, speedIn, useLongMemoryIn);
        }

        @Override
        protected void checkAndPerformAttack(LivingEntity enemy, double distToEnemySqr) {
            double d0 = this.getAttackReachSqr(enemy);
            if (distToEnemySqr <= d0 && this.getTicksUntilNextAttack() <= 0) {
                this.resetAttackCooldown();
                Hydra.this.setLastHurtMob(enemy);
                // this version of the goal intentionally does *not* attack the target, that will be done by the heads
            }
        }
    }
}
