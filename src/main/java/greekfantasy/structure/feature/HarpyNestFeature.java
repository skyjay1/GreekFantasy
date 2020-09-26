package greekfantasy.structure.feature;

import java.util.Random;

import com.mojang.serialization.Codec;

import greekfantasy.GFRegistry;
import greekfantasy.GreekFantasy;
import greekfantasy.entity.HarpyEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.RotatedPillarBlock;
import net.minecraft.block.material.Material;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.IWorldWriter;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.IWorldGenerationReader;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;

public class HarpyNestFeature extends Feature<NoFeatureConfig> {
  
  public static final String NAME = "harpy_nest_feature";

  public HarpyNestFeature(Codec<NoFeatureConfig> codec) {
    super(codec);
  }

  @Override
  public boolean func_241855_a(final ISeedReader reader, final ChunkGenerator chunkGenerator, final Random rand,
      final BlockPos blockPosIn, final NoFeatureConfig config) {
    // position for generation
    final BlockPos blockPos = blockPosIn.add( 4 + rand.nextInt(8), 0, 4 + rand.nextInt(8));
    final int y = reader.getHeight(Heightmap.Type.WORLD_SURFACE, blockPos).getY();
    BlockPos pos = new BlockPos(blockPos.getX(), y, blockPos.getZ());
    
    // conditions for generation
    if (!isDirtOrGrassAt(reader, pos.down())) {
      return false;
    }
    
    final BlockState log = getLogState(reader.getBiome(pos));
    final BlockState leaf = getLeavesState(log);
    
    int variant = 0;//rand.nextInt(2);
    GreekFantasy.LOGGER.info("Building harpy nest (variant " + variant + ") at " + pos);
    switch(variant) {
    case 0: return buildVariant0(reader, chunkGenerator, rand, log, leaf, pos);
    default:
      break;
    }
    
    
    
    return false;
  }
  
  public boolean buildVariant0(final ISeedReader world, final ChunkGenerator chunkGenerator, final Random rand, 
      final BlockState log, final BlockState leaf, final BlockPos pos) {
    // random height
    int rh = rand.nextInt(3) - 1;
    // build tree
    for(int i = 0; i < 14 + rh; i++) {
      set(world, pos.up(i), log);
    }
    set(world, pos.add(0, 4 + rh, 1), log);
    set(world, pos.add(0, 5 + rh, 2), log);
    // add leaves
    // lower canopy
    generateLeavesAround(world, rand, pos.up(3 + rh), leaf, 2);
    generateLeavesAround(world, rand, pos.up(4 + rh), leaf, 2);
    generateLeavesAround(world, rand, pos.up(5 + rh), leaf, 1);
    generateLeavesAround(world, rand, pos.add(0, 5 + rh, 2), leaf, 1);
    generateLeavesAround(world, rand, pos.up(6 + rh), leaf, 1);
    // upper canopy
    generateLeavesAround(world, rand, pos.up(9 + rh), leaf, 1);
    generateLeavesAround(world, rand, pos.up(10 + rh), leaf, 2);
    generateLeavesAround(world, rand, pos.up(11 + rh), leaf, 2);
    generateLeavesAround(world, rand, pos.up(12 + rh), leaf, 2);
    generateLeavesAround(world, rand, pos.up(13 + rh), leaf, 1);
    set(world, pos.up(14 + rh), leaf);
    // nest
    final BlockPos nestPos = pos.add(0, 6 + rh, 2);
    generateNestAround(world, nestPos, true);
    addHarpy(world, nestPos.up());
    if(rand.nextBoolean()) {
      addHarpy(world, nestPos.up());
    }
    
    return true;
  }
  
  // HELPER METHODS //
  
  protected static void set(IWorldWriter writer, BlockPos pos, BlockState state) {
    set(writer, pos, state, 2);
  }
  
  protected static void set(IWorldWriter writer, BlockPos pos, BlockState state, int flag) {
    writer.setBlockState(pos, state, flag);
  }

  protected static boolean isAirOrLeavesAt(IWorldGenerationReader reader, BlockPos pos) {
    return reader.hasBlockState(pos, state -> (state.isAir() || state.isIn(BlockTags.LEAVES)));
  }

  protected static boolean isDirtOrGrassAt(IWorldGenerationReader reader, BlockPos pos) {
    return reader.hasBlockState(pos, state -> {
      Block block = state.getBlock();
      return (isDirt(block) || block == Blocks.GRASS_BLOCK);
    });
  }

  protected static boolean isPlantAt(IWorldGenerationReader reader, BlockPos pos) {
    return reader.hasBlockState(pos, state -> {
      Material m = state.getMaterial();
      return (m == Material.TALL_PLANTS || m == Material.PLANTS);
    });
  }

  protected static boolean isReplaceableAt(IWorldGenerationReader reader, BlockPos pos) {
    return (isAirOrLeavesAt(reader, pos) || isPlantAt(reader, pos));
  }

  protected static BlockState getLogState(final Biome biome) {
    Block log = Blocks.OAK_LOG;
    Biome.Category category = biome.getCategory();
//    if (category == Biome.Category.FOREST) {
//        log = Blocks.BIRCH_LOG;
//    } else 
    if (category == Biome.Category.SAVANNA || category == Biome.Category.DESERT) {
        log = Blocks.ACACIA_LOG;
    } else if (category == Biome.Category.TAIGA || category == Biome.Category.ICY) {
        log = Blocks.SPRUCE_LOG;
    }
    return log.getDefaultState().with(RotatedPillarBlock.AXIS, Direction.Axis.Y);
  }
  
  protected static BlockState getLeavesState(final BlockState log) {
    if(log.isIn(BlockTags.ACACIA_LOGS)) {
      return Blocks.ACACIA_LEAVES.getDefaultState();
    } else if(log.isIn(BlockTags.DARK_OAK_LOGS)) {
      return Blocks.DARK_OAK_LEAVES.getDefaultState();
    } else if(log.isIn(BlockTags.SPRUCE_LOGS)) {
      return Blocks.SPRUCE_LEAVES.getDefaultState();
    } else if(log.isIn(BlockTags.BIRCH_LOGS)) {
      return Blocks.BIRCH_LEAVES.getDefaultState();
    } else if(log.isIn(BlockTags.JUNGLE_LOGS)) {
      return Blocks.JUNGLE_LEAVES.getDefaultState();
    } else {
      return Blocks.OAK_LEAVES.getDefaultState();
    }
  }
  
  protected static void generateLeavesAround(IWorldGenerationReader world, Random rand, BlockPos pos, BlockState leaf, int radius) {
    for(int x = -radius; x <= radius; x++) {
      for(int z = -radius; z <= radius; z++) {
        final BlockPos p = pos.add(x, 0, z);
        if(shouldGenerateLeaf(x, z, radius, rand) && isAirOrLeavesAt(world, p)) {
          set(world, p, leaf);
        }
      }
    }
  }

  protected static void generateNestAround(IWorldGenerationReader world, BlockPos pos, boolean forceReplace) {
    for(int x = -1; x <= 1; x++) {
      for(int z = -1; z <= 1; z++) {
        final BlockPos p = pos.add(x, 0, z);
        if(forceReplace || isAirOrLeavesAt(world, p)) {
          set(world, p.up(2), Blocks.AIR.getDefaultState());
          set(world, p.up(1), Blocks.AIR.getDefaultState());
          set(world, p, GFRegistry.NEST_BLOCK.getDefaultState(), 3);
        }
      }
    }
  }
  
  protected static void addHarpy(final ISeedReader world, final BlockPos pos) {
    // spawn a harpy
    final HarpyEntity entity = GFRegistry.HARPY_ENTITY.create(world.getWorld());
    entity.setLocationAndAngles(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, 0, 0);
    entity.enablePersistence();
    world.addEntity(entity);
  }
  
  protected static boolean shouldGenerateLeaf(final int x, final int z, final int radius, final Random rand) {
    boolean nonStrict = radius > 1;
    boolean valid = !(Math.abs(x) == radius && Math.abs(z) == radius);
    boolean isNonStrictCorner = nonStrict && (Math.abs(x) == (radius - 1) && Math.abs(z) == radius) || (Math.abs(x) == radius && Math.abs(z) == (radius - 1));
    if(valid && isNonStrictCorner) {
      valid = rand.nextBoolean();
    }
    return valid;
  }

}
