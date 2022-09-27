package greekfantasy.worldgen.maze;

import com.google.common.collect.ImmutableMap;
import greekfantasy.GFRegistry;
import greekfantasy.GreekFantasy;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.templatesystem.AlwaysTrueTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.ProcessorRule;
import net.minecraft.world.level.levelgen.structure.templatesystem.ProtectedBlockProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.RandomBlockMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.apache.commons.lang3.tuple.ImmutablePair;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MazePiece extends StructurePiece {

    private static final String KEY_OPENINGS = "Openings";
    private static final String KEY_VARIANT = "Variant";
    private static final String KEY_TEMPLATE = "Template";

    public static final int WIDTH = 6;
    public static final int HEIGHT = 7;

    /**
     * This structure processor replaces some cretan stone brick and polished cretan stone brick with their cracked counterparts.
     */
    public static final StructureProcessor AGE_PROCESSOR = new RuleProcessor(List.of(
            new ProcessorRule(new RandomBlockMatchTest(GFRegistry.BlockReg.CRETAN_STONE_BRICK.get(), 0.44F),
                    AlwaysTrueTest.INSTANCE,
                    GFRegistry.BlockReg.CRACKED_CRETAN_STONE_BRICK.get().defaultBlockState()
            ),
            new ProcessorRule(new RandomBlockMatchTest(GFRegistry.BlockReg.POLISHED_CRETAN_STONE.get(), 0.38F),
                    AlwaysTrueTest.INSTANCE,
                    GFRegistry.BlockReg.CRACKED_POLISHED_CRETAN_STONE.get().defaultBlockState()
            )
    ));

    /**
     * This structure processor prevents replacement of blocks in the FEATURES_CANNOT_REPLACE block tag.
     */
    public static final StructureProcessor PROTECTED_PROCESSOR = new ProtectedBlockProcessor(BlockTags.FEATURES_CANNOT_REPLACE);

    /**
     * Contains the Variant and Direction for each configuration of openings.
     * Used to construct maze pieces with the correct variant.
     * Key=(north, east, south, west); Value=Pair(Variant, Direction)
     */
    public static Map<Vector4i<Boolean>, ImmutablePair<Variant, Direction>> openingsMap = new HashMap<>();

    static {
        add(false, false, false, false, Variant.NONE, Direction.NORTH);
        add(false, false, false, true, Variant.DEAD_END, Direction.EAST);
        add(false, false, true, false, Variant.DEAD_END, Direction.NORTH);
        add(false, false, true, true, Variant.CORNER, Direction.NORTH);
        add(false, true, false, false, Variant.DEAD_END, Direction.WEST);
        add(false, true, false, true, Variant.TWO_WAY, Direction.EAST);
        add(false, true, true, false, Variant.CORNER, Direction.WEST);
        add(false, true, true, true, Variant.THREE_WAY, Direction.NORTH);
        add(true, false, false, false, Variant.DEAD_END, Direction.SOUTH);
        add(true, false, false, true, Variant.CORNER, Direction.EAST);
        add(true, false, true, false, Variant.TWO_WAY, Direction.NORTH);
        add(true, false, true, true, Variant.THREE_WAY, Direction.EAST);
        add(true, true, false, false, Variant.CORNER, Direction.SOUTH);
        add(true, true, false, true, Variant.THREE_WAY, Direction.SOUTH);
        add(true, true, true, false, Variant.THREE_WAY, Direction.WEST);
        add(true, true, true, true, Variant.FOUR_WAY, Direction.NORTH);
    }

    /**
     * Adds an entry to the openings map
     * @param north true if there is an opening on the north
     * @param east true if there is an opening on the east
     * @param south true if there is an opening on the south
     * @param west true if there is an opening on the west
     * @param variant the variant for this configuration of openings
     * @param direction the direction for this configuration of openings
     */
    private static void add(boolean north, boolean east, boolean south, boolean west, Variant variant, Direction direction) {
        Vector4i<Boolean> vec = new Vector4i<Boolean>(north, east, south, west);
        openingsMap.put(vec, ImmutablePair.of(variant, direction));
    }

    private Vector4i<Boolean> openings;
    private Variant variant;
    private ResourceLocation template;

    /**
     * @param openings the north, east, south, and west openings of the piece, in that order
     * @param variant the piece variant
     * @param direction the piece orientation
     * @param depth the structure depth (not used)
     * @param boundingBox the piece bounding box
     */
    public MazePiece(Vector4i<Boolean> openings, Variant variant, Direction direction, int depth, BoundingBox boundingBox) {
        super(GFRegistry.StructureReg.MAZE_ROOM.get(), depth, boundingBox);
        this.openings = openings;
        this.variant = variant;
        this.setOrientation(direction);
    }

    /**
     * Reads the piece from NBT
     * @param tag the compound tag
     */
    public MazePiece(CompoundTag tag) {
        super(GFRegistry.StructureReg.MAZE_ROOM.get(), tag);
        this.variant = Variant.getByName(tag.getString(KEY_VARIANT));
        this.template = ResourceLocation.tryParse(tag.getString(KEY_TEMPLATE));
        ListTag wallTag = tag.getList(KEY_OPENINGS, Tag.TAG_INT);
        Vector4i<Boolean> vec = new Vector4i<>(false, false, false, false);
        if (wallTag.size() == 4) {
            int a = wallTag.getInt(0);
            int b = wallTag.getInt(1);
            int c = wallTag.getInt(2);
            int d = wallTag.getInt(3);
            vec = new Vector4i<>(a != 0, b != 0, c != 0, d != 0);
        }
        this.openings = vec;
    }

    /**
     * Creates a MazePiece with the given origin and indices
     * @param origin the origin block position
     * @param x the x index, usually positive. Used to calculate bounding box.
     * @param z the z index, usually positive. Used to calculate bounding box.
     * @return the constructed MazePiece with no openings, variant of NONE, and facing north
     */
    public static MazePiece create(Vec3i origin, int x, int z) {
        return create(origin, x, 0, z);
    }

    /**
     * Creates a MazePiece with the given origin and indices
     * @param origin the origin block position
     * @param x the x index, usually positive. Used to calculate bounding box.
     * @param y the y index, usually positive. Used to calculate bounding box.
     * @param z the z index, usually positive. Used to calculate bounding box.
     * @return the constructed MazePiece with no openings, variant of NONE, and facing north
     */
    public static MazePiece create(Vec3i origin, int x, int y, int z) {
        Vector4i<Boolean> vec = new Vector4i<>(false, false, false, false);
        Vec3i from = new Vec3i(origin.getX() + x * WIDTH, origin.getY() + y * HEIGHT, origin.getZ() + z * WIDTH);
        Vec3i to = new Vec3i(origin.getX() + (x + 1) * WIDTH - 1, origin.getY() + (y + 1) * HEIGHT, origin.getZ() + (z + 1) * WIDTH - 1);
        return new MazePiece(vec, Variant.NONE, Direction.NORTH, 0, BoundingBox.fromCorners(from, to));
    }

    /**
     * Directly sets the piece variant without updating the openings vector
     * @param variant the variant of the piece
     * @return the modified instance for chaining methods
     */
    public MazePiece withVariant(Variant variant) {
        this.variant = variant;
        return this;
    }

    /**
     * @param direction the orientation of the piece
     * @return the modified instance for chaining methods
     */
    public MazePiece withDirection(Direction direction) {
        setOrientation(direction);
        return this;
    }

    /**
     * Unused.
     * @param template The structure template resource ID
     * @return the modified instance for chaining methods
     */
    public MazePiece withTemplate(ResourceLocation template) {
        this.template = template;
        return this;
    }

    /**
     * Sets the openings and variant of this piece
     *
     * @param north true if there is an opening on the north side
     * @param east true if there is an opening on the east side
     * @param south true if there is an opening on the south side
     * @param west true if there is an opening on the west side
     * @return the modified instance for chaining methods
     * @see #withOpenings(Vector4i)
     */
    public MazePiece withOpenings(boolean north, boolean east, boolean south, boolean west) {
        return withOpenings(new Vector4i<>(north, east, south, west));
    }

    /**
     * Sets the openings and variant of this piece
     *
     * @param vec the openings
     * @return the modified instance for chaining methods
     * @see #withOpenings(boolean, boolean, boolean, boolean)
     */
    public MazePiece withOpenings(final Vector4i<Boolean> vec) {
        this.openings = vec;
        ImmutablePair<Variant, Direction> pair = openingsMap.get(vec);
        this.variant = pair.getLeft();
        this.setOrientation(pair.getRight());
        return this;
    }

    /**
     * If this piece is a dead end, calling this method will give it a chance to become a room instead
     *
     * @param random     the random source
     * @param roomChance the room chance
     * @return the modified instance for chaining methods
     */
    public MazePiece deadEndOrRoom(RandomSource random, float roomChance) {
        if (this.variant == Variant.DEAD_END && random.nextFloat() < roomChance) {
            return withVariant(Variant.ROOM);
        }
        return this;
    }

    @Override
    protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag tag) {
        tag.putString(KEY_VARIANT, this.variant.getSerializedName());
        if (this.template != null) {
            tag.putString(KEY_TEMPLATE, this.template.toString());
        }
    }

    /**
     * This method is responsible for placing blocks into the world.
     *
     * @param level the world gen level
     * @param manager the structure feature manager
     * @param chunkGenerator the chunk generator
     * @param random the random source
     * @param boundingBox the structure bounding box
     * @param chunkPos the structure chunk position
     * @param blockPos the structure block position
     */
    @Override
    public void postProcess(WorldGenLevel level, StructureManager manager, ChunkGenerator chunkGenerator, RandomSource random, BoundingBox boundingBox, ChunkPos chunkPos, BlockPos blockPos) {
        // ensure server exists
        if (null == level.getServer()) {
            GreekFantasy.LOGGER.debug("[Maze] Failed to generate maze piece, server not found");
            return;
        }
        // ensure template was loaded
        if (null == this.template) {
            GreekFantasy.LOGGER.debug("[Maze] No template defined for variant=" + variant + " at " + blockPos
                    + ". Did you forget to bake the piece?");
            return;
        }
        // load template
        Optional<StructureTemplate> oTemplateStructure = level.getServer().getStructureManager().get(this.template);
        if (!oTemplateStructure.isPresent()) {
            GreekFantasy.LOGGER.debug("[Maze] Failed to create structure template from " + this.template);
            return;
        }
		StructureTemplate structureTemplate = oTemplateStructure.get();
        // determine origin and pivot
        Direction adjustedOrientation = Optional.ofNullable(getOrientation()).orElse(Direction.NORTH);
        int offsetX = 0;
        int offsetZ = 0;
        switch (adjustedOrientation) {
            case NORTH:
                offsetX = -1;
                break;
            case SOUTH:
                offsetZ = -1;
                break;
            case EAST:
                offsetX = -1;
                offsetZ = -1;
                break;
            case WEST: default:
                break;
        }
        BlockPos origin = new BlockPos(getBoundingBox().minX() + offsetX + 1, getBoundingBox().minY(), getBoundingBox().minZ() + offsetZ + 1);
        BlockPos pivot = new BlockPos(structureTemplate.getSize().getX() / 2 - 1, 0, structureTemplate.getSize().getZ() / 2 - 1);
		// create placement settings
        StructurePlaceSettings placement = new StructurePlaceSettings()
                .setRotationPivot(pivot).setRotation(getRotation()).setMirror(getMirror()).setRandom(random)
                .setBoundingBox(getBoundingBox()).setFinalizeEntities(true).setKeepLiquids(false)
                .addProcessor(BlockIgnoreProcessor.STRUCTURE_BLOCK)
                .addProcessor(PROTECTED_PROCESSOR)
                .addProcessor(AGE_PROCESSOR);
        // place the template
        structureTemplate.placeInWorld(level, origin, origin, placement, random, Block.UPDATE_CLIENTS);
    }

    /**
     * @return the vector representing openings to the north, east, south, and west (in that order)
     */
    public Vector4i<Boolean> getOpenings() {
        return openings;
    }

    /**
     * @return the variant of this maze piece
     */
    public Variant getVariant() {
        return variant;
    }

    /**
     * @return the resource ID of the structure template
     */
    public ResourceLocation getTemplate() {
        return template;
    }

    /**
     * Rolls a random template ID based on the variant
     *
     * @param random the random source
     * @return the modified instance for chaining methods
     */
    public MazePiece bake(RandomSource random) {
        final WeightedTemplateList templatePool = GreekFantasy.getMazePiece(variant.getTemplatePool());
        WeightedTemplate weightedTemplate = templatePool.sample(random);
        if (weightedTemplate != null) {
            this.template = weightedTemplate.getLocation();
        }
        return this;
    }

    /**
     * Custom implementation of setOrientation that uses all four rotations and ignores mirror.
     * This is necessary for the NBT structure to be rotated and centered correctly in postProcess.
     * Note that the NBT structures are all facing East instead of North as normally expected by this method, this
     * is because they were built with respect to the positive x and z axes.
     * @param direction the piece orientation.
     */
    @Override
    public void setOrientation(@Nullable Direction direction) {
        this.orientation = direction;
        this.mirror = Mirror.NONE;
        if (direction == null) {
            this.rotation = Rotation.COUNTERCLOCKWISE_90;
        } else {
            switch (direction) {
                case SOUTH:
                    this.rotation = Rotation.CLOCKWISE_90;
                    break;
                case WEST:
                    this.rotation = Rotation.CLOCKWISE_180;
                    break;
                case EAST:
                    this.rotation = Rotation.NONE;
                    break;
                default:
                    this.rotation = Rotation.COUNTERCLOCKWISE_90;
                    break;
            }
        }
    }

    /**
     * The Maze Piece variant, used to determine the NBT structure template to use when generating the piece.
     */
    public static enum Variant implements StringRepresentable {
        NONE("none"),
        DEAD_END("dead_end"),
        ROOM("room"),
        CORNER("corner"),
        TWO_WAY("two_way"),
        THREE_WAY("three_way"),
        FOUR_WAY("four_way"),
        BOSS_ROOM("boss_corner"),
        BOSS_ROOM_ENTRANCE("boss_corner_entrance"),
        LOWER_ENTRANCE("lower_entrance"),
        UPPER_ENTRANCE("upper_entrance"),
        UPPER_STAIRWAY("upper_stairway"), // unused
        STAIRWAY("stairway");

        public static final Map<String, Variant> NAME_MAP = ImmutableMap.copyOf(Arrays.stream(values())
                .collect(Collectors.<Variant, String, Variant>toMap(Variant::getSerializedName, Function.identity())));

        private final String name;
        private final ResourceLocation templatePool;

        private Variant(String name) {
            this.name = name;
            this.templatePool = new ResourceLocation(GreekFantasy.MODID, name);
        }

        /**
         * @return the ResourceLocation ID of the {@link WeightedTemplateList} for this variant
         */
        public ResourceLocation getTemplatePool() {
            return templatePool;
        }

        /**
         * @param name the variant name
         * @return the Variant for the given name, or NONE
         */
        public static Variant getByName(final String name) {
            return NAME_MAP.getOrDefault(name, NONE);
        }

        @Override
        public String getSerializedName() {
            return name;
        }
    }
}
