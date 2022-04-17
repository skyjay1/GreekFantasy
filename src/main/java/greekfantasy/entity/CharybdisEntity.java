package greekfantasy.entity;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import javax.annotation.Nullable;

import greekfantasy.GFRegistry;
import greekfantasy.entity.misc.ISwimmingMob;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.item.BoatEntity;
import net.minecraft.entity.passive.WaterMobEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.SwimmerPathNavigator;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.BossInfo;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerBossInfo;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CharybdisEntity extends WaterMobEntity implements ISwimmingMob {
  
  private static final DataParameter<Byte> STATE = EntityDataManager.defineId(CharybdisEntity.class, DataSerializers.BYTE);
  private static final String KEY_STATE = "CharybdisState";
  // bytes to use in STATE
  private static final byte NONE = (byte)0;
  private static final byte SPAWNING = (byte)1;
  private static final byte SWIRLING = (byte)2;
  private static final byte THROWING = (byte)4;
  //bytes to use in World#setEntityState
  private static final byte NONE_CLIENT = 8;
  private static final byte SPAWN_CLIENT = 9;
  private static final byte SWIRL_CLIENT = 10;
  private static final byte THROW_CLIENT = 11;
 
  // other constants for attack, spawn, etc.
  private static final double RANGE = 10.0D;
  private static final int SPAWN_TIME = 50;
  private static final int SWIRL_TIME = 240;
  private static final int THROW_TIME = 34;
 
  private final ServerBossInfo bossInfo = (ServerBossInfo)(new ServerBossInfo(this.getDisplayName(), BossInfo.Color.BLUE, BossInfo.Overlay.PROGRESS));
  
  private int spawnTime;
  private int swirlTime;
  private int throwTime;
  
  private boolean swimmingUp;
  
  public CharybdisEntity(final EntityType<? extends CharybdisEntity> type, final World worldIn) {
    super(type, worldIn);
    this.maxUpStep = 1.0F;
    this.xpReward = 50;
    this.navigation = new SwimmerPathNavigator(this, worldIn);
  }

  public static CharybdisEntity spawnCharybdis(final ServerWorld world, final WhirlEntity whirl) {
    CharybdisEntity entity = GFRegistry.CHARYBDIS_ENTITY.create(world);
    entity.copyPosition(whirl);
    entity.finalizeSpawn(world, world.getCurrentDifficultyAt(whirl.blockPosition()), SpawnReason.CONVERSION, (ILivingEntityData)null, (CompoundNBT)null);
    if(whirl.hasCustomName()) {
      entity.setCustomName(whirl.getCustomName());
      entity.setCustomNameVisible(whirl.isCustomNameVisible());
    }
    entity.setPersistenceRequired();
    entity.yBodyRot = whirl.yBodyRot;
    entity.setPortalCooldown();
    world.addFreshEntity(entity);
    entity.setState(SPAWNING);
    // remove the old whirl
    whirl.remove();
    // trigger spawn for nearby players
    for (ServerPlayerEntity player : world.getEntitiesOfClass(ServerPlayerEntity.class, entity.getBoundingBox().inflate(16.0D))) {
      CriteriaTriggers.SUMMONED_ENTITY.trigger(player, entity);
    }
    // play sound
    world.playLocalSound(entity.getX(), entity.getY(), entity.getZ(), SoundEvents.WITHER_SPAWN, entity.getSoundSource(), 1.2F, 1.0F, false);
    return entity;
  }
  public static AttributeModifierMap.MutableAttribute getAttributes() {
    return MobEntity.createMobAttributes()
        .add(Attributes.MAX_HEALTH, 160.0D)
        .add(Attributes.MOVEMENT_SPEED, 0.15D)
        .add(Attributes.ATTACK_DAMAGE, 4.5D)
        .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
        .add(Attributes.FOLLOW_RANGE, 48.0D);
  }
  
  @Override
  public void defineSynchedData() {
    super.defineSynchedData();
    this.getEntityData().define(STATE, Byte.valueOf(NONE));
  }
  
  @Override
  protected void registerGoals() {
    super.registerGoals();
    this.goalSelector.addGoal(1, new CharybdisEntity.SwimToSurfaceGoal());
    this.goalSelector.addGoal(2, new CharybdisEntity.SwirlGoal(this, SWIRL_TIME, 114, RANGE));
    this.goalSelector.addGoal(3, new CharybdisEntity.ThrowGoal(THROW_TIME, 82, RANGE * 0.75D));
  }
  
  @Override
  public void aiStep() {

    super.aiStep();
    
    // boss info
    this.bossInfo.setPercent(this.getHealth() / this.getMaxHealth());
    
    // spawn particles
    if(this.level.isClientSide() && tickCount % 3 == 0 && this.isInWaterOrBubble()/* && isSwirling()*/) {
      // spawn particles at targeted entities
      getEntitiesInRange(RANGE).forEach(e -> bubbles(e.getX(), e.getY(), e.getZ(), e.getBbWidth(), 5));
      // spawn particles in spiral
      float maxY = this.getBbHeight() * this.getSpawnPercent() * 1.65F;
      float y = 0;
      float nY = 120 * this.getSpawnPercent();
      float dY = maxY / nY;
      double posX = this.getX();
      double posY = this.getY();
      double posZ = this.getZ();
      // for each y-position, increase the angle and spawn particle here
      for(float a = 0, nA = 28 + random.nextInt(4), dA = (2 * (float)Math.PI) / nA; y < maxY; a += dA) {
        float radius = y * 0.5F;
        float cosA = MathHelper.cos(a) * radius;
        float sinA = MathHelper.sin(a) * radius;
        //bubbles(posX + cosA, posY + y, posZ + sinA, 0.125D, 1);
        level.addParticle(ParticleTypes.BUBBLE, posX + cosA, posY + y - (maxY * 0.4), posZ + sinA, 0.0D, 0.085D, 0.0D);
        y += dY;
      }
    }
    
    // update spawn time
    if(isSpawning()) {
      // update timer
      if(--spawnTime <= 0) {
        setState(NONE);
      }
    }
    
    // update swirl attack
    if(isSwirling()) {
      swirlTime++;
      this.setRot(this.yRot + 1, this.xRot);
    } else if(swirlTime > 0) {
      swirlTime = 0;
    }
    
    // update throw attack
    if(isThrowing()) {
      throwTime++;
    } else if(throwTime > 0) {
      throwTime = 0;
    }
  }

  // Misc //
  
  @Override
  public ILivingEntityData finalizeSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
     ILivingEntityData data = super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
     this.setState(SPAWNING);
     return data;
  }
  
  @Override
  protected boolean shouldDespawnInPeaceful() { return true; }
  
  @Override
  public boolean canChangeDimensions() { return false; }
  
  @Override
  public boolean removeWhenFarAway(final double disToPlayer) { return false; }
  
  @Override
  protected boolean canRide(Entity entityIn) { return false; }
  
  @Override
  public boolean isInvulnerableTo(final DamageSource source) {
    return isSpawning() || source == DamageSource.IN_WALL || source == DamageSource.WITHER 
        || source.getDirectEntity() instanceof AbstractArrowEntity || super.isInvulnerableTo(source);
  }
  
  @Override
  protected void handleAirSupply(int air) { }
  
  // Prevent entity collisions //
  
  @Override
  public boolean isPushable() { return false; }
  
  @Override
  protected void pushEntities() { }
  
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
  protected SoundEvent getAmbientSound() { return SoundEvents.HOSTILE_SPLASH; }

  @Override
  protected SoundEvent getHurtSound(DamageSource damageSourceIn) { return SoundEvents.ELDER_GUARDIAN_HURT; }

  @Override
  protected SoundEvent getDeathSound() { return SoundEvents.ELDER_GUARDIAN_DEATH; }

  @Override
  protected float getSoundVolume() { return 1.0F; }
  
  @Override
  protected float getVoicePitch() { return 0.6F + random.nextFloat() * 0.2F; }
  
  // NBT //
  
  @Override
  public void addAdditionalSaveData(CompoundNBT compound) {
     super.addAdditionalSaveData(compound);
     compound.putByte(KEY_STATE, this.getState());
     compound.putInt("SpawnTime", spawnTime);
     compound.putInt("SwirlTime", swirlTime);
     compound.putInt("ThrowTime", throwTime);
  }

  @Override
  public void readAdditionalSaveData(CompoundNBT compound) {
     super.readAdditionalSaveData(compound);
     this.setState(compound.getByte(KEY_STATE));
     spawnTime = compound.getInt("SpawnTime");
     swirlTime = compound.getInt("SwirlTime");
     throwTime = compound.getInt("ThrowTime");
  }
  
  // Swimming //
  
  @Override
  public void setSwimmingUp(boolean swimmingUp) { this.swimmingUp = swimmingUp; }

  @Override
  public boolean isSwimmingUp() { return swimmingUp; }
  
  @Override
  public void travel(final Vector3d vec) {
    if (isEffectiveAi() && isInWater() && isSwimmingUpCalculated()) {
      moveRelative(0.01F, vec);
      move(MoverType.SELF, getDeltaMovement());
      setDeltaMovement(getDeltaMovement().scale(0.9D));
    } else {
      super.travel(vec);
    }
  }

  @Override
  public boolean isPushedByFluid() { return false; }

  @Override
  public boolean isSwimmingUpCalculated() {
    if (this.swimmingUp) {
      return true;
    }
    LivingEntity e = getTarget();
    return e != null && e.isInWater();
  }
  
  // States //
  
  public void setState(final byte state) { 
    this.getEntityData().set(STATE, Byte.valueOf(state));
    byte clientFlag = NONE_CLIENT;
    switch(state) {
    case NONE:
      spawnTime = swirlTime = throwTime = 0;
      break;
    case SPAWNING:
      spawnTime = SPAWN_TIME;
      swirlTime = 0;
      throwTime = 0;
      clientFlag = SPAWN_CLIENT;
      break;
    case SWIRLING:
      spawnTime = 0;
      swirlTime = 1;
      throwTime = 0;
      clientFlag = SWIRL_CLIENT;
      break;
    case THROWING:
      spawnTime = 0;
      swirlTime = 0;
      throwTime = 1;
      clientFlag = THROW_CLIENT;
      break;
    }
    if(!level.isClientSide()) {
      level.broadcastEntityEvent(this, clientFlag);
    }
  }
  
  public byte getState() { return this.getEntityData().get(STATE).byteValue(); }
  
  public boolean isNoneState() { return getState() == NONE; }
  
  public boolean isSpawning() { return spawnTime > 0 || getState() == SPAWNING; }
  
  public boolean isSwirling() { return swirlTime > 0 || getState() == SWIRLING; }
  
  public boolean isThrowing() { return throwTime > 0 || getState() == THROWING; }
  
  public float getSpawnPercent() { return 1.0F - ((float)spawnTime / (float)SPAWN_TIME); }
  
  public float getSwirlPercent() { return (float)swirlTime / (float)SWIRL_TIME; }
  
  public float getThrowPercent() { return (float)throwTime / (float)THROW_TIME; }
  
  @OnlyIn(Dist.CLIENT)
  public void handleEntityEvent(byte id) {
    switch(id) {
    case NONE:
      setState(NONE);
      break;
    case SPAWN_CLIENT:
      setState(SPAWNING);
      break;
    case SWIRL_CLIENT:
      setState(SWIRLING);
      break;
    case THROW_CLIENT:
      setState(THROWING);
      break;
    default:
      super.handleEntityEvent(id);
      break;
    }
  }
  
  // Misc //

  public boolean isWithinDistance(final Entity e, final float dis) {
    return this.distanceToSqr(e) < (dis * dis);
  }
  
  public List<Entity> getEntitiesInRange(final double range) {
    return getCommandSenderWorld().getEntities(this, getBoundingBox().inflate(range, range / 2, range), EntityPredicates.NO_CREATIVE_OR_SPECTATOR.and(e -> e.isInWaterOrBubble()));
  }
  
  public void bubbles(final double posX, final double posY, final double posZ, final double radius, final int count) {
    final double motion = 0.08D;
    for (int i = 0; i < count; i++) {
      level.addParticle(ParticleTypes.BUBBLE, 
          posX + (level.random.nextDouble() - 0.5D) * radius, 
          posY, 
          posZ + (level.random.nextDouble() - 0.5D) * radius,
          (level.random.nextDouble() - 0.5D) * motion, 
          0.5D,
          (level.random.nextDouble() - 0.5D) * motion);
    }
  }
  
  // Goals //
  
  class SwimToSurfaceGoal extends SwimGoal {
    
    public SwimToSurfaceGoal() {
      super(CharybdisEntity.this);
    }
    
    @Override
    public boolean canUse() {
      BlockPos pos = CharybdisEntity.this.blockPosition().above((int)Math.ceil(CharybdisEntity.this.getBbHeight()));
      BlockState state = CharybdisEntity.this.level.getBlockState(pos);
      return state.getBlock() == Blocks.WATER && super.canUse();
    }
   
  }
  
  private static class SwirlGoal extends greekfantasy.entity.ai.SwirlGoal<CharybdisEntity> {

    public SwirlGoal(final CharybdisEntity entityIn, final int lDuration, final int lCooldown, final double lRange) {
      super(entityIn, lDuration, lCooldown, lRange, true);
    }
    
    @Override
    public boolean canUse() {
      return super.canUse() && (entity.isNoneState() || entity.isSwirling());
    }
    
    @Override
    public void start() {
      super.start();
      entity.setState(SWIRLING);
      entity.swirlTime = 1;
    }
    
    @Override
    public boolean canContinueToUse() {
      return super.canContinueToUse() && entity.isSwirling();
    }
    
    @Override
    public void stop() {
      super.stop();
      entity.setState(NONE);
      entity.swirlTime = 0;
    }

    @Override
    protected void onCollideWith(Entity e) {
      // attack the entity and steal some health
      final float attack = (float)entity.getAttribute(Attributes.ATTACK_DAMAGE).getValue();
      if(e.hurt(DamageSource.mobAttack(entity), attack)) {
        entity.heal(Math.abs(attack * 0.25F));
      }
    }

    @Override
    protected boolean canSwirl(Entity e) {
      return target.test(e);
    }
  }
  
  class ThrowGoal extends Goal {
    
    protected final CharybdisEntity entity;
    protected final int duration;
    protected final int cooldown;
    protected final double range;
    
    protected List<Entity> trackedEntities = new ArrayList<>();
    protected int progressTime;
    protected int cooldownTime;
    
    /**
     * @param lDuration the maximum amount of time this goal will run
     * @param lCooldown the minimum amount of time before this goal runs again
     * @param lRange the distance at which entities should be swirled
     **/
    public ThrowGoal(final int lDuration, final int lCooldown, final double lRange) {
      this.setFlags(EnumSet.of(Goal.Flag.MOVE));
      entity = CharybdisEntity.this;
      duration = lDuration;
      cooldown = lCooldown;
      range = lRange;
      cooldownTime = 50;
    }
    
    @Override
    public boolean canUse() {
      if(cooldownTime > 0) {
        cooldownTime--;
      } else if(entity.isNoneState() || entity.isThrowing()) {
        trackedEntities = entity.getEntitiesInRange(range);
        return trackedEntities.size() > 0;
      }
      return false;
    }
    
    @Override
    public void start() {
      entity.setState(THROWING);
      entity.throwTime = 1;
      progressTime = 1;
    }
    
    @Override
    public boolean canContinueToUse() {
      return progressTime > 0 && entity.isThrowing();
    }
    
    @Override
    public void tick() {
      // goal timer
      if(progressTime++ >= duration) {
        final double widthSq = entity.getBbWidth() * entity.getBbWidth();
        // move tracked entities
        trackedEntities = entity.getEntitiesInRange(range);
        for(final Entity target : trackedEntities) {
          // distance math
          double dx = entity.getX() - target.position().x;
          double dz = entity.getZ() - target.position().z;
          final double horizDisSq = dx * dx + dz * dz;
          // throw the entity upward
          if(horizDisSq > widthSq) {
            // calculate the amount of motion to apply based on distance
            final double motion = 1.08D + 0.31D * (1.0D - (horizDisSq / (range * range)));
            target.push(0, motion, 0);
            target.hurtMarked = true;
            // damage boats and other rideable entities
            if(target instanceof BoatEntity || !target.getPassengers().isEmpty()) {
              target.hurt(DamageSource.mobAttack(entity), 6.0F);
            }
          }
        }
        stop();
      }
    }
    
    @Override
    public void stop() {
      entity.setState(NONE);
      entity.throwTime = 0;
      progressTime = 0;
      cooldownTime = cooldown;
      trackedEntities.clear();
    }
  }

}
