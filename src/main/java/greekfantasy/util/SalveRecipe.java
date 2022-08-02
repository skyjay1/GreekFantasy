package greekfantasy.util;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import greekfantasy.GFRegistry;
import greekfantasy.GreekFantasy;
import greekfantasy.item.OliveSalveItem;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapelessRecipe;

import java.util.ArrayList;
import java.util.List;

public class SalveRecipe extends ShapelessRecipe {

    private WeightedMobEffectInstance useEffect;
    private int bonusEffectCount;
    private List<WeightedMobEffectInstance> bonusEffects;
    private int rolls;

    public SalveRecipe(ResourceLocation idIn, WeightedMobEffectInstance useEffect,
                       List<WeightedMobEffectInstance> bonusEffects, int rolls,
                       NonNullList<Ingredient> recipeItemsIn) {
        super(idIn, Serializer.CATEGORY, createSalve(useEffect, bonusEffects, rolls), recipeItemsIn);
        this.useEffect = useEffect;
        this.bonusEffects = bonusEffects;
        this.bonusEffectCount = this.bonusEffects.size();
        this.rolls = rolls;
    }

    private static ItemStack createSalve(final WeightedMobEffectInstance useEffect,
                                         final List<WeightedMobEffectInstance> bonusEffects, final int rolls) {
        ItemStack itemStack = new ItemStack(GFRegistry.ItemReg.OLIVE_SALVE.get());
        // write tag
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.put(OliveSalveItem.KEY_USE_EFFECT, useEffect.asTag());
        compoundTag.putInt(OliveSalveItem.KEY_ROLLS, rolls);
        ListTag list = new ListTag();
        for (WeightedMobEffectInstance instance : bonusEffects) {
            list.add(instance.asTag());
        }
        compoundTag.put(OliveSalveItem.KEY_BONUS_EFFECTS, list);
        // set tag
        itemStack.setTag(compoundTag);
        return itemStack;
    }

    @Override
    public ItemStack assemble(CraftingContainer inv) {
        final ItemStack result = super.assemble(inv);
        return result;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return GFRegistry.RecipeReg.OLIVE_SALVE.get();
    }

    public WeightedMobEffectInstance getUseEffect() {
        return useEffect;
    }

    public List<WeightedMobEffectInstance> getBonusEffects() {
        return bonusEffects;
    }

    public int getRolls() {
        return rolls;
    }

    public int getBonusEffectCount() {
        return bonusEffectCount;
    }

    public static class Serializer extends ShapelessRecipe.Serializer {

        public static final String CATEGORY = "olive_salve";
        private static final String KEY_USE_EFFECT = "use_effect";
        private static final String KEY_BONUS_EFFECTS = "bonus_effects";
        private static final String KEY_ROLLS = "rolls";

        @Override
        public ShapelessRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
            // read the recipe from shapeless recipe serializer
            final ShapelessRecipe recipe = super.fromJson(recipeId, json);
            // read from json using codec parsing
            WeightedMobEffectInstance useEffect = WeightedMobEffectInstance.CODEC.parse(JsonOps.INSTANCE, json.get(KEY_USE_EFFECT))
                    .resultOrPartial(s -> GreekFantasy.LOGGER.error("Failed to parse '" + KEY_USE_EFFECT + "' in olive salve recipe for input: " + s))
                    .orElse(WeightedMobEffectInstance.EMPTY);
            List<WeightedMobEffectInstance> bonusEffects = WeightedMobEffectInstance.CODEC.listOf().parse(JsonOps.INSTANCE, json.get(KEY_BONUS_EFFECTS))
                    .resultOrPartial(s -> GreekFantasy.LOGGER.error("Failed to parse '" + KEY_BONUS_EFFECTS + "' in olive salve recipe for input: " + s))
                    .orElse(List.of());
            int rolls = Codec.INT.parse(JsonOps.INSTANCE, json.get(KEY_ROLLS))
                    .resultOrPartial(s -> GreekFantasy.LOGGER.error("Failed to parse '" + KEY_ROLLS + "' in olive salve recipe for input: " + s))
                    .orElse(0);

            return new SalveRecipe(recipeId, useEffect, bonusEffects, rolls, recipe.getIngredients());
        }

        @Override
        public ShapelessRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            final ShapelessRecipe recipe = super.fromNetwork(recipeId, buffer);
            final int rolls = buffer.readInt();
            final int bonusEffectCount = buffer.readInt();
            final WeightedMobEffectInstance useEffect = WeightedMobEffectInstance.fromTag(buffer.readNbt());
            final List<WeightedMobEffectInstance> bonusEffects = new ArrayList<>(bonusEffectCount);
            for (int i = 0; i < bonusEffectCount; i++) {
                bonusEffects.add(WeightedMobEffectInstance.fromTag(buffer.readNbt()));
            }
            return new SalveRecipe(recipeId, useEffect, bonusEffects, rolls, recipe.getIngredients());
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, ShapelessRecipe recipeIn) {
            super.toNetwork(buffer, recipeIn);
            final SalveRecipe recipe = (SalveRecipe) recipeIn;
            buffer.writeInt(recipe.getRolls());
            buffer.writeInt(recipe.getBonusEffectCount());
            buffer.writeNbt(recipe.getUseEffect().asTag());
            for (int i = 0, n = recipe.getBonusEffectCount(); i < n; i++) {
                buffer.writeNbt(recipe.getBonusEffects().get(i).asTag());
            }
        }
    }
}
