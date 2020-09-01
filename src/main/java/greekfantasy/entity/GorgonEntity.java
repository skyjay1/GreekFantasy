package greekfantasy.entity;

import java.util.EnumSet;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.monster.EndermanEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class GorgonEntity extends CreatureEntity {
  
  // TODO paralyzes upon eye contact (slowness?)

  public GorgonEntity(final EntityType<? extends GorgonEntity> type, final World worldIn) {
    super(type, worldIn);
  }
  
  @Override
  protected void registerGoals() {
    super.registerGoals();
    this.goalSelector.addGoal(0, new SwimGoal(this));
    this.goalSelector.addGoal(4, new LookAtGoal(this, PlayerEntity.class, 10.0F));
    this.goalSelector.addGoal(5, new LookRandomlyGoal(this));
  }
  
  public static AttributeModifierMap.MutableAttribute getAttributes() {
    return MobEntity.func_233666_p_()
        .createMutableAttribute(Attributes.MAX_HEALTH, 24.0D)
        .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.25D)
        .createMutableAttribute(Attributes.ATTACK_DAMAGE, 1.0D);
  }
  
  public boolean isPlayerStaring(final PlayerEntity player) {
    Vector3d vector3d = player.getLook(1.0F).normalize();
    Vector3d vector3d1 = new Vector3d(this.getPosX() - player.getPosX(), this.getPosYEye() - player.getPosYEye(),
        this.getPosZ() - player.getPosZ());
    double d0 = vector3d1.length();
    vector3d1 = vector3d1.normalize();
    double d1 = vector3d.dotProduct(vector3d1);
    return d1 > 1.0D - 0.025D / d0 ? player.canEntityBeSeen(this) : false;
  }
  
  public boolean useStareAttack(final LivingEntity target) {
    // TODO balance?
    target.addPotionEffect(new EffectInstance(Effects.SLOWNESS, 60, 9, false, false));
    target.removeActivePotionEffect(Effects.SPEED);
    // spawn particles
    if (world.isRemote()) {
      final double motion = 0.01D;
      final double radius = 0.8D;
      for (int i = 0; i < 10; i++) {
        world.addParticle(ParticleTypes.END_ROD, 
            this.getPosX() + (world.rand.nextDouble() - 0.5D) * radius, 
            this.getPosYEye() + (world.rand.nextDouble() - 0.5D) * radius, 
            this.getPosZ() + (world.rand.nextDouble() - 0.5D) * radius,
            (world.rand.nextDouble() - 0.5D) * motion, 
            (world.rand.nextDouble() - 0.5D) * motion,
            (world.rand.nextDouble() - 0.5D) * motion);
      }
    }
    return false;
  }
  
  class StareAttackGoal extends Goal {
    private final GorgonEntity entity;
    private int cooldown;
    
    public StareAttackGoal(final GorgonEntity entityIn) {
       this.setMutexFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
       this.entity = entityIn;
    }

    @Override
    public boolean shouldExecute() {
      if(this.cooldown > 0) {
        cooldown--;
      } else if (this.entity.getAttackTarget() instanceof PlayerEntity) {
        double d0 = this.entity.getAttackTarget().getDistanceSq(this.entity);
        return d0 > 256.0D ? false : this.entity.isPlayerStaring((PlayerEntity) this.entity.getAttackTarget());
      }
      return false;
    }

    @Override
    public void startExecuting() {
      final LivingEntity target = this.entity.getAttackTarget();
      this.entity.getNavigator().clearPath();
      this.entity.getLookController().setLookPosition(target.getPosX(), target.getPosYEye(), target.getPosZ());
      this.entity.useStareAttack(target);
      this.cooldown = 100;
    }
    
    @Override
    public boolean shouldContinueExecuting() {
      return false;
    }
    
    @Override
    public void resetTask() {
      this.cooldown = 0;
    }
  }
  
}
