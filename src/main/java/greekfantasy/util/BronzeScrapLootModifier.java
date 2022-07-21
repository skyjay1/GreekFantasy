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
import net.minecraft.tags.TagKey;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.IntProviderType;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.tags.ITag;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;

public class BronzeScrapLootModifier extends LootModifier {

    private final List<String> paths;
    private final TagKey<Item> itemTag;
    private final UniformInt count;

    protected BronzeScrapLootModifier(final LootItemCondition[] conditionsIn, final List<String> paths,
                                      final TagKey<Item> itemTag, final int min, final int max) {
        super(conditionsIn);
        this.paths = ImmutableList.copyOf(paths);
        this.itemTag = itemTag;
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

    public static class Serializer extends GlobalLootModifierSerializer<BronzeScrapLootModifier> {

        private static final ResourceLocation BRONZE_SCRAP = new ResourceLocation(GreekFantasy.MODID, "bronze_scrap");

        private static final String PATHS = "paths";
        private static final String ITEM_TAG = "item_tag";
        private static final String MIN = "min";
        private static final String MAX = "max";

        private final Gson GSON;

        public Serializer() {
            this.GSON = new GsonBuilder().create();
        }

        private Optional<? extends IntProvider> readIntProvider(final JsonObject object, final String key) {
            JsonObject obj = object.getAsJsonObject(key);

            return (obj.isJsonPrimitive() ? ConstantInt.CODEC : UniformInt.CODEC).parse(JsonOps.INSTANCE, obj)
                    .resultOrPartial(s -> GreekFantasy.LOGGER.error("Failed to parse '" + key + "' in loot modifier for input: " + s));
        }

        @Override
        public BronzeScrapLootModifier read(ResourceLocation name, JsonObject object, LootItemCondition[] conditionsIn) {
            List<String> paths = Codec.STRING.listOf().parse(JsonOps.INSTANCE, object.get(PATHS))
                    .resultOrPartial(s -> GreekFantasy.LOGGER.error("Failed to parse '" + PATHS + "' in loot modifier for input: " + s))
                    .orElse(List.of());
            TagKey<Item> tagKey = TagKey.codec(ForgeRegistries.ITEMS.getRegistryKey()).parse(JsonOps.INSTANCE, object.get(ITEM_TAG))
                    .resultOrPartial(s -> GreekFantasy.LOGGER.error("Failed to parse '" + ITEM_TAG + "' in loot modifier for input: " + s))
                    .orElse(ForgeRegistries.ITEMS.tags().createTagKey(BRONZE_SCRAP));

            int min = object.get(MIN).getAsInt();
            int max = object.get(MAX).getAsInt();

            return new BronzeScrapLootModifier(conditionsIn, paths, tagKey, min, max);
        }

        @Override
        public JsonObject write(BronzeScrapLootModifier instance) {
            JsonObject json = makeConditions(instance.conditions);

            json.add(PATHS, Codec.STRING.listOf().encodeStart(JsonOps.INSTANCE, instance.paths)
                    .resultOrPartial(s -> GreekFantasy.LOGGER.error("Failed to write '" + PATHS + "' in loot modifier for input: " + s))
                    .orElse(new JsonArray()));
            json.add(ITEM_TAG, TagKey.codec(ForgeRegistries.ITEMS.getRegistryKey()).encodeStart(JsonOps.INSTANCE, instance.itemTag)
                    .resultOrPartial(s -> GreekFantasy.LOGGER.error("Failed to write '" + ITEM_TAG + "' in loot modifier for input: " + s))
                    .orElse(new JsonPrimitive("minecraft:empty")));

            json.add(MIN, new JsonPrimitive(instance.count.getMinValue()));
            json.add(MAX, new JsonPrimitive(instance.count.getMaxValue()));

            return json;
        }


    }
}
