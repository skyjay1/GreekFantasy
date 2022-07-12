package greekfantasy.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import greekfantasy.GreekFantasy;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import java.util.List;

public class ReplaceDropsLootModifier extends LootModifier {

    private final ItemStack replacement;

    protected ReplaceDropsLootModifier(final LootItemCondition[] conditionsIn,
                                       final ItemStack replacement) {
        super(conditionsIn);
        this.replacement = replacement;
    }

    @Nonnull
    @Override
    protected List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context) {
        GreekFantasy.LOGGER.debug("ReplaceDropsLootModifier: " + replacement.getItem());
        // do not apply when incorrectly parsed or loot is empty
        if(replacement.isEmpty() || generatedLoot.isEmpty()) {
            return generatedLoot;
        }
        // determine loot parameter values
        Entity entity = context.getParamOrNull(LootContextParams.THIS_ENTITY);
        ItemStack tool = context.getParamOrNull(LootContextParams.TOOL);
        BlockState block = context.getParamOrNull(LootContextParams.BLOCK_STATE);
        // do not apply when missing a parameter
        if (entity == null || tool == null || block == null) {
            return generatedLoot;
        }
        // do not apply when using shears or silk touch
        if(tool.canPerformAction(ToolActions.SHEARS_DIG) || EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SILK_TOUCH, tool) > 0) {
            return generatedLoot;
        }
        GreekFantasy.LOGGER.debug("success");
        // replace loot with item
        return List.of(replacement.copy());
    }

    public static class Serializer extends GlobalLootModifierSerializer<ReplaceDropsLootModifier> {

        private static final String REPLACEMENT = "replacement";

        @Override
        public ReplaceDropsLootModifier read(ResourceLocation name, JsonObject object, LootItemCondition[] conditionsIn) {
            ItemStack itemStack = ItemStack.CODEC
                    .parse(JsonOps.INSTANCE, object.get(REPLACEMENT))
                    .resultOrPartial(s -> GreekFantasy.LOGGER.warn("Failed to parse ItemStack from ReplaceDropsLootModifier: " + s))
                    .orElse(ItemStack.EMPTY);
            return new ReplaceDropsLootModifier(conditionsIn, itemStack);
        }

        @Override
        public JsonObject write(ReplaceDropsLootModifier instance) {
            JsonElement itemStack = ItemStack.CODEC
                    .encodeStart(JsonOps.INSTANCE, instance.replacement)
                    .resultOrPartial(s -> GreekFantasy.LOGGER.warn("Failed to write ItemStack to ReplaceDropsLootModifier: " + s))
                    .orElse(JsonOps.INSTANCE.createString(ForgeRegistries.ITEMS.getKey(Items.AIR).toString()));
            JsonObject json = makeConditions(instance.conditions);
            json.add(REPLACEMENT, itemStack);
            return json;
        }
    }
}
