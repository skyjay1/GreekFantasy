package greekfantasy.entity;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;

import greekfantasy.entity.misc.ISwimmingMob;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
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
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.BossInfo;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.server.ServerBossInfo;
import net.minecraftforge.common.BiomeDictionary;

public class CharybdisEntity extends WaterMobEntity implements ISwimmingMob {
  
  private static final DataParameter<Byte> STATE = EntityDataManager.createKey(CharybdisEntity.class, DataSerializers.BYTE);
  private static final String KEY_STATE = "CharybdisState";
  // bytes to use in STATE
  private static final byte NONE = (byte)0;
//  private static final byte SPAWNING = (byte)1;
  private static final byte SWIRLING = (byte)2;
  private static final byte THROWING = (byte)4;
  
  // other constants for attack, spawn, etc.
  private static final double RANGE = 10.0D;
  private static final int SWIRL_TIME = 240;
  private static final int THROW_TIME = 34;
 
  private final ServerBossInfo bossInfo = (ServerBossInfo)(new ServerBossInfo(this.getDisplayName(), BossInfo.Color.BLUE, BossInfo.Overlay.PROGRESS));
  
//  private int spawnTime;
  private int swirlTime;
  private int throwTime;
  
  private boolean swimmingUp;

  
  public CharybdisEntity(final EntityType<? extends CharybdisEntity> type, final World worldIn) {
    super(type, worldIn);
    this.stepHeight = 1.0F;
    this.experienceValue = 50;
  }

  //copied from DolphinEntity
  public static boolean canCharybdisSpawnOn(final EntityType<? extends WaterMobEntity> entity, final IWorld world,
      final SpawnReason reason, final BlockPos pos, final Random rand) {
    if (pos.getY() <= 25 || pos.getY() >= world.getSeaLevel()) {
      return false;
    }

    RegistryKey<Biome> biome = world.func_242406_i(pos).orElse(Biomes.PLAINS);
    return (BiomeDictionary.hasType(biome, BiomeDictionary.Type.OCEAN)) && world.getFluidState(pos).isTagged(FluidTags.WATER);
  }

  public static AttributeModifierMap.MutableAttribute getAttributes() {
    return MobEntity.func_233666_p_()
        .createMutableAttribute(Attributes.MAX_HEALTH, 160.0D)
        .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.15D)
        .createMutableAttribute(Attributes.ATTACK_DAMAGE, 4.5D)
        .createMutableAttribute(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
        .createMutableAttribute(Attributes.FOLLOW_RANGE, 48.0D);
  }
  
  @Override
  public void registerData() {
    super.registerData();
    this.getDataManager().register(STATE, Byte.valueOf(NONE));
  }
  
  @Override
  protected void registerGoals() {
    super.registerGoals();
    this.goalSelector.addGoal(1, new CharybdisEntity.SwimToSurfaceGoal());
    this.goalSelector.addGoal(2, new CharybdisEntity.SwirlGoal(SWIRL_TIME, 114, RANGE));
    this.goalSelector.addGoal(3, new CharybdisEntity.ThrowGoal(THROW_TIME, 82, RANGE * 0.75D));
  }
  
  @Override
  public void livingTick() {

    super.livingTick();
    
    // boss info
    this.bossInfo.setPercent(this.getHealth() / this.getMaxHealth());
    
    // spawn particles
    if(this.world.isRemote() && ticksExisted % 3 == 0 && this.isInWaterOrBubbleColumn()/* && isSwirling()*/) {
      // spawn particles at targeted entities
      getEntitiesInRange(RANGE).forEach(e -> bubbles(e.getPosX(), e.getPosY(), e.getPosZ(), e.getWidth(), 5));
      // spawn particles in spiral
      float maxY = this.getHeight() * 1.65F;
      float y = 0;
      float nY = 120;
      float dY = maxY / nY;
      double posX = this.getPosX();
      double posY = this.getPosY();
      double posZ = this.getPosZ();
      // for each y-position, increase the angle and spawn particle here
      for(float a = 0, nA = 28 + rand.nextInt(4), dA = (2 * (float)Math.PI) / nA; y < maxY; a += dA) {
        float radius = y * 0.5F;
        float cosA = MathHelper.cos(a) * radius;
        float sinA = MathHelper.sin(a) * radius;
        //bubbles(posX + cosA, posY + y, posZ + sinA, 0.125D, 1);
        world.addParticle(ParticleTypes.BUBBLE, posX + cosA, posY + y - (maxY * 0.4), posZ + sinA, 0.0D, 0.085D, 0.0D);
        y += dY;
      }
    }
    
    // update swirl attack
    if(isSwirling()) {
      swirlTime++;
      this.setRotation(this.rotationYaw + 1, this.rotationPitch);
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
  protected boolean isDespawnPeaceful() {
    return true;
  }
  
  @Override
  public boolean isNonBoss() { return false; }
  
  @Override
  public boolean canDespawn(final double disToPlayer) { return this.ticksExisted > 9600 && disToPlayer > 32.0D; }
  
  @Override
  protected boolean canBeRidden(Entity entityIn) { return false; }
  
  @Override
  public boolean isInvulnerableTo(final DamageSource source) {
    return source == DamageSource.IN_WALL || source == DamageSource.WITHER || super.isInvulnerableTo(source);
  }
  
  @Override
  protected void updateAir(int air) { }
  
  // Prevent entity collisions //
  
  @Override
  public boolean canBePushed() { return false; }
  
  @Override
  protected void collideWithNearbyEntities() { }
  
  // Boss //

  @Override
  public void addTrackingPlayer(ServerPlayerEntity player) {
    super.addTrackingPlayer(player);
    this.bossInfo.addPlayer(player);
  }

  @Override
  public void removeTrackingPlayer(ServerPlayerEntity player) {
    super.removeTrackingPlayer(player);
    this.bossInfo.removePlayer(player);
  }

  // Sounds //
  
  @Override
  protected SoundEvent getAmbientSound() { return SoundEvents.ENTITY_HOSTILE_SPLASH; }

  @Override
  protected SoundEvent getHurtSound(DamageSource damageSourceIn) { return SoundEvents.ENTITY_ELDER_GUARDIAN_HURT; }

  @Override
  protected SoundEvent getDeathSound() { return SoundEvents.ENTITY_ELDER_GUARDIAN_DEATH; }

  @Override
  protected float getSoundVolume() { return 1.0F; }
  
  @Override
  protected float getSoundPitch() { return 0.6F + rand.nextFloat() * 0.2F; }
  
  // NBT //
  
  @Override
  public void writeAdditional(CompoundNBT compound) {
     super.writeAdditional(compound);
     compound.putByte(KEY_STATE, this.getState());
     compound.putInt("SwirlTime", swirlTime);
     compound.putInt("ThrowTime", throwTime);
  }

  @Override
  public void readAdditional(CompoundNBT compound) {
     super.readAdditional(compound);
     this.setState(compound.getByte(KEY_STATE));
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
    if (isServerWorld() && isInWater() && isSwimmingUpCalculated()) {
      moveRelative(0.01F, vec);
      move(MoverType.SELF, getMotion());
      setMotion(getMotion().scale(0.9D));
    } else {
      super.travel(vec);
    }
  }

  @Override
  public boolean isPushedByWater() { return false; }

  @Override
  public boolean isSwimmingUpCalculated() {
    if (this.swimmingUp) {
      return true;
    }
    LivingEntity e = getAttackTarget();
    return e != null && e.isInWater();
  }
  
  // States //
  
  public byte getState() { return this.getDataManager().get(STATE).byteValue(); }
  
  public void setState(final byte state) { this.getDataManager().set(STATE, Byte.valueOf(state)); }
  
  public boolean isNoneState() { return getState() == NONE; }
  
  public boolean isSwirling() { return getState() == SWIRLING; }
  
  public boolean isThrowing() { return getState() == THROWING; }
  
  public float getSwirlPercent() { return (float)swirlTime / (float)SWIRL_TIME; }
  
  public float getThrowPercent() { return (float)throwTime / (float)THROW_TIME; }
  
  // Misc //

  public boolean isWithinDistance(final Entity e, final float dis) {
    return this.getDistanceSq(e) < (dis * dis);
  }
  
  public List<Entity> getEntitiesInRange(final double range) {
    return getEntityWorld().getEntitiesInAABBexcluding(this, getBoundingBox().grow(range, range / 2, range), EntityPredicates.CAN_AI_TARGET.and(e -> e.isInWaterOrBubbleColumn()));
  }
  
  public void bubbles(final double posX, final double posY, final double posZ, final double radius, final int count) {
    final double motion = 0.08D;
    for (int i = 0; i < count; i++) {
      world.addParticle(ParticleTypes.BUBBLE, 
          posX + (world.rand.nextDouble() - 0.5D) * radius, 
          posY, 
          posZ + (world.rand.nextDouble() - 0.5D) * radius,
          (world.rand.nextDouble() - 0.5D) * motion, 
          0.5D,
          (world.rand.nextDouble() - 0.5D) * motion);
    }
  }
  
  // Goals //
  
  class SwimToSurfaceGoal extends SwimGoal {
    
    public SwimToSurfaceGoal() {
      super(CharybdisEntity.this);
    }
    
    @Override
    public boolean shouldExecute() {
      BlockPos pos = CharybdisEntity.this.getPosition().up((int)Math.ceil(CharybdisEntity.this.getHeight()));
      BlockState state = CharybdisEntity.this.world.getBlockState(pos);
      return state.getBlock() == Blocks.WATER && super.shouldExecute();
    }
   
  }
  
  class SwirlGoal extends Goal {
    
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
    public SwirlGoal(final int lDuration, final int lCooldown, final double lRange) {
      this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
      entity = CharybdisEntity.this;
      duration = lDuration;
      cooldown = lCooldown;
      range = lRange;
      cooldownTime = 60;
    }
    
    @Override
    public boolean shouldExecute() {
      if(cooldownTime > 0) {
        cooldownTime--;
      } else if((entity.isNoneState() || entity.isSwirling()) && entity.ticksExisted % 10 == 0) {
        trackedEntities = entity.getEntitiesInRange(range);
        return trackedEntities.size() > 0;
      }
      return false;
    }
    
    @Override
    public void startExecuting() {
      entity.setState(SWIRLING);
      entity.swirlTime = 1;
      progressTime = 1;
    }
    
    @Override
    public boolean shouldContinueExecuting() {
      // only check every 10 ticks
      if(entity.ticksExisted % 10 == 1) {
        trackedEntities = entity.getEntitiesInRange(range);
      }
      return progressTime > 0 && entity.isSwirling() && trackedEntities.size() > 0;
    }
    
    @Override
    public void tick() {
      // goal timer
      if(progressTime++ >= duration) {
        resetTask();
        return;
      }
      // move tracked entities
      for(final Entity e : trackedEntities) {
        // try to break boats
        if(e instanceof BoatEntity) {
          if(entity.getRNG().nextInt(8) == 0) {
            e.attackEntityFrom(DamageSource.causeMobDamage(entity), 3.0F);
          }
          continue;
        }
        // distance math
        double dx = entity.getPosX() - e.getPositionVec().x;
        //double dy = entity.getPosY() - e.getPositionVec().y;
        double dz = entity.getPosZ() - e.getPositionVec().z;
        final double horizDisSq = dx * dx + dz * dz;
        if(entity.getBoundingBox().intersects(e.getBoundingBox())) {
          // damage the entity and steal some health
          final float attack = (float)entity.getAttribute(Attributes.ATTACK_DAMAGE).getValue();
          if(e.attackEntityFrom(DamageSource.causeMobDamage(entity), attack)) {
            entity.heal(attack * 0.25F);
          }
        } else {
          // move the target toward this entity
          swirlEntity(e, horizDisSq);
        }
      }
    }
    
    @Override
    public void resetTask() {
      entity.setState(NONE);
      entity.swirlTime = 0;
      progressTime = 0;
      cooldownTime = cooldown;
      trackedEntities.clear();
    }
    
    private void swirlEntity(final Entity target, final double disSq) {
      // calculate the amount of motion to apply based on distance
      final double motion = 0.062D + 0.11D * (1.0D - (disSq / (range * range)));
      final Vector3d normalVec = CharybdisEntity.this.getPositionVec().mul(1.0D, 0, 1.0D).subtract(target.getPositionVec().mul(1.0D, 0, 1.0D)).normalize();
      final Vector3d rotatedVec = normalVec.rotateYaw(1.5707963267F).scale(motion);
      final Vector3d motionVec = target.getMotion().add(normalVec.scale(0.028D)).add(rotatedVec).mul(0.65D, 1.0D, 0.65D);
      target.setMotion(motionVec);
      target.addVelocity(0, 0.0068D, 0);
      target.velocityChanged = true;
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
      this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
      entity = CharybdisEntity.this;
      duration = lDuration;
      cooldown = lCooldown;
      range = lRange;
      cooldownTime = 50;
    }
    
    @Override
    public boolean shouldExecute() {
      if(cooldownTime > 0) {
        cooldownTime--;
      } else if(entity.isNoneState() || entity.isThrowing()) {
        trackedEntities = entity.getEntitiesInRange(range);
        return trackedEntities.size() > 0;
      }
      return false;
    }
    
    @Override
    public void startExecuting() {
      entity.setState(THROWING);
      entity.throwTime = 1;
      progressTime = 1;
    }
    
    @Override
    public boolean shouldContinueExecuting() {
      return progressTime > 0 && entity.isThrowing();
    }
    
    @Override
    public void tick() {
      // goal timer
      if(progressTime++ >= duration) {
        final double widthSq = entity.getWidth() * entity.getWidth();
        // move tracked entities
        trackedEntities = entity.getEntitiesInRange(range);
        for(final Entity target : trackedEntities) {
          // distance math
          double dx = entity.getPosX() - target.getPositionVec().x;
          double dz = entity.getPosZ() - target.getPositionVec().z;
          final double horizDisSq = dx * dx + dz * dz;
          // throw the entity upward
          if(horizDisSq > widthSq) {
            // calculate the amount of motion to apply based on distance
            final double motion = 1.08D + 0.31D * (1.0D - (horizDisSq / (range * range)));
            target.addVelocity(0, motion, 0);
            target.velocityChanged = true;
            // damage boats and other rideable entities
            if(target instanceof BoatEntity || !target.getPassengers().isEmpty()) {
              target.attackEntityFrom(DamageSource.causeMobDamage(entity), 6.0F);
            }
          }
        }
        resetTask();
      }
    }
    
    @Override
    public void resetTask() {
      entity.setState(NONE);
      entity.throwTime = 0;
      progressTime = 0;
      cooldownTime = cooldown;
      trackedEntities.clear();
    }
  }

}
