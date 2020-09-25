package greekfantasy.entity;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.annotation.Nullable;

import greekfantasy.GreekFantasy;
import greekfantasy.entity.ai.GoToBlockGoal;
import greekfantasy.util.PanfluteMusicManager;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IAngerable;
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
import net.minecraft.entity.ai.goal.ResetAngerGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.RangedInteger;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.TickRangeConverter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SatyrEntity extends CreatureEntity implements IAngerable {
  
  private static final DataParameter<Byte> DATA_STATE = EntityDataManager.createKey(SatyrEntity.class, DataSerializers.BYTE);
  private static final DataParameter<Boolean> DATA_SHAMAN = EntityDataManager.createKey(SatyrEntity.class, DataSerializers.BOOLEAN);
  private static final String KEY_SHAMAN = "Shaman";
  
  private static final ResourceLocation DANCING_SONG = new ResourceLocation(GreekFantasy.MODID, "greensleeves");
  private static final ResourceLocation SUMMONING_SONG = new ResourceLocation(GreekFantasy.MODID, "sarias_song");

  protected static final byte NONE = 9;
  protected static final byte DANCING = 10;
  protected static final byte SUMMONING = 11;
  protected static final byte PLAY_SUMMON_SOUND = 12;
  
  protected static final int MAX_SUMMON_TIME = 160;
  protected static final int MAX_PANFLUTE_TIME = 8;
  public int holdingPanfluteTime;
  public int summonTime;
  public boolean hasShamanTexture;
  
  private static final RangedInteger ANGER_RANGE = TickRangeConverter.convertRange(20, 39);
  private int angerTime;
  private UUID angerTarget;
    
  private final Goal meleeAttackGoal = new MeleeAttackGoal(this, 1.0D, false);
  private final Goal summonAnimalsGoal = new SummonAnimalsGoal(MAX_SUMMON_TIME, 300);
  
  public SatyrEntity(final EntityType<? extends SatyrEntity> type, final World worldIn) {
    super(type, worldIn);
    this.setPathPriority(PathNodeType.DANGER_FIRE, -1.0F);
    this.setPathPriority(PathNodeType.DAMAGE_FIRE, -1.0F);
  }
  
  public static AttributeModifierMap.MutableAttribute getAttributes() {
    return MobEntity.func_233666_p_()
        .createMutableAttribute(Attributes.MAX_HEALTH, 24.0D)
        .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.25D)
        .createMutableAttribute(Attributes.ATTACK_DAMAGE, 1.0D);
  }
  
  @Override
  public void registerData() {
    super.registerData();
    this.getDataManager().register(DATA_STATE, Byte.valueOf(NONE));
    this.getDataManager().register(DATA_SHAMAN, Boolean.valueOf(false));
  }
  
  @Override
  protected void registerGoals() {
    super.registerGoals();
    this.goalSelector.addGoal(0, new SwimGoal(this));
    this.goalSelector.addGoal(2, new DancingGoal(0.75D));
    this.goalSelector.addGoal(3, new GoToCampfireGoal(12, 0.9D));
    this.goalSelector.addGoal(4, new LookAtGoal(this, PlayerEntity.class, 6.0F));
    this.goalSelector.addGoal(5, new LookRandomlyGoal(this));
    this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
    this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, 10, true, false, this::func_233680_b_));
    this.targetSelector.addGoal(3, new ResetAngerGoal<>(this, true));
  }
  
  @Override
  public void livingTick() {
    super.livingTick();  
    // play music
    if(this.isSummoning()) {
      PanfluteMusicManager.playMusic(this, SUMMONING_SONG, summonTime, 0.92F, 0.34F);
    } else if(this.isDancing()) {
      PanfluteMusicManager.playMusic(this, DANCING_SONG, this.getEntityWorld().getGameTime(), 0.84F, 0.28F);
    }
    
    // dancing timer
    if(this.isDancing() || this.isSummoning()) {
      this.holdingPanfluteTime = Math.min(this.holdingPanfluteTime + 1, MAX_PANFLUTE_TIME);
    } else {
      this.holdingPanfluteTime = Math.max(this.holdingPanfluteTime - 1, 0);
    }
    
    // summon timer
    if(summonTime > 0 && summonTime++ > MAX_SUMMON_TIME) {
      summonTime = 0;
    }

    // anger timer
    if (!this.world.isRemote()) {
      this.func_241359_a_((ServerWorld) this.world, true);
    }
  }
  
  @Override
  public boolean attackEntityFrom(final DamageSource source, final float amount) {
    final boolean attackEntityFrom = super.attackEntityFrom(source, amount);
    if(attackEntityFrom && source.getImmediateSource() instanceof LivingEntity) {
      // alert all nearby satyr shamans
      final LivingEntity target = (LivingEntity)source.getImmediateSource();
  	  final List<SatyrEntity> shamans = this.getEntityWorld().getEntitiesWithinAABB(SatyrEntity.class, this.getBoundingBox().grow(10.0D), e -> e.isShaman());
  	  for(final SatyrEntity shaman : shamans) {
  	    if(shaman.getAttackTarget() == null) { // if IAngerable#canTargetEntity
  	      // DEBUG
  	  	  shaman.setAngerTarget(target.getUniqueID());
  	  	  shaman.setAngerTime(ANGER_RANGE.getRandomWithinRange(this.rand));
  	    }
  	  }   
    }
    
    return attackEntityFrom;
  }
 
  @Override
  public ILivingEntityData onInitialSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason,
      @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
    spawnDataIn = super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    // random chance to be a satyr shaman
    if(worldIn.getRandom().nextInt(100) < GreekFantasy.CONFIG.SATYR_SHAMAN_CHANCE.get()) {
      this.setShaman(true);
      updateCombatAI();
    }
    return spawnDataIn;
  }
  
  @Override
  public void notifyDataManagerChange(final DataParameter<?> key) {
    super.notifyDataManagerChange(key);
    if(key == DATA_SHAMAN) {
      // change AI task for shaman / non-shaman
      updateCombatAI();
      // update client-side field
      this.hasShamanTexture = this.isShaman();
    } else if(key == DATA_STATE) {
      // update summon time
      if(this.isSummoning()) {
        this.summonTime = 1;
      } else {
        this.summonTime = 0;
      }
    }
  }
  
  @OnlyIn(Dist.CLIENT)
  public void handleStatusUpdate(byte id) {
    if(id == PLAY_SUMMON_SOUND) {
      this.getEntityWorld().playSound(this.getPosX(), this.getPosY(), this.getPosZ(), SoundEvents.ENTITY_WOLF_HOWL, this.getSoundCategory(), 1.1F, 0.9F + this.getRNG().nextFloat() * 0.2F, false);
      this.summonTime = 0;
    } else {
      super.handleStatusUpdate(id);
    }
  }

  @Override
  public void writeAdditional(CompoundNBT compound) {
    super.writeAdditional(compound);
    compound.putBoolean(KEY_SHAMAN, this.isShaman());
    this.writeAngerNBT(compound);
  }

  @Override
  public void readAdditional(CompoundNBT compound) {
    super.readAdditional(compound);
    this.setShaman(compound.getBoolean(KEY_SHAMAN));
    this.readAngerNBT((ServerWorld)this.world, compound);
    this.updateCombatAI();
  }
  
  //IAngerable methods
  
  @Override
  public void func_230258_H__() { this.setAngerTime(ANGER_RANGE.getRandomWithinRange(this.rand)); }
  @Override
  public void setAngerTime(int time) { this.angerTime = time; }
  @Override
  public int getAngerTime() { return this.angerTime; }
  @Override
  public void setAngerTarget(@Nullable UUID target) { this.angerTarget = target; }
  @Override
  public UUID getAngerTarget() { return this.angerTarget; }
 
  // End IAngerable methods
  
  // Dancing, summoning, and shaman getters/setters
  
  public boolean isDancing() { return this.getDataManager().get(DATA_STATE).byteValue() == DANCING; }
  
  public void setDancing(final boolean dancing) { this.getDataManager().set(DATA_STATE, Byte.valueOf(dancing ? DANCING : NONE)); }
  
  public boolean isSummoning() { return this.getDataManager().get(DATA_STATE).byteValue() == SUMMONING; }
  
  public void setSummoning(final boolean summoning) { this.getDataManager().set(DATA_STATE, Byte.valueOf(summoning ? SUMMONING : NONE)); }
  
  public boolean isShaman() { return this.getDataManager().get(DATA_SHAMAN); }
  
  public void setShaman(final boolean shaman) { 
    this.hasShamanTexture = shaman;
    this.getDataManager().set(DATA_SHAMAN, Boolean.valueOf(shaman)); 
  }
  
  protected void updateCombatAI() {
    if(this.isServerWorld()) {
      if(this.isShaman()) {
        this.goalSelector.addGoal(1, summonAnimalsGoal);
        this.goalSelector.removeGoal(meleeAttackGoal);
      } else {
        this.goalSelector.addGoal(1, meleeAttackGoal);
        this.goalSelector.removeGoal(summonAnimalsGoal);
      }
    }
  }
  
  public boolean hasShamanTexture() {
    return this.hasShamanTexture;
  }
    
  /**
   * Only used client-side. Calculates the portion of dancing
   * or summoning completed up to 8 ticks so that the model 
   * animates in that time.
   * @return the percent
   **/
  public float getArmMovementPercent(final float ageInTicks) {
    final float time = this.holdingPanfluteTime + (ageInTicks - (float)Math.floor(ageInTicks));
    return Math.min(1.0F, time / 8.0F);
  }
  
  /**
   * @param world the entity's world
   * @param pos the BlockPos to check around
   * @return if the given pos is a campfire with empty space around it
   **/
  protected static boolean isValidCampfire(final IWorldReader world, final BlockPos pos) {
    // check if the block is actually a campfire
    if(!world.getBlockState(pos).isIn(BlockTags.CAMPFIRES)) {
      return false;
    }
    // check surrounding area (only flat or passable terrain is allowed)
    for(int x = -1; x <= 1; x++) {
      for(int z = -1; z <= 1; z++) {
        if(!(x == 0 && z == 0)) {
          // check for impassable blocks
          final BlockPos p = pos.add(x, 0, z);
          if(!world.getBlockState(p.down()).isSolidSide(world, p.down(), Direction.UP) || 
              world.getBlockState(p).isSolid() ||
              world.getBlockState(p).getMaterial().blocksMovement()) {
            return false;
          }
        }
      }
    }
    return true;
  }

  class SummonAnimalsGoal extends Goal {
    
    protected final int MAX_PROGRESS;
    protected final int MAX_COOLDOWN;
    
    protected int progress;
    protected int cooldown;
    
    public SummonAnimalsGoal(final int summonProgressIn, final int summonCooldownIn) {
      this.setMutexFlags(EnumSet.allOf(Goal.Flag.class));
      MAX_PROGRESS = summonProgressIn;
      MAX_COOLDOWN = summonCooldownIn;
      cooldown = summonCooldownIn / 4;
    }

    @Override
    public boolean shouldExecute() {
      if(SatyrEntity.this.isSummoning()) {
        return true;
      } else if(cooldown > 0) {
        cooldown--;
      } else {
        return SatyrEntity.this.isShaman() && SatyrEntity.this.getAttackTarget() != null;
      }
      return false;
    }
    
    @Override
    public void startExecuting() {
      SatyrEntity.this.setSummoning(true);
      this.progress = 1;
    }
    
    @Override
    public boolean shouldContinueExecuting() {
      return this.progress > 0 && SatyrEntity.this.getAttackTarget() != null;
    }
    
    @Override
    public void tick() {
      super.tick();
      SatyrEntity.this.getNavigator().clearPath();
      SatyrEntity.this.getLookController().setLookPositionWithEntity(SatyrEntity.this.getAttackTarget(), 100.0F, 100.0F);
      if(progress++ > MAX_PROGRESS) {
        // summon animals
        final double x = SatyrEntity.this.getPosX();
        final double y = SatyrEntity.this.getPosY();
        final double z = SatyrEntity.this.getPosZ();
        final float yaw = SatyrEntity.this.rotationYaw;
        final float pitch = SatyrEntity.this.rotationPitch;
        for(int i = 0; i < 3; i++) {
          final WolfEntity wolf = EntityType.WOLF.create(SatyrEntity.this.getEntityWorld());
          wolf.setLocationAndAngles(x, y, z, yaw, pitch);
          wolf.setAngerTime(800);
          wolf.setAngerTarget(SatyrEntity.this.getAttackTarget().getUniqueID());
          SatyrEntity.this.getEntityWorld().addEntity(wolf);
        }
        SatyrEntity.this.getEntityWorld().setEntityState(SatyrEntity.this, PLAY_SUMMON_SOUND);
        resetTask();
      }
      // soft reset when entity is hurt
      if(SatyrEntity.this.hurtTime != 0) {
        this.progress = 0;
        this.cooldown = 30;
        SatyrEntity.this.setSummoning(false);
      }
    }
    
    @Override
    public void resetTask() {
      this.progress = 0;
      this.cooldown = MAX_COOLDOWN;
      SatyrEntity.this.setSummoning(false);
    }
    
  }
  
  class GoToCampfireGoal extends GoToBlockGoal {

    final Direction[] HORIZONTALS = { Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST };
    
    public GoToCampfireGoal(int radius, double speed) {
      super(SatyrEntity.this, radius, speed);
    }
    
    @Override
    public boolean shouldExecute() {
      return SatyrEntity.this.getAttackTarget() == null && !SatyrEntity.this.isSummoning() 
          && SatyrEntity.this.getRNG().nextInt(60) == 0 && super.shouldExecute();
    }

    @Override
    public boolean shouldMoveTo(IWorldReader worldIn, BlockPos pos) {
      final boolean isPassable = !worldIn.getBlockState(pos.up(1)).getMaterial().blocksMovement() && !worldIn.getBlockState(pos).getMaterial().blocksMovement();
      for(final Direction d : HORIZONTALS) {
        if(isPassable && !worldIn.getBlockState(pos.offset(d.getOpposite())).getMaterial().blocksMovement() 
            && worldIn.getBlockState(pos.offset(d)).isIn(BlockTags.CAMPFIRES)) {
          return true;
        }
      }
      return false;
    }
  }
  
  class DancingGoal extends Goal {
    
    private final Direction[] HORIZONTALS = { Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST };
    private final int MAX_TRAVEL_TIME = 120;
    private final int MAX_COOLDOWN = 100;
    private final int MAX_DANCING_TIME = 1200;
    
    private Optional<BlockPos> campfirePos = Optional.empty();
    private Optional<Vector3d> targetPos = Optional.empty();
    
    protected final double moveSpeed;
    private int cooldown = MAX_COOLDOWN / 2;
    private int dancingTime = 0;
    private int travelTime = 0;
    
    public DancingGoal(final double speedIn) {
      this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.JUMP));
      this.moveSpeed = speedIn;
   }
    
    @Override
    public boolean shouldExecute() {
      if(cooldown > 0) {
        cooldown--;
      } else if(!SatyrEntity.this.getNavigator().hasPath() && SatyrEntity.this.getAttackTarget() == null 
          && !SatyrEntity.this.isSummoning() && SatyrEntity.this.rand.nextInt(20) == 0) {
        // find a nearby campfire
        this.campfirePos = findCampfire();
        return this.updateTarget();
      }
      return false;
    }
    
    @Override
    public void startExecuting() {
      super.startExecuting();
      if(targetPos.isPresent()) {
        SatyrEntity.this.getNavigator().tryMoveToXYZ(targetPos.get().x, targetPos.get().y, targetPos.get().z, moveSpeed);
        SatyrEntity.this.setDancing(true);
        dancingTime = 1;
      }
    }
    
    @Override
    public boolean shouldContinueExecuting() {
      boolean isCampfireValid = SatyrEntity.this.ticksExisted % 15 > 0 ? true : this.campfirePos.isPresent() && SatyrEntity.isValidCampfire(SatyrEntity.this.getEntityWorld(), this.campfirePos.get());
      return SatyrEntity.this.getAttackTarget() == null && this.targetPos.isPresent() && isCampfireValid;
    }
    
    @Override
    public void tick() {
      super.tick();
      if(this.dancingTime++ < MAX_DANCING_TIME && this.travelTime++ < MAX_TRAVEL_TIME) {         
        // if we're close to the target, update target and path
        if(isNearTarget(1.2D)) {
          this.updateTarget();
          SatyrEntity.this.jump();
          SatyrEntity.this.getNavigator().tryMoveToXYZ(targetPos.get().x, targetPos.get().y, targetPos.get().z, moveSpeed);
          this.travelTime = 0;
        }
      } else {
        resetTask();
      }
    }
   
    @Override
    public void resetTask() {
      super.resetTask();
      this.campfirePos = Optional.empty();
      this.targetPos = Optional.empty();
      this.dancingTime = 0;
      this.travelTime = 0;
      this.cooldown = MAX_COOLDOWN;
      SatyrEntity.this.setDancing(false);
    }
 
    /**
     * Checks each direction to determine if a campfire is there
     * @return an Optional containing the BlockPos if found, otherwise empty
     **/
    private Optional<BlockPos> findCampfire() {
      // check adjacent blocks for campfire
      for(final Direction d : HORIZONTALS) {
        final BlockPos pos = SatyrEntity.this.getPosition().offset(d, 2);
        if(SatyrEntity.isValidCampfire(SatyrEntity.this.getEntityWorld(), pos)) {
          return Optional.of(pos);
        }
      }
      return Optional.empty();
    }
    
    /**
     * Checks if a campfire has been found, and if so, updates
     * which block the entity should path toward
     * @return whether there is now a target to move toward
     **/
    private boolean updateTarget() {
      if(this.campfirePos.isPresent()) {
        final Direction nextDir = getClosestDirection().rotateY();
        final BlockPos target = campfirePos.get().offset(nextDir);
        this.targetPos = Optional.of(new Vector3d(target.getX() + 0.5D, target.getY(), target.getZ() + 0.5D));
        return true;
      }
      return false;
    }
    
    /**
     * Checks each possible direction offset from a campfire
     * @return the Direction offset that is closest to the entity
     **/
    private Direction getClosestDirection() {
      Direction dClosest = Direction.NORTH;
      final Vector3d curPos = SatyrEntity.this.getPositionVec();
      double dMin = 100.0D;
      if(this.campfirePos.isPresent()) {
        for(final Direction dir : HORIZONTALS) {
          final BlockPos dPos = campfirePos.get().offset(dir);
          final Vector3d dVec = new Vector3d(dPos.getX() + 0.5D, dPos.getY(), dPos.getZ() + 0.5D);
          final double dSq = curPos.squareDistanceTo(dVec);
          if(dSq < dMin) {
            dClosest = dir;
            dMin = dSq;
          }
        }
      }
      return dClosest;
    }
    
    private boolean isNearTarget(final double distance) {
      return this.targetPos.isPresent() && this.targetPos.get().isWithinDistanceOf(SatyrEntity.this.getPositionVec(), distance);
    }
  }
  
 
  
}
