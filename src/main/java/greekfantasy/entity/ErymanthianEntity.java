package greekfantasy.entity;

import javax.annotation.Nullable;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.monster.HoglinEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;

public class ErymanthianEntity extends HoglinEntity {
  
  public ErymanthianEntity(final EntityType<? extends ErymanthianEntity> type, final World worldIn) {
    super(type, worldIn);
    this.func_234370_t_(true); // set IsImmuneToZombification
    this.enablePersistence();
    this.setChild(false);
  }
  
  public static AttributeModifierMap.MutableAttribute getAttributes() {
    return MonsterEntity.func_234295_eP_()
        .createMutableAttribute(Attributes.MAX_HEALTH, 40.0D)
        .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.3D)
        .createMutableAttribute(Attributes.KNOCKBACK_RESISTANCE, 0.82D)
        .createMutableAttribute(Attributes.ATTACK_KNOCKBACK, 1.25D)
        .createMutableAttribute(Attributes.ATTACK_DAMAGE, 8.0D);
  }
  
  @Override
  protected void registerGoals() {
    super.registerGoals();
    
  }
  
  @Override
  public void livingTick() {
    super.livingTick();
  }
  
  // Hoglin overrides //

  @Override
  public boolean isBreedingItem(ItemStack stack) {
    return false;
  }
  
  @Override
  public boolean canFallInLove() {
    return false;
  }
  
  @Override
  public boolean func_234365_eM_() { // canBeHunted
    return false;
  }
  
  @Override
  public boolean func_234364_eK_() { // canBeZombified
    return false;
  }
  
  @Override
  public ILivingEntityData onInitialSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
     ILivingEntityData data = super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
     this.func_234370_t_(true); // set IsImmuneToZombification
     this.enablePersistence();
     this.setChild(false);
     return data;
  }
  
  @Override
  protected void onGrowingAdult() {
    this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(8.0D);
  }
  
  @Override
  protected float getSoundVolume() { return 1.5F; }
  
  @Override
  protected float getSoundPitch() { return 0.54F + rand.nextFloat() * 0.24F; }

  // NBT methods //
  
  @Override
  public void writeAdditional(CompoundNBT compound) {
    super.writeAdditional(compound);
  }

  @Override
  public void readAdditional(CompoundNBT compound) {
    super.readAdditional(compound);
  }
}
