package greekfantasy.item;

import java.util.UUID;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.block.BlockState;
import net.minecraft.enchantment.IVanishable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IItemTier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TieredItem;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class ClubItem extends TieredItem implements IVanishable {

  public static final UUID ATTACK_KNOCKBACK_MODIFIER = UUID.fromString("d5df356d-0c5c-4629-bf18-e8dcde25bcb9");
  public static final double ATTACK_KNOCKBACK_AMOUNT = 1.5D;
  protected final Multimap<Attribute, AttributeModifier> attributeModifiers;

  public ClubItem(IItemTier tier, Item.Properties properties) {
    super(tier, properties);
    final double attackDamage = 5.5D + tier.getAttackDamage();
    final double attackSpeed = -3.5D;
    ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
    builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", attackDamage, AttributeModifier.Operation.ADDITION));
    builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", attackSpeed, AttributeModifier.Operation.ADDITION));
    builder.put(Attributes.ATTACK_KNOCKBACK, new AttributeModifier(ATTACK_KNOCKBACK_MODIFIER, "Weapon modifier", ATTACK_KNOCKBACK_AMOUNT, AttributeModifier.Operation.ADDITION));
    this.attributeModifiers = builder.build();
  }

  /**
   * Current implementations of this method in child classes do not use the entry argument beside ev. They just raise the damage
   * on the stack.
   */
  @Override
  public boolean hitEntity(ItemStack stack, LivingEntity target, LivingEntity attacker) {
    // damage item
    stack.damageItem(2, attacker, (entity) -> entity.sendBreakAnimation(EquipmentSlotType.MAINHAND));
    if(attacker instanceof PlayerEntity && !((PlayerEntity)attacker).getCooldownTracker().hasCooldown(this)) {
      // determine knockback amount
      float knockback = 0.0F;
      for(final AttributeModifier modifier : this.getAttributeModifiers(EquipmentSlotType.MAINHAND, stack).get(Attributes.ATTACK_KNOCKBACK)) {
        knockback += modifier.getAmount();
      }
      // apply knockback
      target.applyKnockback(knockback * 0.75F, (double)MathHelper.sin(attacker.rotationYaw * ((float)Math.PI / 180F)), (double)(-MathHelper.cos(attacker.rotationYaw * ((float)Math.PI / 180F))));
    }
    return true;
  }

  /**
   * Called when a Block is destroyed using this Item. Return true to trigger the "Use Item" statistic.
   */
  @Override
  public boolean onBlockDestroyed(ItemStack stack, World worldIn, BlockState state, BlockPos pos, LivingEntity entityLiving) {
    if (!worldIn.isRemote && state.getBlockHardness(worldIn, pos) != 0.0F) {
      stack.damageItem(1, entityLiving, (entity) -> entity.sendBreakAnimation(EquipmentSlotType.MAINHAND));
    }
    return true;
  }

  /**
   * Gets a map of item attribute modifiers, used by ItemSword to increase hit damage.
   */
  @Override
  public Multimap<Attribute, AttributeModifier> getAttributeModifiers(final EquipmentSlotType equipmentSlot, final ItemStack stack) {
    return equipmentSlot == EquipmentSlotType.MAINHAND ? this.attributeModifiers : super.getAttributeModifiers(equipmentSlot, stack);
  }
}
