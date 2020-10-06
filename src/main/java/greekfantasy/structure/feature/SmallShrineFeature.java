package greekfantasy.structure.feature;

import java.util.Random;

import com.mojang.serialization.Codec;

import greekfantasy.GreekFantasy;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.template.BlockIgnoreStructureProcessor;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.TemplateManager;

public class SmallShrineFeature extends Feature<NoFeatureConfig> {
  
  private static final ResourceLocation STRUCTURE_LIMESTONE = new ResourceLocation(GreekFantasy.MODID, "small_limestone_shrine");
  private static final ResourceLocation STRUCTURE_MARBLE = new ResourceLocation(GreekFantasy.MODID, "small_marble_shrine");

  public SmallShrineFeature(final Codec<NoFeatureConfig> codec) {
    super(codec);
  }

  @Override
  public boolean func_241855_a(final ISeedReader reader, final ChunkGenerator chunkGenerator, final Random rand,
      final BlockPos blockPosIn, final NoFeatureConfig config) {
    // template loading
    final ResourceLocation structure = rand.nextBoolean() ? STRUCTURE_LIMESTONE : STRUCTURE_MARBLE;
    final TemplateManager manager = reader.getWorld().getStructureTemplateManager();
    final Template template = manager.getTemplateDefaulted(structure);
    
    // position for generation
    final BlockPos blockPos = blockPosIn.add( 4 + rand.nextInt(8), 0, 4 + rand.nextInt(8));
    final int y = reader.getHeight(Heightmap.Type.WORLD_SURFACE, blockPos).getY();
    BlockPos pos = new BlockPos(blockPos.getX(), y - 1, blockPos.getZ());
    
    // check for valid position
    if(pos.getY() < 3 || !reader.getBlockState(pos).isSolid() || reader.getBlockState(pos.up(1)).isSolid()) {
      return false;
    }
    
    // rotation / mirror
    Mirror mirror = Mirror.NONE;
    Rotation rotation = Rotation.randomRotation(rand);
    
    // placement settings
    ChunkPos chunkPos = new ChunkPos(pos);
    MutableBoundingBox mbb = new MutableBoundingBox(chunkPos.getXStart(), pos.getY() - 16, chunkPos.getZStart(), chunkPos.getXEnd(), pos.getY() + 16, chunkPos.getZEnd());
    PlacementSettings placement = new PlacementSettings()
        .setRotation(rotation).setMirror(mirror).setRandom(rand).setBoundingBox(mbb)
        .addProcessor(BlockIgnoreStructureProcessor.STRUCTURE_BLOCK);
    
    // actually generate the structure
    return template.func_237146_a_(reader, pos, pos, placement, rand, 2);
  }

}
