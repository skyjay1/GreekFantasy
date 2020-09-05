package greekfantasy.entity;

import javax.annotation.Nullable;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.passive.horse.CoatColors;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CentaurEntity extends CreatureEntity {
  
  private static final DataParameter<Boolean> DATA_QUIVER = EntityDataManager.createKey(CentaurEntity.class, DataSerializers.BOOLEAN);
  private static final DataParameter<Byte> DATA_COLOR = EntityDataManager.createKey(CentaurEntity.class, DataSerializers.BYTE);
  
  private static final String TAG_QUIVER = "HasQuiver";
  private static final String TAG_COLOR = "Color";
  
  private static final byte REARING_START = 6;
  private static final byte REARING_END = 7;
  
  private boolean isRearing;
  
  public int tailCounter;
  private float rearingAmount;
  private float prevRearingAmount;
  private int rearingCounter;

  public CentaurEntity(final EntityType<? extends CentaurEntity> type, final World worldIn) {
    super(type, worldIn);
  }

  public static AttributeModifierMap.MutableAttribute getAttributes() {
    return MobEntity.func_233666_p_().createMutableAttribute(Attributes.MAX_HEALTH, 24.0D)
        .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.25D).createMutableAttribute(Attributes.ATTACK_DAMAGE, 1.0D);
  }

  @Override
  protected void registerGoals() {
    super.registerGoals();
    this.goalSelector.addGoal(4, new LookAtGoal(this, PlayerEntity.class, 6.0F));
    this.goalSelector.addGoal(5, new LookRandomlyGoal(this));
  }

  @Override
  protected void registerData() {
    super.registerData();
    this.getDataManager().register(DATA_QUIVER, Boolean.valueOf(false));
    this.getDataManager().register(DATA_COLOR, Byte.valueOf((byte) 0));
  }

  @Override
  public void livingTick() {
    if (this.rand.nextInt(200) == 0) {
      this.moveTail();
    }

    super.livingTick();
  }

  /**
   * Called to update the entity's position/logic.
   */
  @Override
  public void tick() {
    super.tick();
    // tail movement logic
    if (this.tailCounter > 0 && ++this.tailCounter > 8) {
      this.tailCounter = 0;
    }
    // rearing logic
    this.prevRearingAmount = this.rearingAmount;
    if (this.isRearing()) {
      this.rearingAmount += (1.0F - this.rearingAmount) * 0.4F + 0.05F;
      if (this.rearingAmount > 1.0F) {
        this.rearingAmount = 1.0F;
      }
    } else {
      this.rearingAmount += (0.8F * this.rearingAmount * this.rearingAmount * this.rearingAmount - this.rearingAmount) * 0.6F
          - 0.05F;
      if (this.rearingAmount < 0.0F) {
        this.rearingAmount = 0.0F;
      }
    }
    if (this.isServerWorld() && this.rearingCounter > 0 && ++this.rearingCounter > 20) {
      this.rearingCounter = 0;
      this.setRearing(false);
    }
  }
  
  public void writeAdditional(CompoundNBT compound) {
    super.writeAdditional(compound);
    compound.putByte(TAG_COLOR, (byte) this.getCoatColor().getId());
    compound.putBoolean(TAG_QUIVER, this.hasQuiver());
 }

 /**
  * (abstract) Protected helper method to read subclass entity data from NBT.
  */
 public void readAdditional(CompoundNBT compound) {
    super.readAdditional(compound);
    this.setCoatColor(CoatColors.func_234254_a_(compound.getByte(TAG_COLOR)));
    this.setQuiver(compound.getBoolean(TAG_QUIVER));
 }

  @Nullable
  public ILivingEntityData onInitialSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason,
      @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
    final CoatColors color = Util.getRandomObject(CoatColors.values(), this.rand);
    this.setCoatColor(color);
    this.setQuiver(this.rand.nextInt(3) > 0);
    return super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
  }

  @Override
  protected int calculateFallDamage(float distance, float damageMultiplier) {
    return MathHelper.ceil((distance * 0.5F - 3.0F) * damageMultiplier);
  }

  @Override
  public boolean isOnLadder() {
    return false;
  }

  @Override
  protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
     if (this.rand.nextInt(3) == 0) {
        this.makeRear();
     }

     return null;
  }
  
  @Override
  protected SoundEvent getAmbientSound() {
     if (this.rand.nextInt(10) == 0 && !this.isMovementBlocked()) {
        this.makeRear();
     }

     return null;
  }

  @Override
  protected float getSoundVolume() {
     return 0.8F;
  }

  @Override
  public int getTalkInterval() {
     return 400;
  }
  
  @OnlyIn(Dist.CLIENT)
  public void handleStatusUpdate(byte id) {
    switch(id) {
    case REARING_START:
      this.isRearing = true;
      break;
    case REARING_END:
      this.isRearing = false;
      break;
    default:
      super.handleStatusUpdate(id);
      break;
    }
  }
  
  public void setCoatColor(final CoatColors color) {
    this.getDataManager().set(DATA_COLOR, (byte) color.getId());
  }
  
  public CoatColors getCoatColor() {
    return CoatColors.func_234254_a_(this.getDataManager().get(DATA_COLOR).intValue());
  }
  
  public void setQuiver(final boolean quiver) {
    this.getDataManager().set(DATA_QUIVER, quiver);
  }
  
  public boolean hasQuiver() {
    return this.getDataManager().get(DATA_QUIVER).booleanValue();
  }
  
  public void makeRear() {
    if(this.isServerWorld()) {
      this.rearingCounter = 1;
      this.setRearing(true);
    }
  }
  
  public void setRearing(final boolean rearing) {
    this.isRearing = rearing;
    this.world.setEntityState(this, rearing ? REARING_START : REARING_END);
  }

  public boolean isRearing() {
    return this.isRearing;
  }

  private void moveTail() {
    this.tailCounter = 1;
  }

  public float getRearingAmount(float f) {
    return this.rearingAmount;
  }
  
  public boolean hasBullHead() {
    return false;
  }
}
