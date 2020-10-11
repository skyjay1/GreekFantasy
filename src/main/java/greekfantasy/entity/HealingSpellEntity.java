package greekfantasy.entity;

import greekfantasy.GFRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.network.IPacket;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
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
    this.setPosition(thrower.getPosX() - (double)(thrower.getWidth() + 1.0F) * 0.5D * (double)MathHelper.sin(thrower.renderYawOffset * ((float)Math.PI / 180F)), thrower.getPosYEye() - (double)0.25F, thrower.getPosZ() + (double)(thrower.getWidth() + 1.0F) * 0.5D * (double)MathHelper.cos(thrower.renderYawOffset * ((float)Math.PI / 180F)));
    addParticles(ParticleTypes.ENCHANTED_HIT, 12);
  }
  
  public static HealingSpellEntity create(World worldIn, LivingEntity thrower) {
    return new HealingSpellEntity(worldIn, thrower);
  }

  @Override
  protected void onEntityHit(EntityRayTraceResult raytrace) {
    super.onEntityHit(raytrace);
    Entity thrower = func_234616_v_();
    if (raytrace.getEntity() != thrower && raytrace.getEntity() instanceof LivingEntity) {
       ((LivingEntity)raytrace.getEntity()).addPotionEffect(new EffectInstance(Effects.INSTANT_HEALTH, 1, 0));
    }

 }
  @Override
  protected void onImpact(RayTraceResult raytrace) {
    super.onImpact(raytrace);
    addParticles(ParticleTypes.HEART, 6 + rand.nextInt(6));
    if (!this.world.isRemote() && this.isAlive()) {
      remove();
    }
  }

  @Override
  public void tick() {
    Entity entity = func_234616_v_();
    if (entity instanceof net.minecraft.entity.player.PlayerEntity && !entity.isAlive()) {
      remove();
    } else {
      // spawn particles
      addParticles(ParticleTypes.HAPPY_VILLAGER, 2);
      // super method
      super.tick();
    }
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
      final double x = getPosX() + 0.5D;
      final double y = getPosY() + 0.1D;
      final double z = getPosZ() + 0.5D;
      final double motion = 0.08D;
      final double radius = getWidth();
      for (int i = 0; i < count; i++) {
        world.addParticle(type, 
            x + (world.rand.nextDouble() - 0.5D) * radius, 
            y + getHeight() / 2, 
            z + (world.rand.nextDouble() - 0.5D) * radius,
            (world.rand.nextDouble() - 0.5D) * motion, 
            0.5D,
            (world.rand.nextDouble() - 0.5D) * motion);
      }
    }
  }

}
