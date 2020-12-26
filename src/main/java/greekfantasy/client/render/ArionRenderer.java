package greekfantasy.client.render;

import greekfantasy.GreekFantasy;
import greekfantasy.entity.ArionEntity;
import net.minecraft.client.renderer.entity.ChestedHorseRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;

public class ArionRenderer<T extends ArionEntity> extends ChestedHorseRenderer<T> {
  
  private static final ResourceLocation TEXTURE = new ResourceLocation(GreekFantasy.MODID, "textures/entity/arion.png");
 
  public ArionRenderer(final EntityRendererManager renderManagerIn) {
    super(renderManagerIn, 1.0F);
  }

  @Override
  public ResourceLocation getEntityTexture(final T entity) {
    return TEXTURE;
  }
}
