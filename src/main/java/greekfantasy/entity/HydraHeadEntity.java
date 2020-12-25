package greekfantasy.entity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.annotation.Nullable;

import greekfantasy.GFRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkHooks;

public class HydraHeadEntity extends Entity {
  
  protected static final DataParameter<Optional<UUID>> HYDRA = EntityDataManager.createKey(HydraHeadEntity.class, DataSerializers.OPTIONAL_UNIQUE_ID);
  protected static String KEY_HYDRA = "Hydra";
  
  public HydraHeadEntity(final EntityType<? extends HydraHeadEntity> type, final World world) {
    super(type, world);
  }

  public HydraHeadEntity(HydraEntity hydraEntity, String name) {
//     super(GFRegistry.HYDRA_HEAD_ENTITY, hydraEntity.world);
     super(hydraEntity.getType(), hydraEntity.world);
     this.setHydra(hydraEntity.getUniqueID());
     this.setPosition(hydraEntity.getPosX(), hydraEntity.getPosY(), hydraEntity.getPosZ());
  }

  @Override
  protected void registerData() { 
    this.getDataManager().register(HYDRA, Optional.empty());
  }
  
  @Override
  public void tick() {
    super.tick();
    if(!world.isRemote() && !hasHydra()) {
      remove();
      return;
    }
    // movement
    Vector3d motion = this.getMotion();
    double d0 = this.getPosX() + motion.x;
    double d1 = this.getPosY() + motion.y;
    double d2 = this.getPosZ() + motion.z;
    // actually move the entity
    this.setPosition(d0, d1, d2);
  }

  @Override
  public boolean canBeCollidedWith() {
     return true;
  }

  @Override
  public boolean attackEntityFrom(DamageSource source, float amount) {
     return this.isInvulnerableTo(source) || !hasHydra() ? false : getHydra().attackEntityPartFrom(this, source, amount);
  }

  @Override
  public boolean isEntityEqual(Entity entityIn) {
     return this == entityIn || (hasHydra() && getHydra() == entityIn);
  }

  @Override
  public IPacket<?> createSpawnPacket() {
    return NetworkHooks.getEntitySpawningPacket(this);
  }

  // Owner methods //
  
  public boolean hasHydra() { return getHydraID().isPresent(); }
    
  public Optional<UUID> getHydraID() { return this.getDataManager().get(HYDRA); }
  
  public void setHydra(@Nullable final UUID uuid) { this.getDataManager().set(HYDRA, Optional.ofNullable(uuid)); }
    
  public HydraEntity getHydra() {
    final World world = this.getEntityWorld();
    if(hasHydra()) {
      if(world instanceof ServerWorld) {
        return (HydraEntity) ((ServerWorld) world).getEntityByUuid(getHydraID().get());
      }
    }
    
    return null;
  }
  
  // NBT methods //

  @Override
  protected void readAdditional(CompoundNBT compound) {
//    if (compound.hasUniqueId(KEY_HYDRA)) {
//      setHydra(compound.getUniqueId(KEY_HYDRA));
//    }
  }

  @Override
  protected void writeAdditional(CompoundNBT compound) {
//    if (hasHydra()) {
//      compound.putUniqueId(KEY_HYDRA, getHydraID().get());
//    }
  }
}
