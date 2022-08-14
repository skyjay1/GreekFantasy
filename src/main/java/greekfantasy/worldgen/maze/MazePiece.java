package greekfantasy.worldgen.maze;

import com.google.common.collect.ImmutableMap;
import greekfantasy.GFRegistry;
import greekfantasy.GreekFantasy;
import greekfantasy.entity.Dryad;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.Vec3i;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.RandomSource;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.TemplateStructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.templatesystem.AlwaysTrueTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.ProcessorRule;
import net.minecraft.world.level.levelgen.structure.templatesystem.RandomBlockMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

public class MazePiece extends StructurePiece {

	private static final String KEY_WALLS = "Walls";
	private static final String KEY_VARIANT = "Variant";
	private static final String KEY_TEMPLATE = "Template";

	private static final ResourceLocation processorListId = new ResourceLocation(GreekFantasy.MODID, "maze");
	public static final int SIZE = 8;
	public static final int HEIGHT = 7;

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
	
	public static Map<Vector4i<Boolean>, ImmutablePair<Variant, Direction>> map = new HashMap<>();
	static {
		add(false, false, false, false, Variant.NONE, Direction.WEST);
		add(false, false, false, true, Variant.DEAD_END, Direction.NORTH);
		add(false, false, true, false, Variant.DEAD_END, Direction.WEST);
		add(false, false, true, true, Variant.CORNER, Direction.SOUTH);
		add(false, true, false, false, Variant.DEAD_END, Direction.SOUTH);
		add(false, true, false, true, Variant.TWO_WAY, Direction.NORTH);
		add(false, true, true, false, Variant.CORNER, Direction.EAST);
		add(false, true, true, true, Variant.THREE_WAY, Direction.WEST);
		add(true, false, false, false, Variant.DEAD_END, Direction.EAST);
		add(true, false, false, true, Variant.CORNER, Direction.NORTH);
		add(true, false, true, false, Variant.TWO_WAY, Direction.WEST);
		add(true, false, true, true, Variant.THREE_WAY, Direction.NORTH);
		add(true, true, false, false, Variant.CORNER, Direction.WEST);
		add(true, true, false, true, Variant.THREE_WAY, Direction.EAST);
		add(true, true, true, false, Variant.THREE_WAY, Direction.SOUTH);
		add(true, true, true, true, Variant.FOUR_WAY, Direction.WEST);
	}

	private static void add(boolean north, boolean east, boolean south, boolean west, Variant variant, Direction direction) {
		Vector4i<Boolean> vec = new Vector4i<Boolean>(north, east, south, west);
		map.put(vec, ImmutablePair.of(variant, direction));
	}

	private Vector4i<Boolean> walls;
	private Variant variant;
	private ResourceLocation template;
	
	public MazePiece(Vector4i<Boolean> wallsIn, Variant variantIn, Direction direction, int depth, BoundingBox boundingBox) {
		super(GFRegistry.StructureFeatureReg.MAZE_ROOM, depth, boundingBox);
		this.walls = wallsIn;
		this.variant = variantIn;
		this.setOrientation(direction);
	}

	public MazePiece(CompoundTag tag) {
		super(GFRegistry.StructureFeatureReg.MAZE_ROOM, tag);
		this.variant = Variant.getByName(tag.getString(KEY_VARIANT));
		this.template = ResourceLocation.tryParse(tag.getString(KEY_TEMPLATE));
		ListTag wallTag = tag.getList(KEY_WALLS, Tag.TAG_INT);
		Vector4i<Boolean> vec = new Vector4i<>(false, false, false, false);
		if(wallTag.size() == 4) {
			int a = wallTag.getInt(0);
			int b = wallTag.getInt(1);
			int c = wallTag.getInt(2);
			int d = wallTag.getInt(3);
			vec = new Vector4i<>(a != 0, b != 0, c != 0, d != 0);
		}
		this.walls = vec;
	}

	public static MazePiece create(Vec3i origin, int x, int z) {
		Vector4i<Boolean> vec = new Vector4i<>(false, false, false, false);
		Vec3i from = new Vec3i(origin.getX() + x * SIZE, origin.getY(), origin.getZ() + z * SIZE);
		Vec3i to = new Vec3i(origin.getX() + (x + 1) * SIZE - 1, origin.getY() + HEIGHT, origin.getZ() + (z + 1) * SIZE - 1);
		return new MazePiece(vec, Variant.NONE, Direction.NORTH, 0, BoundingBox.fromCorners(from, to));
	}

	public MazePiece withVariant(Variant variant) {
		this.variant = variant;
		return this;
	}

	public MazePiece withDirection(Direction direction) {
		setOrientation(direction);
		return this;
	}

	public MazePiece withTemplate(ResourceLocation template) {
		this.template = template;
		return this;
	}

	/**
	 * Sets the walls and variant of this piece
	 * @param north true if there are north walls
	 * @param east true if there are east walls
	 * @param south true if there are south walls
	 * @param west true if there are west walls
	 * @return the modified instance for chaining methods
	 */
	public MazePiece withWalls(boolean north, boolean east, boolean south, boolean west) {
		return withWalls(new Vector4i<>(north, east, south, west));
	}

	/**
	 * Sets the walls and variant of this piece
	 * @param vec the walls
	 * @return the modified instance for chaining methods
	 */
	public MazePiece withWalls(final Vector4i<Boolean> vec) {
		this.walls = vec;
		ImmutablePair<Variant, Direction> pair = map.get(vec);
		this.variant = pair.getLeft();
		this.setOrientation(pair.getRight());
		return this;
	}

	/**
	 * If this piece is a dead end, calling this method will give it a chance to become a room instead
	 * @param random the random source
	 * @param roomChance the room chance
	 * @return the modified instance for chaining methods
	 */
	public MazePiece deadEndOrRoom(RandomSource random, float roomChance) {
		if(this.variant == Variant.DEAD_END && random.nextFloat() < roomChance) {
			return withVariant(Variant.ROOM);
		}
		return this;
	}


	@Override
	protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag tag) {
		tag.putString(KEY_VARIANT, this.variant.getSerializedName());
		if(this.template != null) {
			tag.putString(KEY_TEMPLATE, this.template.toString());
		}
	}

	@Override
	public void postProcess(WorldGenLevel level, StructureFeatureManager manager, ChunkGenerator chunkGenerator, Random random, BoundingBox boundingBox, ChunkPos chunkPos, BlockPos blockPos) {
		// ensure server exists
		if(null == level.getServer()) {
			GreekFantasy.LOGGER.debug("[Maze] Failed to generate maze piece, server not found");
			return;
		}
		// ensure template was loaded
		if(null == this.template) {
			GreekFantasy.LOGGER.debug("[Maze] No template defined for variant=" + variant + " at " + blockPos);
			return;
		}
		// load template
		Optional<StructureTemplate> oTemplateStructure = level.getServer().getStructureManager().get(this.template);
		if(!oTemplateStructure.isPresent()) {
			GreekFantasy.LOGGER.debug("[Maze] Failed to create structure template from " + this.template);
			return;
		}
		// create placement settings
		BlockPos origin = new BlockPos(getBoundingBox().minX(), getBoundingBox().minY(), getBoundingBox().minZ());
		switch (getOrientation()) {
			case NORTH: default:
				break;
			case SOUTH:
				origin = origin.relative(Direction.SOUTH, SIZE - 1);
				break;
			case EAST:
				origin = origin.relative(Direction.EAST, SIZE - 1);
				break;
			case WEST:
				break;
		}



		StructurePlaceSettings placement = new StructurePlaceSettings()
				.setRotation(getRotation()).setMirror(getMirror()).setRandom(random)
				.setBoundingBox(getBoundingBox().inflatedBy(8))
				.addProcessor(BlockIgnoreProcessor.STRUCTURE_BLOCK)
				.addProcessor(new BlockIgnoreProcessor(List.of(RegistryObject.create(new ResourceLocation(GreekFantasy.MODID, "cretan_stone"), ForgeRegistries.BLOCKS).get())))
				.addProcessor(AGE_PROCESSOR);
		// place the template
		GreekFantasy.LOGGER.debug("[Maze] Placed " + variant + " at " + origin);
		oTemplateStructure.get().placeInWorld(level, origin, origin, placement, random, Block.UPDATE_CLIENTS);
	}

	public Vector4i<Boolean> getWalls() {
		return walls;
	}

	public Variant getVariant() {
		return variant;
	}

	public ResourceLocation getTemplate() {
		return template;
	}

	/**
	 * Rolls a random template ID based on the variant
	 * @param random the random source
	 * @return the modified instance for chaining methods
	 */
	public MazePiece bake(RandomSource random) {
		final WeightedTemplateList templatePool = GreekFantasy.WEIGHTED_TEMPLATES.get(variant.getTemplatePool()).orElse(WeightedTemplateList.EMPTY);
		WeightedTemplate weightedTemplate = templatePool.sample(random);
		if(weightedTemplate != null) {
			this.template = weightedTemplate.getLocation();
		}
		return this;
	}

	public static enum Variant implements StringRepresentable {
		NONE("none"),
		ROOM("room"),
		BOSS_ROOM("boss_corner"),
		BOSS_ROOM_ENTRANCE("boss_corner_entrance"),
		DEAD_END("dead_end"),
		TWO_WAY("two_way"),
		CORNER("corner"),
		THREE_WAY("three_way"),
		FOUR_WAY("four_way");

		public static ImmutableMap<String, Variant> NAME_MAP = ImmutableMap.<String, Variant>builder()
				.put(NONE.name, NONE).put(ROOM.name, ROOM).put(BOSS_ROOM.name, BOSS_ROOM)
				.put(BOSS_ROOM_ENTRANCE.name, BOSS_ROOM_ENTRANCE).put(DEAD_END.name, DEAD_END)
				.put(TWO_WAY.name, TWO_WAY).put(CORNER.name, CORNER).put(THREE_WAY.name, THREE_WAY)
				.put(FOUR_WAY.name, FOUR_WAY)
				.build();
		
		private final String name;
		private final ResourceLocation templatePool;
		
		private Variant(String name) {
			this.name = name;
			this.templatePool = new ResourceLocation(GreekFantasy.MODID, name);
		}

		public ResourceLocation getTemplatePool() {
			return templatePool;
		}

		public static Variant getByName(final String name) {
			return NAME_MAP.getOrDefault(name, NONE);
		}

		@Override
		public String getSerializedName() {
			return name;
		}
	}
}
