package greekfantasy.entity;

import javax.annotation.Nullable;

import greekfantasy.GFRegistry;
import net.minecraft.block.SoundType;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.IFlyingAnimal;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraft.entity.passive.horse.CoatColors;
import net.minecraft.entity.passive.horse.CoatTypes;
import net.minecraft.entity.passive.horse.HorseEntity;
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
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeMod;

public class PegasusEntity extends AbstractHorseEntity implements IFlyingAnimal {
  
  private static final DataParameter<Integer> DATA_COLOR = EntityDataManager.createKey(PegasusEntity.class, DataSerializers.VARINT);
  private static final String KEY_COLOR = "Color";
  
  private static final int FLYING_INTERVAL = 8;
  protected int flyingTime;
  protected int navigatorTimer;
  
  protected boolean isFlying;

//  protected final GroundPathNavigator groundNavigator;
//  protected final FlyingPathNavigator flyingNavigator;
    
  public PegasusEntity(EntityType<? extends PegasusEntity> type, World worldIn) {
    super(type, worldIn);
//    groundNavigator = new GroundPathNavigator(this, worldIn);
//    flyingNavigator = new FlyingPathNavigator(this, worldIn);
//    flyingNavigator.setCanSwim(true);
  }
  
  public static AttributeModifierMap.MutableAttribute getAttributes() {
    return AbstractHorseEntity.func_234237_fg_()
        .createMutableAttribute(Attributes.ARMOR, 1.0D)
        .createMutableAttribute(ForgeMod.ENTITY_GRAVITY.get(), 0.04D)
        .createMutableAttribute(Attributes.FLYING_SPEED, 1.32F);
  }
  
  @Override
  protected void registerData() {
    super.registerData();
    this.getDataManager().register(DATA_COLOR, 0);
  }
  
  @Override
  public void registerGoals() {
    super.registerGoals();
    this.goalSelector.addGoal(4, new AvoidPlayersGoal(this));
  }

  @Override
  public void livingTick() {
    super.livingTick();
    
    // update flying
    if(flyingTime > 0) {
      flyingTime--;
    }
    if(!isBeingRidden()) {
      isFlying = false;
    }
    this.onGround = true;
    // fall slowly when being ridden
    if(isBeingRidden() && this.getMotion().y < -0.1D) {
      this.setMotion(this.getMotion().mul(1.0, 0.95D, 1.0));
    }
    
    // take damage when too high
    if(this.getPositionVec().y > 300) {
      this.attackEntityFrom(DamageSource.OUT_OF_WORLD, 2.0F);
    }
  }
  
  // CALLED FROM ON INITIAL SPAWN //
  
  @Override
  protected void func_230273_eI_() {
    this.getAttribute(Attributes.MAX_HEALTH).setBaseValue((double)this.getModifiedMaxHealth());
    this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(this.getModifiedMovementSpeed());
    this.getAttribute(Attributes.HORSE_JUMP_STRENGTH).setBaseValue(this.getModifiedJumpStrength());
    this.getAttribute(Attributes.FLYING_SPEED).setBaseValue(this.getAttributeValue(Attributes.MOVEMENT_SPEED) + 1.25D);
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
    CoatColors color;
    if (spawnDataIn instanceof HorseEntity.HorseData) {
       color = ((HorseEntity.HorseData)spawnDataIn).variant;
    } else {
       color = Util.getRandomObject(CoatColors.values(), this.rand);
       spawnDataIn = new HorseEntity.HorseData(color);
    }
    // set color and type
    this.setCoatColor(color, Util.getRandomObject(CoatTypes.values(), this.rand));
    return super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
  }
  
  // FLYING //
  
  @Override
  public boolean canJump() {
     return super.canJump() && flyingTime <= 0;
  }

  @OnlyIn(Dist.CLIENT)
  public void setJumpPower(int jumpPowerIn) {
    // Do nothing
  }
  
  @OnlyIn(Dist.CLIENT)
  public void flyingJump() {
    if(flyingTime <= 0 && canJump()) {
      // move upward
      float jumpMotion = (float) this.getHorseJumpStrength() + 0.82F;
      this.addVelocity(0, jumpMotion, 0);
      this.markVelocityChanged();
      this.onGround = true;
      // reset flying time
      flyingTime = FLYING_INTERVAL;
      isFlying = true;
    }
  }
  
  @Override
  public void handleStartJump(int jumpPower) {
    //super.handleStartJump(jumpPower);
    if(!this.onGround) {
      this.setRearing(false);
    }
  }
  
  @Override
  public boolean isHorseJumping() { return false; }
  
  @Override
  protected int calculateFallDamage(float distance, float damageMultiplier) {
    return 0; //MathHelper.ceil((distance * 0.5F - 3.0F) * damageMultiplier);
  }
  
  @Override
  public void travel(final Vector3d vec) {
    super.travel(vec);
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
  public int getMaxTemper() {
    return 160;
  }
  
  // Mate 

  @Override
  public boolean canMateWith(final AnimalEntity otherAnimal) {
    if (otherAnimal == this) {
      return false;
    } else {
      return otherAnimal instanceof PegasusEntity && this.canMate() && ((PegasusEntity)otherAnimal).canMate();
    }
  }
  
  @Override
  public AgeableEntity func_241840_a(ServerWorld world, AgeableEntity mate) {
     PegasusEntity child;
     PegasusEntity pegasusMate = (PegasusEntity) mate;
     child = GFRegistry.PEGASUS_ENTITY.create(world);
     int i = this.rand.nextInt(9);
     CoatColors coatcolors;
     if (i < 4) {
       coatcolors = this.getCoatColor();
     } else if (i < 8) {
       coatcolors = pegasusMate.getCoatColor();
     } else {
       coatcolors = Util.getRandomObject(CoatColors.values(), this.rand);
     }

     int j = this.rand.nextInt(5);
     CoatTypes coattypes;
     if (j < 2) {
       coattypes = this.getCoatType();
     } else if (j < 4) {
       coattypes = pegasusMate.getCoatType();
     } else {
       coattypes = Util.getRandomObject(CoatTypes.values(), this.rand);
     }

     child.setCoatColor(coatcolors, coattypes);

     this.setOffspringAttributes(mate, child);
     return child;
  }
  
  // Color
  
  public void setPackedCoatColor(int packedColorsTypes) { this.dataManager.set(DATA_COLOR, packedColorsTypes); }

  public int getPackedCoatColor() { return this.dataManager.get(DATA_COLOR); }

  public void setCoatColor(CoatColors color, CoatTypes type) { this.setPackedCoatColor(color.getId() & 255 | type.getId() << 8 & '\uff00'); }

  public CoatColors getCoatColor() { return CoatColors.func_234254_a_(this.getPackedCoatColor() & 255); }

  public CoatTypes getCoatType() { return CoatTypes.func_234248_a_((this.getPackedCoatColor() & '\uff00') >> 8); }
  
  // NBT
  
  @Override
  public void writeAdditional(CompoundNBT compound) {
    super.writeAdditional(compound);
    compound.putInt(KEY_COLOR, this.getPackedCoatColor());
  }

  @Override
  public void readAdditional(CompoundNBT compound) {
    super.readAdditional(compound);
    this.setPackedCoatColor(compound.getInt(KEY_COLOR));
  }

  class AvoidPlayersGoal extends AvoidEntityGoal<PlayerEntity> {

    public AvoidPlayersGoal(final PegasusEntity pegasus) {
      super(pegasus, PlayerEntity.class, 16.0F, 1.1D, 0.95D, (entity) -> {
        return !entity.isDiscrete() && EntityPredicates.CAN_AI_TARGET.test(entity) && !pegasus.isBeingRidden()
            && (!pegasus.isTame() || pegasus.getOwnerUniqueId() == null || !entity.getUniqueID().equals(pegasus.getOwnerUniqueId()));
      });
    }
    
    @Override
    public boolean shouldExecute() {
      this.navigation = this.entity.getNavigator();
      return super.shouldExecute();
    }    
  }  
}
