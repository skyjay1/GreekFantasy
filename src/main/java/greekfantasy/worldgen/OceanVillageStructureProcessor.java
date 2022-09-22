package greekfantasy.worldgen;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import greekfantasy.GFRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class OceanVillageStructureProcessor extends StructureProcessor {

    public static final OceanVillageStructureProcessor PROCESSOR = new OceanVillageStructureProcessor();
    public static final Codec<OceanVillageStructureProcessor> CODEC = Codec.unit(() -> PROCESSOR);

    private static final Map<Block, List<Block>> CORAL_LIST = ImmutableMap.<Block, List<Block>>builder()
            .put(Blocks.HORN_CORAL_BLOCK, List.of(Blocks.HORN_CORAL_BLOCK, Blocks.FIRE_CORAL_BLOCK, Blocks.BUBBLE_CORAL_BLOCK, Blocks.BRAIN_CORAL_BLOCK, Blocks.TUBE_CORAL_BLOCK))
            .put(Blocks.HORN_CORAL, List.of(Blocks.HORN_CORAL, Blocks.FIRE_CORAL, Blocks.BUBBLE_CORAL, Blocks.BRAIN_CORAL, Blocks.TUBE_CORAL))
            .put(Blocks.HORN_CORAL_FAN, List.of(Blocks.HORN_CORAL_FAN, Blocks.FIRE_CORAL_FAN, Blocks.BUBBLE_CORAL_FAN, Blocks.BRAIN_CORAL_FAN, Blocks.TUBE_CORAL_FAN))
            .put(Blocks.HORN_CORAL_WALL_FAN, List.of(Blocks.HORN_CORAL_WALL_FAN, Blocks.FIRE_CORAL_WALL_FAN, Blocks.BUBBLE_CORAL_WALL_FAN, Blocks.BRAIN_CORAL_WALL_FAN, Blocks.TUBE_CORAL_WALL_FAN))
            .build();

    private static final int coralListSize = List.copyOf(CORAL_LIST.values()).get(0).size();

    public OceanVillageStructureProcessor() {
        super();
    }

    @Override
    protected StructureProcessorType<?> getType() {
        return GFRegistry.StructureProcessorReg.OCEAN_VILLAGE_PROCESSOR;
    }

    @Nullable
    @Override
    public StructureTemplate.StructureBlockInfo process(LevelReader level, BlockPos rawPos, BlockPos pos, StructureTemplate.StructureBlockInfo rawBlockInfo, StructureTemplate.StructureBlockInfo blockInfo, StructurePlaceSettings placementSettings, @Nullable StructureTemplate template) {
        // determine if the block is coral
        if (CORAL_LIST.containsKey(blockInfo.state.getBlock())) {
            final long seed = placementSettings.getBoundingBox().hashCode();
            RandomSource random = RandomSource.create(seed);
            random.nextInt();
            // determine random index
            int index = random.nextInt(coralListSize);
            // determine blockstate
            Block mappedBlock = CORAL_LIST.get(blockInfo.state.getBlock()).get(index);
            BlockState replaced = copyMatchingProperties(blockInfo.state, mappedBlock.defaultBlockState());
            return new StructureTemplate.StructureBlockInfo(blockInfo.pos, replaced, blockInfo.nbt);
        }
        return blockInfo;
    }

    public static <BS extends StateHolder<?, BS>> BS copyMatchingProperties(BS from, BS to) {
        for (Property<?> property : from.getProperties())
            if (to.hasProperty(property))
                to = withGet(from, to, property);
        return to;
    }

    private static <S extends StateHolder<?, S>, T extends Comparable<T>> S withGet(S from, S to, Property<T> property) {
        return to.setValue(property, from.getValue(property));
    }
}
