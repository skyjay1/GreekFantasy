package greekfantasy.entity;

import java.util.EnumSet;

import net.minecraft.block.BlockState;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.FlyingMovementController;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.passive.IFlyingAnimal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.FlyingPathNavigator;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ElpisEntity extends CreatureEntity implements IFlyingAnimal {
  
  private static final String KEY_HOME = "Home";
  public static final int wanderDistance = 8;
  
  public ElpisEntity(final EntityType<? extends CreatureEntity> type, final World world) {
    super(type, world);
    this.moveController = new FlyingMovementController(this, 20, true);
    this.setPathPriority(PathNodeType.DANGER_FIRE, -1.0F);
    this.setPathPriority(PathNodeType.DAMAGE_FIRE, -1.0F);
  }
  
  public static AttributeModifierMap.MutableAttribute getAttributes() {
    return MobEntity.func_233666_p_()
        .createMutableAttribute(Attributes.MAX_HEALTH, 24.0D)
        .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.25D)
        .createMutableAttribute(Attributes.ATTACK_DAMAGE, 0.5D)
        .createMutableAttribute(Attributes.FLYING_SPEED, 0.4D);
  }
  
  @Override
  protected void registerData() {
    super.registerData();
  }
  
  @Override
  protected void registerGoals() {
    super.registerGoals();
    this.goalSelector.addGoal(3, new ElpisEntity.PanicGoal(1.0D));
    this.goalSelector.addGoal(4, new ElpisEntity.MoveRandomGoal(20, wanderDistance, 0.5D));
    this.goalSelector.addGoal(5, new LookAtGoal(this, PlayerEntity.class, 6.0F));
    this.goalSelector.addGoal(6, new LookRandomlyGoal(this));
  }
  
  @Override
  public void livingTick() {
    super.livingTick();
    if (this.world.isRemote && rand.nextInt(12) == 0) {
      final Vector3d pos = this.getPositionVec();
      double px = pos.x + (this.rand.nextDouble() - 0.5D) * (double) this.getWidth();
      double py = pos.y + this.rand.nextDouble() * (double) this.getHeight();
      double pz = pos.z + (this.rand.nextDouble() - 0.5D) * (double) this.getWidth();
      this.world.addParticle(ParticleTypes.AMBIENT_ENTITY_EFFECT, px, py, pz, 1.0F, 0.60F, 0.92F);
    }    
  }

  @Override
  public void tick() {
    super.tick();
    setNoGravity(true);
  }
  
  @Override
  public float getBlockPathWeight(BlockPos pos, IWorldReader worldIn) {
    return worldIn.isAirBlock(pos) ? 10.0F : 0.0F;
  }

  @Override
  public boolean onLivingFall(float distance, float damageMultiplier) {
    return false;
  }

  @Override
  protected void updateFallState(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
  }
  
  @Override
  protected PathNavigator createNavigator(World worldIn) {
    FlyingPathNavigator flyingpathnavigator = new FlyingPathNavigator(this, worldIn);
    flyingpathnavigator.setCanOpenDoors(false);
    flyingpathnavigator.setCanSwim(false);
    flyingpathnavigator.setCanEnterDoors(true);
    return flyingpathnavigator;
  }

  @Override
  public SoundCategory getSoundCategory() {
    return SoundCategory.NEUTRAL;
  }
  
  @Override
  public void writeAdditional(CompoundNBT compound) {
    super.writeAdditional(compound);
    if(this.getHomePosition() != BlockPos.ZERO && this.getMaximumHomeDistance() > -1.0F) {
      compound.putInt(KEY_HOME + ".x", getHomePosition().getX());
      compound.putInt(KEY_HOME + ".y", getHomePosition().getY());
      compound.putInt(KEY_HOME + ".z", getHomePosition().getZ());
    }
  }
  
  @Override
  public void readAdditional(CompoundNBT compound) {
    super.readAdditional(compound);
    if(compound.contains(KEY_HOME + ".x")) {
      final int x = compound.getInt(KEY_HOME + ".x");
      final int y = compound.getInt(KEY_HOME + ".y");
      final int z = compound.getInt(KEY_HOME + ".z");
      this.setHomePosAndDistance(new BlockPos(x, y, z), wanderDistance);
    }
  }
  
  public BlockPos getWanderCenter() {
    final BlockPos home = this.getHomePosition();
    return this.getMaximumHomeDistance() > -1.0F && home != BlockPos.ZERO ? home : this.getPosition();
  }
  
  class PanicGoal extends net.minecraft.entity.ai.goal.PanicGoal {

    public PanicGoal(double speed) {
      super(ElpisEntity.this, speed);
    }
    
  }

  class MoveRandomGoal extends Goal {
    
    private final int chance;
    private final int radius;
    private final double speed;
    
    public MoveRandomGoal(final int chanceIn, final int radiusIn, final double speedIn) {
      setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
      chance = chanceIn;
      radius = radiusIn;
      speed = speedIn;
    }

    @Override
    public boolean shouldExecute() {
      return (ElpisEntity.this.getNavigator().noPath() && ElpisEntity.this.rand.nextInt(chance) == 0);
    }

    @Override
    public boolean shouldContinueExecuting() {
      return false;
    }

    @Override
    public void tick() {
      BlockPos pos = ElpisEntity.this.getWanderCenter();

      for (int checks = 0; checks < 3; checks++) {
        BlockPos posCheck = pos.add(ElpisEntity.this.rand.nextInt(radius * 2) - radius, ElpisEntity.this.rand.nextInt(radius) - (radius / 2), ElpisEntity.this.rand.nextInt(radius * 2) - radius);
        if (ElpisEntity.this.world.isAirBlock(posCheck)) {
          ElpisEntity.this.getNavigator().tryMoveToXYZ(posCheck.getX() + 0.5D, posCheck.getY() + 0.5D, posCheck.getZ() + 0.5D, speed);
          //ElpisEntity.this.moveController.setMoveTo(posCheck.getX() + 0.5D, posCheck.getY() + 0.5D, posCheck.getZ() + 0.5D, speed);
          if (ElpisEntity.this.getAttackTarget() == null) {
            ElpisEntity.this.getLookController().setLookPosition(posCheck.getX() + 0.5D, posCheck.getY() + 0.5D, posCheck.getZ() + 0.5D, 180.0F, 20.0F);
          }
          break;
        }
      }
    }
  }

  @OnlyIn(Dist.CLIENT)
  public float getAlpha(final float partialTick) {
    final float minAlpha = 0.18F;
    final float cosAlpha = 0.5F + 0.5F * MathHelper.cos((this.getEntityId() + this.ticksExisted + partialTick) * 0.025F);
    return MathHelper.clamp(cosAlpha, minAlpha, 1.0F);
  }

}
