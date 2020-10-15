package greekfantasy.entity;

import java.util.EnumSet;
import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import greekfantasy.GFRegistry;
import greekfantasy.GreekFantasy;
import greekfantasy.item.ClubItem;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BossInfo;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerBossInfo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class GeryonEntity extends MonsterEntity {
  
  private static final DataParameter<Byte> STATE = EntityDataManager.createKey(GeryonEntity.class, DataSerializers.BYTE);
  // bytes to use in STATE
  private static final byte NONE = (byte)0;
  private static final byte SMASH = (byte)1;
  private static final byte SUMMON_COW = (byte)2;
  // bytes to use in World#setEntityState
  private static final byte SMASH_CLIENT = 9;
  private static final byte SUMMON_COW_CLIENT = 10;
  
  private static final int MAX_SMASH_TIME = 45;
  private static final int MAX_SUMMON_TIME = 45;
  private static final double SMASH_RANGE = 12.0D;
  
  private final ServerBossInfo bossInfo = (ServerBossInfo)(new ServerBossInfo(this.getDisplayName(), BossInfo.Color.BLUE, BossInfo.Overlay.PROGRESS)).setDarkenSky(true);
  
  private int smashTime;
  private int summonTime;
  
  public GeryonEntity(final EntityType<? extends GeryonEntity> type, final World worldIn) {
    super(type, worldIn);
    this.stepHeight = 1.0F;
  }

  public static AttributeModifierMap.MutableAttribute getAttributes() {
    return MobEntity.func_233666_p_()
        .createMutableAttribute(Attributes.MAX_HEALTH, 120.0D)
        .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.16D)
        .createMutableAttribute(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
        .createMutableAttribute(Attributes.FOLLOW_RANGE, 32.0D)
        .createMutableAttribute(Attributes.ATTACK_DAMAGE, 1.5D)
        .createMutableAttribute(Attributes.ATTACK_KNOCKBACK, ClubItem.ATTACK_KNOCKBACK_AMOUNT);
  }
  
  public static boolean canGeryonSpawnOn(final EntityType<? extends MobEntity> entity, final IWorld world, final SpawnReason reason, 
      final BlockPos pos, final Random rand) {
    return world.canBlockSeeSky(pos.up()) && MobEntity.canSpawnOn(entity, world, reason, pos, rand);
  }
  
  @Override
  public void registerData() {
    super.registerData();
    this.getDataManager().register(STATE, Byte.valueOf(NONE));
  }

  @Override
  protected void registerGoals() {
    super.registerGoals();
    this.goalSelector.addGoal(1, new GeryonEntity.SummonCowGoal(MAX_SUMMON_TIME, 440));
    this.goalSelector.addGoal(2, new GeryonEntity.SmashAttackGoal(SMASH_RANGE, 210));
    this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.0D, false));
    this.goalSelector.addGoal(5, new LookAtGoal(this, PlayerEntity.class, 10.0F));
    this.goalSelector.addGoal(6, new LookRandomlyGoal(this));
    this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
    this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
    this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, IronGolemEntity.class, true));
  }
  
  @Override
  public void livingTick() {
    super.livingTick();
    
    // boss info
    this.bossInfo.setPercent(this.getHealth() / this.getMaxHealth());
    
    // update smash attack
    if(this.isSmashAttack()) {
      smashTime++;
    } else if(smashTime > 0) {
      smashTime = 0;
    }
        
    // update summoning
    if(this.isSummoning()) {
      summonTime++;
      if(this.getHeldItem(Hand.OFF_HAND).isEmpty()) {
        this.setHeldItem(Hand.OFF_HAND, new ItemStack(GFRegistry.HORN));
      }
    } else if(summonTime > 0) {
      summonTime = 0;
      if(this.getHeldItem(Hand.OFF_HAND).getItem() == GFRegistry.HORN) {
        this.setHeldItem(Hand.OFF_HAND, ItemStack.EMPTY);
      }
    }

    // spawn particles
    if (horizontalMag(this.getMotion()) > (double)2.5000003E-7F && this.rand.nextInt(5) == 0) {
      addBlockParticles(2);
    }
 }
  
  @Override
  protected void damageEntity(final DamageSource source, final float amountIn) {
    float amount = amountIn;
    if (!source.isDamageAbsolute() && GreekFantasy.CONFIG.GERYON_RESISTANCE.get()) {
      amount *= 0.4F;
    }
    super.damageEntity(source, amount);
  }
  
  @Override
  public boolean attackEntityAsMob(final Entity entityIn) {
    if (super.attackEntityAsMob(entityIn)) {
      // apply extra upward velocity when attacking
      entityIn.setMotion(entityIn.getMotion().add(0.0D, (double)0.25F, 0.0D));
      return true;
    }
    return false;
  }

  @Override
  protected float getJumpUpwardsMotion() {
    return 0.82F * this.getJumpFactor();
  }

  @Override
  public boolean canBePushed() { return false; }
  
  @Override
  public boolean isNonBoss() { return false; }
  
  @Override
  public boolean canDespawn(final double disToPlayer) { return false; }
  
  @Override
  protected boolean canBeRidden(Entity entityIn) { return false; }
  
  @Override
  public ILivingEntityData onInitialSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason,
      @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
    final ItemStack club = new ItemStack(GFRegistry.IRON_CLUB);
    this.setHeldItem(Hand.MAIN_HAND, club);
    return super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
  }
  

  @Override
  public void writeAdditional(CompoundNBT compound) {
     super.writeAdditional(compound);
  }

  @Override
  public void readAdditional(CompoundNBT compound) {
     super.readAdditional(compound);
  }
  
  public boolean isSmashAttack() { return this.getDataManager().get(STATE).byteValue() == SMASH; }
  
  public void setSmashAttack(final boolean smash) { this.getDataManager().set(STATE, Byte.valueOf(smash ? SMASH : NONE)); }
 
  public boolean isSummoning() { return this.getDataManager().get(STATE).byteValue() == SUMMON_COW; }
  
  public void setSummoning(final boolean smash) { this.getDataManager().set(STATE, Byte.valueOf(smash ? SUMMON_COW : NONE)); }
  
  @OnlyIn(Dist.CLIENT)
  public void handleStatusUpdate(byte id) {
    switch(id) {
    case SMASH_CLIENT:
      // spawn particles for all nearby entities
      final List<Entity> targets = this.getEntityWorld().getEntitiesWithinAABBExcludingEntity(GeryonEntity.this, GeryonEntity.this.getBoundingBox().grow(SMASH_RANGE, SMASH_RANGE / 2, SMASH_RANGE));
      for(final Entity e : targets) {
        addSmashParticlesAt(e);
      }
      // add sound and block particles here
      world.playSound(this.getPosX(), this.getPosY(), this.getPosZ(), SoundEvents.ENTITY_IRON_GOLEM_ATTACK, this.getSoundCategory(), 2.0F, 0.4F, false);
      addBlockParticles(45);
      break;
    case SUMMON_COW_CLIENT:
      for(int i = 0; i < 4; i++) {
        this.getEntityWorld().playSound(this.getPosX(), this.getPosY(), this.getPosZ(), SoundEvents.BLOCK_NOTE_BLOCK_DIDGERIDOO, this.getSoundCategory(), 2.0F, 0.2F, false);
      }
      for(int i = 0; i < 2; i++) {
        this.getEntityWorld().playSound(this.getPosX(), this.getPosY(), this.getPosZ(), SoundEvents.BLOCK_NOTE_BLOCK_DIDGERIDOO, this.getSoundCategory(), 1.8F, 0.4F, false);
      }
      break;
    default:
      super.handleStatusUpdate(id);
      break;
    }
  }
  
  /**
   * Adds particles using the data of the block below this entity
   * @param count the number of particles to add
   **/
  private void addBlockParticles(final int count) {
    int i = MathHelper.floor(this.getPosX());
    int j = MathHelper.floor(this.getPosY() - (double)0.2F);
    int k = MathHelper.floor(this.getPosZ());
    BlockPos pos = new BlockPos(i, j, k);
    BlockState blockstate = this.world.getBlockState(pos);
    if (!blockstate.isAir(this.world, pos)) {
      final BlockParticleData data = new BlockParticleData(ParticleTypes.BLOCK, blockstate).setPos(pos);
      final double radius = this.getWidth();
      final double motion = 4.0D;
      for(int c = 0; c < count; c++) {
        this.world.addParticle(data, 
           this.getPosX() + (this.rand.nextDouble() - 0.5D) * radius, 
           this.getPosY() + 0.1D, 
           this.getPosZ() + (this.rand.nextDouble() - 0.5D) * radius, 
           motion * (this.rand.nextDouble() - 0.5D), 0.5D, (this.rand.nextDouble() - 0.5D) * motion);
      }
    }
  }
  
  @OnlyIn(Dist.CLIENT)
  private void addSmashParticlesAt(final Entity e) {
    final double x = e.getPosX() + 0.5D;
    final double y = e.getPosY() + 0.1D;
    final double z = e.getPosZ() + 0.5D;
    final double motion = 0.08D;
    final double radius = e.getWidth();
    for (int i = 0; i < 25; i++) {
      world.addParticle(ParticleTypes.CRIT, 
          x + (world.rand.nextDouble() - 0.5D) * radius, 
          y, 
          z + (world.rand.nextDouble() - 0.5D) * radius,
          (world.rand.nextDouble() - 0.5D) * motion, 
          0.5D,
          (world.rand.nextDouble() - 0.5D) * motion);
    }
  }
  
  @OnlyIn(Dist.CLIENT)
  public float getSmashTime(final float partialTick) {
    return smashTime + (partialTick < 1.0F ? partialTick : 0);
  }
  
  @OnlyIn(Dist.CLIENT)
  public float getSmashPercent(final float partialTick) {
    return smashTime > 0 ? getSmashTime(partialTick) / (float)MAX_SMASH_TIME : 0;
  }
  
  @OnlyIn(Dist.CLIENT)
  public float getSummonTime(final float partialTick) {
    return summonTime + (partialTick < 1.0F ? partialTick : 0);
  }
  
  @OnlyIn(Dist.CLIENT)
  public float getSummonPercent(final float partialTick) {
    return summonTime > 0 ? getSummonTime(partialTick) / (float)MAX_SUMMON_TIME : 0;
  }
  
  /**
   * @param entity the entity to check
   * @return whether the given entity should not be affected by smash attack
   **/
  private boolean isExemptFromSmashAttack(final Entity entity) {
    return entity.getType() == GFRegistry.MAD_COW_ENTITY || entity.hasNoGravity();
  }
  
  /**
   * Applies a smash attack to the given entity
   * @param entity the target entity
   **/
  private void useSmashAttack(final Entity entity) {
    // if entitiy is touching the ground, knock it into the air and apply stun
    if(entity.isOnGround() && !isExemptFromSmashAttack(entity)) {
      entity.addVelocity(0.0D, 0.65D, 0.0D);
      entity.attackEntityFrom(DamageSource.causeMobDamage(this), (float) this.getAttribute(Attributes.ATTACK_DAMAGE).getValue());
      // stun effect (for living entities)
      if(entity instanceof LivingEntity) {
        ((LivingEntity)entity).addPotionEffect(new EffectInstance(GFRegistry.STUNNED_EFFECT, 35, 0));
      }
    }
  }
  
  // Boss Logic

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
  
  // End Boss Logic
  
  class SmashAttackGoal extends Goal {
    
    private final double range;
    private final int maxCooldown;
    private int cooldown = 90;
    
    public SmashAttackGoal(final double rangeIn, final int maxCooldownIn) {
      this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
      range = rangeIn;
      maxCooldown = maxCooldownIn;
    }
    
    @Override
    public void startExecuting() {
      GeryonEntity.this.setSmashAttack(true);
    }
    
    @Override
    public boolean shouldExecute() {
      if(this.cooldown > 0) {
        cooldown--;
        return false;
      }
      return GeryonEntity.this.getAttackTarget() != null && !GeryonEntity.this.isSummoning()
          && GeryonEntity.this.getDistanceSq(GeryonEntity.this.getAttackTarget()) < (range * range);      
    }
    
    @Override
    public void tick() {
      GeryonEntity.this.getNavigator().clearPath();
      GeryonEntity.this.getLookController().setLookPositionWithEntity(GeryonEntity.this.getAttackTarget(), GeryonEntity.this.getHorizontalFaceSpeed(), GeryonEntity.this.getVerticalFaceSpeed());
      if(GeryonEntity.this.smashTime >= GeryonEntity.MAX_SMASH_TIME) {
        // notify client (spawns particles around entities)
        GeryonEntity.this.getEntityWorld().setEntityState(GeryonEntity.this, GeryonEntity.SMASH_CLIENT);
        // get a list of nearby entities and use smash attack on each one
        GeryonEntity.this.getEntityWorld().getEntitiesWithinAABBExcludingEntity(GeryonEntity.this, GeryonEntity.this.getBoundingBox().grow(range, range / 2, range))
          .forEach(e -> GeryonEntity.this.useSmashAttack(e));;
        // finish task
        this.resetTask();
      }
    }
    
    @Override
    public boolean shouldContinueExecuting() { 
      return GeryonEntity.this.isSmashAttack() && GeryonEntity.this.getAttackTarget() != null 
              && GeryonEntity.this.getDistanceSq(GeryonEntity.this.getAttackTarget()) < (range * range); 
    }
    
    @Override
    public void resetTask() {
      GeryonEntity.this.setSmashAttack(false);
      GeryonEntity.this.smashTime = 0;
      cooldown = maxCooldown;
    }
  }
  
  class SummonCowGoal extends Goal {
    protected final int maxProgress;
    protected final int maxCooldown;
    
    protected int progress;
    protected int cooldown;
    
    public SummonCowGoal(final int summonProgressIn, final int summonCooldownIn) {
      this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
      maxProgress = summonProgressIn;
      maxCooldown = summonCooldownIn;
      cooldown = 60;
    }
    
    @Override
    public boolean shouldExecute() {
      if(cooldown > 0) {
        cooldown--;
      } else {
        return GeryonEntity.this.getAttackTarget() != null;
      }
      return false;
    }
    
    @Override
    public void startExecuting() {
      GeryonEntity.this.setSummoning(true);
      this.progress = 1;
    }
    
    @Override
    public boolean shouldContinueExecuting() {
      return this.progress > 0 && GeryonEntity.this.getAttackTarget() != null && GeryonEntity.this.isSummoning();
    }
    
    @Override
    public void tick() {
      super.tick();
      GeryonEntity.this.getNavigator().clearPath();
      GeryonEntity.this.getLookController().setLookPositionWithEntity(GeryonEntity.this.getAttackTarget(), 100.0F, 100.0F);
      if(progress++ > maxProgress) {
        // summon mad cow
        final MadCowEntity madCow = GFRegistry.MAD_COW_ENTITY.create(GeryonEntity.this.getEntityWorld());
        madCow.setLocationAndAngles(GeryonEntity.this.getPosX(), GeryonEntity.this.getPosY() + 0.5D, GeryonEntity.this.getPosZ(), 0, 0);
        madCow.setAttackTarget(GeryonEntity.this.getAttackTarget());
        GeryonEntity.this.getEntityWorld().addEntity(madCow);
        GeryonEntity.this.getEntityWorld().setEntityState(GeryonEntity.this, SUMMON_COW_CLIENT);
        resetTask();
      }
    }
    
    @Override
    public void resetTask() {
      this.progress = 0;
      this.cooldown = maxCooldown;
      GeryonEntity.this.setSummoning(false);
    }
  }
  
}
