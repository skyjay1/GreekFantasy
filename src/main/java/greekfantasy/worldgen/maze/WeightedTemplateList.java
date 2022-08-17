package greekfantasy.worldgen.maze;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import greekfantasy.util.WeightedUtil;
import net.minecraft.util.RandomSource;

import javax.annotation.Nullable;
import java.util.List;

public class WeightedTemplateList {

    public static final WeightedTemplateList EMPTY = new WeightedTemplateList(List.of());

    public static final Codec<WeightedTemplateList> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        WeightedTemplate.CODEC.listOf().fieldOf("structures").forGetter(WeightedTemplateList::getTemplates)
    ).apply(instance, WeightedTemplateList::new));

    private final List<WeightedTemplate> templates;

    public WeightedTemplateList(List<WeightedTemplate> templates) {
        this.templates = templates;
    }

    /**
     * @return the list of WeightedTemplates
     */
    public List<WeightedTemplate> getTemplates() {
        return templates;
    }

    /**
     * Selects a random template from the contained list, using the random source and item weights.
     * @param random the random source
     * @return a single element, or null if the list was empty
     */
    @Nullable
    public WeightedTemplate sample(final RandomSource random) {
        return WeightedUtil.sample(this.templates, random);
    }
}
