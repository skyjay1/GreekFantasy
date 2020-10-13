package greekfantasy.entity;

import greekfantasy.GFRegistry;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.network.IPacket;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.ITeleporter;
import net.minecraftforge.fml.network.NetworkHooks;

public class HealingSpellEntity extends ProjectileEntity {

  public HealingSpellEntity(EntityType<? extends HealingSpellEntity> entityType, World world) {
    super(entityType, world);
  }
  
  private HealingSpellEntity(World worldIn, LivingEntity thrower) {
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
  protected void onEntityHit(EntityRayTraceResult raytrace) {
    super.onEntityHit(raytrace);
    Entity thrower = func_234616_v_();
    if (raytrace.getEntity() != thrower && raytrace.getEntity() instanceof LivingEntity) {
      final LivingEntity entity = (LivingEntity)raytrace.getEntity();
      entity.addPotionEffect(new EffectInstance(Effects.INSTANT_HEALTH, 1, 1));
      addParticles(entity.getCreatureAttribute() == CreatureAttribute.UNDEAD ? ParticleTypes.DAMAGE_INDICATOR : ParticleTypes.HEART, 6 + rand.nextInt(6));
    }
  }
  
  @Override
  protected void onImpact(RayTraceResult raytrace) {
    super.onImpact(raytrace);
    if (!this.world.isRemote() && this.isAlive()) {
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
    if(this.ticksExisted > 300) {
      remove();
      return;
    }
    // check for impact
    RayTraceResult raytraceresult = ProjectileHelper.func_234618_a_(this, this::func_230298_a_);
    if (raytraceresult != null && raytraceresult.getType() != RayTraceResult.Type.MISS
        && !net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, raytraceresult)) {
      this.onImpact(raytraceresult);
    }
    // particle trail
    if(this.ticksExisted > 2) {
      addParticles(ParticleTypes.HAPPY_VILLAGER, 2);
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
  
  private void addParticles(final IParticleData type, final int count) {
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
