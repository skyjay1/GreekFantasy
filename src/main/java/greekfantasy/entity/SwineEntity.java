package greekfantasy.entity;

import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import javax.annotation.Nullable;

import greekfantasy.GFRegistry;
import greekfantasy.entity.misc.IHasOwner;
import net.minecraft.block.BlockState;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SwineEntity extends CreatureEntity implements IHasOwner<SwineEntity> {
  
  protected static final DataParameter<Optional<UUID>> OWNER = EntityDataManager.createKey(SpartiEntity.class, DataSerializers.OPTIONAL_UNIQUE_ID);

 
  public SwineEntity(final EntityType<? extends SwineEntity> type, final World worldIn) {
    super(type, worldIn);
  }
  
  public static AttributeModifierMap.MutableAttribute getAttributes() {
    return MobEntity.func_233666_p_()
        .createMutableAttribute(Attributes.MAX_HEALTH, 10.0D)
        .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.25D)
        .createMutableAttribute(Attributes.ATTACK_DAMAGE, 0.0D);
  }
  
  @Override
  protected void registerData() {
    super.registerData();
    this.getDataManager().register(OWNER, Optional.empty());
  }
  
  @Override
  protected void registerGoals() {
    super.registerGoals();
  }
  
  // Owner-sync methods
  
  @Override
  public void tick() {
    super.tick();
    final LivingEntity livingOwner = this.getOwner();
    if(livingOwner instanceof PlayerEntity) {
      // check if owner should be released
      if(livingOwner.getActivePotionEffect(GFRegistry.SWINE_EFFECT) == null) {
        this.setOwner((UUID)null);
        this.remove();
        return;
      }
      PlayerEntity player = (PlayerEntity)livingOwner;
      // previous variables
      this.setPositionAndRotation(player.getPosX(), player.getPosY(), player.getPosZ(), player.rotationYaw, player.rotationPitch);
      this.prevPosX = player.prevPosX;
      this.prevPosY = player.prevPosY;
      this.prevPosZ = player.prevPosZ;
      this.prevRotationYaw = player.prevRotationYaw;
      this.prevRotationPitch = player.prevRotationPitch;
      this.prevLimbSwingAmount = player.prevLimbSwingAmount;
      this.prevRenderYawOffset = player.prevRenderYawOffset;
      this.prevRotationYawHead = player.prevRotationYawHead;
      
      // current variables
      this.limbSwing = player.limbSwing;
      this.limbSwingAmount = player.limbSwingAmount;
      this.renderYawOffset = player.renderYawOffset;
      this.rotationYawHead = player.rotationYawHead;
      
      // health
      this.setHealth(player.getHealth());
    } else {
      // look for owner
      
    }
  }
  

  @Override
  public void onDeath(DamageSource cause) {
    super.onDeath(cause);
    if(this.hasOwner()) {
      this.getOwner().attackEntityFrom(DamageSource.MAGIC, 1000.0F);
    }
  }
  
  @Override
  protected void damageEntity(final DamageSource source, final float amountIn) {
    super.damageEntity(source, source.isDamageAbsolute() ? amountIn : amountIn * 0.5F);
  }
  
  // Misc pig methods

  protected SoundEvent getAmbientSound() {
    return SoundEvents.ENTITY_PIG_AMBIENT;
  }

  protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
    return SoundEvents.ENTITY_PIG_HURT;
  }

  protected SoundEvent getDeathSound() {
    return SoundEvents.ENTITY_PIG_DEATH;
  }

  protected void playStepSound(BlockPos pos, BlockState blockIn) {
    this.playSound(SoundEvents.ENTITY_PIG_STEP, 0.15F, 1.0F);
  }
 
  @Override
  public boolean canDespawn(double distanceToClosestPlayer) {
    return false;
  }
  
  //Owner methods //
    
   @Override
   public Optional<UUID> getOwnerID() { return this.getDataManager().get(OWNER); }
   
   @Override
   public void setOwner(@Nullable final UUID uuid) { 
     this.getDataManager().set(OWNER, Optional.ofNullable(uuid));
     if(uuid != null) {
       this.setCustomName(this.getEntityWorld().getPlayerByUuid(uuid).getDisplayName());
     }
   }
     
   @Override
   public LivingEntity getOwner() {
     if(hasOwner()) {
       return this.getEntityWorld().getPlayerByUuid(getOwnerID().get());
     }
     return null;
   }
   
   @Override
   public boolean isTamingItem(final ItemStack item) { return false; }
   
   @Override
   public float getHealAmount(final ItemStack item) { return 0; }
   
   @Override
   public int getTameChance(final Random rand) { return 0; }
 
   // NBT methods
   
   @Override
   public void writeAdditional(CompoundNBT compound) {
     super.writeAdditional(compound);
     this.writeOwner(compound);
   }

   @Override
   public void readAdditional(CompoundNBT compound) {
     super.readAdditional(compound);
     this.readOwner(compound);
   }
   
   
}
