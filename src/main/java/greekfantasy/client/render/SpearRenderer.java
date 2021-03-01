package greekfantasy.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import greekfantasy.GreekFantasy;
import greekfantasy.entity.misc.SpearEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.model.TridentModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;

public class SpearRenderer<T extends SpearEntity> extends EntityRenderer<T> {
  
  private final TridentModel tridentModel = new TridentModel();
  
  public SpearRenderer(EntityRendererManager renderManagerIn) { super(renderManagerIn); }

  @Override
  public void render(T entity, float entityYaw, float partialTick, MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight) {
    matrixStack.push();
    
    matrixStack.rotate(Vector3f.YP.rotationDegrees(MathHelper.lerp(partialTick, entity.prevRotationYaw, entity.rotationYaw) - 90.0F));
    matrixStack.rotate(Vector3f.ZP.rotationDegrees(MathHelper.lerp(partialTick, entity.prevRotationPitch, entity.rotationPitch) + 90.0F));
    
    IVertexBuilder vertexBuilder = ItemRenderer.getEntityGlintVertexBuilder(buffer, RenderType.getEntityCutoutNoCull(getEntityTexture(entity)), false, entity.isEnchanted());
    this.tridentModel.render(matrixStack, vertexBuilder, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
    
    matrixStack.pop();
    
    super.render(entity, entityYaw, partialTick, matrixStack, buffer, packedLight);
  }

  @Override
  public ResourceLocation getEntityTexture(T entity) { return entity.getTexture(); }
  
  public static class SpearItemStackRenderer extends ItemStackTileEntityRenderer {
    final TridentModel spearModel = new TridentModel();
    final ResourceLocation texture;
    
    public SpearItemStackRenderer(final String name) {
      texture = new ResourceLocation(GreekFantasy.MODID, "textures/entity/spear/" + name + ".png");
    }
    
    @Override
    public void func_239207_a_(ItemStack item, TransformType transform, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLightIn, int combinedOverlayIn) {
      matrixStack.push();
      matrixStack.scale(-1.0F, -1.0F, 1.0F);
      IVertexBuilder vertexBuilder = ItemRenderer.getEntityGlintVertexBuilder(buffer, RenderType.getEntityCutoutNoCull(texture), false, item.hasEffect());
      spearModel.render(matrixStack, vertexBuilder, combinedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
      matrixStack.pop();
    }
  }
}

