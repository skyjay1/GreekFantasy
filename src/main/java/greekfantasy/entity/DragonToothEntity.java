package greekfantasy.entity;

import greekfantasy.GFRegistry;
import greekfantasy.GreekFantasy;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileItemEntity;
import net.minecraft.item.Item;
import net.minecraft.network.IPacket;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.ITeleporter;
import net.minecraftforge.fml.network.NetworkHooks;

public class DragonToothEntity extends ProjectileItemEntity {

  public DragonToothEntity(EntityType<? extends DragonToothEntity> entityType, World world) {
    super(entityType, world);
  }

  private DragonToothEntity(World worldIn, LivingEntity thrower) {
    super(GFRegistry.DRAGON_TOOTH_ENTITY, thrower, worldIn);
  }

  private DragonToothEntity(World worldIn, double x, double y, double z) {
    super(GFRegistry.DRAGON_TOOTH_ENTITY, x, y, z, worldIn);
  }
  
  public static DragonToothEntity create(World worldIn, double x, double y, double z) {
    return new DragonToothEntity(worldIn, x, y, z);
  }
  
  public static DragonToothEntity create(World worldIn, LivingEntity thrower) {
    return new DragonToothEntity(worldIn, thrower);
  }

  @Override
  protected Item getDefaultItem() {
    return GFRegistry.DRAGON_TOOTH;
  }

  @Override
  protected void onEntityHit(EntityRayTraceResult raytrace) {
    super.onEntityHit(raytrace);
    final float damage = GreekFantasy.CONFIG.doesDragonToothSpawnSparti() ? 0.0F : 1.5F;
    raytrace.getEntity().attackEntityFrom(DamageSource.causeThrownDamage(this, func_234616_v_()), damage);
  }

  @Override
  protected void onImpact(RayTraceResult raytrace) {
    super.onImpact(raytrace);
    if (!this.world.isRemote() && this.isAlive() && GreekFantasy.CONFIG.doesDragonToothSpawnSparti()) {
      Entity thrower = func_234616_v_();
      // spawn a configurable number of sparti
      for(int i = 0, n = GreekFantasy.CONFIG.getNumSpartiSpawned(); i < n; i++) {
        final SpartiEntity sparti = GFRegistry.SPARTI_ENTITY.create(world);
        sparti.setLocationAndAngles(raytrace.getHitVec().x, raytrace.getHitVec().y, raytrace.getHitVec().z, 0, 0);
        if (thrower instanceof PlayerEntity) {
          sparti.rotationPitch = thrower.rotationYaw + 180.0F;
          sparti.setOwner((PlayerEntity) thrower);
        }
        sparti.setSpawning();
        sparti.setEquipmentOnSpawn();
        world.addEntity(sparti);
      }
      remove();
    }
  }

  @Override
  public void tick() {
    Entity entity = func_234616_v_();
    if (entity instanceof net.minecraft.entity.player.PlayerEntity && !entity.isAlive()) {
      remove();
    } else {
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

}
