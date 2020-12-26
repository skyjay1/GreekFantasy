package greekfantasy.entity.misc;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.network.IPacket;
import net.minecraft.particles.IParticleData;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.ITeleporter;
import net.minecraftforge.fml.network.NetworkHooks;

public abstract class EffectProjectileEntity extends ProjectileEntity {
  
  protected int lifespan = 300;

  public EffectProjectileEntity(EntityType<? extends ProjectileEntity> entityType, World world) {
    super(entityType, world);
  }

  @Override
  protected void onEntityHit(EntityRayTraceResult raytrace) {
    super.onEntityHit(raytrace);
    Entity thrower = func_234616_v_();
    if (raytrace.getEntity() != thrower && raytrace.getEntity() instanceof LivingEntity) {
      final LivingEntity entity = (LivingEntity)raytrace.getEntity();
      // add potion effects
      for(final EffectInstance effect : getPotionEffects(entity)) {
        entity.addPotionEffect(effect);
      }
      // impact may inflict damage
      float damage = getImpactDamage(entity);
      if(damage > 0 && thrower instanceof LivingEntity) {
        entity.attackEntityFrom(DamageSource.causeIndirectDamage(this, (LivingEntity)thrower), damage);
      }
      // add particle effect
      addParticles(getImpactParticle(entity), 6 + rand.nextInt(6));
    }
  }
  
  @Override
  protected void onImpact(RayTraceResult raytrace) {
    super.onImpact(raytrace);
    if (this.isAlive()) {
      remove();
    }
  }

  @Override
  public void tick() {
    Entity thrower = func_234616_v_();
    if (thrower instanceof net.minecraft.entity.player.PlayerEntity && !thrower.isAlive()) {
      remove();
      return;
    }
    // remove if too old
    if(this.ticksExisted > lifespan) {
      remove();
      return;
    }
    // check for impact
    if(!this.getEntityWorld().isRemote()) {
      RayTraceResult raytraceresult = ProjectileHelper.func_234618_a_(this, this::func_230298_a_);
      if (raytraceresult != null && raytraceresult.getType() != RayTraceResult.Type.MISS
          && !net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, raytraceresult)) {
        this.onImpact(raytraceresult);
      }
    }
    // particle trail
    if(this.ticksExisted > 2) {
      addParticles(getTrailParticle(), 2);
    }
    // movement
    Vector3d motion = this.getMotion();
    double d0 = this.getPosX() + motion.x;
    double d1 = this.getPosY() + motion.y;
    double d2 = this.getPosZ() + motion.z;
    // lerp rotation and pitch
    this.func_234617_x_();
    // actually move the entity
    this.setPosition(d0, d1, d2);
    // super method
    super.tick();
  }

  @Override
  public Entity changeDimension(ServerWorld serverWorld, ITeleporter iTeleporter) {
    Entity entity = func_234616_v_();
    if (entity != null && entity.world.getDimensionKey() != serverWorld.getDimensionKey()) {
      setShooter((Entity) null);
    }
    return super.changeDimension(serverWorld, iTeleporter);
  }
  
  @Override
  public IPacket<?> createSpawnPacket() {
    return NetworkHooks.getEntitySpawningPacket(this);
  }

  @Override
  protected void registerData() {
  }
  
  abstract List<EffectInstance> getPotionEffects(final LivingEntity entity);
  
  abstract IParticleData getImpactParticle(final LivingEntity entity);
  
  abstract IParticleData getTrailParticle();
  
  abstract float getImpactDamage(final LivingEntity entity);
  
  protected void addParticles(final IParticleData type, final int count) {
    if(this.getEntityWorld().isRemote()) {
      final double x = getPosX();
      final double y = getPosY() + 0.1D;
      final double z = getPosZ();
      final double motion = 0.08D;
      final double width = getWidth() / 2;
      final double height = getHeight() / 2;
      for (int i = 0; i < count; i++) {
        world.addParticle(type, 
            x + (world.rand.nextDouble() - 0.5D) * width, 
            y + height, 
            z + (world.rand.nextDouble() - 0.5D) * width,
            (world.rand.nextDouble() - 0.5D) * motion, 
            (world.rand.nextDouble() - 0.5D) * motion,
            (world.rand.nextDouble() - 0.5D) * motion);
      }
    }
  }

}
