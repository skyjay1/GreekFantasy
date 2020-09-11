package greekfantasy.structure;

import com.mojang.serialization.Codec;

import greekfantasy.GreekFantasy;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.gen.feature.template.TemplateManager;

public class HarpyNestStructure { // extends Structure<NoFeatureConfig> {
  
  //private static final List<Biome.SpawnListEntry> SPAWN_LIST = Lists.newArrayList(new Biome.SpawnListEntry[] { new Biome.SpawnListEntry(EntityType.WITCH, 1, 1, 1) });
  
  public static final String NAME = "harpy_nest";

//  public HarpyNestStructure(Codec<NoFeatureConfig> codec) {
//    super(codec);
//    // TODO Auto-generated constructor stub
//  }
//
//  @Override
//  public IStartFactory<NoFeatureConfig> getStartFactory() {
//    return HarpyNestStructure.Start::new;
//  }
//  
//  @Override
//  public String getStructureName() {
//    return GreekFantasy.MODID + ":" + NAME;
//  }
//  
//  
//  
//  public static class Start extends StructureStart<NoFeatureConfig> {
//
//    public Start(Structure<NoFeatureConfig> s, int chunkX, int chunkZ, MutableBoundingBox boudingBox, int i3,
//        long seed) {
//      super(s, chunkX, chunkZ, boudingBox, i3, seed);
//    }
//
//    @Override
//    public void func_230364_a_(final DynamicRegistries reg, final ChunkGenerator generator, final TemplateManager templateManagerIn,
//        final int chunkX, final int chunkZ, final Biome biome, final NoFeatureConfig config) {
//      int x = chunkX * 16 + 4 + this.rand.nextInt(8);
//      int z = chunkZ * 16 + 4 + this.rand.nextInt(8);
//      int y = generator.getHeight(x, z, Heightmap.Type.WORLD_SURFACE_WG);
//      final Rotation rotation = Util.getRandomObject(Rotation.values(), this.rand);
//      final Mirror mirror = Util.getRandomObject(Mirror.values(), this.rand);
//      final String template = HarpyNestStructure.NAME + "_" + (1 + this.rand.nextInt(2));
//      
//      final HarpyNestPiece nestPiece = new HarpyNestPiece(templateManagerIn, new BlockPos(x, y, z), template, rotation, mirror);
//      this.components.add(nestPiece);
//      this.recalculateStructureSize();
//    }
//    
//  }

}
