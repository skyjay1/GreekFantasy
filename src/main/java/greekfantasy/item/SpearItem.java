package greekfantasy.item;

import java.util.List;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

import greekfantasy.entity.misc.SpearEntity;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
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
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;

public class SpearItem extends TieredItem implements IVanishable {
  
  public static final String KEY_POTION = "Potion";
  
  protected final Multimap<Attribute, AttributeModifier> spearAttributes;
  public final Consumer<Entity> onHitEntity;
  
  public SpearItem(IItemTier tier, Item.Properties properties) { this(tier, properties, e -> {}); }

  public SpearItem(IItemTier tier, Item.Properties properties, final Consumer<Entity> hitEntityConsumer) {
    super(tier, properties);
    onHitEntity = hitEntityConsumer;
    // item properties
    ImmutableMultimap.Builder<Attribute, AttributeModifier> mapBuilder = ImmutableMultimap.builder();
    mapBuilder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Tool modifier", 5.0D + tier.getAttackDamageBonus(), AttributeModifier.Operation.ADDITION));
    mapBuilder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Tool modifier", -2.9D, AttributeModifier.Operation.ADDITION));
    this.spearAttributes = mapBuilder.build();
  }

  @Override
  public boolean canAttackBlock(final BlockState state, final World world, final BlockPos pos, final PlayerEntity player) {
    return !player.isCreative();
  }

  @Override
  public UseAction getUseAnimation(final ItemStack stack) { return UseAction.SPEAR; }

  @Override
  public int getUseDuration(final ItemStack stack) { return 72000; }
  
  @Override
  public boolean isFoil(final ItemStack stack) {
    return super.isFoil(stack) || stack.getOrCreateTagElement(KEY_POTION).contains(KEY_POTION);
  }

  @Override
  public void releaseUsing(final ItemStack stack, final World world, final LivingEntity entity, final int duration) {
    if (!(entity instanceof PlayerEntity)) {
      return;
    }

    PlayerEntity player = (PlayerEntity) entity;

    int useDuration = getUseDuration(stack) - duration;
    if (useDuration < 10) {
      return;
    }

    if (!world.isClientSide()) {
      throwSpear(world, player, stack);
    }

    player.awardStat(Stats.ITEM_USED.get(this));
  }
  
  protected void throwSpear(final World world, final PlayerEntity thrower, final ItemStack stack) {
    stack.hurtAndBreak(1, thrower, e -> e.broadcastBreakEvent(thrower.getUsedItemHand()));
    SpearEntity spear = new SpearEntity(world, thrower, stack, onHitEntity);
    spear.shootFromRotation(thrower, thrower.xRot, thrower.yRot, 0.0F, 2.25F, 1.0F);
    // set pickup status and remove the itemstack
    if (thrower.abilities.instabuild) {
      spear.pickup = AbstractArrowEntity.PickupStatus.CREATIVE_ONLY;
    } else {
      thrower.inventory.removeItem(stack);
    }
    world.addFreshEntity(spear);
    world.playSound(null, spear, SoundEvents.TRIDENT_THROW, SoundCategory.PLAYERS, 1.0F, 1.0F);
  }

  @Override
  public ActionResult<ItemStack> use(final World world, final PlayerEntity player, final Hand hand) {
    ItemStack stack = player.getItemInHand(hand);
    if (stack.getDamageValue() >= stack.getMaxDamage() - 1) {
      return ActionResult.fail(stack);
    }
    player.startUsingItem(hand);
    return ActionResult.consume(stack);
  }

  @Override
  public boolean hurtEnemy(final ItemStack stack, final LivingEntity target, final LivingEntity user) {
    stack.hurtAndBreak(1, user, e -> e.broadcastBreakEvent(EquipmentSlotType.MAINHAND));
    final CompoundNBT nbt = stack.getOrCreateTagElement(SpearItem.KEY_POTION).copy();
    if(nbt.contains(SpearItem.KEY_POTION)) {
      nbt.putByte("Id", (byte) Effect.getId(ForgeRegistries.POTIONS.getValue(new ResourceLocation(nbt.getString(SpearItem.KEY_POTION)))));
      target.addEffect(EffectInstance.load(nbt));
    }
    return true;
  }

  @Override
  public boolean mineBlock(final ItemStack stack, final World world, final BlockState state, 
      final BlockPos pos, final LivingEntity entity) {
    if (state.getDestroySpeed(world, pos) != 0.0D) {
      stack.hurtAndBreak(2, entity, p_220046_0_ -> p_220046_0_.broadcastBreakEvent(EquipmentSlotType.MAINHAND));
    }
    return true;
  }

  @SuppressWarnings("deprecation")
  @Override
  public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(final EquipmentSlotType slot) {
    return slot == EquipmentSlotType.MAINHAND ? this.spearAttributes : super.getDefaultAttributeModifiers(slot);
  }
  
  @Override
  public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
    return super.canApplyAtEnchantingTable(stack, enchantment) || enchantment == Enchantments.LOYALTY;
  }

  @Override
  public int getEnchantmentValue() {
     return Math.max(1, super.getEnchantmentValue() / 2);
  }
  
  @OnlyIn(Dist.CLIENT)
  public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
    final CompoundNBT nbt = stack.getOrCreateTagElement(KEY_POTION);
    if(nbt.contains(KEY_POTION)) {
      Effect potion = ForgeRegistries.POTIONS.getValue(new ResourceLocation(nbt.getString(KEY_POTION)));
      int level = 1 + nbt.getInt("Amplifier");
      tooltip.add(new TranslationTextComponent(potion.getDescriptionId()).append(" ")
          .append(new TranslationTextComponent("enchantment.level." + level))
          .withStyle(TextFormatting.GREEN));
    }
  }
}
