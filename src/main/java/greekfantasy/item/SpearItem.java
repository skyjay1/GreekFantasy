package greekfantasy.item;

import java.util.function.Consumer;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

import greekfantasy.entity.misc.SpearEntity;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.enchantment.IVanishable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IItemTier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TieredItem;
import net.minecraft.item.UseAction;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SpearItem extends TieredItem implements IVanishable {
  protected final Multimap<Attribute, AttributeModifier> spearAttributes;
  public final Consumer<Entity> onHitEntity;
  
  public SpearItem(IItemTier tier, Item.Properties properties) { this(tier, properties, e -> {}); }

  public SpearItem(IItemTier tier, Item.Properties properties, final Consumer<Entity> hitEntityConsumer) {
    super(tier, properties);
    onHitEntity = hitEntityConsumer;
    // item properties
    ImmutableMultimap.Builder<Attribute, AttributeModifier> mapBuilder = ImmutableMultimap.builder();
    mapBuilder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Tool modifier", 5.0D + tier.getAttackDamage(), AttributeModifier.Operation.ADDITION));
    mapBuilder.put(Attributes.ATTACK_SPEED, new AttributeModifier(ATTACK_SPEED_MODIFIER, "Tool modifier", -2.9D, AttributeModifier.Operation.ADDITION));
    this.spearAttributes = mapBuilder.build();
  }

  @Override
  public boolean canPlayerBreakBlockWhileHolding(final BlockState state, final World world, final BlockPos pos, final PlayerEntity player) {
    return !player.isCreative();
  }

  @Override
  public UseAction getUseAction(final ItemStack stack) { return UseAction.SPEAR; }

  @Override
  public int getUseDuration(final ItemStack stack) { return 72000; }

  @Override
  public void onPlayerStoppedUsing(final ItemStack stack, final World world, final LivingEntity entity, final int duration) {
    if (!(entity instanceof PlayerEntity)) {
      return;
    }

    PlayerEntity player = (PlayerEntity) entity;

    int useDuration = getUseDuration(stack) - duration;
    if (useDuration < 10) {
      return;
    }

    if (!world.isRemote()) {
      throwSpear(world, player, stack);
    }

    player.addStat(Stats.ITEM_USED.get(this));
  }
  
  protected void throwSpear(final World world, final PlayerEntity thrower, final ItemStack stack) {
    stack.damageItem(1, thrower, e -> e.sendBreakAnimation(thrower.getActiveHand()));
    SpearEntity spear = new SpearEntity(world, thrower, stack, onHitEntity);
    spear.setDirectionAndMovement(thrower, thrower.rotationPitch, thrower.rotationYaw, 0.0F, 2.25F, 1.0F);
    // set pickup status and remove the itemstack
    if (thrower.abilities.isCreativeMode) {
      spear.pickupStatus = AbstractArrowEntity.PickupStatus.CREATIVE_ONLY;
    } else {
      thrower.inventory.deleteStack(stack);
    }
    world.addEntity(spear);
    world.playMovingSound(null, spear, SoundEvents.ITEM_TRIDENT_THROW, SoundCategory.PLAYERS, 1.0F, 1.0F);
  }

  @Override
  public ActionResult<ItemStack> onItemRightClick(final World world, final PlayerEntity player, final Hand hand) {
    ItemStack stack = player.getHeldItem(hand);
    if (stack.getDamage() >= stack.getMaxDamage() - 1) {
      return ActionResult.resultFail(stack);
    }
    player.setActiveHand(hand);
    return ActionResult.resultConsume(stack);
  }

  @Override
  public boolean hitEntity(final ItemStack stack, final LivingEntity target, final LivingEntity user) {
    stack.damageItem(1, user, e -> e.sendBreakAnimation(EquipmentSlotType.MAINHAND));
    return true;
  }

  @Override
  public boolean onBlockDestroyed(final ItemStack stack, final World world, final BlockState state, 
      final BlockPos pos, final LivingEntity entity) {
    if (state.getBlockHardness(world, pos) != 0.0D) {
      stack.damageItem(2, entity, p_220046_0_ -> p_220046_0_.sendBreakAnimation(EquipmentSlotType.MAINHAND));
    }
    return true;
  }

  @SuppressWarnings("deprecation")
  @Override
  public Multimap<Attribute, AttributeModifier> getAttributeModifiers(final EquipmentSlotType slot) {
    return slot == EquipmentSlotType.MAINHAND ? this.spearAttributes : super.getAttributeModifiers(slot);
  }
  
  @Override
  public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
    return super.canApplyAtEnchantingTable(stack, enchantment) || enchantment == Enchantments.LOYALTY;
  }

  @Override
  public int getItemEnchantability() {
     return Math.max(1, super.getItemEnchantability() / 2);
  }
}
