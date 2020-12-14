package greekfantasy.entity.misc;

import java.util.List;

import com.google.common.collect.ImmutableList;

import greekfantasy.GFRegistry;
import net.minecraft.entity.CreatureAttribute;
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

public class HealingSpellEntity extends EffectProjectileEntity {
  
  public HealingSpellEntity(EntityType<? extends ProjectileEntity> entityType, World world) {
    super(entityType, world);
  }
  
  protected HealingSpellEntity(World worldIn, LivingEntity thrower) {
    this(GFRegistry.HEALING_SPELL_ENTITY, worldIn);
    super.setShooter(thrower);
    this.setPosition(thrower.getPosX(), thrower.getPosYEye() - 0.1D, thrower.getPosZ());
    // this unmapped method from ProjectileEntity does some math, then calls #shoot
    // params: thrower, rotationPitch, rotationYaw, ???, speed, inaccuracy
    func_234612_a_(thrower, thrower.rotationPitch, thrower.rotationYaw, 0.0F, 0.75F, 0.5F);
    markVelocityChanged();
  }
  
  public static HealingSpellEntity create(World worldIn, LivingEntity thrower) {
    return new HealingSpellEntity(worldIn, thrower);
  }

  @Override
  public IPacket<?> createSpawnPacket() {
    return NetworkHooks.getEntitySpawningPacket(this);
  }

  @Override
  protected List<EffectInstance> getPotionEffects(final LivingEntity entity) { 
    return ImmutableList.of(new EffectInstance(Effects.INSTANT_HEALTH, 1, 1)); 
  }
  
  protected IParticleData getImpactParticle(final LivingEntity entity) { return entity.getCreatureAttribute() == CreatureAttribute.UNDEAD ? ParticleTypes.DAMAGE_INDICATOR : ParticleTypes.HEART; }
  
  protected IParticleData getTrailParticle() { return ParticleTypes.HAPPY_VILLAGER; }
  
  protected float getImpactDamage(final LivingEntity entity) { return 0.0F; }
}
