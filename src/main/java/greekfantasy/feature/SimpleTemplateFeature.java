package greekfantasy.feature;

import java.util.Random;

import com.mojang.serialization.Codec;

import greekfantasy.GreekFantasy;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.IWorldGenerationReader;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;

public abstract class SimpleTemplateFeature extends Feature<NoFeatureConfig> {

  public SimpleTemplateFeature(final Codec<NoFeatureConfig> codec) {
    super(codec);
  }
  
  protected abstract ResourceLocation getStructure(final Random rand);
  
  protected boolean isValidPosition(final ISeedReader reader, final BlockPos pos, final BlockPos size, final Rotation r) {
    final BlockPos c1 = pos;
//    final BlockPos c2 = pos.add(new BlockPos(-size.getX(), 0, 0).rotate(r));
//    final BlockPos c3 = pos.add(new BlockPos(0, 0, -size.getZ()).rotate(r));
    final BlockPos c4 = pos.add(new BlockPos(-size.getX(), 0, -size.getZ()).rotate(r));
    return isValidPosition(reader, c1) && isValidPosition(reader, c4);
  }
  
  protected boolean isValidPosition(final ISeedReader reader, final BlockPos pos) {
    return pos.getY() > 3 && reader.getBlockState(pos).isSolid() && isReplaceableAt(reader, pos.up(1));
  }
  
  public static boolean isPlantAt(IWorldGenerationReader reader, BlockPos pos) {
    return reader.hasBlockState(pos, state -> {
      Material m = state.getMaterial();
      return (m == Material.TALL_PLANTS || m == Material.PLANTS);
    });
  }
  
  protected static boolean isReplaceableAt(IWorldGenerationReader reader, BlockPos pos) {
    return (isAirAt(reader, pos) || isPlantAt(reader, pos));
  }
  
  public static BlockPos getRandomPosition(final ISeedReader reader, BlockPos origin, final Random rand, int down) {
    final BlockPos blockPos = origin.add(4 + rand.nextInt(8), 0, 4 + rand.nextInt(8));
    return getHeightPos(reader, blockPos);
  }
  
  public static BlockPos getHeightPos(final ISeedReader world, final BlockPos original) {
    int y = world.getHeight(Heightmap.Type.WORLD_SURFACE, original).getY();
    final BlockPos pos = new BlockPos(original.getX(), y, original.getZ());
    return world.getBlockState(pos).matchesBlock(Blocks.SNOW) ? pos.down(2) : pos.down();
  }
  
  public static void fillBelow(final ISeedReader world, final BlockPos origin, final BlockPos size, final Rotation rotation, final Block[] blocks) {
    BlockPos tmp;
    BlockPos pos;
    Block block;
    for(int dx = 0, lx = size.getX(); dx < lx; dx++) {
      for(int dz = 0, lz = size.getZ(); dz < lz; dz++) {
        tmp = new BlockPos(dx, 0, dz).rotate(rotation);
        pos = origin.add(tmp);
        // set blocks below this one to the provided block(s)
        if(world.chunkExists(pos.getX() >> 4, pos.getZ() >> 4) && world.getBlockState(pos.up()).isSolid()) {
          while(pos.getY() > 0 && isReplaceableAt(world, pos)) {
            block = blocks[world.getRandom().nextInt(blocks.length)];
            world.setBlockState(pos, block.getDefaultState(), 2);
            pos = pos.down();
          }
        }
      }
    }
  }
  
  public static boolean isValidDimension(final ISeedReader world) {
    String dimName = world.getWorld().getDimensionKey().getLocation().toString();
    return GreekFantasy.CONFIG.IS_FEATURES_WHITELIST.get() == GreekFantasy.CONFIG.FEATURES_DIMENSION_WHITELIST.get().contains(dimName);
  }
}