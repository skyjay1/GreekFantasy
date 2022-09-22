package greekfantasy.enchantment;

import greekfantasy.GFRegistry;
import greekfantasy.GreekFantasy;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Map;
import java.util.Random;

public class PoisoningEnchantment extends Enchantment {

    private static final ResourceLocation SNAKESKIN = new ResourceLocation(GreekFantasy.MODID, "armor/snakeskin");

    public PoisoningEnchantment(final Enchantment.Rarity rarity) {
        super(rarity, EnchantmentCategory.ARMOR, new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET});
    }

    @Override
    public void doPostHurt(LivingEntity user, Entity attacker, int level) {
        Map.Entry<EquipmentSlot, ItemStack> enchants = EnchantmentHelper.getRandomItemWith(GFRegistry.EnchantmentReg.POISONING.get(), user);
        if (shouldHit(level, user.getRandom())) {
            if (attacker instanceof LivingEntity) {
                int duration = getDuration(level, user.getRandom());
                ((LivingEntity) attacker).addEffect(new MobEffectInstance(MobEffects.POISON, duration, level));
            }

            if (enchants != null) {
                enchants.getValue().hurtAndBreak(2, user, e -> e.broadcastBreakEvent(enchants.getKey()));
            }
        }
    }

    public static boolean shouldHit(int level, RandomSource rand) {
        if (level <= 0) {
            return false;
        }
        return (rand.nextFloat() < 0.15F * level);
    }

    public static int getDuration(int level, RandomSource rand) {
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
        return GreekFantasy.CONFIG.POISONING_TRADEABLE.get();
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
        return stack.is(ForgeRegistries.ITEMS.tags().createTagKey(SNAKESKIN)) && super.canApplyAtEnchantingTable(stack);
    }
}
