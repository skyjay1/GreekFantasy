package greekfantasy.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import greekfantasy.GFRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraftforge.common.world.ModifiableStructureInfo;
import net.minecraftforge.common.world.StructureModifier;

public class AddSpawnsStructureModifier implements StructureModifier {

    public static final Codec<AddSpawnsStructureModifier> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            RegistryCodecs.homogeneousList(Registry.STRUCTURE_REGISTRY, Structure.DIRECT_CODEC).fieldOf("structures").forGetter(AddSpawnsStructureModifier::getStructures),
                  MobCategory.CODEC.fieldOf("category").forGetter(AddSpawnsStructureModifier::getCategory),
                  MobSpawnSettings.SpawnerData.CODEC.fieldOf("spawn").forGetter(AddSpawnsStructureModifier::getSpawn)
            ).apply(builder, AddSpawnsStructureModifier::new));

    protected final HolderSet<Structure> structures;
    protected final MobCategory category;
    protected final MobSpawnSettings.SpawnerData spawn;

    public AddSpawnsStructureModifier(HolderSet<Structure> structures, MobCategory category, MobSpawnSettings.SpawnerData spawn) {
        this.structures = structures;
        this.category = category;
        this.spawn = spawn;
    }

    public HolderSet<Structure> getStructures() {
        return structures;
    }

    public MobCategory getCategory() {
        return category;
    }

    public MobSpawnSettings.SpawnerData getSpawn() {
        return spawn;
    }

    @Override
    public void modify(Holder<Structure> structure, Phase phase, ModifiableStructureInfo.StructureInfo.Builder builder) {
        if (phase == Phase.ADD && this.structures.contains(structure)) {
            builder.getStructureSettings()
                    .getOrAddSpawnOverrides(category)
                    .addSpawn(spawn);
        }
    }

    @Override
    public Codec<? extends StructureModifier> codec() {
        return GFRegistry.StructureModifierReg.ADD_SPAWNS_MODIFIER.get();
    }
}
