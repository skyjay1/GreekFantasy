package greekfantasy.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;

import greekfantasy.GreekFantasy;
import greekfantasy.client.render.model.CyclopesModel;
import greekfantasy.entity.CyclopesEntity;
import net.minecraft.client.renderer.entity.BipedRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;

public class CyclopesRenderer<T extends CyclopesEntity> extends BipedRenderer<T, CyclopesModel<T>> {
  
  private static final ResourceLocation TEXTURE = new ResourceLocation(GreekFantasy.MODID, "textures/entity/cyclopes.png");
  private static final float SCALE = 1.15F;

  public CyclopesRenderer(final EntityRendererManager renderManagerIn) {
    super(renderManagerIn, new CyclopesModel<T>(0.0F), 0.5F);
  }

  @Override
  public ResourceLocation getEntityTexture(final T entity) {
    return TEXTURE;
  }
  
  @Override
  protected void preRenderCallback(final T entity, MatrixStack matrix, float ageInTicks) {
    matrix.scale(SCALE, SCALE, SCALE);
  }
}
