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
  protected void onEntityHit(EntityRayTraceResult raytrace) {
    super.onEntityHit(raytrace);
    final float damage = 4.0F;
    raytrace.getEntity().attackEntityFrom(DamageSource.causeThrownDamage(this, getShooter()), damage);
  }

  @Override
  protected void onImpact(RayTraceResult raytrace) {
    super.onImpact(raytrace);
    if(rand.nextFloat() < 0.028F && !(getShooter() instanceof PlayerEntity && ((PlayerEntity)getShooter()).isCreative())) {
      final Vector3d vec = raytrace.getHitVec();
      final ItemEntity item = new ItemEntity(this.world, vec.x, vec.y + 0.25D, vec.z, new ItemStack(getDefaultItem()));
      this.world.addEntity(item);
    } else {
      this.playSound(SoundEvents.ENTITY_ITEM_BREAK, 1.0F, 1.0F + rand.nextFloat() * 0.2F);
    }
    this.remove();
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
     return 0.08F;
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
