package greekfantasy.item;

import com.google.common.collect.ImmutableMultimap;
import greekfantasy.entity.misc.Spear;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.Vanishable;
import com.google.common.collect.Multimap;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.UUID;

public class SpearItem extends TieredItem implements Vanishable {

    protected static final UUID BASE_ATTACK_RANGE_UUID = KnifeItem.BASE_ATTACK_RANGE_UUID;
    public static final String KEY_MOB_EFFECT = "Potion";

    protected Multimap<Attribute, AttributeModifier> spearAttributes;
    protected final float attackRange;
    protected final int setFire;

    public SpearItem(Tier tier, Item.Properties properties) {
        this(tier, 1.0F, properties);
    }

    public SpearItem(Tier tier, float attackRange, Item.Properties properties) {
        this(tier, attackRange, properties, 0);
    }

    public SpearItem(Tier tier, float attackRange, Item.Properties properties, int setFire) {
        super(tier, properties);
        this.attackRange = attackRange;
        this.setFire = setFire;
    }

    @Override
    public boolean canAttackBlock(final BlockState state, final Level world, final BlockPos pos, final Player player) {
        return !player.isCreative();
    }

    @Override
    public UseAnim getUseAnimation(final ItemStack stack) {
        return UseAnim.SPEAR;
    }

    @Override
    public int getUseDuration(final ItemStack stack) {
        return 72000;
    }

    @Override
    public boolean isFoil(final ItemStack stack) {
        return super.isFoil(stack) || stack.getOrCreateTagElement(KEY_MOB_EFFECT).contains(KEY_MOB_EFFECT);
    }

    @Override
    public void releaseUsing(final ItemStack stack, final Level world, final LivingEntity entity, final int duration) {
        if (!(entity instanceof Player)) {
            return;
        }

        Player player = (Player) entity;

        int useDuration = getUseDuration(stack) - duration;
        if (useDuration < 10) {
            return;
        }

        if (!world.isClientSide()) {
            throwSpear(world, player, stack);
        }

        player.awardStat(Stats.ITEM_USED.get(this));
    }

    protected void throwSpear(final Level world, final Player thrower, final ItemStack stack) {
        stack.hurtAndBreak(1, thrower, e -> e.broadcastBreakEvent(thrower.getUsedItemHand()));
        Spear spear = new Spear(world, thrower, stack, setFire);
        spear.shootFromRotation(thrower, thrower.getXRot(), thrower.getYRot(), 0.0F, 2.25F, 1.0F);
        // set pickup status and remove the itemstack
        if (thrower.getAbilities().instabuild) {
            spear.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
        } else {
            thrower.getInventory().removeItem(stack);
        }
        world.addFreshEntity(spear);
        world.playSound(null, spear, SoundEvents.TRIDENT_THROW, SoundSource.PLAYERS, 1.0F, 1.0F);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(final Level world, final Player player, final InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.getDamageValue() >= stack.getMaxDamage() - 1) {
            return InteractionResultHolder.fail(stack);
        }
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public boolean hurtEnemy(final ItemStack stack, final LivingEntity target, final LivingEntity user) {
        stack.hurtAndBreak(1, user, e -> e.broadcastBreakEvent(EquipmentSlot.MAINHAND));
        final CompoundTag nbt = stack.getOrCreateTagElement(SpearItem.KEY_MOB_EFFECT).copy();
        if (nbt.contains(SpearItem.KEY_MOB_EFFECT)) {
            nbt.putByte("Id", (byte) MobEffect.getId(ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation(nbt.getString(SpearItem.KEY_MOB_EFFECT)))));
            MobEffectInstance effectInstance = MobEffectInstance.load(nbt);
            if(effectInstance != null) {
                target.addEffect(effectInstance);
            }
        }
        return true;
    }

    @Override
    public boolean mineBlock(final ItemStack stack, final Level world, final BlockState state,
                             final BlockPos pos, final LivingEntity entity) {
        if (state.getDestroySpeed(world, pos) != 0.0D) {
            stack.hurtAndBreak(2, entity, p_220046_0_ -> p_220046_0_.broadcastBreakEvent(EquipmentSlot.MAINHAND));
        }
        return true;
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        // lazy-load attributes because forge attributes are not registered at time of item creation
        if(null == this.spearAttributes) {
            // item properties
            ImmutableMultimap.Builder<Attribute, AttributeModifier> mapBuilder = ImmutableMultimap.builder();
            mapBuilder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Tool modifier", 5.0D + getTier().getAttackDamageBonus(), AttributeModifier.Operation.ADDITION));
            mapBuilder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Tool modifier", -2.9D, AttributeModifier.Operation.ADDITION));
            mapBuilder.put(ForgeMod.ATTACK_RANGE.get(), new AttributeModifier(BASE_ATTACK_RANGE_UUID, "Weapon modifier", attackRange, AttributeModifier.Operation.ADDITION));
            this.spearAttributes = mapBuilder.build();
        }
        return slot == EquipmentSlot.MAINHAND ? this.spearAttributes : super.getAttributeModifiers(slot, stack);
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return super.canApplyAtEnchantingTable(stack, enchantment) || enchantment == Enchantments.LOYALTY;
    }

    @Override
    public int getEnchantmentValue() {
        return Math.max(1, super.getEnchantmentValue() / 2);
    }

    @Override
    public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        final CompoundTag nbt = stack.getOrCreateTagElement(KEY_MOB_EFFECT);
        if (nbt.contains(KEY_MOB_EFFECT)) {
            MobEffect potion = ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation(nbt.getString(KEY_MOB_EFFECT)));
            int level = 1 + nbt.getInt("Amplifier");
            tooltip.add(new TranslatableComponent(potion.getDescriptionId()).append(" ")
                    .append(new TranslatableComponent("enchantment.level." + level))
                    .withStyle(ChatFormatting.GREEN));
        }
    }

    @Override
    public void initializeClient(java.util.function.Consumer<net.minecraftforge.client.IItemRenderProperties> consumer) {
        consumer.accept(new net.minecraftforge.client.IItemRenderProperties() {
            @Override
            public net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer getItemStackRenderer() {
                final ResourceLocation id = ForgeRegistries.ITEMS.getKey(SpearItem.this);
                return greekfantasy.client.blockentity.BlockEntityRendererProvider.getSpear(id);
            }
        });
    }
}
