package greekfantasy.entity;

import java.util.EnumSet;

import greekfantasy.GreekFantasy;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class GorgonEntity extends MonsterEntity {
  
  // TODO paralyzes upon eye contact (slowness?)
  
  private static final byte STARE_ATTACK = 9;

  public GorgonEntity(final EntityType<? extends GorgonEntity> type, final World worldIn) {
    super(type, worldIn);
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
    this.goalSelector.addGoal(0, new SwimGoal(this));
    this.goalSelector.addGoal(3, new StareAttackGoal(this));
    this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.0D, true));
    this.goalSelector.addGoal(5, new LookAtGoal(this, PlayerEntity.class, 10.0F));
    this.goalSelector.addGoal(6, new LookRandomlyGoal(this));
    this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
    this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
  }
  
  @OnlyIn(Dist.CLIENT)
  public void handleStatusUpdate(byte id) {
    switch(id) {
    case STARE_ATTACK:
      stareAttackParticles();
      break;
    default:
      super.handleStatusUpdate(id);
      break;
    }
  }
  
  public void stareAttackParticles() {
    if (world.isRemote()) {
      final double motion = 0.08D;
      final double radius = 1.2D;
      for (int i = 0; i < 5; i++) {
        world.addParticle(ParticleTypes.END_ROD, 
            this.getPosX() + (world.rand.nextDouble() - 0.5D) * radius, 
            this.getPosYEye() + (world.rand.nextDouble() - 0.5D) * radius * 0.75D, 
            this.getPosZ() + (world.rand.nextDouble() - 0.5D) * radius,
            (world.rand.nextDouble() - 0.5D) * motion, 
            (world.rand.nextDouble() - 0.5D) * motion * 0.5D,
            (world.rand.nextDouble() - 0.5D) * motion);
      }
    }
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
    GreekFantasy.LOGGER.info("Gorgon stare attack - activate!");
    target.addPotionEffect(new EffectInstance(Effects.SLOWNESS, 60, 9, false, false));
    target.removeActivePotionEffect(Effects.SPEED);
    if(this.isServerWorld()) {
      this.world.setEntityState(this, STARE_ATTACK);
    }
    return false;
  }
  
  class StareAttackGoal extends Goal {
    private final GorgonEntity entity;
    private final int MAX_COOLDOWN = 200;
    private int cooldown;
    
    public StareAttackGoal(final GorgonEntity entityIn) {
       this.setMutexFlags(EnumSet.of(Goal.Flag.LOOK));
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
      this.entity.getLookController().setLookPositionWithEntity(target, 100.0F, 100.0F);
      this.entity.useStareAttack(target);
      this.cooldown = MAX_COOLDOWN;
    }
    
    @Override
    public boolean shouldContinueExecuting() {
      return false;
    }
    
    @Override
    public void resetTask() {
      this.cooldown = MAX_COOLDOWN;
    }
  }
  
}
