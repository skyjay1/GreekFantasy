package greekfantasy.entity;

import java.util.EnumSet;
import java.util.Random;

import greekfantasy.GreekFantasy;
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
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class SatyrEntity extends CreatureEntity implements IHoofedEntity {
  
  // uses pipe to summon wild creatures
  
  private static final DataParameter<Boolean> DATA_DANCING = EntityDataManager.createKey(SatyrEntity.class, DataSerializers.BOOLEAN);

  public SatyrEntity(final EntityType<? extends SatyrEntity> type, final World worldIn) {
    super(type, worldIn);
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
    this.goalSelector.addGoal(3, new DancingGoal(this));
    this.goalSelector.addGoal(4, new LookAtGoal(this, PlayerEntity.class, 6.0F));
    this.goalSelector.addGoal(5, new LookRandomlyGoal(this));
  }

  @Override
  public void setStomping(final boolean dancing) {
    this.getDataManager().set(DATA_DANCING, Boolean.valueOf(dancing));
  }

  @Override
  public boolean isStomping() {
    return this.getDataManager().get(DATA_DANCING).booleanValue();
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
    private final double dancingSpeed = 0.9D;
    
    public DancingGoal(final SatyrEntity entityIn) {
      this.setMutexFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
      this.entity = entityIn;
   }

    @Override
    public boolean shouldExecute() {
      if(this.entity.getRNG().nextInt(10) > 0) {
        return false;
      }
      // try to find a nearby campfire
      Vector3d target = findCampfire();
      if(null == target) {
        return false;
      }
      targetX = target.x;
      targetY = target.y;
      targetZ = target.z;
      GreekFantasy.LOGGER.info("Starting dancing goal! at " + target);
      return true;
    }
    
    @Override
    public void startExecuting() {
      this.entity.setStomping(true);
      dancingTime = 1;
    }
    
    @Override
    public void tick() {
      if(dancingTime++ < maxDancingTime) {
        // TODO calculate next position
        double percentDone = dancingTime / maxDancingTime;
        double x = this.targetX + Math.cos(Math.PI * 2 * (percentDone * dancingSpeed)) * 1.5D;
        double y = this.targetY;
        double z = this.targetZ + Math.cos(Math.PI * 2 * (percentDone * dancingSpeed) + Math.PI) * 1.5D;
        double disSq = this.entity.getDistanceSq(x, y, z);
        this.entity.getNavigator().tryMoveToXYZ(x, y, z, disSq > 4.0D ? 0.8D : this.dancingSpeed);
      } else {
        // stop dancing
        this.entity.setStomping(false);;
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
      this.entity.setStomping(false);
    }
    
    private Vector3d findCampfire() {
      GreekFantasy.LOGGER.info("Checking for nearby campfire...");
      final Random rand = this.entity.getRNG();
      final BlockPos entityPos = this.entity.getPosition();
      BlockPos toCheck = entityPos;
      final int radius = 4;

      for (int i = 0; i < 10; i++) {
        toCheck = entityPos.add(rand.nextInt(radius * 2) - radius, 1 - rand.nextInt(3),
            rand.nextInt(radius * 2) - radius);

        if (this.entity.getEntityWorld().getBlockState(toCheck).getBlock() == Blocks.CAMPFIRE) {
          // check if there are empty blocks around the campfire
          // TODO
          
          return new Vector3d(toCheck.getX() + 0.5D, toCheck.getY(), toCheck.getZ() + 0.5D);
        }
      }
      return null;
    }
    
  }
  
}
