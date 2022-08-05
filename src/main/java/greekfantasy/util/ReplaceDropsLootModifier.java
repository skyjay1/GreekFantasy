package greekfantasy.util;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

public class ReplaceDropsLootModifier extends LootModifier {

    public static final Supplier<Codec<ReplaceDropsLootModifier>> CODEC_SUPPLIER = Suppliers.memoize(() -> RecordCodecBuilder.create(inst ->
            codecStart(inst)
                    .and(ItemStack.CODEC.fieldOf("replacement").forGetter(ReplaceDropsLootModifier::getReplacement))
                    .apply(inst, ReplaceDropsLootModifier::new)));


    private final ItemStack replacement;

    protected ReplaceDropsLootModifier(final LootItemCondition[] conditionsIn,
                                       final ItemStack replacement) {
        super(conditionsIn);
        this.replacement = replacement;
    }

    public ItemStack getReplacement() {
        return replacement;
    }

    @Nonnull
    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        // do not apply when incorrectly parsed or loot is empty
        if (replacement.isEmpty() || generatedLoot.isEmpty()) {
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
        if (tool.canPerformAction(ToolActions.SHEARS_DIG) || EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SILK_TOUCH, tool) > 0) {
            return generatedLoot;
        }
        // replace loot with item
        return ObjectArrayList.of(replacement.copy());
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC_SUPPLIER.get();
    }
}
