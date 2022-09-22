package greekfantasy.worldgen;

import com.mojang.serialization.Codec;
import greekfantasy.GFRegistry;
import greekfantasy.entity.Satyr;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.horse.Variant;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import java.util.Optional;

public class SatyrStructureProcessor extends StructureProcessor {

    public static final SatyrStructureProcessor PROCESSOR = new SatyrStructureProcessor();
    public static final Codec<SatyrStructureProcessor> CODEC = Codec.unit(() -> PROCESSOR);

    public SatyrStructureProcessor() {
        super();
    }

    @Override
    protected StructureProcessorType<?> getType() {
        return GFRegistry.StructureProcessorReg.SATYR_PROCESSOR;
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
        if (entityType.isPresent() && entityType.get() == GFRegistry.EntityReg.SATYR.get()) {
            // determine color variant
            final long seed = placementSettings.getBoundingBox().hashCode();
            RandomSource random = RandomSource.create(seed);
            Variant variant = Util.getRandom(net.minecraft.world.entity.animal.horse.Variant.values(), random);
            // write color variant to compound
            tag.putByte(Satyr.KEY_VARIANT, (byte) variant.getId());
            // create modified entity info
            return new StructureTemplate.StructureEntityInfo(entityInfo.pos, entityInfo.blockPos, tag);
        }
        return entityInfo;
    }
}
