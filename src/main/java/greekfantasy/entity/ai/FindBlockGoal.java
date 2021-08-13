package greekfantasy.entity.ai;

import java.util.EnumSet;
import java.util.Optional;
import java.util.Random;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IWorldReader;

public abstract class FindBlockGoal extends Goal {

  protected final CreatureEntity creature;
  protected final int searchRadiusXZ;
  protected final int searchRadiusY;
  protected final int maxCooldown;
  
  protected int cooldown;
  protected Optional<BlockPos> targetPos = Optional.empty();
  
  public FindBlockGoal(final CreatureEntity entity, final int radius, final int cooldown) {
    this(entity, radius, Math.max(1, radius / 2), cooldown);
  }

  public FindBlockGoal(final CreatureEntity entity, final int radiusXZ, final int radiusY, final int cooldownIn) {
    setMutexFlags(EnumSet.noneOf(Goal.Flag.class));
    this.creature = entity;
    this.searchRadiusXZ = radiusXZ;
    this.searchRadiusY = radiusY;
    this.maxCooldown = cooldownIn;
    this.cooldown = 10;
  }

  @Override
  public boolean shouldExecute() {
    if(cooldown > 0) {
      cooldown--;
    } else {
      // if already near targetPos, do not execute
      if (isOnBlock() || isNearTarget(1.2D)) {
        return false;
      }
  
      this.targetPos = findNearbyBlock();
      return this.targetPos.isPresent();
    }
    return false;
  }

  @Override
  public boolean shouldContinueExecuting() {
    return false;
  }

  @Override
  public void startExecuting() {
    this.cooldown = maxCooldown;
    onFoundBlock(this.creature.getEntityWorld(), this.targetPos.get());
  }
  
  /**
   * Required to use this goal. Determines if the given block pos should be used.
   * @param worldIn the world
   * @param pos the BlockPos to check
   * @return whether the entity should move toward the given block
   **/
  public abstract boolean isTargetBlock(final IWorldReader worldIn, final BlockPos pos);
  
  /**
   * Called when the block has been found and a target will be remembered.
   * @param worldIn the world
   * @param target the BlockPos that was found
   **/
  public abstract void onFoundBlock(final IWorldReader worldIn, final BlockPos target);
  
  /** @return whether the entity is already at the target position **/
  protected boolean isOnBlock() {
    return isTargetBlock(this.creature.getEntityWorld(), this.creature.getPosition());
  }
  
  /** 
   * @param distance the maximum distance to consider "near" the target
   * @return whether the entity is within the given distance to the target 
   **/
  protected boolean isNearTarget(final double distance) {
    return this.targetPos.isPresent() && Vector3d.copyCenteredHorizontally(targetPos.get()).isWithinDistanceOf(this.creature.getPositionVec(), distance);
  }
 
  /**
   * Iterates through several randomly chosen blocks in a nearby radius
   * until one is found that can become the target.
   * @return an Optional containing a block pos if one is found, otherwise empty
   **/
  protected Optional<BlockPos> findNearbyBlock() {
    Random rand = this.creature.getRNG();
    BlockPos pos1 = this.creature.getPosition().down();
    // choose 20 random positions to check
    for (int i = 0; i < 20; i++) {
      BlockPos pos2 = pos1.add(rand.nextInt(searchRadiusXZ * 2) - searchRadiusXZ, rand.nextInt(searchRadiusY * 2) - searchRadiusY,
          rand.nextInt(searchRadiusXZ * 2) - searchRadiusXZ);
      // check the block to see if creature should move here
      if (this.isTargetBlock(this.creature.getEntityWorld(), pos2)) {
        return Optional.of(new BlockPos(pos2.getX(), pos2.getY(), pos2.getZ()));
      }
    }
    return Optional.empty();
  }

}