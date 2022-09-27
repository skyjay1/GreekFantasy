package greekfantasy.util;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import greekfantasy.GFRegistry;
import greekfantasy.GreekFantasy;
import greekfantasy.item.QuestItem;
import net.minecraft.Util;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Quest {

    public static final Quest EMPTY = new Quest("ERROR", List.of(), true);

    public static final Codec<Quest> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.optionalFieldOf("description", "").forGetter(Quest::getDescriptionId),
            Codec.STRING.listOf().optionalFieldOf("components", List.of()).forGetter(Quest::getComponentsIds),
            Codec.BOOL.optionalFieldOf("disabled", false).forGetter(Quest::isDisabled)
    ).apply(instance, Quest::new));

    private final String description;
    private final List<String> components;
    private final boolean disabled;

    /**
     * @param description the translation key for the quest title
     * @param components the translation keys for the quest contents
     * @param disabled true if the quest should not be used or appear in-game
     */
    public Quest(String description, List<String> components, boolean disabled) {
        this.description = description;
        this.components = ImmutableList.copyOf(components);
        this.disabled = disabled;
    }

    public String getDescriptionId() {
        return description;
    }

    public List<String> getComponentsIds() {
        return components;
    }

    /**
     * @return true if the quest should not be used or appear in-game
     */
    public boolean isDisabled() {
        return disabled;
    }

    /**
     * @return a new text component for the quest description
     */
    public MutableComponent getDescription() {
        return new TranslatableComponent(getDescriptionId());
    }

    /**
     * @return a new list of text components for the quest contents
     */
    public List<MutableComponent> getComponents() {
        List<MutableComponent> list = new ArrayList<>();
        for(String id : getComponentsIds()) {
            list.add(new TranslatableComponent(id));
        }
        return list;
    }

    /**
     * @param id the quest ID
     * @return the translation key for the given quest
     */
    public static String getDescriptionFromKey(final ResourceLocation id) {
        return "quest." + id.getNamespace() + "." + id.getPath();
    }

    /**
     * Attempts to choose a random quest ID for a quest that is not disabled.
     * @param rand a random instance
     * @return the random quest ID, or "empty" if it failed
     */
    public static ResourceLocation getRandomQuestId(final Random rand) {
        ResourceLocation[] keys = GreekFantasy.QUEST_MAP.keySet().toArray(new ResourceLocation[0]);
        // attempt to locate a non-disabled quest
        for(int attempts = 0; attempts < 10; attempts++) {
            ResourceLocation id = Util.getRandom(keys, rand);
            if(!GreekFantasy.QUEST_MAP.getOrDefault(id, Quest.EMPTY).isDisabled()) {
                return id;
            }
        }
        return new ResourceLocation("empty");
    }

    /**
     * @param questId the quest ID
     * @return an item stack containing a quest item with the given quest ID
     */
    public static ItemStack createQuestItemStack(final ResourceLocation questId) {
        ItemStack itemStack = new ItemStack(GFRegistry.ItemReg.QUEST.get());
        itemStack.getOrCreateTag().putString(QuestItem.KEY_QUEST, questId.toString());
        return itemStack;
    }
}
