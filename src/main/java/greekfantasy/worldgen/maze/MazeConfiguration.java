package greekfantasy.worldgen.maze;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public class MazeConfiguration implements FeatureConfiguration {

    public static final Codec<MazeConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.DOUBLE.optionalFieldOf("probability", 1.0D).forGetter(MazeConfiguration::getProbability),
        Codec.DOUBLE.optionalFieldOf("room_chance", 0.5D).forGetter(MazeConfiguration::getRoomChance),
        Codec.INT.optionalFieldOf("piece_count_x", 20).forGetter(MazeConfiguration::getPieceCountX),
        Codec.INT.optionalFieldOf("piece_count_z", 20).forGetter(MazeConfiguration::getPieceCountZ),
        Codec.DOUBLE.optionalFieldOf("random_connection_ratio", 0.0D).forGetter(MazeConfiguration::getRandomConnectionRatio)
    ).apply(instance, MazeConfiguration::new));

    /**
     * The maximum number of pieces along the x or z direction, calculated by maximum structure size and piece size.
     */
    public static final int MAX_COUNT = (128 / MazePiece.WIDTH) - 2;

    private final double probability;
    private final double roomChance;
    private final int pieceCountX;
    private final int pieceCountZ;
    private final double randomConnectionRatio;

    public MazeConfiguration(double probability, double roomChance, int pieceCountX, int pieceCountZ, double randomConnectionRatio) {
        this.probability = probability;
        this.roomChance = roomChance;
        this.pieceCountX = pieceCountX;
        this.pieceCountZ = pieceCountZ;
        this.randomConnectionRatio = randomConnectionRatio;
        if(pieceCountX < 2 || pieceCountX > MAX_COUNT) {
            throw new IllegalArgumentException("Error parsing maze configuration: piece_count_x must be between 2 and " + MAX_COUNT + ", inclusive.");
        }
        if(pieceCountZ < 2 || pieceCountZ > MAX_COUNT) {
            throw new IllegalArgumentException("Error parsing maze configuration: piece_count_z must be between 2 and " + MAX_COUNT + ", inclusive.");
        }
    }

    /**
     * @return the probability that the structure will spawn at an otherwise viable location
     */
    public double getProbability() {
        return probability;
    }

    /**
     * @return the probability that a dead end will be a room instead
     */
    public double getRoomChance() {
        return roomChance;
    }

    /**
     * @return the number of pieces along the x direction
     */
    public int getPieceCountX() {
        return pieceCountX;
    }

    /**
     * @return the number of pieces along the z direction
     */
    public int getPieceCountZ() {
        return pieceCountZ;
    }

    /**
     * @return the percent of the maze that has random connections
     */
    public double getRandomConnectionRatio() {
        return randomConnectionRatio;
    }
}