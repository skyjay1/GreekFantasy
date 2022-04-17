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
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class CurseEntity extends EffectProjectileEntity {
  
  private static final Effect[] CURSES = { 
      Effects.BLINDNESS, Effects.MOVEMENT_SLOWDOWN, Effects.POISON, Effects.HARM, Effects.WEAKNESS,
      Effects.WITHER, Effects.LEVITATION, Effects.GLOWING, Effects.DIG_SLOWDOWN, Effects.HUNGER,
      Effects.CONFUSION, Effects.UNLUCK };
  
  public CurseEntity(EntityType<? extends ProjectileEntity> entityType, World world) {
    super(entityType, world);
  }
  
  protected CurseEntity(World worldIn, LivingEntity thrower) {
    this(GFRegistry.CURSE_ENTITY, worldIn);
    this.lifespan = 90;
    super.setOwner(thrower);
    this.setPos(thrower.getX(), thrower.getEyeY() - 0.1D, thrower.getZ());
    // this unmapped method from ProjectileEntity does some math, then calls #shoot
    // params: thrower, rotationPitch, rotationYaw, ???, speed, inaccuracy
    shootFromRotation(thrower, thrower.xRot, thrower.yRot, 0.0F, 0.78F, 0.4F);
    markHurt();
  }
  
  public static CurseEntity create(World worldIn, LivingEntity thrower) {
    return new CurseEntity(worldIn, thrower);
  }

  @Override
  public IPacket<?> getAddEntityPacket() {
    return NetworkHooks.getEntitySpawningPacket(this);
  }

  @Override
  protected List<EffectInstance> getPotionEffects(final LivingEntity entity) { 
    return ImmutableList.of(new EffectInstance(CURSES[entity.getRandom().nextInt(CURSES.length)], 200, entity.getRandom().nextInt(2))); 
  }
  
  protected IParticleData getImpactParticle(final LivingEntity entity) { return ParticleTypes.DAMAGE_INDICATOR; }
  
  protected IParticleData getTrailParticle() { return ParticleTypes.SMOKE; }
  
  protected float getImpactDamage(final LivingEntity entity) { return 0.5F; }
}
