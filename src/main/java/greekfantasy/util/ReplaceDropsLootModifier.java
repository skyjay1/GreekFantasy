package greekfantasy.util;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.Supplier;

public class ReplaceDropsLootModifier extends LootModifier {

    public static final Supplier<Codec<ReplaceDropsLootModifier>> CODEC_SUPPLIER = Suppliers.memoize(() -> RecordCodecBuilder.create(inst ->
            codecStart(inst)
                    .and(ItemStack.CODEC.fieldOf("itemstack").forGetter(ReplaceDropsLootModifier::getItemStack))
                    .and(Codec.BOOL.optionalFieldOf("replace", true).forGetter(ReplaceDropsLootModifier::isReplace))
                    .apply(inst, ReplaceDropsLootModifier::new)));


    private final ItemStack itemStack;
    private final boolean replace;

    protected ReplaceDropsLootModifier(final LootItemCondition[] conditionsIn,
                                       final ItemStack itemStack, final boolean replace) {
        super(conditionsIn);
        this.itemStack = itemStack;
        this.replace = replace;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public boolean isReplace() { return replace; }

    @Nonnull
    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        // ensure item stack was parsed correctly
        if (itemStack.isEmpty()) {
            return generatedLoot;
        }
        // replace loot with item
        if (isReplace()) {
            return ObjectArrayList.of(itemStack.copy());
        }
        // add loot to drops
        generatedLoot.add(itemStack.copy());
        return generatedLoot;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC_SUPPLIER.get();
    }
}
