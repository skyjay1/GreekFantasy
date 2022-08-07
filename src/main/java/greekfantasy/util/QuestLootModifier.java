package greekfantasy.util;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.Supplier;

public class QuestLootModifier extends LootModifier {

    public static final Supplier<Codec<QuestLootModifier>> CODEC_SUPPLIER = Suppliers.memoize(() -> RecordCodecBuilder.create(inst ->
            codecStart(inst)
                    .and(Codec.STRING.listOf().fieldOf("paths").forGetter(QuestLootModifier::getPaths))
                    .and(IntProvider.CODEC.fieldOf("count").forGetter(QuestLootModifier::getCount))
                    .apply(inst, QuestLootModifier::new)));

    private final List<String> paths;
    private final IntProvider count;

    protected QuestLootModifier(final LootItemCondition[] conditionsIn, final List<String> paths,
                                final IntProvider count) {
        super(conditionsIn);
        this.paths = ImmutableList.copyOf(paths);
        this.count = count;
    }

    public List<String> getPaths() {
        return paths;
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
        // add the given number of random quests
        for (int i = 0, n = count.sample(context.getRandom()); i < n; i++) {
            ItemStack itemStack = Quest.createQuestItemStack(Quest.getRandomQuestId(context.getRandom()));
            generatedLoot.add(itemStack);
        }
        return generatedLoot;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC_SUPPLIER.get();
    }
}
