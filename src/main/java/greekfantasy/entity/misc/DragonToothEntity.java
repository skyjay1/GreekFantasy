package greekfantasy.entity.misc;

import greekfantasy.GFRegistry;
import greekfantasy.GreekFantasy;
import greekfantasy.entity.SpartiEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileItemEntity;
import net.minecraft.item.Item;
import net.minecraft.network.IPacket;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IServerWorld;
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
  protected void onHitEntity(EntityRayTraceResult raytrace) {
    super.onHitEntity(raytrace);
    final float damage = GreekFantasy.CONFIG.doesDragonToothSpawnSparti() ? 0.0F : 1.5F;
    raytrace.getEntity().hurt(DamageSource.thrown(this, getOwner()), damage);
  }

  @Override
  protected void onHit(RayTraceResult raytrace) {
    super.onHit(raytrace);
    if (!this.level.isClientSide() && level instanceof IServerWorld && this.isAlive() && GreekFantasy.CONFIG.doesDragonToothSpawnSparti()) {
      Entity thrower = getOwner();
      // spawn a configurable number of sparti
      for(int i = 0, n = GreekFantasy.CONFIG.getNumSpartiSpawned(), life = 20 * GreekFantasy.CONFIG.getSpartiLifespan(); i < n; i++) {
        final SpartiEntity sparti = GFRegistry.SPARTI_ENTITY.create(level);
        sparti.moveTo(raytrace.getLocation().x, raytrace.getLocation().y, raytrace.getLocation().z, 0, 0);
        level.addFreshEntity(sparti);
        if (thrower instanceof PlayerEntity) {
          sparti.xRot = MathHelper.wrapDegrees(thrower.yRot + 180.0F);
          sparti.setTame(true);
          sparti.setOwnerUUID(thrower.getUUID());
        }
        sparti.setLimitedLife(life);
        sparti.finalizeSpawn((IServerWorld)level, level.getCurrentDifficultyAt(new BlockPos(raytrace.getLocation())), SpawnReason.MOB_SUMMONED, null, null);
      }
      remove();
    }
  }

  @Override
  public void tick() {
    Entity entity = getOwner();
    if (entity instanceof net.minecraft.entity.player.PlayerEntity && !entity.isAlive()) {
      remove();
    } else {
      super.tick();
    }
  }

  @Override
  public Entity changeDimension(ServerWorld serverWorld, ITeleporter iTeleporter) {
    Entity entity = getOwner();
    if (entity != null && entity.level.dimension() != serverWorld.dimension()) {
      setOwner((Entity) null);
    }
    return super.changeDimension(serverWorld, iTeleporter);
  }
  
  @Override
  public IPacket<?> getAddEntityPacket() {
    return NetworkHooks.getEntitySpawningPacket(this);
  }

  @Override
  protected float getGravity() {
    return 0.11F;
  }
}
