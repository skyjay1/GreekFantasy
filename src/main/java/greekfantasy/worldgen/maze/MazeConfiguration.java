package greekfantasy.worldgen.maze;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public class MazeConfiguration implements FeatureConfiguration {

    public static final Codec<MazeConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.DOUBLE.optionalFieldOf("probability", 1.0D).forGetter(MazeConfiguration::getProbability),
        Codec.INT.optionalFieldOf("piece_count_x", 20).forGetter(MazeConfiguration::getPieceCountX),
        Codec.INT.optionalFieldOf("piece_count_z", 20).forGetter(MazeConfiguration::getPieceCountZ)
    ).apply(instance, MazeConfiguration::new));

    private final double probability;
    private final int pieceCountX;
    private final int pieceCountZ;

    public MazeConfiguration(double probability, int pieceCountX, int pieceCountZ) {
        this.probability = probability;
        this.pieceCountX = pieceCountX;
        this.pieceCountZ = pieceCountZ;
    }

    public double getProbability() {
        return probability;
    }

    public int getPieceCountX() {
        return pieceCountX;
    }

    public int getPieceCountZ() {
        return pieceCountZ;
    }
}