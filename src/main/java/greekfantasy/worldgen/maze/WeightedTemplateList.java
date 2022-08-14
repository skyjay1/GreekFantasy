package greekfantasy.worldgen.maze;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import greekfantasy.util.WeightedMobEffectInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.RandomSource;

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

    public List<WeightedTemplate> getTemplates() {
        return templates;
    }

    public WeightedTemplate sample(final RandomSource random) {
        return WeightedMobEffectInstance.sample(this.templates, random);
    }
}
