package greekfantasy.effect;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifierManager;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;

public class SwineEffect extends Effect {
  
  protected static final UUID UUID_SWINE = UUID.fromString("5b73458d-f6f6-465d-8738-6d851e494c53");

  public SwineEffect() {
    super(EffectType.HARMFUL, 0xF926FF);
    this.addAttributesModifier(Attributes.MAX_HEALTH, UUID_SWINE.toString(), -10.0D, AttributeModifier.Operation.ADDITION);
  }
  
  public void removeAttributesModifiersFromEntity(LivingEntity entityLivingBaseIn, AttributeModifierManager attributeMapIn, int amplifier) {
    super.removeAttributesModifiersFromEntity(entityLivingBaseIn, attributeMapIn, amplifier);
    if(entityLivingBaseIn instanceof PlayerEntity) {
      ((PlayerEntity)entityLivingBaseIn).setForcedPose(null);
    }
  }

 public void applyAttributesModifiersToEntity(LivingEntity entityLivingBaseIn, AttributeModifierManager attributeMapIn, int amplifier) {
   super.applyAttributesModifiersToEntity(entityLivingBaseIn, attributeMapIn, amplifier);
   if(entityLivingBaseIn instanceof PlayerEntity) {
     ((PlayerEntity)entityLivingBaseIn).setForcedPose(Pose.FALL_FLYING);
   }
 }
  
  /**
   * checks if Potion effect is ready to be applied this tick.
   */
  @Override
  public boolean isReady(int duration, int amplifier) {
    return super.isReady(duration, amplifier);
//     return true;
  }
  
  @Override
  public void performEffect(LivingEntity entityLivingBaseIn, int amplifier) {
//    if(entityLivingBaseIn instanceof PlayerEntity && !entityLivingBaseIn.isSpectator()) {
//      PlayerEntity player = (PlayerEntity)entityLivingBaseIn;
//      player.setForcedPose(Pose.FALL_FLYING);
//    }
  }
  

  /**
   * Get a fresh list of items that can cure this Potion.
   * All new PotionEffects created from this Potion will call this to initialize the default curative items
   * @see PotionEffect#getCurativeItems
   * @return A list of items that can cure this Potion
   */
  @Override
  public List<ItemStack> getCurativeItems() {
//     ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
//     ret.add(new ItemStack(Items.MILK_BUCKET));
     return new ArrayList<ItemStack>();
  }
}