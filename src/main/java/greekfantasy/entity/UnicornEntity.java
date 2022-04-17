package greekfantasy.entity;

import javax.annotation.Nullable;

import greekfantasy.GFRegistry;
import net.minecraft.block.SoundType;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectType;
import net.minecraft.potion.Effects;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.eventbus.api.Event;

public class UnicornEntity extends AbstractHorseEntity {
  
  public UnicornEntity(EntityType<? extends UnicornEntity> type, World worldIn) {
    super(type, worldIn);
  }
  
  public static AttributeModifierMap.MutableAttribute getAttributes() {
    return AbstractHorseEntity.createBaseHorseAttributes().add(Attributes.ARMOR, 1.0D);
  }
  
  @Override
  public void registerGoals() {
    super.registerGoals();
    this.goalSelector.addGoal(4, new AvoidEntityGoal<>(this, PlayerEntity.class, 16.0F, 1.1D, 0.95D, (entity) -> {
      return !entity.isDiscrete() && EntityPredicates.NO_CREATIVE_OR_SPECTATOR.test(entity) && !this.isVehicle() &&
          (!this.isTamed() || this.getOwnerUUID() == null || !entity.getUUID().equals(this.getOwnerUUID()));
   }));
  }
  
  // CALLED FROM ON INITIAL SPAWN //
  
  @Override
  protected void randomizeAttributes() {
    this.getAttribute(Attributes.MAX_HEALTH).setBaseValue((double)this.generateRandomMaxHealth());
    this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(this.generateRandomSpeed());
    this.getAttribute(Attributes.JUMP_STRENGTH).setBaseValue(this.generateRandomJumpStrength());
  }
  
  @Override
  protected float generateRandomMaxHealth() {
    return super.generateRandomMaxHealth() + 28.0F;
  }

  @Override
  protected double generateRandomJumpStrength() {
    return super.generateRandomJumpStrength() + 0.22F;
  }

  @Override
  protected double generateRandomSpeed() {
    return super.generateRandomSpeed() + 0.16F;
  }
  
  // MISC //

  /**
   * Called when the mob's health reaches 0.
   */
  public void die(DamageSource cause) {
    super.die(cause);
    if(cause.getEntity() instanceof LivingEntity) {
      LivingEntity killer = (LivingEntity)cause.getEntity();
      killer.addEffect(new EffectInstance(Effects.UNLUCK, 10_000, 0, false, false, true, new EffectInstance(Effects.BAD_OMEN, 10_000, 0)));
    }
  }
  
  @Override
  protected void actuallyHurt(final DamageSource source, final float amountIn) {
    super.actuallyHurt(source, source.isBypassMagic() || source.isBypassArmor() ? amountIn : amountIn * 0.5F);
  }

  @Override
  public boolean canBeAffected(EffectInstance potioneffectIn) {
    if (potioneffectIn.getEffect().getCategory() == EffectType.HARMFUL) {
      PotionEvent.PotionApplicableEvent event = new PotionEvent.PotionApplicableEvent(this, potioneffectIn);
      event.setResult(Event.Result.DENY);
      MinecraftForge.EVENT_BUS.post(event);
      return event.getResult() == Event.Result.ALLOW;
    }
    return super.canBeAffected(potioneffectIn);
  }
  
  @Override
  public double getPassengersRidingOffset() { return super.getPassengersRidingOffset() - 0.385D; }
  
  @Override
  protected void playGallopSound(SoundType sound) {
    super.playGallopSound(sound);
    if (this.random.nextInt(10) == 0) {
      this.playSound(SoundEvents.HORSE_BREATHE, sound.getVolume() * 0.6F, sound.getPitch());
    }

    ItemStack stack = this.inventory.getItem(1);
    if (isArmor(stack))
      stack.onHorseArmorTick(level, this);
  }
  
  @Override
  protected SoundEvent getAmbientSound() {
    super.getAmbientSound();
    return SoundEvents.HORSE_AMBIENT;
  }

  @Override
  protected SoundEvent getDeathSound() {
    super.getDeathSound();
    return SoundEvents.HORSE_DEATH;
  }

  @Override
  protected SoundEvent getEatingSound() {
    return SoundEvents.HORSE_EAT;
  }

  @Override
  protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
    super.getHurtSound(damageSourceIn);
    return SoundEvents.HORSE_HURT;
  }

  @Override
  protected SoundEvent getAngrySound() {
    super.getAngrySound();
    return SoundEvents.HORSE_ANGRY;
  }

  @Override
  public ActionResultType mobInteract(PlayerEntity player, Hand hand) {
    ItemStack itemstack = player.getItemInHand(hand);
    if (!this.isBaby()) {
      if (this.isTamed() && player.isSecondaryUseActive()) {
        this.openInventory(player);
        return ActionResultType.sidedSuccess(this.level.isClientSide());
      }

      if (this.isVehicle()) {
        return super.mobInteract(player, hand);
      }
      
      if((itemstack.isEmpty() && this.isTamed()) || itemstack.getItem() == GFRegistry.GOLDEN_BRIDLE) {
        this.doPlayerRide(player);
        return ActionResultType.sidedSuccess(this.level.isClientSide());
      }
    }

    if (!itemstack.isEmpty()) {
      if (this.isFood(itemstack)) {
        return this.fedFood(player, itemstack);
      }

      ActionResultType actionresulttype = itemstack.interactLivingEntity(player, this, hand);
      if (actionresulttype.consumesAction()) {
        return actionresulttype;
      }

      if (!this.isTamed()) {
        this.makeMad();
        return ActionResultType.sidedSuccess(this.level.isClientSide());
      }

      boolean flag = !this.isBaby() && !this.isSaddled() && itemstack.getItem() == Items.SADDLE;
      if (this.isArmor(itemstack) || flag) {
        this.openInventory(player);
        return ActionResultType.sidedSuccess(this.level.isClientSide());
      }
    }

//    if (this.isChild()) {
      return super.mobInteract(player, hand);
//    } else {
//      this.mountTo(player);
//      return ActionResultType.sidedSuccess(this.world.isRemote());
//    }
  }

//  @Override
//  public boolean isArmor(ItemStack stack) {
//    return stack.getItem() instanceof HorseArmorItem;
//  }
  
  @Override
  public int getMaxTemper() {
    return 160;
  }

  @Override
  public boolean canMate(final AnimalEntity otherAnimal) {
    if (otherAnimal == this) {
      return false;
    } else {
      return otherAnimal instanceof UnicornEntity && this.canParent() && ((UnicornEntity)otherAnimal).canParent();
    }
  }
  
  @Nullable
  @Override
  public AgeableEntity getBreedOffspring(ServerWorld world, AgeableEntity mate) {
    UnicornEntity unicorn = GFRegistry.UNICORN_ENTITY.create(world);
    this.setOffspringAttributes(mate, unicorn);
    return unicorn;
  }

}
