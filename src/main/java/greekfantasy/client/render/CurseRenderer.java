package greekfantasy.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import greekfantasy.GreekFantasy;
import greekfantasy.client.render.model.CurseModel;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.util.ResourceLocation;

public class CurseRenderer<T extends ProjectileEntity> extends EntityRenderer<T> {
  
  private static final ResourceLocation TEXTURE = new ResourceLocation(GreekFantasy.MODID, "textures/entity/curse.png");
//  private static final float SCALE = 1.0F;
  
  protected CurseModel<T> entityModel;
  
  public CurseRenderer(final EntityRendererManager renderManagerIn) {
    super(renderManagerIn);
    entityModel = new CurseModel<T>();
  }

  @Override
  public void render(final T entityIn, final float rotationYawIn, final float ageInTicks, final MatrixStack matrixStackIn,
      final IRenderTypeBuffer bufferIn, final int packedLightIn) {
    matrixStackIn.push();
    matrixStackIn.translate(0, -1.125D, 0);
//    matrixStackIn.scale(SCALE, -SCALE, SCALE);
    final IVertexBuilder vertexBuilder = bufferIn.getBuffer(RenderType.getEntityCutoutNoCull(getEntityTexture(entityIn)));
    super.render(entityIn, rotationYawIn, ageInTicks, matrixStackIn, bufferIn, packedLightIn);
    entityModel.setRotationAngles(entityIn, 0, 0, ageInTicks, 0, 0);
    entityModel.render(matrixStackIn, vertexBuilder, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
    matrixStackIn.pop();
  }
  
  @Override
  public ResourceLocation getEntityTexture(final T entity) {
    return TEXTURE;
  }
  
}
