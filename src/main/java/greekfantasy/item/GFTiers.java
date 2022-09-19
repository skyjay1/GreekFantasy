package greekfantasy.item;

import greekfantasy.GFRegistry;
import greekfantasy.GreekFantasy;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.TierSortingRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class GFTiers {

    public static final GFTier BIDENT = new GFTier(0, 786, 8.0F, 3.0F, 14, () -> Ingredient.of(Items.BLAZE_ROD));
    public static final GFTier FLINT = new GFTier(0, 88, 3.0F, 1.0F, 12, () -> Ingredient.of(Items.FLINT));
    public static final GFTier IVORY = new GFTier(1, 835, 6.0F, 2.0F, 10, () -> Ingredient.of(Items.BONE));
    public static final GFTier THYRSUS = new GFTier(0, 224, 3.0F, 1.5F, 10, () -> Ingredient.of(GFRegistry.ItemReg.PINECONE.get()));

    static {
        TierSortingRegistry.registerTier(BIDENT, new ResourceLocation(GreekFantasy.MODID, "bident"), BIDENT.getBetterThan(), BIDENT.getWorseThan());
        TierSortingRegistry.registerTier(FLINT, new ResourceLocation(GreekFantasy.MODID, "flint"), FLINT.getBetterThan(), FLINT.getWorseThan());
        TierSortingRegistry.registerTier(IVORY, new ResourceLocation(GreekFantasy.MODID, "ivory"), IVORY.getBetterThan(), IVORY.getWorseThan());
        TierSortingRegistry.registerTier(THYRSUS, new ResourceLocation(GreekFantasy.MODID, "thyrsus"), THYRSUS.getBetterThan(), THYRSUS.getWorseThan());
    }

    private static final class GFTier implements Tier {
        private final int level;
        private final int uses;
        private final float speed;
        private final float damage;
        private final int enchantmentValue;
        private Ingredient repairIngredient;
        private final Supplier<Ingredient> repairIngredientSupplier;

        private final List<Object> betterThan;
        private final List<Object> worseThan;

        public GFTier(int level, int uses, float speed, float damage, int enchantmentValue, Supplier<Ingredient> repairIngredient) {
            this.level = level;
            this.uses = uses;
            this.speed = speed;
            this.damage = damage;
            this.enchantmentValue = enchantmentValue;
            this.repairIngredientSupplier = repairIngredient;
            this.betterThan = new ArrayList<>();
            this.worseThan = new ArrayList<>();
            for (Tier t : TierSortingRegistry.getSortedTiers()) {
                if (t.getLevel() < level) {
                    betterThan.add(t);
                }
                if (t.getLevel() > level) {
                    worseThan.add(t);
                }
            }
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
            if (null == this.repairIngredient) {
                this.repairIngredient = repairIngredientSupplier.get();
            }
            return this.repairIngredient;
        }

        public List<Object> getWorseThan() {
            return worseThan;
        }

        public List<Object> getBetterThan() {
            return betterThan;
        }
    }
}
