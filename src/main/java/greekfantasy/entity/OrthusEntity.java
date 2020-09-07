package greekfantasy.entity;

import greekfantasy.GreekFantasy;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class OrthusEntity extends MonsterEntity {
  
  // TODO add fire breathing attack
  private static final byte FIRE_START = 6;
  private static final byte FIRE_END = 7;
  
  private boolean isFireBreathing;
  
  public OrthusEntity(final EntityType<? extends OrthusEntity> type, final World worldIn) {
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
    this.goalSelector.addGoal(5, new LookAtGoal(this, PlayerEntity.class, 6.0F));
    //this.goalSelector.addGoal(6, new LookRandomlyGoal(this));
    if(GreekFantasy.CONFIG.ORTHUS_ATTACK.get()) {
      // TODO make goal
    }
  }
  
  @Override
  public void livingTick() {
    super.livingTick();
    
    if(this.isServerWorld() && this.isFireBreathing() && this.getAttackTarget() == null) {
      this.setFireBreathing(false);
    }
    
    // spawn particles
    if (world.isRemote() && this.isFireBreathing()) {
      spawnFireParticles();
    }
  }
  
  @OnlyIn(Dist.CLIENT)
  public void handleStatusUpdate(byte id) {
    switch(id) {
    case FIRE_START:
      this.isFireBreathing = true;
      break;
    case FIRE_END:
      this.isFireBreathing = false;
      break;
    default:
      super.handleStatusUpdate(id);
      break;
    }
  }
  
  public void spawnFireParticles() {
    final double motion = 0.08D;
    final double radius = 1.2D;
    for (int i = 0; i < 5; i++) {
      world.addParticle(ParticleTypes.FLAME, 
          this.getPosX() + (world.rand.nextDouble() - 0.5D) * radius, 
          this.getPosYEye() + (world.rand.nextDouble() - 0.5D) * radius * 0.75D, 
          this.getPosZ() + (world.rand.nextDouble() - 0.5D) * radius,
          (world.rand.nextDouble() - 0.5D) * motion, 
          (world.rand.nextDouble() - 0.5D) * motion * 0.5D,
          (world.rand.nextDouble() - 0.5D) * motion);
    }
  }
  
  public void setFireBreathing(final boolean fireBreathing) {
    this.isFireBreathing = fireBreathing;
    if(this.isServerWorld()) {
      this.world.setEntityState(this, fireBreathing ? FIRE_START : FIRE_END);    
    }
  }

  public boolean isFireBreathing() {
    return this.isFireBreathing;
  }

}
