package greekfantasy.entity;

import javax.annotation.Nullable;

import greekfantasy.GFRegistry;
import net.minecraft.block.SoundType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraft.entity.passive.horse.CoatColors;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.Util;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;

public class PegasusEntity extends AbstractHorseEntity {
  
  private static final DataParameter<Byte> DATA_COLOR = EntityDataManager.createKey(PegasusEntity.class, DataSerializers.BYTE);
  private static final String KEY_COLOR = "Color";
  
  public PegasusEntity(EntityType<? extends PegasusEntity> type, World worldIn) {
    super(type, worldIn);
  }
  
  public static AttributeModifierMap.MutableAttribute getAttributes() {
    return AbstractHorseEntity.func_234237_fg_().createMutableAttribute(Attributes.ARMOR, 1.0D);
  }
  
  @Override
  protected void registerData() {
    super.registerData();
    this.getDataManager().register(DATA_COLOR, Byte.valueOf((byte) 0));
  }
  
  @Override
  public void registerGoals() {
    super.registerGoals();
    this.goalSelector.addGoal(4, new AvoidEntityGoal<>(this, PlayerEntity.class, 16.0F, 1.1D, 0.95D, (entity) -> {
      return !entity.isDiscrete() && EntityPredicates.CAN_AI_TARGET.test(entity) && !this.isBeingRidden() &&
          (!this.isTame() || this.getOwnerUniqueId() == null || !entity.getUniqueID().equals(this.getOwnerUniqueId()));
   }));
  }
  
  // CALLED FROM ON INITIAL SPAWN //
  
  @Override
  protected void func_230273_eI_() {
    this.getAttribute(Attributes.MAX_HEALTH).setBaseValue((double)this.getModifiedMaxHealth());
    this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(this.getModifiedMovementSpeed());
    this.getAttribute(Attributes.HORSE_JUMP_STRENGTH).setBaseValue(this.getModifiedJumpStrength());
  }
  
  @Override
  protected float getModifiedMaxHealth() {
    return super.getModifiedMaxHealth() + 28.0F;
  }

  @Override
  protected double getModifiedJumpStrength() {
    return super.getModifiedJumpStrength() + 0.22F;
  }

  @Override
  protected double getModifiedMovementSpeed() {
    return super.getModifiedMovementSpeed() + 0.16F;
  }
  
  @Nullable
  public ILivingEntityData onInitialSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason,
      @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
    final CoatColors color = Util.getRandomObject(CoatColors.values(), this.rand);
    this.setCoatColor(color);
    return super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
  }
  
  // MISC //
  
  @Override
  public double getMountedYOffset() { return super.getMountedYOffset() - 0.385D; }
  
  @Override
  protected void playGallopSound(SoundType sound) {
    super.playGallopSound(sound);
    if (this.rand.nextInt(10) == 0) {
      this.playSound(SoundEvents.ENTITY_HORSE_BREATHE, sound.getVolume() * 0.6F, sound.getPitch());
    }
  }
  
  @Override
  protected SoundEvent getAmbientSound() {
    super.getAmbientSound();
    return SoundEvents.ENTITY_HORSE_AMBIENT;
  }

  @Override
  protected SoundEvent getDeathSound() {
    super.getDeathSound();
    return SoundEvents.ENTITY_HORSE_DEATH;
  }

  @Override
  protected SoundEvent func_230274_fe_() {
    return SoundEvents.ENTITY_HORSE_EAT;
  }

  @Override
  protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
    super.getHurtSound(damageSourceIn);
    return SoundEvents.ENTITY_HORSE_HURT;
  }

  @Override
  protected SoundEvent getAngrySound() {
    super.getAngrySound();
    return SoundEvents.ENTITY_HORSE_ANGRY;
  }

  @Override
  public ActionResultType func_230254_b_(PlayerEntity player, Hand hand) {
    ItemStack itemstack = player.getHeldItem(hand);
    if (!this.isChild()) {
      if (this.isTame() && player.isSecondaryUseActive()) {
        this.openGUI(player);
        return ActionResultType.func_233537_a_(this.world.isRemote());
      }

      if (this.isBeingRidden()) {
        return super.func_230254_b_(player, hand);
      }
      
      if((itemstack.isEmpty() && this.isTame()) || itemstack.getItem() == GFRegistry.GOLDEN_BRIDLE) {
        this.mountTo(player);
        return ActionResultType.func_233537_a_(this.world.isRemote());
      }
    }

    if (!itemstack.isEmpty()) {
      if (this.isBreedingItem(itemstack)) {
        return this.func_241395_b_(player, itemstack);
      }

      ActionResultType actionresulttype = itemstack.interactWithEntity(player, this, hand);
      if (actionresulttype.isSuccessOrConsume()) {
        return actionresulttype;
      }

      if (!this.isTame()) {
        this.makeMad();
        return ActionResultType.func_233537_a_(this.world.isRemote());
      }

      boolean flag = !this.isChild() && !this.isHorseSaddled() && itemstack.getItem() == Items.SADDLE;
      if (this.isArmor(itemstack) || flag) {
        this.openGUI(player);
        return ActionResultType.func_233537_a_(this.world.isRemote());
      }
    }

//    if (this.isChild()) {
      return super.func_230254_b_(player, hand);
//    } else {
//      this.mountTo(player);
//      return ActionResultType.func_233537_a_(this.world.isRemote());
//    }
  }
  
  @Override
  public void tick() {
    super.tick();
    //this.onGround = true;
    //this.isAirBorne = false;
    if(this.getMotion().y < 0.0D) {
      this.setMotion(this.getMotion().mul(1.0D, 0.68D, 1.0D));
    }
  }
  
  @Override
  public void travel(Vector3d travelVector) {
    super.travel(travelVector);
  }
  
  @Override
  public void handleStartJump(int jumpPower) {
    super.handleStartJump(jumpPower);
    this.setRearing(false);
  }
  
  @Override
  protected int calculateFallDamage(float distance, float damageMultiplier) {
    return 0; //MathHelper.ceil((distance * 0.5F - 3.0F) * damageMultiplier);
  }

  @Override
  public int getMaxTemper() {
    return 160;
  }

  @Override
  public boolean canMateWith(final AnimalEntity otherAnimal) {
    if (otherAnimal == this) {
      return false;
    } else {
      return otherAnimal instanceof PegasusEntity && this.canMate() && ((PegasusEntity)otherAnimal).canMate();
    }
  }
  
  // Color
  
  public void setCoatColor(final CoatColors color) { this.getDataManager().set(DATA_COLOR, (byte) color.getId()); }
 
  public CoatColors getCoatColor() { return CoatColors.func_234254_a_(this.getDataManager().get(DATA_COLOR).intValue()); }
  
  // NBT
  
  @Override
  public void writeAdditional(CompoundNBT compound) {
    super.writeAdditional(compound);
    compound.putByte(KEY_COLOR, (byte) this.getCoatColor().getId());
  }

  @Override
  public void readAdditional(CompoundNBT compound) {
    super.readAdditional(compound);
    this.setCoatColor(CoatColors.func_234254_a_(compound.getByte(KEY_COLOR)));
  }

}
