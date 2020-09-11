package greekfantasy.structure;

import greekfantasy.GreekFantasy;
import greekfantasy.structure.feature.HarpyNestFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(GreekFantasy.MODID)
public class GFStructures {

  @ObjectHolder(HarpyNestFeature.NAME)
  public static final Feature<NoFeatureConfig> HARPY_NEST_FEATURE = null;
  
//  @ObjectHolder(HarpyNestStructure.NAME)
//  public static Structure<NoFeatureConfig> HARPY_NEST_STRUCTURE;
//
//  public static final IStructurePieceType HARPY_NEST_PIECE = register("harpy_nest", HarpyNestPiece::new);
//
//  private static IStructurePieceType register(String key, IStructurePieceType type) {
//    return Registry.register(Registry.STRUCTURE_PIECE, new ResourceLocation(GreekFantasy.MODID, key), type);
//  }
}
