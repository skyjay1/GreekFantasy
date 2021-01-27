package greekfantasy.entity;

import java.util.EnumSet;
import java.util.Optional;

import greekfantasy.GFRegistry;
import greekfantasy.entity.ai.FindBlockGoal;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.FlyingMovementController;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomFlyingGoal;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.passive.IFlyingAnimal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.FlyingPathNavigator;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public class HarpyEntity extends MonsterEntity implements IFlyingAnimal {
  
  private static final DataParameter<Optional<BlockPos>> DATA_NEST = EntityDataManager.createKey(HarpyEntity.class, DataSerializers.OPTIONAL_BLOCK_POS);
  private static final String KEY_NEST = "Nest";
  
  public float flyingTime;
  private boolean isGoingToNest;

  public HarpyEntity(final EntityType<? extends HarpyEntity> type, final World worldIn) {
    super(type, worldIn);
    this.moveController = new FlyingMovementController(this, 10, false);
    this.setPathPriority(PathNodeType.DANGER_FIRE, -1.0F);
    this.setPathPriority(PathNodeType.DAMAGE_FIRE, -1.0F);
  }
  
  public static AttributeModifierMap.MutableAttribute getAttributes() {
    return MobEntity.func_233666_p_()
        .createMutableAttribute(Attributes.MAX_HEALTH, 24.0D)
        .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.22D)
        .createMutableAttribute(Attributes.ATTACK_DAMAGE, 3.0D)
        .createMutableAttribute(Attributes.FLYING_SPEED, 1.29D);
  }
  
  @Override
  protected void registerData() {
    super.registerData();
    this.getDataManager().register(DATA_NEST, Optional.empty());
  }
  
  @Override
  protected void registerGoals() {
    super.registerGoals();
    this.goalSelector.addGoal(0, new SwimGoal(this));
    this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0D, true));
    this.goalSelector.addGoal(2, new HarpyEntity.FindNestGoal(6, 10, 60));
    this.goalSelector.addGoal(3, new HarpyEntity.GoToNestGoal(0.9D, 120));
    this.goalSelector.addGoal(4, new WaterAvoidingRandomFlyingGoal(this, 0.9D) {
      @Override
      public boolean shouldExecute() { return !HarpyEntity.this.isGoingToNest && HarpyEntity.this.getRNG().nextInt(200) == 0 && super.shouldExecute(); }
    });
    this.goalSelector.addGoal(5, new LookAtGoal(this, PlayerEntity.class, 6.0F));
    this.goalSelector.addGoal(6, new LookRandomlyGoal(this));
    this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
    this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
  }

  @Override
  protected PathNavigator createNavigator(World worldIn) {
    FlyingPathNavigator flyingpathnavigator = new FlyingPathNavigator(this, worldIn);
    flyingpathnavigator.setCanOpenDoors(false);
    flyingpathnavigator.setCanSwim(true);
    flyingpathnavigator.setCanEnterDoors(true);
    return flyingpathnavigator;
  }
  
  @Override
  public void livingTick() {
    super.livingTick();
    // update falling moveSpeed
    Vector3d m = getMotion();
    if (this.isServerWorld() && !this.onGround && m.y < 0.0D) {
      final double multY = this.getAttackTarget() != null ? 0.9D : 0.6D;
      setMotion(m.mul(1.0D, multY, 1.0D));
    }
    // update flying counter
    if(this.isFlying()) {
      flyingTime = Math.min(1.0F, flyingTime + 0.1F);
    } else {
      flyingTime = Math.max(0.0F, flyingTime - 0.1F);
    }
    // nest checker
    if(this.ticksExisted % 90 == 0) {
      // check nest
      final Optional<BlockPos> nestPos = HarpyEntity.this.getNestPos();
      // check if there is still a nest at the position
      if(nestPos.isPresent() && !world.getBlockState(nestPos.get()).isIn(GFRegistry.NEST_BLOCK)) {
        HarpyEntity.this.setNestPos(Optional.empty());
      }
    }
  }
  
  @Override
  protected SoundEvent getAmbientSound() {
    return rand.nextFloat() < 0.18F ? SoundEvents.ENTITY_WITCH_AMBIENT : SoundEvents.ENTITY_PARROT_AMBIENT;
  }

  @Override
  protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
    return rand.nextFloat() < 0.09F ? SoundEvents.ENTITY_WITCH_HURT : SoundEvents.ENTITY_PARROT_HURT;
  }

  @Override
  protected SoundEvent getDeathSound() {
    return SoundEvents.ENTITY_GHAST_DEATH;
  }

  @Override
  protected float getSoundVolume() { return 0.8F; }
  
  @Override
  protected float getSoundPitch() { return 0.7F + rand.nextFloat() * 0.2F; }
  
  @Override
  public void writeAdditional(CompoundNBT compound) {
    super.writeAdditional(compound);
    final Optional<BlockPos> nestPos = this.getNestPos();
    if(nestPos.isPresent()) {
      compound.putInt(KEY_NEST + ".x", nestPos.get().getX());
      compound.putInt(KEY_NEST + ".y", nestPos.get().getY());
      compound.putInt(KEY_NEST + ".z", nestPos.get().getZ());
    }
  }

  @Override
  public void readAdditional(CompoundNBT compound) {
    super.readAdditional(compound);
    if(compound.contains(KEY_NEST + ".x")) {
      final int x = compound.getInt(KEY_NEST + ".x");
      final int y = compound.getInt(KEY_NEST + ".y");
      final int z = compound.getInt(KEY_NEST + ".z");
      this.setNestPos(Optional.of(new BlockPos(x, y, z)));
    }
  }

  // Flying methods

  @Override
  public boolean onLivingFall(float distance, float damageMultiplier) { return false; }

  @Override
  protected void updateFallState(double y, boolean onGroundIn, BlockState state, BlockPos pos) { }

  @Override
  protected boolean makeFlySound() { return true; }

  @Override
  protected float playFlySound(float volume) {
    this.playSound(SoundEvents.ENTITY_PARROT_FLY, 0.25F, 0.9F);
    return volume;
  }

  public boolean isFlying() {
    return !this.onGround || this.getMotion().lengthSquared() > 0.06D;
  }
  
  public void setNestPos(final Optional<BlockPos> pos) {
    this.getDataManager().set(DATA_NEST, pos);
    if(pos.isPresent()) {
      this.setHomePosAndDistance(pos.get(), (int)(this.getAttribute(Attributes.FOLLOW_RANGE).getBaseValue() * 1.75D));
    } else {
      this.setHomePosAndDistance(BlockPos.ZERO, -1);
    }
  }
  
  public Optional<BlockPos> getNestPos() {
    return this.getDataManager().get(DATA_NEST);
  }
  
  class FindNestGoal extends FindBlockGoal {
    
    public FindNestGoal(final int radiusXZ, final int radiusY, final int cooldown) {
      super(HarpyEntity.this, radiusXZ, radiusY, cooldown);
    }
    
    @Override
    public boolean shouldExecute() {
      return !isNearTarget(2.5D) && super.shouldExecute();
    }
    
    @Override
    protected Optional<BlockPos> findNearbyBlock() {
      final Optional<BlockPos> nestPos = HarpyEntity.this.getNestPos();
      if(nestPos.isPresent() && isTargetBlock(HarpyEntity.this.world, nestPos.get())) {
        return nestPos;
      }
      return super.findNearbyBlock();
    }

    @Override
    public boolean isTargetBlock(IWorldReader worldIn, BlockPos pos) {
      return worldIn.getBlockState(pos).isIn(GFRegistry.NEST_BLOCK);
    }
    
    @Override
    public void onFoundBlock(final IWorldReader worldIn, final BlockPos pos) {
      HarpyEntity.this.setNestPos(Optional.of(pos));
    }
  }
  
  class GoToNestGoal extends Goal {

    private final double speed;
    private final int chance;
    
    public GoToNestGoal(final double speedIn, final int chanceIn) {
      setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
      speed = speedIn;
      chance = chanceIn;
    }
    
    @Override
    public boolean shouldExecute() {
      return HarpyEntity.this.getNestPos().isPresent() && !isNearNest(4.0D)
          && HarpyEntity.this.getAttackTarget() == null && HarpyEntity.this.getRNG().nextInt(chance) == 0;
    }
    
    @Override
    public void startExecuting() {
      final Optional<BlockPos> nestPos = HarpyEntity.this.getNestPos();
      if(nestPos.isPresent()) {
        final Vector3d vec = new Vector3d(nestPos.get().getX() + 0.5D, nestPos.get().getY(), nestPos.get().getZ() + 0.5D);
        HarpyEntity.this.getNavigator().tryMoveToXYZ(vec.getX(), vec.getY(), vec.getZ(), this.speed);
        HarpyEntity.this.isGoingToNest = true;
      }
    }
    
    @Override
    public boolean shouldContinueExecuting() {
      return HarpyEntity.this.getNestPos().isPresent() && !HarpyEntity.this.getNavigator().noPath() && HarpyEntity.this.getAttackTarget() == null ;
    }
    
    @Override
    public void resetTask() {
      HarpyEntity.this.getNavigator().clearPath();
      HarpyEntity.this.isGoingToNest = false;
    }
    
    protected boolean isNearNest(final double distance) {
      final Optional<BlockPos> nestPos = HarpyEntity.this.getNestPos();
      if(nestPos.isPresent()) {
        final Vector3d vec = new Vector3d(nestPos.get().getX() + 0.5D, nestPos.get().getY() + 0.5D, nestPos.get().getZ() + 0.5D);
        return vec.isWithinDistanceOf(HarpyEntity.this.getPositionVec(), distance);
      }
      return false;
    }

  }
}
