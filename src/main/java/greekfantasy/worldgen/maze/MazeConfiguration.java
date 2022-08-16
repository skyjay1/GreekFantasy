package greekfantasy.worldgen.maze;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public class MazeConfiguration implements FeatureConfiguration {

    public static final Codec<MazeConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.DOUBLE.optionalFieldOf("probability", 1.0D).forGetter(MazeConfiguration::getProbability),
        Codec.DOUBLE.optionalFieldOf("room_chance", 0.5D).forGetter(MazeConfiguration::getRoomChance),
        Codec.INT.optionalFieldOf("piece_count_x", 20).forGetter(MazeConfiguration::getPieceCountX),
        Codec.INT.optionalFieldOf("piece_count_z", 20).forGetter(MazeConfiguration::getPieceCountZ)
    ).apply(instance, MazeConfiguration::new));

    public static final int MAX_COUNT = (128 / MazePiece.WIDTH) - 2;

    private final double probability;
    private final double roomChance;
    private final int pieceCountX;
    private final int pieceCountZ;

    public MazeConfiguration(double probability, double roomChance, int pieceCountX, int pieceCountZ) {
        this.probability = probability;
        this.roomChance = roomChance;
        this.pieceCountX = pieceCountX;
        this.pieceCountZ = pieceCountZ;
        if(pieceCountX < 2 || pieceCountX > MAX_COUNT) {
            throw new IllegalArgumentException("Error parsing maze configuration: piece_count_x must be between 2 and " + MAX_COUNT + ", inclusive.");
        }
        if(pieceCountZ < 2 || pieceCountZ > MAX_COUNT) {
            throw new IllegalArgumentException("Error parsing maze configuration: piece_count_z must be between 2 and " + MAX_COUNT + ", inclusive.");
        }
    }

    public double getProbability() {
        return probability;
    }

    public double getRoomChance() {
        return roomChance;
    }

    public int getPieceCountX() {
        return pieceCountX;
    }

    public int getPieceCountZ() {
        return pieceCountZ;
    }
}