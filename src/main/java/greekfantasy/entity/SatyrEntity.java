package greekfantasy.entity;

import java.util.EnumSet;
import java.util.Random;

import net.minecraft.block.Blocks;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class SatyrEntity extends CreatureEntity implements IHoofedEntity {
  
  // uses pipe to summon wild creatures
  
  protected boolean isDancing;

  public SatyrEntity(final EntityType<? extends SatyrEntity> type, final World worldIn) {
    super(type, worldIn);
  }
  
  @Override
  protected void registerGoals() {
    super.registerGoals();
    this.goalSelector.addGoal(0, new SwimGoal(this));
    this.goalSelector.addGoal(4, new LookAtGoal(this, PlayerEntity.class, 6.0F));
    this.goalSelector.addGoal(5, new LookRandomlyGoal(this));
  }

  public static AttributeModifierMap.MutableAttribute getAttributes() {
    return MobEntity.func_233666_p_()
        .createMutableAttribute(Attributes.MAX_HEALTH, 24.0D)
        .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.25D)
        .createMutableAttribute(Attributes.ATTACK_DAMAGE, 1.0D);
  }

  @Override
  public boolean isStomping() {
    return isDancing;
  }

  @Override
  public float getStompingSpeed() {
    return 0.34F;
  }

  
  static class SummonAnimalsGoal extends Goal {

    @Override
    public boolean shouldExecute() {
      // TODO Auto-generated method stub
      return false;
    }
    
  }
  
  static class DancingGoal extends Goal {
    
    private SatyrEntity entity;
    private double targetX;
    private double targetY;
    private double targetZ;
    
    private int dancingTime;
    private final int maxDancingTime = 200;
    private final double dancingSpeed = 0.2D;
    
    public DancingGoal(final SatyrEntity entityIn) {
      this.setMutexFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
      this.entity = entityIn;
   }

    @Override
    public boolean shouldExecute() {
      // TODO random range check before running this
      Vector3d target = findCampfire();
      if(null == target) {
        return false;
      }
      targetX = target.x;
      targetY = target.y;
      targetZ = target.z;
      return true;
    }
    
    @Override
    public void startExecuting() {
      this.entity.isDancing = true;
      dancingTime = 1;
    }
    
    @Override
    public void tick() {
      if(dancingTime++ < maxDancingTime) {
        // TODO calculate next position
        double x = this.targetX + Math.cos(dancingTime * dancingSpeed) * 1.5D;
        double y = this.targetY;
        double z = this.targetZ + Math.sin(dancingTime * dancingSpeed) * 1.5D;
        this.entity.getNavigator().tryMoveToXYZ(x, y, z, this.dancingSpeed);
      } else {
        // stop dancing
        this.entity.isDancing = false;
        this.dancingTime = 0;
      }
    }
    
    @Override
    public boolean shouldContinueExecuting() {
      final BlockPos pos = new BlockPos(targetX, targetY, targetZ);
      return this.entity.getEntityWorld().getBlockState(pos).getBlock() == Blocks.CAMPFIRE && dancingTime > 1;
    }
    
    @Override
    public void resetTask() {
      this.dancingTime = 0;
      this.entity.isDancing = false;
    }
    
    private Vector3d findCampfire() {
      Random rand = this.entity.getRNG();
      BlockPos pos1 = this.entity.getPosition();
      int radius = 8;

      for (int i = 0; i < 10; i++) {
        BlockPos pos2 = pos1.add(rand.nextInt(radius * 2) - radius, 2 - rand.nextInt(8),
            rand.nextInt(radius * 2) - radius);

        if (this.entity.getEntityWorld().getBlockState(pos2).getBlock() == Blocks.CAMPFIRE) {
          // check if there are empty blocks around the campfire
          // TODO
          
          return new Vector3d(pos2.getX(), pos2.getY(), pos2.getZ());
        }
      }
      return null;
    }
    
  }
  
}
