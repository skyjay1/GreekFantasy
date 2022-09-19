package greekfantasy.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import greekfantasy.GreekFantasy;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import java.util.List;

public class ReplaceDropsLootModifier extends LootModifier {

    private final ItemStack itemStack;
    private final boolean doReplace;

    protected ReplaceDropsLootModifier(final LootItemCondition[] conditionsIn,
                                       final ItemStack itemStack, final boolean doReplace) {
        super(conditionsIn);
        this.itemStack = itemStack;
        this.doReplace = doReplace;
    }

    @Nonnull
    @Override
    protected List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context) {
        GreekFantasy.LOGGER.debug("replacedrops for " + itemStack.getDisplayName() + " replace=" + doReplace);
        // ensure item stack was parsed correctly
        if (itemStack.isEmpty()) {
            return generatedLoot;
        }
        // replace loot with item
        if (doReplace) {
            return List.of(itemStack.copy());
        }
        // add loot to drops
        generatedLoot.add(itemStack.copy());
        return generatedLoot;
    }

    public static class Serializer extends GlobalLootModifierSerializer<ReplaceDropsLootModifier> {

        private static final String ITEMSTACK = "itemstack";
        private static final String REPLACE = "replace";

        @Override
        public ReplaceDropsLootModifier read(ResourceLocation name, JsonObject object, LootItemCondition[] conditionsIn) {
            ItemStack itemStack = ItemStack.CODEC
                    .parse(JsonOps.INSTANCE, object.get(ITEMSTACK))
                    .resultOrPartial(s -> GreekFantasy.LOGGER.warn("Failed to parse '" + ITEMSTACK + "' from ReplaceDropsLootModifier: " + s))
                    .orElse(ItemStack.EMPTY);
            boolean doReplace = Codec.BOOL
                    .parse(JsonOps.INSTANCE, object.get(REPLACE))
                    .resultOrPartial(s -> GreekFantasy.LOGGER.warn("Failed to parse '" + REPLACE + "' from ReplaceDropsLootModifier: " + s))
                    .orElse(true);
            return new ReplaceDropsLootModifier(conditionsIn, itemStack, doReplace);
        }

        @Override
        public JsonObject write(ReplaceDropsLootModifier instance) {
            JsonElement itemStack = ItemStack.CODEC
                    .encodeStart(JsonOps.INSTANCE, instance.itemStack)
                    .resultOrPartial(s -> GreekFantasy.LOGGER.warn("Failed to write ItemStack to ReplaceDropsLootModifier: " + s))
                    .orElse(JsonOps.INSTANCE.createString(ForgeRegistries.ITEMS.getKey(Items.AIR).toString()));
            JsonElement doReplace = JsonOps.INSTANCE.createBoolean(instance.doReplace);
            JsonObject json = makeConditions(instance.conditions);
            json.add(ITEMSTACK, itemStack);
            json.add(REPLACE, doReplace);
            return json;
        }
    }
}
