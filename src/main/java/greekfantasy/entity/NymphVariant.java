package greekfantasy.entity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public interface NymphVariant extends StringRepresentable {

    /**
     * @return The Tag Key for log blocks associated with this variant
     */
    TagKey<Block> getLogs();

    /**
     * @return The Tag Key for biomes associated with this variant
     */
    TagKey<Biome> getBiome();

    /**
     * @return A sapling associated with this variant
     */
    BlockState getSapling();

    /**
     * @return The entity death loot table for this variant
     */
    ResourceLocation getDeathLootTable();

    /**
     * @return The trade loot table for this variant
     */
    ResourceLocation getTradeLootTable();
}