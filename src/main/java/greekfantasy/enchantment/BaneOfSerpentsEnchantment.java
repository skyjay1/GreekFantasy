package greekfantasy.enchantment;

import greekfantasy.GreekFantasy;
import greekfantasy.entity.util.GFMobType;
import greekfantasy.item.KnifeItem;
import greekfantasy.item.SpearItem;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.enchantment.DamageEnchantment;
import net.minecraft.world.item.enchantment.Enchantment;

public class BaneOfSerpentsEnchantment extends DamageEnchantment {

    public static final int SERPENTS = 3;

    public BaneOfSerpentsEnchantment(final Rarity rarity) {
        super(rarity, SERPENTS, EquipmentSlot.MAINHAND);
    }

    @Override
    public boolean checkCompatibility(Enchantment enchantment) {
        return !(enchantment instanceof DamageEnchantment);
    }

    @Override
    public float getDamageBonus(int level, MobType mobType, ItemStack enchantedItem) {
        return mobType == GFMobType.SERPENT ? (float) level * 2.5F : 0.0F;
    }

    @Override
    public void doPostAttack(LivingEntity user, Entity target, int level) {
        if (target instanceof LivingEntity) {
            LivingEntity livingentity = (LivingEntity) target;
            if (level > 0 && livingentity.getMobType() == GFMobType.SERPENT) {
                int i = 20 + user.getRandom().nextInt(10 * level);
                livingentity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, i, 3));
            }
        }

    }

    @Override
    public boolean isTreasureOnly() {
        return false;
    }

    @Override
    public boolean isTradeable() {
        return GreekFantasy.CONFIG.BANE_OF_SERPENTS_ENABLED.get();
    }

    @Override
    public boolean isDiscoverable() {
        return GreekFantasy.CONFIG.BANE_OF_SERPENTS_ENABLED.get();
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        return GreekFantasy.CONFIG.BANE_OF_SERPENTS_ENABLED.get()
                && (stack.getItem() instanceof SwordItem || stack.getItem() instanceof AxeItem
                || stack.getItem() instanceof KnifeItem || stack.getItem() instanceof SpearItem)
                && super.canApplyAtEnchantingTable(stack);
    }
}
