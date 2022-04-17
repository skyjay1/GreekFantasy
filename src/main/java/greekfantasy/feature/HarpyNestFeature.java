package greekfantasy.feature;

import java.util.Objects;
import java.util.Optional;
import java.util.Random;

import com.mojang.serialization.Codec;

import greekfantasy.GFRegistry;
import greekfantasy.entity.HarpyEntity;
import greekfantasy.util.BiomeHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.RotatedPillarBlock;
import net.minecraft.block.material.Material;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.IWorldWriter;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
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
  public boolean place(final ISeedReader reader, final ChunkGenerator chunkGenerator, final Random rand,
      final BlockPos blockPosIn, final NoFeatureConfig config) {
    // check dimension from config
    if(!SimpleTemplateFeature.isValidDimension(reader)) {
      return false;
    }
    // position for generation
    final BlockPos blockPos = blockPosIn.offset( 4 + rand.nextInt(8), 0, 4 + rand.nextInt(8));
    final int y = reader.getHeightmapPos(Heightmap.Type.WORLD_SURFACE, blockPos).getY();
    BlockPos pos = new BlockPos(blockPos.getX(), y, blockPos.getZ());
    
    // conditions for generation
    if (!isDirtOrGrassAt(reader, pos.below())) {
      return false;
    }
    
    final Optional<RegistryKey<Biome>> biome = reader.getLevel().getBiomeName(pos);
    final boolean isDesert = Objects.equals(biome, Optional.of(Biomes.DESERT)) || Objects.equals(biome, Optional.of(Biomes.DESERT_HILLS));
    final BlockState log = getLogState(biome);
    final BlockState leaf = isDesert ? Blocks.AIR.defaultBlockState() : getLeavesState(log).setValue(LeavesBlock.DISTANCE, Integer.valueOf(3));
    
    final int variant = rand.nextInt(2);
    final Rotation rotation = Rotation.getRandom(rand);
    switch(variant) {
    default:
    case 0: return buildVariant0(reader, chunkGenerator, rand, log, leaf, pos, rotation);
    case 1: return buildVariant1(reader, chunkGenerator, rand, log, leaf, pos, rotation);
    }
  }
  
  public boolean buildVariant0(final ISeedReader world, final ChunkGenerator chunkGenerator, final Random rand, 
      final BlockState log, final BlockState leaf, final BlockPos pos, final Rotation rot) {
    // random height
    int rh = rand.nextInt(3) - 1;
    // build tree
    for(int i = 0; i < 14 + rh; i++) {
      set(world, pos.above(i), log);
    }
    set(world, rot(pos.above(4 + rh), 0, 1, rot), log);
    set(world, rot(pos.above(5 + rh), 0, 2, rot), log);
    // add leaves
    if(leaf.getBlock() != Blocks.AIR) {
      // lower canopy
      generateLeavesAround(world, rand, pos.above(3 + rh), leaf, 2);
      generateLeavesAround(world, rand, pos.above(4 + rh), leaf, 2);
      generateLeavesAround(world, rand, pos.above(5 + rh), leaf, 1);
      generateLeavesAround(world, rand, rot(pos.above(5 + rh), 0, 1, rot), leaf, 1);
      generateLeavesAround(world, rand, rot(pos.above(5 + rh), 0, 2, rot), leaf, 1);
      generateLeavesAround(world, rand, pos.above(6 + rh), leaf, 1);
      // upper canopy
      generateLeavesAround(world, rand, pos.above(9 + rh), leaf, 1);
      generateLeavesAround(world, rand, pos.above(10 + rh), leaf, 2);
      generateLeavesAround(world, rand, pos.above(11 + rh), leaf, 2);
      generateLeavesAround(world, rand, pos.above(12 + rh), leaf, 2);
      generateLeavesAround(world, rand, pos.above(13 + rh), leaf, 1);
      set(world, pos.above(14 + rh), leaf);
    }
    // nest
    final BlockPos nestPos = pos.offset(new BlockPos(0, 6 + rh, 2).rotate(rot));
    generateNestAround(world, nestPos, true);
    addHarpy(world, rand, nestPos.above());
    if(rand.nextBoolean()) {
      addHarpy(world, rand, nestPos.above());
    }
    
    return true;
  }
  
  public boolean buildVariant1(final ISeedReader world, final ChunkGenerator chunkGenerator, final Random rand, 
      final BlockState log, final BlockState leaf, final BlockPos pos, final Rotation rot) {
    // random height
    int rh = rand.nextInt(3) - 1;
    // build tree
    for(int i = 0; i < 6 + rh; i++) {
      set(world, pos.above(i), log);
    }
    // -z branch
    set(world, rot(pos.above(4 + rh), 0, -1, rot), log);
    set(world, rot(pos.above(5 + rh), 0, -2, rot), log);
    // +z branch
    set(world, rot(pos.above(4 + rh), 0, 1, rot), log);
    set(world, rot(pos.above(5 + rh), 0, 2, rot), log);
    // -x branch
    set(world, rot(pos.above(4 + rh), -1, 0, rot), log);
    set(world, rot(pos.above(5 + rh), -2, 0, rot), log);
    set(world, rot(pos.above(5 + rh), -2, -1, rot), log);
    set(world, rot(pos.above(6 + rh), -3, 0, rot), log);
    // +x branch
    set(world, rot(pos.above(5 + rh), 1, 0, rot), log);
    set(world, rot(pos.above(5 + rh), 2, 0, rot), log);
    // add leaves
    if(leaf.getBlock() != Blocks.AIR) {
      generateLeavesAround(world, rand, pos.above(3 + rh), leaf, 1);
      generateLeavesAround(world, rand, pos.above(4 + rh), leaf, 2);
      generateLeavesAround(world, rand, pos.above(5 + rh), leaf, 3);
      // -x side
      generateLeavesAround(world, rand, rot(pos.above(5 + rh), -2, 0, rot), leaf, 1);
      generateLeavesAround(world, rand, rot(pos.above(6 + rh), -3, 0, rot), leaf, 1);
      generateLeavesAround(world, rand, rot(pos.above(6 + rh), -2, 1, rot), leaf, 1);
      generateLeavesAround(world, rand, rot(pos.above(7 + rh), -2, 1, rot), leaf, 1);
      generateLeavesAround(world, rand, rot(pos.above(7 + rh), -2, 0, rot), leaf, 1);
      generateLeavesAround(world, rand, rot(pos.above(6 + rh), -2, -1, rot), leaf, 1);
      // +x side
      generateLeavesAround(world, rand, rot(pos.above(5 + rh), 2, 0, rot), leaf, 1);
      generateLeavesAround(world, rand, rot(pos.above(6 + rh), 2, 0, rot), leaf, 1);
      // -z side
      generateLeavesAround(world, rand, rot(pos.above(6 + rh), 0, -2, rot), leaf, 1);
      generateLeavesAround(world, rand, rot(pos.above(7 + rh), 0, -2, rot), leaf, 1);
      // +z side
      generateLeavesAround(world, rand, rot(pos.above(6 + rh), 0, 2, rot), leaf, 1);
      generateLeavesAround(world, rand, rot(pos.above(7 + rh), 0, 2, rot), leaf, 1);
    }
    // nest
    final BlockPos nestPos = pos.above(6 + rh);
    generateNestAround(world, nestPos, true);
    addHarpy(world, rand, nestPos.above());
    if(rand.nextBoolean()) {
      addHarpy(world, rand, nestPos.above());
    }
    // a few more leaves (imperfect nest)
    set(world, rot(nestPos, -1, 1, rot), leaf, 3);
    set(world, rot(nestPos.above(1), -1, 1, rot), leaf);
    set(world, rot(nestPos.above(2), -1, 1, rot), leaf);
    
    return true;
  }
  
  // HELPER METHODS //
  
  protected static void set(IWorldWriter writer, BlockPos pos, BlockState state) {
    set(writer, pos, state, 2);
  }
  
  protected static void set(IWorldWriter writer, BlockPos pos, BlockState state, int flag) {
    writer.setBlock(pos, state, flag);
  }
  
  protected static BlockPos rot(final BlockPos origin, final int x, final int z, final Rotation rot) {
    return origin.offset(new BlockPos(x, 0, z).rotate(rot));
  }

  protected static boolean isAirOrLeavesAt(IWorldGenerationReader reader, BlockPos pos) {
    return reader.isStateAtPosition(pos, state -> (state.isAir() || state.is(BlockTags.LEAVES)));
  }

  protected static boolean isDirtOrGrassAt(IWorldGenerationReader reader, BlockPos pos) {
    return reader.isStateAtPosition(pos, state -> {
      Block block = state.getBlock();
      return (isDirt(block) || block == Blocks.GRASS_BLOCK);
    });
  }

  protected static boolean isPlantAt(IWorldGenerationReader reader, BlockPos pos) {
    return reader.isStateAtPosition(pos, state -> {
      Material m = state.getMaterial();
      return (m == Material.REPLACEABLE_PLANT || m == Material.PLANT);
    });
  }

  protected static boolean isReplaceableAt(IWorldGenerationReader reader, BlockPos pos) {
    return (isAirOrLeavesAt(reader, pos) || isPlantAt(reader, pos));
  }

  protected static BlockState getLogState(final Optional<RegistryKey<Biome>> biome) {
    return BiomeHelper.getLogForBiome(biome).setValue(RotatedPillarBlock.AXIS, Direction.Axis.Y);
  }
  
  protected static BlockState getLeavesState(final BlockState log) {
    if(log.is(BlockTags.ACACIA_LOGS)) {
      return Blocks.ACACIA_LEAVES.defaultBlockState();
    } else if(log.is(BlockTags.DARK_OAK_LOGS)) {
      return Blocks.DARK_OAK_LEAVES.defaultBlockState();
    } else if(log.is(BlockTags.SPRUCE_LOGS)) {
      return Blocks.SPRUCE_LEAVES.defaultBlockState();
    } else if(log.is(BlockTags.BIRCH_LOGS)) {
      return Blocks.BIRCH_LEAVES.defaultBlockState();
    } else if(log.is(BlockTags.JUNGLE_LOGS)) {
      return Blocks.JUNGLE_LEAVES.defaultBlockState();
    } else if(log.is(BiomeHelper.getOliveLogs())) {
      return GFRegistry.OLIVE_LEAVES.defaultBlockState();
    } else {
      return Blocks.OAK_LEAVES.defaultBlockState();
    }
  }
  
  protected static void generateLeavesAround(IWorldGenerationReader world, Random rand, BlockPos pos, BlockState leaf, int radius) {
    for(int x = -radius; x <= radius; x++) {
      for(int z = -radius; z <= radius; z++) {
        final BlockPos p = pos.offset(x, 0, z);
        if(shouldGenerateLeaf(x, z, radius, rand) && isAirOrLeavesAt(world, p)) {
          set(world, p, leaf);
        }
      }
    }
  }

  protected static void generateNestAround(IWorldGenerationReader world, BlockPos pos, boolean forceReplace) {
    for(int x = -1; x <= 1; x++) {
      for(int z = -1; z <= 1; z++) {
        final BlockPos p = pos.offset(x, 0, z);
        if(forceReplace || isAirOrLeavesAt(world, p)) {
          set(world, p.above(2), Blocks.AIR.defaultBlockState());
          set(world, p.above(1), Blocks.AIR.defaultBlockState());
          set(world, p, GFRegistry.NEST_BLOCK.defaultBlockState(), 3);
        }
      }
    }
  }
  
  protected static void addHarpy(final ISeedReader world, final Random rand, final BlockPos pos) {
    // spawn a harpy
    final HarpyEntity entity = GFRegistry.HARPY_ENTITY.create(world.getLevel());
    entity.moveTo(pos.getX() + rand.nextDouble(), pos.getY() + 0.5D, pos.getZ() + rand.nextDouble(), 0, 0);
    entity.setPersistenceRequired();
    world.addFreshEntity(entity);
  }
  
  protected static boolean shouldGenerateLeaf(final int x, final int z, final int radius, final Random rand) {
    boolean valid = !(Math.abs(x) == radius && Math.abs(z) == radius);
    boolean isNonStrictCorner = radius > 1 && (Math.abs(x) == (radius - 1) && Math.abs(z) == radius) || (Math.abs(x) == radius && Math.abs(z) == (radius - 1));
    boolean isNonStrictCorner2 = radius > 2 && (Math.abs(x) == (radius - 2) && Math.abs(z) == (radius - 1)) || (Math.abs(x) == (radius - 1) && Math.abs(z) == (radius - 2));
    // cut off almost-corners some of the time
    if(valid && isNonStrictCorner2) {
      valid = rand.nextInt(3) == 0;
    }
    // cut off corners most of the time
    if(valid && isNonStrictCorner) {
      valid = rand.nextInt(4) == 0;
    }
    return valid;
  }

}
