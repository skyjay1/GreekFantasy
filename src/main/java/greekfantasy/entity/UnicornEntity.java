package greekfantasy.entity;

import net.minecraft.block.SoundType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;

public class UnicornEntity extends AbstractHorseEntity {
  
  public UnicornEntity(EntityType<? extends UnicornEntity> type, World worldIn) {
    super(type, worldIn);
  }
  
  public static AttributeModifierMap.MutableAttribute getAttributes() {
    return AbstractHorseEntity.func_234237_fg_()
        .createMutableAttribute(Attributes.MAX_HEALTH, 64.0D)
        .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.25D);
  }
  
  @Override
  public void registerGoals() {
    super.registerGoals();
    this.goalSelector.addGoal(4, new AvoidEntityGoal<>(this, PlayerEntity.class, 16.0F, 1.6D, 1.4D, (entity) -> {
      return !entity.isDiscrete() && EntityPredicates.CAN_AI_TARGET.test(entity) && 
          (this.getOwnerUniqueId() == null || !entity.getUniqueID().equals(this.getOwnerUniqueId()));
   }));
  }
  
  // CALLED FROM ON INITIAL SPAWN
  @Override
  protected void func_230273_eI_() {
  }
  
  @Override
  protected void playGallopSound(SoundType sound) {
    super.playGallopSound(sound);
    if (this.rand.nextInt(10) == 0) {
       this.playSound(SoundEvents.ENTITY_HORSE_BREATHE, sound.getVolume() * 0.6F, sound.getPitch());
    }

    ItemStack stack = this.horseChest.getStackInSlot(1);
    if (isArmor(stack)) stack.onHorseArmorTick(world, this);
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
  public boolean canMateWith(final AnimalEntity otherAnimal) {
    if (otherAnimal == this) {
      return false;
    } else {
      return otherAnimal instanceof UnicornEntity && this.canMate() && ((UnicornEntity)otherAnimal).canMate();
    }
  }

}
