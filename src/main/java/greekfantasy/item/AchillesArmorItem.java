package greekfantasy.item;

import greekfantasy.GFRegistry;
import greekfantasy.GreekFantasy;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class AchillesArmorItem extends ArmorItem {
  protected static final IArmorMaterial MATERIAL = new AchillesArmorMaterial();
   
  private static final String TEXTURE_1 = GreekFantasy.MODID + ":textures/models/armor/achilles_layer_1.png";
  private static final String TEXTURE_2 = GreekFantasy.MODID + ":textures/models/armor/achilles_layer_2.png";
  private static final String TEXTURE_3 = GreekFantasy.MODID + ":textures/models/armor/achilles_layer_3.png";
  @OnlyIn(Dist.CLIENT)
  private greekfantasy.client.render.model.armor.AchillesHelmetModel MODEL;

  public AchillesArmorItem(final EquipmentSlotType slot, Properties builderIn) {
    super(MATERIAL, slot, builderIn);
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
    if(slot == EquipmentSlotType.HEAD) {
      return TEXTURE_3;
    } else if(slot == EquipmentSlotType.LEGS) {
      return TEXTURE_2;
    } else {
      return TEXTURE_1;
    }
  }
  
  /** Override this method to have an item handle its own armor rendering.
  *
  * @param entityLiving The entity wearing the armor
  * @param itemStack    The itemStack to render the model of
  * @param armorSlot    The slot the armor is in
  * @param _default     Original armor model. Will have attributes set.
  * @return A ModelBiped to render instead of the default
  */
 @OnlyIn(Dist.CLIENT)
 public <A extends net.minecraft.client.renderer.entity.model.BipedModel<?>> A getArmorModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlotType armorSlot, A _default) {
   // head slot uses different model, but the rest can use default
   if(armorSlot != EquipmentSlotType.HEAD) {
     return _default;
   }
   // update head model
   if(MODEL == null) {
     MODEL = new greekfantasy.client.render.model.armor.AchillesHelmetModel(1.0F);
   }
   MODEL.isChild = _default.isChild;
   MODEL.isSneak = _default.isSneak;
   MODEL.isSitting = _default.isSitting;
   MODEL.setRotationAngles(entityLiving, entityLiving.limbSwing, entityLiving.limbSwingAmount, (float)entityLiving.ticksExisted, entityLiving.rotationYawHead, entityLiving.rotationPitch);
   return (A) MODEL;
 }
  
  public static class AchillesArmorMaterial implements IArmorMaterial {
    private static final String NAME = "achilles";
    @Override
    public int getDamageReductionAmount(EquipmentSlotType slot) { return ArmorMaterial.DIAMOND.getDamageReductionAmount(slot); }
    @Override
    public int getDurability(EquipmentSlotType slot) { return ArmorMaterial.IRON.getDurability(slot); }
    @Override
    public int getEnchantability() { return ArmorMaterial.GOLD.getEnchantability(); }
    @Override
    public float getKnockbackResistance() { return ArmorMaterial.NETHERITE.getKnockbackResistance(); }
    @Override
    public String getName() { return NAME; }
    @Override
    public Ingredient getRepairMaterial() { return Ingredient.fromItems(GFRegistry.FIERY_GEAR); }
    @Override
    public SoundEvent getSoundEvent() { return SoundEvents.ITEM_ARMOR_EQUIP_GOLD; }
    @Override
    public float getToughness() { return ArmorMaterial.NETHERITE.getToughness() * 2.0F; }
  }
}
