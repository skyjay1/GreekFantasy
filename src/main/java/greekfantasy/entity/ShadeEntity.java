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
  
  protected static final DataParameter<Integer> DATA_XP = EntityDataManager.createKey(ShadeEntity.class, DataSerializers.VARINT);
  protected static final DataParameter<Optional<UUID>> OWNER_UNIQUE_ID = EntityDataManager.createKey(ShadeEntity.class, DataSerializers.OPTIONAL_UNIQUE_ID);
  
  protected static final String KEY_XP = "StoredXP";
  protected static final String KEY_OWNER = "Owner";
  protected static final String KEY_DESPAWN = "NoDespawn";
  
  public ShadeEntity(final EntityType<? extends ShadeEntity> type, final World worldIn) {
    super(type, worldIn);
    this.stepHeight = 1.0F;
    this.experienceValue = 0;
  }
  
  public static AttributeModifierMap.MutableAttribute getAttributes() {
    return MobEntity.func_233666_p_()
        .createMutableAttribute(Attributes.MAX_HEALTH, 12.0D)
        .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.21D)
        .createMutableAttribute(Attributes.KNOCKBACK_RESISTANCE, 0.86D)
        .createMutableAttribute(Attributes.ATTACK_DAMAGE, 0.1D);
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
  protected void registerData() {
    super.registerData();
    this.getDataManager().register(DATA_XP, Integer.valueOf(0));
    this.getDataManager().register(OWNER_UNIQUE_ID, Optional.empty());
  }
  
  @Override
  public void livingTick() {
    super.livingTick();
    // spawn particles
    if (world.isRemote()) {
      final double motion = 0.08D;
      final double radius = 1.2D;
      for (int i = 0; i < 5; i++) {
        world.addParticle(ParticleTypes.SMOKE, 
            this.getPosX() + (world.rand.nextDouble() - 0.5D) * radius, 
            this.getPosY() + 0.75D + (world.rand.nextDouble() - 0.5D) * radius * 0.75D, 
            this.getPosZ() + (world.rand.nextDouble() - 0.5D) * radius,
            (world.rand.nextDouble() - 0.5D) * motion, 
            (world.rand.nextDouble() - 0.5D) * motion * 0.5D,
            (world.rand.nextDouble() - 0.5D) * motion);
      }
    }
  }

  @Override
  public boolean attackEntityAsMob(final Entity entity) {
    if (super.attackEntityAsMob(entity)) {
      // remove XP or give wither effect
      if(entity instanceof PlayerEntity) {
        final PlayerEntity player = (PlayerEntity)entity;
        if(GreekFantasy.CONFIG.SHADE_ATTACK.get() && player.experienceTotal > 0) {
          // steal XP from player
          final int xpSteal = Math.min(player.experienceTotal, 10);
          player.giveExperiencePoints(-xpSteal);
          this.setStoredXP(this.getStoredXP() + xpSteal);
        } else {
          // brief wither effect
          player.addPotionEffect(new EffectInstance(Effects.WITHER, 80));
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
    return source.getTrueSource() instanceof PlayerEntity && isInvulnerableToPlayer((PlayerEntity)source.getTrueSource());
  }
  
  @Override
  protected SoundEvent getAmbientSound() { return SoundEvents.AMBIENT_CAVE; }
  @Override
  protected SoundEvent getHurtSound(DamageSource damageSourceIn) { return SoundEvents.ENTITY_ENDERMAN_HURT; }
  @Override
  protected float getSoundVolume() { return 0.8F; }
  @Override
  public boolean canAttack(final EntityType<?> typeIn) { return typeIn == EntityType.PLAYER; }
  @Override
  public boolean canBePushed() { return false; }
  @Override
  protected void collideWithNearbyEntities() { }
  @Override
  public CreatureAttribute getCreatureAttribute() { return CreatureAttribute.UNDEAD; }
  @Override
  protected boolean isDespawnPeaceful() { return true; }
  @Override
  public boolean onLivingFall(float distance, float damageMultiplier) { return false; }
  @Override
  protected void updateFallState(double y, boolean onGroundIn, BlockState state, BlockPos pos) { }
  
  @Nullable
  public UUID getOwnerUniqueId() { return this.dataManager.get(OWNER_UNIQUE_ID).orElse((UUID)null); }
  
  public void setOwnerUniqueId(@Nullable UUID uniqueId) { this.dataManager.set(OWNER_UNIQUE_ID, Optional.ofNullable(uniqueId)); }
  
  public int getStoredXP() { return this.getDataManager().get(DATA_XP).intValue(); }
  
  public void setStoredXP(int xp) { this.getDataManager().set(DATA_XP, xp); }

  @Override
  public void writeAdditional(CompoundNBT compound) {
    super.writeAdditional(compound);
    compound.putInt(KEY_XP, this.getStoredXP());
    if (this.getOwnerUniqueId() != null) {
      compound.putUniqueId(KEY_OWNER, this.getOwnerUniqueId());
    }
  }

  @Override
  public void readAdditional(CompoundNBT compound) {
    super.readAdditional(compound);
    this.setStoredXP(compound.getInt(KEY_XP));
    if (compound.hasUniqueId(KEY_OWNER)) {
       this.setOwnerUniqueId(compound.getUniqueId(KEY_OWNER));
    }
  }

  @Override
  protected int getExperiencePoints(final PlayerEntity attackingPlayer) {
    return 0;
  }
  
  @Override
  public ILivingEntityData onInitialSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
    if(this.getStoredXP() == 0) {
      this.setStoredXP(5 + this.rand.nextInt(10));
    }
    return super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
  }
  
  @Override
  public void onDeath(final DamageSource source) {
    if(source.getTrueSource() instanceof PlayerEntity) {
      PlayerEntity player = (PlayerEntity)source.getTrueSource();
      player.giveExperiencePoints(getStoredXP());
    }
    
    super.onDeath(source);
  }
  
  public boolean canTargetPlayerEntity(final LivingEntity entity) {
    return entity instanceof PlayerEntity && !isInvulnerableToPlayer((PlayerEntity)entity);
  }
  
  public boolean isInvulnerableToPlayer(final PlayerEntity player) {
    if(GreekFantasy.CONFIG.SHADE_PLAYER_ONLY.get() && !player.isCreative()) {
      // check uuid to see if it matches
      final UUID uuidPlayer = PlayerEntity.getOfflineUUID(player.getDisplayName().getUnformattedComponentText());
      return this.getOwnerUniqueId() != null && !uuidPlayer.equals(this.getOwnerUniqueId());
    }
    return false;
  }
}
