package greekfantasy.entity;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.world.World;

public class ShadeEntity extends CreatureEntity {
  
  public static final DataParameter<Integer> DATA_XP = EntityDataManager.createKey(ShadeEntity.class, DataSerializers.VARINT);
  public static final String KEY_XP = "StoredXP";
  
  // created when player dies
  // when killed, drops amount of XP that player had at time of death
  // attacks by draining XP or, if XP=0, drains health
  // when killed, drops stored XP

  public ShadeEntity(final EntityType<? extends ShadeEntity> type, final World worldIn) {
    super(type, worldIn);
  }
  
  public static AttributeModifierMap.MutableAttribute getAttributes() {
    return MobEntity.func_233666_p_()
        .createMutableAttribute(Attributes.MAX_HEALTH, 12.0D)
        .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.25D)
        .createMutableAttribute(Attributes.ATTACK_DAMAGE, 0.01D);
  }
  
  @Override
  protected void registerGoals() {
    super.registerGoals();
  }
  
  @Override
  protected void registerData() {
    super.registerData();
    this.getDataManager().register(DATA_XP, Integer.valueOf(0));
  }
  
  @Override
  public void livingTick() {
    super.livingTick();
    // spawn particles
    if (world.isRemote()) {
      final double motion = 0.08D;
      final double radius = 1.2D;
      for (int i = 0; i < 5; i++) {
        world.addParticle(ParticleTypes.SMOKE, 
            this.getPosX() + (world.rand.nextDouble() - 0.5D) * radius, 
            this.getPosY() + 0.75D + (world.rand.nextDouble() - 0.5D) * radius * 0.75D, 
            this.getPosZ() + (world.rand.nextDouble() - 0.5D) * radius,
            (world.rand.nextDouble() - 0.5D) * motion, 
            (world.rand.nextDouble() - 0.5D) * motion * 0.5D,
            (world.rand.nextDouble() - 0.5D) * motion);
      }
    }
  }
  
  @Override
  public boolean attackEntityAsMob(final Entity entity) {
    if (super.attackEntityAsMob(entity)) {
      // remove XP or increase attack damage
      if(entity instanceof PlayerEntity) {
        final PlayerEntity player = (PlayerEntity)entity;
        if(player.experienceTotal > 0) {
          final int xpSteal = Math.min(player.experienceTotal, 50);
          player.giveExperiencePoints(-xpSteal);
          this.setStoredXP(this.getStoredXP() + xpSteal);
        }
      }
      return true;
    }
    return false;
  }

  @Override
  public void writeAdditional(CompoundNBT compound) {
    super.writeAdditional(compound);
    compound.putInt(KEY_XP, this.getStoredXP());
  }


  @Override
  public void readAdditional(CompoundNBT compound) {
    super.readAdditional(compound);
    this.setStoredXP(compound.getInt(KEY_XP));
  }
  
  public int getStoredXP() {
    return this.getDataManager().get(DATA_XP).intValue();
  }

  public void setStoredXP(int xp) {
    this.getDataManager().set(DATA_XP, xp);
  }
  
}
