package greekfantasy.structure;

import java.util.Random;

import greekfantasy.GreekFantasy;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.gen.feature.structure.TemplateStructurePiece;
import net.minecraft.world.gen.feature.template.BlockIgnoreStructureProcessor;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.TemplateManager;

public class HarpyNestPiece { // extends TemplateStructurePiece {
  
//  private final String templateName;
//  private final Rotation rotation;
//  private final Mirror mirror;
//  
//  public HarpyNestPiece(final TemplateManager templateManager, final BlockPos templatePositionIn, 
//      final String nameIn, final Rotation rotationIn, final Mirror mirrorIn) {
//    super(GFStructures.HARPY_NEST_PIECE, 0);
//    this.templateName = nameIn;
//    this.rotation = rotationIn;
//    this.mirror = mirrorIn;
//    this.templatePosition = templatePositionIn;
//    this.loadTemplate(templateManager);
//  }
//
//  public HarpyNestPiece(TemplateManager templateManager, CompoundNBT nbt) {
//    super(GFStructures.HARPY_NEST_PIECE, nbt);
//    this.templateName = nbt.getString("Name");
//    this.rotation = Rotation.valueOf(nbt.getString("Rotation"));
//    this.mirror = Mirror.valueOf(nbt.getString("Mirror"));
//    this.loadTemplate(templateManager);
//  }
//
//  @Override
//  protected void handleDataMarker(String arg0, BlockPos arg1, IServerWorld arg2, Random arg3, MutableBoundingBox arg4) {
//    // TODO Auto-generated method stub
//    
//  }
//  
//  @Override
//  protected void readAdditional(CompoundNBT tagCompound) {
//    super.readAdditional(tagCompound);
//    tagCompound.putString("Name", this.templateName);
//    tagCompound.putString("Rotation", this.placeSettings.getRotation().name());
//    tagCompound.putString("Mirror", this.placeSettings.getMirror().name());
//  }
//  
//  private void loadTemplate(TemplateManager templateManager) {
//    Template template = templateManager.getTemplateDefaulted(new ResourceLocation(GreekFantasy.MODID, this.templateName));
//    PlacementSettings placementsettings = (new PlacementSettings()).setIgnoreEntities(true).setRotation(this.rotation).setMirror(this.mirror).addProcessor(BlockIgnoreStructureProcessor.STRUCTURE_BLOCK);
//    this.setup(template, this.templatePosition, placementsettings);
//  }

}
