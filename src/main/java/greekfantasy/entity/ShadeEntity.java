package greekfantasy.entity;

import java.util.Optional;
import java.util.UUID;

import javax.annotation.Nullable;

import greekfantasy.GreekFantasy;
import net.minecraft.block.BlockState;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;

public class ShadeEntity extends MonsterEntity {
  
  protected static final DataParameter<Integer> DATA_XP = EntityDataManager.defineId(ShadeEntity.class, DataSerializers.INT);
  protected static final DataParameter<Optional<UUID>> OWNER_UNIQUE_ID = EntityDataManager.defineId(ShadeEntity.class, DataSerializers.OPTIONAL_UUID);
  
  protected static final String KEY_XP = "StoredXP";
  protected static final String KEY_OWNER = "Owner";
  protected static final String KEY_DESPAWN = "NoDespawn";
  
  public ShadeEntity(final EntityType<? extends ShadeEntity> type, final World worldIn) {
    super(type, worldIn);
    this.maxUpStep = 1.0F;
    this.xpReward = 0;
  }
  
  public static AttributeModifierMap.MutableAttribute getAttributes() {
    return MobEntity.createMobAttributes()
        .add(Attributes.MAX_HEALTH, 12.0D)
        .add(Attributes.MOVEMENT_SPEED, 0.21D)
        .add(Attributes.KNOCKBACK_RESISTANCE, 0.86D)
        .add(Attributes.ATTACK_DAMAGE, 0.1D);
  }
  
  @Override
  protected void registerGoals() {
    super.registerGoals();
    this.goalSelector.addGoal(0, new SwimGoal(this));
    this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.0D, true));
    this.goalSelector.addGoal(4, new LookAtGoal(this, PlayerEntity.class, 12.0F));
    this.goalSelector.addGoal(5, new LookRandomlyGoal(this));
    this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
    this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, 10, true, true, this::canTargetPlayerEntity));
  }
  
  @Override
  protected void defineSynchedData() {
    super.defineSynchedData();
    this.getEntityData().define(DATA_XP, Integer.valueOf(0));
    this.getEntityData().define(OWNER_UNIQUE_ID, Optional.empty());
  }
  
  @Override
  public void aiStep() {
    super.aiStep();
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
      if(entity instanceof PlayerEntity) {
        final PlayerEntity player = (PlayerEntity)entity;
        if(GreekFantasy.CONFIG.SHADE_ATTACK.get() && player.totalExperience > 0) {
          // steal XP from player
          final int xpSteal = Math.min(player.totalExperience, 10);
          player.giveExperiencePoints(-xpSteal);
          this.setStoredXP(this.getStoredXP() + xpSteal);
        } else {
          // brief wither effect
          player.addEffect(new EffectInstance(Effects.WITHER, 80));
        }
      }
      return true;
    }
    return false;
  }
  
  @Override
  public boolean isInvulnerableTo(final DamageSource source) {
    if(super.isInvulnerableTo(source)) {
      return true;
    }
    return source.getEntity() instanceof PlayerEntity && isInvulnerableToPlayer((PlayerEntity)source.getEntity());
  }
  
  @Override
  protected SoundEvent getAmbientSound() { return SoundEvents.AMBIENT_CAVE; }
  @Override
  protected SoundEvent getHurtSound(DamageSource damageSourceIn) { return SoundEvents.ENDERMAN_HURT; }
  @Override
  protected float getSoundVolume() { return 0.8F; }
  @Override
  public boolean canAttackType(final EntityType<?> typeIn) { return typeIn == EntityType.PLAYER; }
  @Override
  public boolean isPushable() { return false; }
  @Override
  protected void pushEntities() { }
  @Override
  public CreatureAttribute getMobType() { return CreatureAttribute.UNDEAD; }
  @Override
  protected boolean shouldDespawnInPeaceful() { return true; }
  @Override
  public boolean causeFallDamage(float distance, float damageMultiplier) { return false; }
  @Override
  protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) { }
  
  @Nullable
  public UUID getOwnerUniqueId() { return this.entityData.get(OWNER_UNIQUE_ID).orElse((UUID)null); }
  
  public void setOwnerUniqueId(@Nullable UUID uniqueId) { this.entityData.set(OWNER_UNIQUE_ID, Optional.ofNullable(uniqueId)); }
  
  public int getStoredXP() { return this.getEntityData().get(DATA_XP).intValue(); }
  
  public void setStoredXP(int xp) { this.getEntityData().set(DATA_XP, xp); }

  @Override
  public void addAdditionalSaveData(CompoundNBT compound) {
    super.addAdditionalSaveData(compound);
    compound.putInt(KEY_XP, this.getStoredXP());
    if (this.getOwnerUniqueId() != null) {
      compound.putUUID(KEY_OWNER, this.getOwnerUniqueId());
    }
  }

  @Override
  public void readAdditionalSaveData(CompoundNBT compound) {
    super.readAdditionalSaveData(compound);
    this.setStoredXP(compound.getInt(KEY_XP));
    if (compound.hasUUID(KEY_OWNER)) {
       this.setOwnerUniqueId(compound.getUUID(KEY_OWNER));
    }
  }

  @Override
  protected int getExperienceReward(final PlayerEntity attackingPlayer) {
    return 0;
  }
  
  @Override
  public ILivingEntityData finalizeSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
    if(this.getStoredXP() == 0) {
      this.setStoredXP(5 + this.random.nextInt(10));
    }
    return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
  }
  
  @Override
  public void die(final DamageSource source) {
    if(source.getEntity() instanceof PlayerEntity) {
      PlayerEntity player = (PlayerEntity)source.getEntity();
      player.giveExperiencePoints(getStoredXP());
    }
    
    super.die(source);
  }
  
  public boolean canTargetPlayerEntity(final LivingEntity entity) {
    return entity instanceof PlayerEntity && !isInvulnerableToPlayer((PlayerEntity)entity);
  }
  
  public boolean isInvulnerableToPlayer(final PlayerEntity player) {
    if(GreekFantasy.CONFIG.SHADE_PLAYER_ONLY.get() && !player.isCreative()) {
      // check uuid to see if it matches
      final UUID uuidPlayer = PlayerEntity.createPlayerUUID(player.getDisplayName().getContents());
      return this.getOwnerUniqueId() != null && !uuidPlayer.equals(this.getOwnerUniqueId());
    }
    return false;
  }
}
