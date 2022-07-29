package greekfantasy.util;

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
import java.util.Set;

public class Quest {

    public static final Quest EMPTY = new Quest("ERROR", List.of());

    public static final Codec<Quest> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("description").forGetter(Quest::getDescriptionId),
            Codec.STRING.listOf().fieldOf("components").forGetter(Quest::getComponentsIds)
    ).apply(instance, Quest::new));

    private final String description;
    private final List<String> components;

    public Quest(String description, List<String> components) {
        this.description = description;
        this.components = components;
    }

    public String getDescriptionId() {
        return description;
    }

    public List<String> getComponentsIds() {
        return components;
    }

    public MutableComponent getDescription() {
        return new TranslatableComponent(getDescriptionId());
    }

    public List<MutableComponent> getComponents() {
        List<MutableComponent> list = new ArrayList<>();
        for(String id : getComponentsIds()) {
            list.add(new TranslatableComponent(id));
        }
        return list;
    }

    public static String getDescriptionFromKey(final ResourceLocation id) {
        return "quest." + id.getNamespace() + "." + id.getPath();
    }

    public static ResourceLocation getRandomQuestId(final Random rand) {
        ResourceLocation[] keys = GreekFantasy.QUESTS.getKeys().toArray(new ResourceLocation[0]);
        return Util.getRandom(keys, rand);
    }

    public static ItemStack createQuestItemStack(final ResourceLocation questId) {
        ItemStack itemStack = new ItemStack(GFRegistry.ItemReg.QUEST.get());
        itemStack.getOrCreateTag().putString(QuestItem.KEY_QUEST, questId.toString());
        return itemStack;
    }
}
