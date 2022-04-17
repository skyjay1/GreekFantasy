package greekfantasy.enchantment;

import greekfantasy.GFRegistry;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;

import java.util.Map;
import java.util.Random;

public class PoisonEnchantment extends Enchantment {

    public PoisonEnchantment(final Enchantment.Rarity rarity) {
        super(rarity, EnchantmentType.ARMOR, new EquipmentSlotType[]{EquipmentSlotType.HEAD, EquipmentSlotType.CHEST, EquipmentSlotType.LEGS, EquipmentSlotType.FEET});
    }

    @Override
    public boolean canEnchant(ItemStack item) {
        if (item.getItem() instanceof net.minecraft.item.ArmorItem) {
            return true;
        }
        return super.canEnchant(item);
    }

    @Override
    public ITextComponent getFullname(int level) {
        return ((IFormattableTextComponent) super.getFullname(level)).withStyle(TextFormatting.GREEN);
    }

    @Override
    public void doPostHurt(LivingEntity user, Entity attacker, int level) {
        Random rand = user.getRandom();
        Map.Entry<EquipmentSlotType, ItemStack> enchants = EnchantmentHelper.getRandomItemWith(GFRegistry.POISON_ENCHANTMENT, user);
        if (shouldHit(level, rand)) {
            if (attacker instanceof LivingEntity) {
                int duration = getDuration(level, rand);
                ((LivingEntity) attacker).addEffect(new EffectInstance(Effects.POISON, duration, level));
            }

            if (enchants != null) {
                enchants.getValue().hurtAndBreak(2, user, e -> e.broadcastBreakEvent(enchants.getKey()));
            }
        }
    }

    public static boolean shouldHit(int level, Random rand) {
        if (level <= 0) {
            return false;
        }
        return (rand.nextFloat() < 0.15F * level);
    }

    public static int getDuration(int level, Random rand) {
        if (level > 5) {
            return 200;
        }
        return (level * 2 + rand.nextInt(3)) * 20;
    }

    @Override
    public int getMinCost(int level) {
        return 10 + 20 * (level - 1);
    }

    @Override
    public int getMaxCost(int level) {
        return 50 + super.getMaxCost(level);
    }

    @Override
    public boolean isTreasureOnly() {
        return false;
    }

    @Override
    public boolean isTradeable() {
        return false;
    }

    @Override
    public boolean isDiscoverable() {
        return false;
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        return false;
    }
}
