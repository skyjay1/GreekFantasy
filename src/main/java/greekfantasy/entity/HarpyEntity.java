package greekfantasy.entity;

import greekfantasy.GFRegistry;
import greekfantasy.entity.ai.GoToBlockGoal;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.FlyingMovementController;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.passive.IFlyingAnimal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.pathfinding.FlyingPathNavigator;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public class HarpyEntity extends MonsterEntity implements IFlyingAnimal {
    
  public float flyingTime;

  public HarpyEntity(final EntityType<? extends HarpyEntity> type, final World worldIn) {
    super(type, worldIn);
    this.moveController = new FlyingMovementController(this, 10, false);
    this.setPathPriority(PathNodeType.DANGER_FIRE, -1.0F);
    this.setPathPriority(PathNodeType.DAMAGE_FIRE, -1.0F);
  }
  
  public static AttributeModifierMap.MutableAttribute getAttributes() {
    return MobEntity.func_233666_p_()
        .createMutableAttribute(Attributes.MAX_HEALTH, 24.0D)
        .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.25D)
        .createMutableAttribute(Attributes.ATTACK_DAMAGE, 1.0D)
        .createMutableAttribute(Attributes.FLYING_SPEED, 0.9D);
  }
  
  @Override
  protected void registerGoals() {
    super.registerGoals();
    this.goalSelector.addGoal(0, new SwimGoal(this));
    this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.0D, true));
    this.goalSelector.addGoal(5, new LookAtGoal(this, PlayerEntity.class, 6.0F));
    this.goalSelector.addGoal(6, new LookRandomlyGoal(this));
    this.goalSelector.addGoal(7, new GoToBlockGoal(this, 10, 12, 0.9D) {
      @Override
      public boolean shouldMoveTo(IWorldReader worldIn, BlockPos pos) { return worldIn.getBlockState(pos).isIn(GFRegistry.NEST_BLOCK); }
      @Override
      protected Vector3d getVecForBlockPos(final BlockPos pos) { return super.getVecForBlockPos(pos).add(0.0D, 1.0D, 0.0D); }
    });
    this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
    this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
  }
  
//  @Override
//  public void tick() {
//    this.setNoGravity(true);
//    super.tick();
//    this.setNoGravity(false);
//  }

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
  }

  @Override
  public boolean onLivingFall(float distance, float damageMultiplier) {
    return false;
  }

  @Override
  protected void updateFallState(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
  }
  
  @Override
  protected float playFlySound(float volume) {
    this.playSound(SoundEvents.ENTITY_PARROT_FLY, 0.25F, 0.9F);
    return volume;
 }

  public boolean isFlying() {
    return !this.onGround || this.getMotion().lengthSquared() > 0.06D;
  }
}
