package greekfantasy.item;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.function.Supplier;

public class GFTiers {

    public static final Tier FLINT = new GFTier(0, 88, 3.0F, 1.0F, 12, () -> Ingredient.of(new ItemStack(Items.FLINT)));
    public static final Tier IVORY = new GFTier(1, 835, 6.0F, 2.0F, 10, () -> Ingredient.of(new ItemStack(Items.BONE)));

    private static final class GFTier implements Tier {
        private final int level;
        private final int uses;
        private final float speed;
        private final float damage;
        private final int enchantmentValue;
        private Ingredient repairIngredient;
        private final Supplier<Ingredient> repairIngredientSupplier;

        public GFTier(int level, int uses, float speed, float damage, int enchantmentValue, Supplier<Ingredient> repairIngredient) {
            this.level = level;
            this.uses = uses;
            this.speed = speed;
            this.damage = damage;
            this.enchantmentValue = enchantmentValue;
            this.repairIngredientSupplier = repairIngredient;
        }

        @Override
        public int getUses() {
            return this.uses;
        }

        @Override
        public float getSpeed() {
            return this.speed;
        }

        @Override
        public float getAttackDamageBonus() {
            return this.damage;
        }

        @Override
        public int getLevel() {
            return this.level;
        }

        @Override
        public int getEnchantmentValue() {
            return this.enchantmentValue;
        }

        @Override
        public Ingredient getRepairIngredient() {
            if(null == this.repairIngredient) {
                this.repairIngredient = repairIngredientSupplier.get();
            }
            return this.repairIngredient;
        }
    }
}
