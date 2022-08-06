package greekfantasy.entity.monster;

import greekfantasy.GreekFantasy;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
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
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeMod;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

public class Shade extends Monster {

    protected static final EntityDataAccessor<Integer> DATA_XP = SynchedEntityData.defineId(Shade.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Optional<UUID>> OWNER_UNIQUE_ID = SynchedEntityData.defineId(Shade.class, EntityDataSerializers.OPTIONAL_UUID);

    protected static final String KEY_XP = "StoredXP";
    protected static final String KEY_OWNER = "Owner";

    public Shade(final EntityType<? extends Shade> type, final Level level) {
        super(type, level);
        this.xpReward = 0;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 12.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.21D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.86D)
                .add(Attributes.ATTACK_DAMAGE, 0.1D)
                .add(ForgeMod.STEP_HEIGHT_ADDITION.get(), 0.5D);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 12.0F));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, true, this::canTargetPlayer));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.getEntityData().define(DATA_XP, Integer.valueOf(0));
        this.getEntityData().define(OWNER_UNIQUE_ID, Optional.empty());
    }

    @Override
    public void tick() {
        super.tick();
        // spawn particles
        if (level.isClientSide()) {
            final double motion = 0.08D;
            final double radius = 1.2D;
            for (int i = 0; i < 5; i++) {
                level.addParticle(ParticleTypes.SMOKE,
                        this.getX() + (level.random.nextDouble() - 0.5D) * radius,
                        this.getY() + 0.75D + (level.random.nextDouble() - 0.5D) * radius * 0.75D,
                        this.getZ() + (level.random.nextDouble() - 0.5D) * radius,
                        (level.random.nextDouble() - 0.5D) * motion,
                        (level.random.nextDouble() - 0.5D) * motion * 0.5D,
                        (level.random.nextDouble() - 0.5D) * motion);
            }
        }
    }

    @Override
    public boolean doHurtTarget(final Entity entity) {
        if (super.doHurtTarget(entity)) {
            // remove XP or give wither effect
            if (entity instanceof Player player) {
                if (player.totalExperience > 0) {
                    // steal XP from player
                    final int xpSteal = Math.min(player.totalExperience, 10);
                    player.giveExperiencePoints(-xpSteal);
                    this.setStoredXP(this.getStoredXP() + xpSteal);
                } else {
                    // brief wither effect
                    player.addEffect(new MobEffectInstance(MobEffects.WITHER, 60));
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean canBeAffected(MobEffectInstance effectInstance) {
        return effectInstance.getEffect() != MobEffects.WITHER && super.canBeAffected(effectInstance);
    }

    @Override
    public boolean isInvulnerableTo(final DamageSource source) {
        if (super.isInvulnerableTo(source)) {
            return true;
        }
        return source.getEntity() instanceof Player player && isInvulnerableToPlayer(player);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.AMBIENT_CAVE;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.ENDERMAN_HURT;
    }

    @Override
    protected float getSoundVolume() {
        return 0.8F;
    }

    @Override
    public boolean canAttackType(final EntityType<?> typeIn) {
        return typeIn == EntityType.PLAYER;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    protected void pushEntities() {
    }

    @Override
    public MobType getMobType() {
        return MobType.UNDEAD;
    }

    @Override
    protected boolean shouldDespawnInPeaceful() {
        return true;
    }

    @Override
    public boolean causeFallDamage(float distance, float damageMultiplier, DamageSource damageSource) {
        return false;
    }

    @Override
    protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
    }

    @Nullable
    public UUID getOwnerUUID() {
        return this.entityData.get(OWNER_UNIQUE_ID).orElse(null);
    }

    public void setOwnerUUID(@Nullable UUID uniqueId) {
        this.entityData.set(OWNER_UNIQUE_ID, Optional.ofNullable(uniqueId));
    }

    public int getStoredXP() {
        return this.getEntityData().get(DATA_XP).intValue();
    }

    public void setStoredXP(int xp) {
        this.getEntityData().set(DATA_XP, xp);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt(KEY_XP, this.getStoredXP());
        UUID uuid = getOwnerUUID();
        if (uuid != null) {
            compound.putUUID(KEY_OWNER, uuid);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setStoredXP(compound.getInt(KEY_XP));
        if (compound.hasUUID(KEY_OWNER)) {
            this.setOwnerUUID(compound.getUUID(KEY_OWNER));
        }
    }

    @Override
    protected boolean isAlwaysExperienceDropper() {
        return true;
    }

    @Override
    public int getExperienceReward() {
        return this.getStoredXP();
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag) {
        if (this.getStoredXP() == 0) {
            this.setStoredXP(5 + this.random.nextInt(10));
        }
        return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    public boolean canTargetPlayer(final LivingEntity entity) {
        return entity instanceof Player player && !isInvulnerableToPlayer(player);
    }

    public boolean isInvulnerableToPlayer(final Player player) {
        if (GreekFantasy.CONFIG.SHADE_IMMUNE_TO_NONOWNER.get() && !player.isCreative()) {
            // check uuid to see if it matches
            final UUID uuidPlayer = player.getUUID();
            return this.getOwnerUUID() != null && !uuidPlayer.equals(this.getOwnerUUID());
        }
        return false;
    }
}
