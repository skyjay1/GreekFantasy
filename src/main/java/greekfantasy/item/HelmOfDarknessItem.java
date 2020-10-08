package greekfantasy.item;

import java.util.List;

import javax.annotation.Nullable;

import greekfantasy.GreekFantasy;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class HelmOfDarknessItem extends ArmorItem {
   
  private static final String TEXTURE = GreekFantasy.MODID + ":textures/models/armor/winged_layer_2.png";

  public HelmOfDarknessItem(Properties builderIn) {
    super(ArmorMaterial.IRON, EquipmentSlotType.HEAD, builderIn);
  }

  /**
   * Called each tick as long the item is on a player inventory. Uses by maps to check if is on a player hand and
   * update it's contents.
   */
  @Override
  public void inventoryTick(final ItemStack stack, final World worldIn, final Entity entityIn, 
      final int itemSlot, final boolean isSelected) {
    if(itemSlot == EquipmentSlotType.HEAD.getIndex() && entityIn instanceof LivingEntity) {
      final LivingEntity entity = (LivingEntity)entityIn;
      entity.addPotionEffect(new EffectInstance(Effects.INVISIBILITY, 20, 0, false, true, false));
    }
  }

  @OnlyIn(Dist.CLIENT)
  public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
    tooltip.add(new TranslationTextComponent("effect.minecraft.invisibility").mergeStyle(TextFormatting.GRAY)
        .appendString(" ").append(new TranslationTextComponent("enchantment.level.infinity").mergeStyle(TextFormatting.GRAY)));
  }

  /**
   * Called by RenderBiped and RenderPlayer to determine the armor texture that
   * should be use for the currently equipped item. This will only be called on
   * instances of ItemArmor.
   *
   * Returning null from this function will use the default value.
   *
   * @param stack  ItemStack for the equipped armor
   * @param entity The entity wearing the armor
   * @param slot   The slot the armor is in
   * @param type   The subtype, can be null or "overlay"
   * @return Path of texture to bind, or null to use default
   */
  @Override
  public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlotType slot, String type) {
    return TEXTURE;
  }
}
