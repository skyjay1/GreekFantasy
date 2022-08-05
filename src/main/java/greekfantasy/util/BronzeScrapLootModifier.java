package greekfantasy.util;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.tags.ITag;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class BronzeScrapLootModifier extends LootModifier {

    public static final Supplier<Codec<BronzeScrapLootModifier>> CODEC_SUPPLIER = Suppliers.memoize(() -> RecordCodecBuilder.create(inst ->
            codecStart(inst)
                    .and(Codec.STRING.listOf().fieldOf("paths").forGetter(BronzeScrapLootModifier::getPaths))
                    .and(TagKey.codec(Registry.ITEM_REGISTRY).fieldOf("item_tag").forGetter(BronzeScrapLootModifier::getItemTag))
                    .and(IntProvider.CODEC.fieldOf("count").forGetter(BronzeScrapLootModifier::getCount))
                    .apply(inst, BronzeScrapLootModifier::new)));

    private final List<String> paths;
    private final TagKey<Item> itemTag;
    private final IntProvider count;

    protected BronzeScrapLootModifier(final LootItemCondition[] conditionsIn, final List<String> paths,
                                      final TagKey<Item> itemTag, final IntProvider count) {
        super(conditionsIn);
        this.paths = ImmutableList.copyOf(paths);
        this.itemTag = itemTag;
        this.count = count;
    }

    public List<String> getPaths() {
        return paths;
    }

    public TagKey<Item> getItemTag() {
        return itemTag;
    }

    public IntProvider getCount() {
        return count;
    }

    @Nonnull
    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        ResourceLocation lootTable = context.getQueriedLootTableId();
        boolean matchesPath = false;
        // iterate over each path to check if this modifier can apply
        final String lootTableName = lootTable.toString();
        for (String path : paths) {
            if (lootTableName.contains(path)) {
                matchesPath = true;
                break;
            }
        }
        if (!matchesPath) {
            return generatedLoot;
        }
        // resolve item tag
        ITag<Item> items = ForgeRegistries.ITEMS.tags().getTag(this.itemTag);
        if (items.isEmpty()) {
            return generatedLoot;
        }
        // add items from the item tag
        for (int i = 0, n = count.sample(context.getRandom()); i < n; i++) {
            Optional<Item> item = items.getRandomElement(context.getRandom());
            if (item.isPresent()) {
                generatedLoot.add(new ItemStack(item.get()));
            }
        }
        return generatedLoot;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC_SUPPLIER.get();
    }
}
