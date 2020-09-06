package greekfantasy.client.render;

import greekfantasy.GreekFantasy;
import greekfantasy.client.model.CyclopesModel;
import greekfantasy.entity.CyclopesEntity;
import net.minecraft.client.renderer.entity.BipedRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;

public class CyclopesRenderer<T extends CyclopesEntity> extends BipedRenderer<T, CyclopesModel<T>> {
  
  private static final ResourceLocation TEXTURE = new ResourceLocation(GreekFantasy.MODID, "textures/entity/cyclopes.png");

  public CyclopesRenderer(final EntityRendererManager renderManagerIn) {
    super(renderManagerIn, new CyclopesModel<T>(0.0F), 0.0F);
  }

  @Override
  public ResourceLocation getEntityTexture(final T entity) {
    return TEXTURE;
  }
}
