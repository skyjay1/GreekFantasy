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
  public static final UUID MOVE_SPEED_MODIFIER = UUID.fromString("aaa7e73d-1121-45e2-8b0e-dd2042f7dddc");
  public static final double ATTACK_KNOCKBACK_AMOUNT = 1.8D;
  protected final Multimap<Attribute, AttributeModifier> attributeModifiers;

  public ClubItem(IItemTier tier, Item.Properties properties) {
    super(tier, properties);
    final double attackDamage = 5.5D + tier.getAttackDamageBonus();
    final double attackSpeed = -3.5D;
    final double moveSpeed = -0.1D;
    ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
    builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", attackDamage, AttributeModifier.Operation.ADDITION));
    builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", attackSpeed, AttributeModifier.Operation.ADDITION));
    builder.put(Attributes.ATTACK_KNOCKBACK, new AttributeModifier(ATTACK_KNOCKBACK_MODIFIER, "Weapon modifier", ATTACK_KNOCKBACK_AMOUNT, AttributeModifier.Operation.ADDITION));
    builder.put(Attributes.MOVEMENT_SPEED, new AttributeModifier(MOVE_SPEED_MODIFIER, "Weapon modifier", moveSpeed, AttributeModifier.Operation.MULTIPLY_TOTAL));
    this.attributeModifiers = builder.build();
  }

  /**
   * Current implementations of this method in child classes do not use the entry argument beside ev. They just raise the damage
   * on the stack.
   */
  @Override
  public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
    // damage item
    stack.hurtAndBreak(2, attacker, (entity) -> entity.broadcastBreakEvent(EquipmentSlotType.MAINHAND));
    if(attacker instanceof PlayerEntity && !((PlayerEntity)attacker).getCooldowns().isOnCooldown(this)) {
      // determine knockback amount
      float knockback = 0.0F;
      for(final AttributeModifier modifier : this.getAttributeModifiers(EquipmentSlotType.MAINHAND, stack).get(Attributes.ATTACK_KNOCKBACK)) {
        knockback += modifier.getAmount();
      }
      // apply knockback
      target.knockback(knockback * 0.75F, (double)MathHelper.sin(attacker.yRot * ((float)Math.PI / 180F)), (double)(-MathHelper.cos(attacker.yRot * ((float)Math.PI / 180F))));
    }
    return true;
  }

  /**
   * Called when a Block is destroyed using this Item. Return true to trigger the "Use Item" statistic.
   */
  @Override
  public boolean mineBlock(ItemStack stack, World worldIn, BlockState state, BlockPos pos, LivingEntity entityLiving) {
    if (!worldIn.isClientSide && state.getDestroySpeed(worldIn, pos) != 0.0F) {
      stack.hurtAndBreak(1, entityLiving, (entity) -> entity.broadcastBreakEvent(EquipmentSlotType.MAINHAND));
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
