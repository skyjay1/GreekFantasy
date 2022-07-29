package greekfantasy.util;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import greekfantasy.GreekFantasy;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;

import javax.annotation.Nonnull;
import java.util.List;

public class QuestLootModifier extends LootModifier {

    private final List<String> paths;
    private final UniformInt count;

    protected QuestLootModifier(final LootItemCondition[] conditionsIn, final List<String> paths,
                                final int min, final int max) {
        super(conditionsIn);
        this.paths = ImmutableList.copyOf(paths);
        this.count = UniformInt.of(min, max);
    }

    @Nonnull
    @Override
    protected List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context) {
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

    public static class Serializer extends GlobalLootModifierSerializer<QuestLootModifier> {

        private static final String PATHS = "paths";
        private static final String MIN = "min";
        private static final String MAX = "max";

        private final Gson GSON;

        public Serializer() {
            this.GSON = new GsonBuilder().create();
        }

        @Override
        public QuestLootModifier read(ResourceLocation name, JsonObject object, LootItemCondition[] conditionsIn) {
            List<String> paths = Codec.STRING.listOf().parse(JsonOps.INSTANCE, object.get(PATHS))
                    .resultOrPartial(s -> GreekFantasy.LOGGER.error("Failed to parse '" + PATHS + "' in loot modifier for input: " + s))
                    .orElse(List.of());

            int min = object.get(MIN).getAsInt();
            int max = object.get(MAX).getAsInt();

            return new QuestLootModifier(conditionsIn, paths, min, max);
        }

        @Override
        public JsonObject write(QuestLootModifier instance) {
            JsonObject json = makeConditions(instance.conditions);

            json.add(PATHS, Codec.STRING.listOf().encodeStart(JsonOps.INSTANCE, instance.paths)
                    .resultOrPartial(s -> GreekFantasy.LOGGER.error("Failed to write '" + PATHS + "' in loot modifier for input: " + s))
                    .orElse(new JsonArray()));

            json.add(MIN, new JsonPrimitive(instance.count.getMinValue()));
            json.add(MAX, new JsonPrimitive(instance.count.getMaxValue()));

            return json;
        }


    }
}
