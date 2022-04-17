package greekfantasy.entity.misc;

import greekfantasy.GFRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.IPacket;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.ITeleporter;
import net.minecraftforge.fml.network.NetworkHooks;

public class DiscusEntity extends ProjectileItemEntity {

  public DiscusEntity(EntityType<? extends DiscusEntity> entityType, World world) {
    super(entityType, world);
  }

  private DiscusEntity(World worldIn, LivingEntity thrower) {
    super(GFRegistry.DISCUS_ENTITY, thrower, worldIn);
  }

  private DiscusEntity(World worldIn, double x, double y, double z) {
    super(GFRegistry.DISCUS_ENTITY, x, y, z, worldIn);
  }
  
  public static DiscusEntity create(World worldIn, double x, double y, double z) {
    return new DiscusEntity(worldIn, x, y, z);
  }
  
  public static DiscusEntity create(World worldIn, LivingEntity thrower) {
    return new DiscusEntity(worldIn, thrower);
  }

  @Override
  protected Item getDefaultItem() {
    return GFRegistry.DISCUS;
  }

  @Override
  protected void onHitEntity(EntityRayTraceResult raytrace) {
    super.onHitEntity(raytrace);
    final float damage = 4.0F;
    raytrace.getEntity().hurt(DamageSource.thrown(this, getOwner()), damage);
  }

  @Override
  protected void onHit(RayTraceResult raytrace) {
    super.onHit(raytrace);
    if(random.nextFloat() < 0.028F && !(getOwner() instanceof PlayerEntity && ((PlayerEntity)getOwner()).isCreative())) {
      final Vector3d vec = raytrace.getLocation();
      final ItemEntity item = new ItemEntity(this.level, vec.x, vec.y + 0.25D, vec.z, new ItemStack(getDefaultItem()));
      this.level.addFreshEntity(item);
    } else {
      this.playSound(SoundEvents.ITEM_BREAK, 1.0F, 1.0F + random.nextFloat() * 0.2F);
    }
    this.remove();
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
  
  /**
   * Gets the amount of gravity to apply to the thrown entity with each tick.
   */
  @Override
  protected float getGravity() {
     return 0.08F;
  }

  @Override
  public Entity changeDimension(ServerWorld serverWorld, ITeleporter iTeleporter) {
    Entity entity = this.getOwner();
    if (entity != null && entity.level.dimension() != serverWorld.dimension()) {
      setOwner((Entity) null);
    }
    return super.changeDimension(serverWorld, iTeleporter);
  }
  
  @Override
  public IPacket<?> getAddEntityPacket() {
    return NetworkHooks.getEntitySpawningPacket(this);
  }

}
