package greekfantasy.client.render;

import greekfantasy.GreekFantasy;
import net.minecraft.client.renderer.entity.AbstractHorseRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.layers.LeatherHorseArmorLayer;
import net.minecraft.client.renderer.entity.model.HorseModel;
import net.minecraft.entity.passive.horse.HorseEntity;
import net.minecraft.util.ResourceLocation;

public class ArionRenderer extends AbstractHorseRenderer<HorseEntity, HorseModel<HorseEntity>> {
  
  private static final ResourceLocation TEXTURE = new ResourceLocation(GreekFantasy.MODID, "textures/entity/arion.png");
 
  public ArionRenderer(final EntityRendererManager renderManagerIn) {
    super(renderManagerIn, new HorseModel<>(0.0F), 1.1F);
    this.addLayer(new LeatherHorseArmorLayer(this));
  }

  @Override
  public ResourceLocation getTextureLocation(final HorseEntity entity) {
    return TEXTURE;
  }
}
