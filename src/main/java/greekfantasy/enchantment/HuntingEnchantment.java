package greekfantasy.enchantment;

import greekfantasy.GreekFantasy;
import greekfantasy.item.KnifeItem;
import greekfantasy.item.SpearItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraftforge.registries.ForgeRegistries;

public class HuntingEnchantment extends Enchantment {

    private static final TagKey<EntityType<?>> BOSSES = ForgeRegistries.ENTITY_TYPES.tags().createTagKey(new ResourceLocation("forge", "bosses"));

    public HuntingEnchantment(final Enchantment.Rarity rarity) {
        super(rarity, EnchantmentCategory.WEAPON, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @Override
    public void doPostAttack(LivingEntity user, Entity target, int level) {
        // check for cooldown
        final ItemStack item = user.getItemInHand(InteractionHand.MAIN_HAND);
        // if it's an animal, use high attack damage
        if (target.canChangeDimensions() && !target.getType().is(BOSSES) && target instanceof Animal animal
                && user.getRandom().nextFloat() < 0.30F + 0.15F * level) {
            // determine damage amount
            float amount = Math.min(99.0F, (animal.getMaxHealth() + animal.getArmorValue()) * 1.25F);
            // apply damage
            DamageSource source = DamageSource.mobAttack(user).bypassArmor().bypassMagic();
            target.hurt(source, amount);
        }
    }

    @Override
    public int getMinCost(int level) {
        return 5 + super.getMinCost(level);
    }

    @Override
    public int getMaxCost(int level) {
        return 5 + super.getMaxCost(level);
    }

    @Override
    public boolean isTreasureOnly() {
        return GreekFantasy.CONFIG.HUNTING_ENABLED.get();
    }

    @Override
    public boolean isTradeable() {
        return GreekFantasy.CONFIG.HUNTING_TRADEABLE.get();
    }

    @Override
    public boolean isDiscoverable() {
        return GreekFantasy.CONFIG.HUNTING_ENABLED.get();
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        return GreekFantasy.CONFIG.HUNTING_ENABLED.get()
                && (stack.getItem() instanceof SwordItem || stack.getItem() instanceof AxeItem
                || stack.getItem() instanceof KnifeItem || stack.getItem() instanceof SpearItem)
                && super.canApplyAtEnchantingTable(stack);
    }
}
