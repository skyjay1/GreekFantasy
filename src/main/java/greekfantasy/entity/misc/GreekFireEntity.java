package greekfantasy.entity.misc;

import greekfantasy.GFRegistry;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileItemEntity;
import net.minecraft.item.Item;
import net.minecraft.network.IPacket;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.ITeleporter;
import net.minecraftforge.fml.network.NetworkHooks;

public class GreekFireEntity extends ProjectileItemEntity {

  public GreekFireEntity(EntityType<? extends GreekFireEntity> entityType, World world) {
    super(entityType, world);
  }

  private GreekFireEntity(World worldIn, LivingEntity thrower) {
    super(GFRegistry.GREEK_FIRE_ENTITY, thrower, worldIn);
  }

  private GreekFireEntity(World worldIn, double x, double y, double z) {
    super(GFRegistry.GREEK_FIRE_ENTITY, x, y, z, worldIn);
  }
  
  public static GreekFireEntity create(World worldIn, double x, double y, double z) {
    return new GreekFireEntity(worldIn, x, y, z);
  }
  
  public static GreekFireEntity create(World worldIn, LivingEntity thrower) {
    return new GreekFireEntity(worldIn, thrower);
  }

  @Override
  protected Item getDefaultItem() {
    return GFRegistry.GREEK_FIRE;
  }

  @Override
  protected void onEntityHit(EntityRayTraceResult raytrace) {
    super.onEntityHit(raytrace);
    causeExplosion(raytrace.getHitVec());
  }

  @Override
  protected void onImpact(RayTraceResult raytrace) {
    super.onImpact(raytrace);
    if (!this.world.isRemote() && world instanceof IServerWorld && this.isAlive()) {
      causeExplosion(raytrace.getHitVec());
      remove();
    }
  }
  
  protected Explosion causeExplosion(final Vector3d vec) {
    // cause explosion at this location
    final float size = 1.25F;
    Explosion exp = this.world.createExplosion(this.getShooter(), DamageSource.ON_FIRE, null, vec.x, vec.y, vec.z, size, false, Explosion.Mode.DESTROY);
    // place greek fire around the area
    for (BlockPos pos : exp.getAffectedBlockPositions()) {
      // place fire
      if (world.rand.nextInt(3) > 0 && this.world.isAirBlock(pos)
          && this.world.getBlockState(pos.down()).isOpaqueCube(this.world, pos.down())) {
        this.world.setBlockState(pos, AbstractFireBlock.getFireForPlacement(this.world, pos));
      }
    }
    // ignite any nearby entities
    final double size2 = size * 1.5F;
    final AxisAlignedBB aabb = new AxisAlignedBB(vec.subtract(size2, size2, size2), vec.add(size2, size2, size2));
    for(final Entity e : world.getEntitiesWithinAABBExcludingEntity(this, aabb)) {
      e.setFire(12 + rand.nextInt(6));
    }
    return exp;
  }

  @Override
  public void tick() {
    Entity entity = getShooter();
    if (entity instanceof net.minecraft.entity.player.PlayerEntity && !entity.isAlive()) {
      remove();
    } else {
      super.tick();
    }
  }
  
  /**
   * Gets the amount of gravity to apply to the thrown entity with each tick.
   */
  @Override
  protected float getGravityVelocity() {
     return 0.09F;
  }

  @Override
  public Entity changeDimension(ServerWorld serverWorld, ITeleporter iTeleporter) {
    Entity entity = this.getShooter();
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
