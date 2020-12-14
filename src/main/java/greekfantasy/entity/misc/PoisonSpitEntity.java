package greekfantasy.entity.misc;

import java.util.List;

import com.google.common.collect.ImmutableList;

import greekfantasy.GFRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.network.IPacket;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class PoisonSpitEntity extends EffectProjectileEntity {
  
  public PoisonSpitEntity(EntityType<? extends ProjectileEntity> entityType, World world) {
    super(entityType, world);
  }
  
  protected PoisonSpitEntity(World worldIn, LivingEntity thrower) {
    this(GFRegistry.POISON_SPIT_ENTITY, worldIn);
    this.lifespan = 80;
    super.setShooter(thrower);
    this.setPosition(thrower.getPosX(), thrower.getPosYEye() - 0.1D, thrower.getPosZ());
    // this unmapped method from ProjectileEntity does some math, then calls #shoot
    // params: thrower, rotationPitch, rotationYaw, ???, speed, inaccuracy
    func_234612_a_(thrower, thrower.rotationPitch, thrower.rotationYaw, 0.0F, 0.78F, 0.85F);
    markVelocityChanged();
  }
  
  public static PoisonSpitEntity create(World worldIn, LivingEntity thrower) {
    return new PoisonSpitEntity(worldIn, thrower);
  }
  
  @Override
  public IPacket<?> createSpawnPacket() {
    return NetworkHooks.getEntitySpawningPacket(this);
  }

  protected List<EffectInstance> getPotionEffects(final LivingEntity entity) { 
    return ImmutableList.of(new EffectInstance(Effects.POISON, 90, 1)); 
  }
  
  protected IParticleData getImpactParticle(final LivingEntity entity) { return ParticleTypes.DAMAGE_INDICATOR; }
  
  protected IParticleData getTrailParticle() { return ParticleTypes.SNEEZE; }
  
  protected float getImpactDamage(final LivingEntity entity) { return 0.5F; }
}
