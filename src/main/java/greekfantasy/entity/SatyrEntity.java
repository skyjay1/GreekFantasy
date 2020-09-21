package greekfantasy.entity;

import java.util.EnumSet;
import java.util.Optional;

import javax.annotation.Nullable;

import greekfantasy.GFRegistry;
import greekfantasy.GreekFantasy;
import greekfantasy.entity.ai.GoToBlockGoal;
import greekfantasy.util.PanfluteMusicManager;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SatyrEntity extends CreatureEntity {
  
  private static final DataParameter<Boolean> DATA_DANCING = EntityDataManager.createKey(SatyrEntity.class, DataSerializers.BOOLEAN);
  private static final ResourceLocation SONG = new ResourceLocation(GreekFantasy.MODID, "sound_of_silence");
  
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
    this.getDataManager().register(DATA_DANCING, Boolean.valueOf(false));
  }
  
  @Override
  protected void registerGoals() {
    super.registerGoals();
    this.goalSelector.addGoal(0, new SwimGoal(this));
    this.goalSelector.addGoal(2, new DancingGoal(0.75D));
    this.goalSelector.addGoal(3, new GoToCampfireGoal(12, 0.9D));
    this.goalSelector.addGoal(4, new LookAtGoal(this, PlayerEntity.class, 6.0F));
    this.goalSelector.addGoal(5, new LookRandomlyGoal(this));
  }
  
  @Override
  public void livingTick() {
    super.livingTick();
    if(this.isDancing() && this.getHeldItem(Hand.MAIN_HAND).getItem() == GFRegistry.PANFLUTE) {
      PanfluteMusicManager.playMusic(this, SONG, this.getEntityWorld().getGameTime(), 0.8F, 0.2F);
    }
  }
  
  @Override
  public ILivingEntityData onInitialSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason,
      @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
    final ILivingEntityData data = super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    // it makes our lives easier if main hand is always right (model changes when holding pipe, etc)
    this.setLeftHanded(false);
    return data;
  }
  
  public boolean isDancing() {
    return this.getDataManager().get(DATA_DANCING).booleanValue();
  }
  
  public void setDancing(final boolean dancing) {
    this.getDataManager().set(DATA_DANCING, Boolean.valueOf(dancing));
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
              world.getBlockState(p).getMaterial().blocksMovement()) {
            return false;
          }
        }
      }
    }
    return true;
  }

  // TODO uses pipe to summon wild creatures
  static class SummonAnimalsGoal extends Goal {

    @Override
    public boolean shouldExecute() {
      // TODO Auto-generated method stub
      return false;
    }
    
  }
  
  class GoToCampfireGoal extends GoToBlockGoal {

    final Direction[] HORIZONTALS = { Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST };
    
    public GoToCampfireGoal(int radius, double speed) {
      super(SatyrEntity.this, radius, speed);
    }
    
    @Override
    public boolean shouldExecute() {
      return SatyrEntity.this.getAttackTarget() == null && SatyrEntity.this.getRNG().nextInt(60) == 0 && super.shouldExecute();
    }

    @Override
    public boolean shouldMoveTo(IWorldReader worldIn, BlockPos pos) {
      for(final Direction d : HORIZONTALS) {
        if(!worldIn.getBlockState(pos).getMaterial().blocksMovement() 
            && !worldIn.getBlockState(pos.offset(d.getOpposite())).getMaterial().blocksMovement() 
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
      } else if(SatyrEntity.this.rand.nextInt(60) == 0) {
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
        if(SatyrEntity.this.getHeldItem(Hand.MAIN_HAND).isEmpty()) {
          SatyrEntity.this.setHeldItem(Hand.MAIN_HAND, new ItemStack(GFRegistry.PANFLUTE));
        }
      }
    }
    
    @Override
    public boolean shouldContinueExecuting() {
      boolean isCampfireValid = SatyrEntity.this.ticksExisted % 10 > 0 ? true : this.campfirePos.isPresent() && SatyrEntity.this.getEntityWorld().getBlockState(this.campfirePos.get()).isIn(BlockTags.CAMPFIRES);
      return this.targetPos.isPresent() && isCampfireValid;
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
      if(SatyrEntity.this.getHeldItem(Hand.MAIN_HAND).getItem() == GFRegistry.PANFLUTE) {
        SatyrEntity.this.setHeldItem(Hand.MAIN_HAND, ItemStack.EMPTY);
      }
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
