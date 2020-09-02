package greekfantasy.entity;

import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.FlyingMovementController;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.passive.IFlyingAnimal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.pathfinding.FlyingPathNavigator;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class HarpyEntity extends MonsterEntity implements IFlyingAnimal {

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
        .createMutableAttribute(Attributes.ATTACK_DAMAGE, 1.0D);
  }
  
  @Override
  protected void registerGoals() {
    super.registerGoals();
    this.goalSelector.addGoal(4, new LookAtGoal(this, PlayerEntity.class, 6.0F));
    this.goalSelector.addGoal(5, new LookRandomlyGoal(this));
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
    
    Vector3d m = getMotion();
    if (!this.onGround && m.y < 0.0D) {
      setMotion(m.mul(1.0D, 0.6D, 1.0D));
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
