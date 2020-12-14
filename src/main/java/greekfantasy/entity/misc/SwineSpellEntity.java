package greekfantasy.entity.misc;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import greekfantasy.GFRegistry;
import greekfantasy.GreekFantasy;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.network.IPacket;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class SwineSpellEntity extends EffectProjectileEntity {
  
  public SwineSpellEntity(EntityType<? extends ProjectileEntity> entityType, World world) {
    super(entityType, world);
  }
  
  protected SwineSpellEntity(World worldIn, LivingEntity thrower) {
    this(GFRegistry.SWINE_SPELL_ENTITY, worldIn);
    super.setShooter(thrower);
    this.setPosition(thrower.getPosX(), thrower.getPosYEye() - 0.1D, thrower.getPosZ());
    // this unmapped method from ProjectileEntity does some math, then calls #shoot
    // params: thrower, rotationPitch, rotationYaw, ???, speed, inaccuracy
    func_234612_a_(thrower, thrower.rotationPitch, thrower.rotationYaw, 0.0F, 0.75F, 0.5F);
    markVelocityChanged();
  }
  
  public static SwineSpellEntity create(World worldIn, LivingEntity thrower) {
    return new SwineSpellEntity(worldIn, thrower);
  }

  @Override
  public IPacket<?> createSpawnPacket() {
    return NetworkHooks.getEntitySpawningPacket(this);
  }

  protected List<EffectInstance> getPotionEffects(final LivingEntity entity) {
    final Effect effect = GreekFantasy.CONFIG.isSwineEnabled() ? GFRegistry.SWINE_EFFECT : Effects.SLOWNESS;
    return ImmutableList.of(
        new EffectInstance(effect, GreekFantasy.CONFIG.getSwineWandDuration(), 0, false, false, true), 
        new EffectInstance(Effects.MINING_FATIGUE, GreekFantasy.CONFIG.getSwineWandDuration() - 1, 1, false, false, false),
        new EffectInstance(Effects.WEAKNESS, GreekFantasy.CONFIG.getSwineWandDuration() - 1, 1, false, false, false)); 
  }
  
  protected IParticleData getImpactParticle(final LivingEntity entity) { return ParticleTypes.ENCHANT; }
  
  protected IParticleData getTrailParticle() { return ParticleTypes.ENCHANTED_HIT; }
  
  protected float getImpactDamage(final LivingEntity entity) { return 0.0F; }
}
