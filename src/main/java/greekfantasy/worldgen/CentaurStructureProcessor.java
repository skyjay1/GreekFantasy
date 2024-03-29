package greekfantasy.worldgen;

import com.mojang.serialization.Codec;
import greekfantasy.GFRegistry;
import greekfantasy.entity.Centaur;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.horse.Markings;
import net.minecraft.world.entity.animal.horse.Variant;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import java.util.Optional;

public class CentaurStructureProcessor extends StructureProcessor {

    public static final CentaurStructureProcessor PROCESSOR = new CentaurStructureProcessor();
    public static final Codec<CentaurStructureProcessor> CODEC = Codec.unit(() -> PROCESSOR);

    public CentaurStructureProcessor() {
        super();
    }

    @Override
    protected StructureProcessorType<?> getType() {
        return GFRegistry.StructureProcessorReg.CENTAUR_PROCESSOR;
    }

    @Override
    public StructureTemplate.StructureEntityInfo processEntity(LevelReader level, BlockPos seedPos,
                                                   StructureTemplate.StructureEntityInfo rawEntityInfo,
                                                   StructureTemplate.StructureEntityInfo entityInfo,
                                                   StructurePlaceSettings placementSettings,
                                                   StructureTemplate template) {
        // read entity type from tag
        CompoundTag tag = entityInfo.nbt.copy();
        Optional<EntityType<?>> entityType = EntityType.by(tag);
        // ensure entity is satyr
        if(entityType.isPresent() && entityType.get() == GFRegistry.EntityReg.CENTAUR.get()) {
            // determine color variant
            final long seed = placementSettings.getBoundingBox().hashCode();
            RandomSource random = RandomSource.create(seed);
            Variant variant = Util.getRandom(Variant.values(), random);
            Markings markings = Markings.values()[Mth.floor(Math.random() * Markings.values().length)];
            // write color variant to compound
            tag.putInt(Centaur.KEY_VARIANT, variant.getId() & 255 | markings.getId() << 8 & '\uff00');

            // create modified entity info
            return new StructureTemplate.StructureEntityInfo(entityInfo.pos, entityInfo.blockPos, tag);
        }
        return entityInfo;
    }
}
